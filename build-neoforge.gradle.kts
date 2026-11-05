// Shared node script for NeoForge nodes (1.21.1-neoforge, 26.1.2-neoforge): ModStitch + ModDevGradle.
// The Forge 1.20.1 node uses build.gradle.kts (Architectury Loom).
plugins {
    id("dev.isxander.modstitch.base") version "0.8.5"
}

fun prop(name: String): String = project.property(name).toString()
fun optProp(name: String): String? = project.findProperty(name)?.toString()?.takeIf { it.isNotBlank() }

val modId = prop("mod.id")
val mcVersion = prop("deps.minecraft")
// Outer name must differ from the moddevgradle receiver property, or it resolves to itself.
val targetNeoForgeVersion = prop("deps.neoforge")
val nodeName = stonecutter.current.project
val targetJavaVersion = if (stonecutter.current.parsed >= "26.1") 25 else 21
val artifactVersion = "${prop("mod.version")}+$mcVersion"
// 1.21.4+ splits datagen into clientData / serverData run types.
val splitDatagen = stonecutter.current.parsed >= "1.21.4"

// Per-node datagen output (datapack layouts differ between Minecraft versions).
val generatedResources = file("src/generated/resources")
sourceSets.main { resources.srcDir(generatedResources) }
// Plain JUnit tests need the game classes (no game bootstrap); ModDevGradle only puts them on main.
sourceSets.test {
    compileClasspath += sourceSets.main.get().compileClasspath
    runtimeClasspath += sourceSets.main.get().runtimeClasspath
}

modstitch {
    minecraftVersion = mcVersion
    javaVersion = targetJavaVersion

    metadata {
        modId = prop("mod.id")
        modName = prop("mod.name")
        modVersion = artifactVersion
        modGroup = prop("mod.group")
        modAuthor = prop("mod.authors")
        modDescription = prop("mod.description")
        modLicense = prop("mod.license")
    }

    moddevgradle {
        neoForgeVersion = targetNeoForgeVersion
        defaultRuns()

        configureNeoForge {
            runs {
                register("data") {
                    if (splitDatagen) clientData() else data()
                    ideName = "Minecraft Data ($nodeName)"
                    programArguments.addAll(
                        "--mod", modId, "--all",
                        "--output", generatedResources.absolutePath,
                        "--existing", rootProject.file("src/main/resources").absolutePath,
                    )
                }
                configureEach {
                    gameDirectory = rootProject.file("run/$nodeName")
                    systemProperty("forge.logging.markers", "REGISTRIES")
                    systemProperty("neoforge.enabledGameTestNamespaces", modId)
                }
            }
        }
    }
}

base.archivesName = "$modId-neoforge"

repositories {
    maven("https://maven.blamejared.com/") {
        name = "BlameJared (JEI / Patchouli)"
        content {
            includeGroup("mezz.jei")
            includeGroup("vazkii.patchouli")
        }
    }
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:${prop("test.junit")}"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    // NeoForge 1.20.5+ mod jars are Mojang-named: plain configurations, no remapping needed.
    optProp("deps.jei")?.let { jei ->
        compileOnly("mezz.jei:jei-$mcVersion-common-api:$jei")
        compileOnly("mezz.jei:jei-$mcVersion-neoforge-api:$jei")
        // The loader jar already contains every JEI module; its POM dependencies would duplicate classes.
        runtimeOnly("mezz.jei:jei-$mcVersion-neoforge:$jei") { isTransitive = false }
    }
    optProp("deps.patchouli")?.let { patchouli ->
        compileOnly("vazkii.patchouli:Patchouli:$patchouli:api")
        runtimeOnly("vazkii.patchouli:Patchouli:$patchouli")
    }
    project.properties.keys.filter { it.startsWith("deps.mod.") }.sorted().forEach { key ->
        implementation(prop(key))
    }
}

java {
    withSourcesJar()
    toolchain.languageVersion = JavaLanguageVersion.of(targetJavaVersion)
}

tasks {
    withType<JavaCompile>().configureEach {
        options.encoding = "UTF-8"
        options.release = targetJavaVersion
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
        filesMatching(listOf("META-INF/neoforge.mods.toml", "pack.mcmeta")) { expand(props) }
        // NeoForge ships META-INF/neoforge.mods.toml; the Forge metadata belongs to the 1.20.1 node.
        exclude("META-INF/mods.toml")
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
        )
    }
}
