pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        maven("https://maven.architectury.dev")
        maven("https://maven.fabricmc.net/")
        maven("https://maven.minecraftforge.net/")
        maven("https://maven.neoforged.net/releases/")
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
    id("dev.kikugie.stonecutter") version "0.9.7"
}

stonecutter {
    // Build scripts are Kotlin DSL; mod source stays plain Java.
    kotlinController = true
    // Default node script: Architectury Loom (Forge 1.20.1).
    centralScript = "build.gradle.kts"

    create(rootProject) {
        // Node id != logical version, otherwise "-forge" is parsed as a SemVer pre-release.
        versions("1.20.1-forge" to "1.20.1")
        // NeoForge nodes share a ModStitch / ModDevGradle script.
        versions(
            "1.21.1-neoforge" to "1.21.1",
            "26.1.2-neoforge" to "26.1.2",
        ).buildscript("build-neoforge.gradle.kts")

        // Source committed to git is always the 1.20.1-forge variant.
        vcsVersion = "1.20.1-forge"
    }
}

rootProject.name = "FoodIndustrial"
