import java.security.MessageDigest
import java.util.zip.ZipFile

// Central node script: Forge nodes built with Architectury Loom (currently only 1.20.1-forge).
// NeoForge nodes use build-neoforge.gradle.kts (see settings.gradle.kts).
plugins {
    id("dev.architectury.loom") version "1.11.458"
}

fun prop(name: String): String = project.property(name).toString()
fun optProp(name: String): String? = project.findProperty(name)?.toString()?.takeIf { it.isNotBlank() }
fun names(value: String?): Set<String> = value?.split(',')?.map { it.trim() }?.filter { it.isNotEmpty() }?.toSet() ?: emptySet()
/** Optional mods in the dev runtime: fi.devWith (always), plus -PfiWith, minus -PfiWithout. */
fun devRuntimeMods(): Set<String> = names(optProp("fi.devWith")) + names(optProp("fiWith")) - names(optProp("fiWithout"))

val modId = prop("mod.id")
val loader = prop("loom.platform")
val mcVersion = prop("deps.minecraft")
val nodeName = stonecutter.current.project
val javaVersion = if (stonecutter.current.parsed >= "1.20.5") 21 else 17
val artifactVersion = "${prop("mod.version")}+$mcVersion"

group = prop("mod.group")
version = artifactVersion
base.archivesName = "$modId-$loader"

// Per-node datagen output (datapack layouts differ between Minecraft versions).
val generatedResources = file("src/generated/resources")
sourceSets.main { resources.srcDir(generatedResources) }

// Loom holds a configuration lock per project while it sets up Minecraft ("Waiting for lock to be
// released ... held by pid"). A Gradle process killed during that setup leaves the lock behind; Loom
// waits for its pid for up to an hour, and on Windows that pid is soon reused by unrelated programs.
// A lock whose holder is still running but is not a Java process is such a leftover: mark it
// disowned, which makes Loom take it over and rebuild its cache. Holders that are Java processes
// (other Gradle builds) are left alone.
run {
    val key = "${projectDir.absolutePath}:${project.path}"
    val sha1 = MessageDigest.getInstance("SHA-1").digest(key.toByteArray()).joinToString("") { "%02x".format(it) }
    val lock = gradle.gradleUserHomeDir.resolve("caches/fabric-loom/.$sha1.lock")
    val pid = if (lock.isFile) lock.readText().trim().toLongOrNull() else null
    val command = pid?.let { ProcessHandle.of(it).flatMap { holder -> holder.info().command() }.orElse(null) }
    val executable = command?.substringAfterLast('/')?.substringAfterLast('\\')?.lowercase()
    if (executable != null && executable !in setOf("java", "java.exe", "javaw", "javaw.exe")) {
        logger.lifecycle("Loom configuration lock of $path is held by pid $pid ($command), not a Java process: left over from a killed build, released")
        lock.writeText("disowned")
    }
}

// Jars of the integrated mods (deps.mod.*, deps.opt.* and what they nest), listed for the data run.
val externalJarList: File = layout.buildDirectory.file("fi-external-jars.txt").get().asFile

loom {
    silentMojangMappingsLicense()

    mixin { defaultRefmapName = "$modId.refmap.json" }
    // Loom's Forge runs do NOT read [[mixins]] from mods.toml: register the config here.
    forge { mixinConfig("$modId.mixins.json") }

    runs {
        named("client") { name("Minecraft Client ($nodeName)") }
        named("server") { name("Minecraft Server ($nodeName)") }
        create("data") {
            data()
            name("Minecraft Data ($nodeName)")
            programArgs(
                "--mod", modId, "--all",
                "--output", generatedResources.absolutePath,
                "--existing", rootProject.file("src/main/resources").absolutePath,
            )
            // The other mods' jars, so integration recipes that clash with their own are left out (I25).
            property("foodindustrial.externalJars", externalJarList.absolutePath)
            // Hand-made nutrition data of vanilla and the bundled mods' foods, input of the data run (I34).
            property("foodindustrial.nutritionInputs", rootProject.file("tools/nutrition").absolutePath)
        }
        configureEach {
            runDir("../../run/$nodeName")
            property("forge.logging.markers", "REGISTRIES")
            property("forge.logging.console.level", "debug")
            property("forge.enabledGameTestNamespaces", modId)
            ideConfigGenerated(stonecutter.current.isActive)
        }
    }
}

repositories {
    mavenCentral()
    maven("https://maven.minecraftforge.net/")
    maven("https://maven.neoforged.net/releases/")
    maven("https://maven.blamejared.com/") {
        name = "BlameJared (JEI / Patchouli)"
        content {
            includeGroup("mezz.jei")
            includeGroup("vazkii.patchouli")
        }
    }
    exclusiveContent {
        forRepository { maven("https://www.cursemaven.com") { name = "CurseMaven" } }
        filter { includeGroup("curse.maven") }
    }
    exclusiveContent {
        forRepository { maven("https://api.modrinth.com/maven") { name = "Modrinth" } }
        filter { includeGroup("maven.modrinth") }
    }
    // Nested mods extracted from the dependency mods (see below), as modules: Loom remaps the mods of one
    // configuration together only when they are modules, and a mod calling into its nested mod (GTCEu into
    // Registrate) needs that one on the remap classpath, or the calls keep their SRG names (2026-11-11).
    exclusiveContent {
        forRepository { flatDir { name = "JiJ"; dirs(layout.buildDirectory.dir("jij")) } }
        filter { includeGroup("jij") }
    }
}

dependencies {
    minecraft("com.mojang:minecraft:$mcVersion")
    mappings(loom.officialMojangMappings())
    "forge"("net.minecraftforge:forge:$mcVersion-${prop("deps.forge")}")

    testImplementation(platform("org.junit:junit-bom:${prop("test.junit")}"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    // Forge mod jars are SRG-named: always use Loom's mod* configurations so they get remapped.
    optProp("deps.jei")?.let { jei ->
        "modCompileOnly"("mezz.jei:jei-$mcVersion-common-api:$jei")
        "modCompileOnly"("mezz.jei:jei-$mcVersion-$loader-api:$jei")
        // The loader jar already contains every JEI module; its POM dependencies would duplicate classes.
        "modRuntimeOnly"("mezz.jei:jei-$mcVersion-$loader:$jei") { isTransitive = false }
    }
    optProp("deps.patchouli")?.let { patchouli ->
        "modCompileOnly"("vazkii.patchouli:Patchouli:$patchouli:api")
        "modRuntimeOnly"("vazkii.patchouli:Patchouli:$patchouli")
    }
    val modCoordinates = project.properties.keys.filter { it.startsWith("deps.mod.") }.sorted().map { prop(it) }
    modCoordinates.forEach { "modImplementation"(it) }
    // Dev runtime only (deps.dev.<name>): mods that fill material tags for the dev runs, never compiled against.
    project.properties.keys.filter { it.startsWith("deps.dev.") }.sorted().forEach { "modLocalRuntime"(prop(it)) }
    // Optional mods (deps.opt.<name>): compiled against always, in the dev runtime only when named in
    // -PfiWith=<name>,… (or "all"), so runs with and without each mod are possible (I24–I27, 2026-11-11).
    // fi.devWith names those in every dev run (Jade, 2026-11-12, user); -PfiWithout=<name>,… leaves them out.
    val withMods = devRuntimeMods()
    val optionalRuntime = mutableListOf<String>()
    project.properties.keys.filter { it.startsWith("deps.opt.") }.sorted().forEach { key ->
        "modCompileOnly"(prop(key))
        val name = key.removePrefix("deps.opt.")
        if (name in withMods || "all" in withMods && name !in names(optProp("fiWithout"))) {
            "modLocalRuntime"(prop(key))
            optionalRuntime += prop(key)
        }
    }
    // Mods whose Jar-in-Jar contents the dev runtime needs: the deps.mod.* mods, the optional mods in
    // the runtime and JEI (15.20+ bundles its config library mezz_config). The nested mods of every
    // optional mod also join the compile classpath: code against Create's kinetic blocks needs the
    // Flywheel and Ponder classes Create bundles (I25, 2026-11-11).
    val optionalCoordinates = project.properties.keys.filter { it.startsWith("deps.opt.") }.sorted().map { prop(it) }
    val jijRuntime = (modCoordinates + optionalRuntime + listOfNotNull(optProp("deps.jei")?.let { "mezz.jei:jei-$mcVersion-$loader:$it" })).toSet()
    val jijCoordinates = jijRuntime + optionalCoordinates

    // Loom dev does not unpack Jar-in-Jar contents (META-INF/jarjar) of dependency mods, e.g. Thermal
    // Foundation bundles thermal_core (mod id "thermal"). Extract them once here: nested mods are remapped
    // like any other mod, nested plain libraries join the Forge runtime classpath.
    if (jijCoordinates.isNotEmpty()) {
        val jijDir = layout.buildDirectory.dir("jij").get().asFile
        // Resolve through the root project: resolving in this project before Loom finishes its setup
        // locks Loom's local repositories ("Cannot mutate content repository descriptor ...").
        with(rootProject.repositories) {
            if (findByName("JiJ CurseMaven") == null) {
                exclusiveContent {
                    forRepository { maven("https://www.cursemaven.com") { name = "JiJ CurseMaven" } }
                    filter { includeGroup("curse.maven") }
                }
                exclusiveContent {
                    forRepository { maven("https://api.modrinth.com/maven") { name = "JiJ Modrinth" } }
                    filter { includeGroup("maven.modrinth") }
                }
                exclusiveContent {
                    forRepository { maven("https://maven.blamejared.com/") { name = "JiJ BlameJared" } }
                    filter { includeGroup("mezz.jei") }
                }
                mavenCentral()
            }
        }
        // Nested jars by coordinate; a nested mod bundled by several mods (or twice, NuclearCraft ships two
        // Flywheels) is used once, in its highest version.
        data class Nested(val coordinate: String, val file: File, val modId: String?)
        val nestedJars = mutableListOf<Nested>()
        val externalJars = mutableListOf<File>()
        jijCoordinates.forEach { coordinate ->
            val modJar = rootProject.configurations
                .detachedConfiguration(rootProject.dependencies.create(coordinate))
                .apply { isTransitive = false }
                .singleFile
            if (coordinate in modCoordinates || coordinate in optionalCoordinates) {
                externalJars += modJar
            }
            ZipFile(modJar).use { zip ->
                zip.entries().asSequence()
                    .filter { it.name.startsWith("META-INF/jarjar/") && it.name.endsWith(".jar") }
                    .forEach { entry ->
                        val nested = jijDir.resolve(entry.name.substringAfterLast('/'))
                        if (!nested.exists()) {
                            nested.parentFile.mkdirs()
                            zip.getInputStream(entry).use { input -> nested.outputStream().use { input.copyTo(it) } }
                        }
                        // A mod's id from mods.toml; a game library (FMLModType: GAMELIBRARY, e.g. mixinextras-forge)
                        // counts as a mod under its module name, so FML loads it and applies its MixinConfigs:
                        // as a plain runtime library MixinExtras never starts and GTCEu's @Local mixins fail.
                        val modId = ZipFile(nested).use { inner ->
                            inner.getEntry("META-INF/mods.toml")?.let { toml ->
                                inner.getInputStream(toml).bufferedReader().readText()
                                    .let { Regex("""modId\s*=\s*"([^"]+)"""").find(it)?.groupValues?.get(1) ?: "?" }
                            } ?: inner.getEntry("META-INF/MANIFEST.MF")?.let { manifest ->
                                val text = inner.getInputStream(manifest).bufferedReader().readText()
                                if (Regex("""FMLModType:\s*GAMELIBRARY""").containsMatchIn(text)) {
                                    Regex("""Automatic-Module-Name:\s*(\S+)""").find(text)?.groupValues?.get(1)
                                        ?: Regex("""^(.*?)-\d[^-]*$""").find(nested.nameWithoutExtension)?.groupValues?.get(1) ?: nested.name
                                } else null
                            }
                        }
                        nestedJars += Nested(coordinate, nested, modId)
                        if (modId != null && (coordinate in modCoordinates || coordinate in optionalCoordinates)) {
                            externalJars += nested
                        }
                    }
            }
        }
        externalJarList.parentFile.mkdirs()
        externalJarList.writeText(externalJars.joinToString("\n") { it.absolutePath })
        fun versionKey(file: File): List<Int> = Regex("\\d+").findAll(file.name.substringAfter("1.20.1")).map { it.value.toInt() }.toList()
        val versionOrder = Comparator<File> { a, b ->
            val x = versionKey(a)
            val y = versionKey(b)
            (0 until maxOf(x.size, y.size)).map { (x.getOrElse(it) { 0 }).compareTo(y.getOrElse(it) { 0 }) }.firstOrNull { it != 0 } ?: 0
        }
        // The highest version of each nested mod, chosen separately among the jars the runtime takes and
        // among those compiled against (the runtime may hold only some of the optional mods).
        // Libraries without a mod id (Registrate) go by their file name without the version: Create and
        // GTCEu nest different Registrates, and two of them on the module path cannot both load.
        fun key(jar: Nested): String = jar.modId ?: Regex("""^(.*?)-\d[^-]*$""").find(jar.file.nameWithoutExtension)?.groupValues?.get(1)
            ?: jar.file.nameWithoutExtension
        fun highest(jars: List<Nested>): Map<String, File> = jars.groupBy { key(it) }
            .mapValues { (_, group) -> group.map { it.file }.maxWith(versionOrder) }
        val chosenRuntime = highest(nestedJars.filter { it.coordinate in jijRuntime })
        val chosenCompile = highest(nestedJars.filter { it.coordinate in optionalCoordinates })
        nestedJars.forEach { jar ->
            val isMod = jar.modId != null
            val module = "jij:${jar.file.nameWithoutExtension}"
            if (jar.coordinate in jijRuntime && chosenRuntime[key(jar)] == jar.file) {
                // A bundled mixinextras-forge (Create, GTCEu) needs the common MixinExtras classes beside it in dev.
                Regex("""mixinextras-forge-(.+)\.jar""").matchEntire(jar.file.name)?.let { match ->
                    "forgeRuntimeLibrary"("io.github.llamalad7:mixinextras-common:${match.groupValues[1]}")
                }
                if (isMod) "modLocalRuntime"(module) else "forgeRuntimeLibrary"(files(jar.file))
            }
            if (jar.coordinate in optionalCoordinates && isMod && chosenCompile[key(jar)] == jar.file) {
                "modCompileOnly"(module)
            }
        }
    }
}

java {
    withSourcesJar()
    toolchain.languageVersion = JavaLanguageVersion.of(javaVersion)
}

tasks {
    // Build from Stonecutter-preprocessed sources. Loom registers createMinecraftArtifacts lazily.
    configureEach { if (name == "createMinecraftArtifacts") dependsOn("stonecutterGenerate") }

    withType<JavaCompile>().configureEach {
        options.encoding = "UTF-8"
        options.release = javaVersion
        dependsOn("stonecutterGenerate")
    }

    test {
        useJUnitPlatform()
    }

    processResources {
        val props = mapOf(
            "id" to modId,
            "name" to project.property("mod.name"),
            "version" to artifactVersion,
            "authors" to project.property("mod.authors"),
            "description" to project.property("mod.description"),
            "license" to project.property("mod.license"),
            "loaderRange" to project.property("vers.loaderRange"),
            "loaderDepRange" to project.property("vers.loaderDepRange"),
            "minecraftRange" to project.property("vers.minecraftRange"),
            "packFormat" to project.property("vers.packFormat"),
        )
        inputs.properties(props)
        filesMatching(listOf("META-INF/mods.toml", "pack.mcmeta")) { expand(props) }
        // Forge ships META-INF/mods.toml; the NeoForge metadata belongs to the other nodes.
        exclude("META-INF/neoforge.mods.toml")
    }

    withType<Jar>().configureEach {
        archiveVersion = artifactVersion
    }

    jar {
        manifest.attributes(
            "Specification-Title" to modId,
            "Specification-Vendor" to prop("mod.authors"),
            "Specification-Version" to "1",
            "Implementation-Title" to project.name,
            "Implementation-Version" to artifactVersion,
            "Implementation-Vendor" to prop("mod.authors"),
            // Forge 1.20.1 production loads mixin configs from the manifest.
            "MixinConfigs" to "$modId.mixins.json",
        )
    }
}
