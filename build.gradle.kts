import java.security.MessageDigest
import java.util.zip.ZipFile

// Central node script: Forge nodes built with Architectury Loom (currently only 1.20.1-forge).
// NeoForge nodes use build-neoforge.gradle.kts (see settings.gradle.kts).
plugins {
    id("dev.architectury.loom") version "1.11.458"
}

fun prop(name: String): String = project.property(name).toString()
fun optProp(name: String): String? = project.findProperty(name)?.toString()?.takeIf { it.isNotBlank() }

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

    // Loom dev does not unpack Jar-in-Jar contents (META-INF/jarjar) of dependency mods, e.g. Thermal
    // Foundation bundles thermal_core (mod id "thermal"). Extract them once here: nested mods are remapped
    // like any other mod, nested plain libraries join the Forge runtime classpath. Dev runtime only.
    if (modCoordinates.isNotEmpty()) {
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
                mavenCentral()
            }
        }
        val modJars = rootProject.configurations
            .detachedConfiguration(*modCoordinates.map { rootProject.dependencies.create(it) }.toTypedArray())
            .apply { isTransitive = false }
        modJars.files.forEach { modJar ->
            ZipFile(modJar).use { zip ->
                zip.entries().asSequence()
                    .filter { it.name.startsWith("META-INF/jarjar/") && it.name.endsWith(".jar") }
                    .forEach { entry ->
                        val nested = jijDir.resolve(entry.name.substringAfterLast('/'))
                        if (!nested.exists()) {
                            nested.parentFile.mkdirs()
                            zip.getInputStream(entry).use { input -> nested.outputStream().use { input.copyTo(it) } }
                        }
                        val isMod = ZipFile(nested).use { it.getEntry("META-INF/mods.toml") != null }
                        if (isMod) "modLocalRuntime"(files(nested)) else "forgeRuntimeLibrary"(files(nested))
                    }
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
