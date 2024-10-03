import net.minecrell.pluginyml.bukkit.BukkitPluginDescription

plugins {
    id("java")
    id("xyz.jpenilla.run-paper") version "2.3.1"
    id("net.minecrell.plugin-yml.bukkit") version "0.6.0"
    id("io.papermc.paperweight.userdev") version "1.7.3"
    id("maven-publish")
    id("com.modrinth.minotaur") version "2.+"
}

val minecraftVersion = "1.21.1"
val pluginVersion: String = "1.14.2.1" + if (System.getenv("runnumber") != null) "." + System.getenv("runnumber") else ""

group = "org.example"
version = pluginVersion

repositories {
    mavenLocal()
    maven { url = uri("https://papermc.io/repo/repository/maven-public/") }
}

dependencies {
    library("com.google.code.gson:gson:2.11.0")
    library("club.minnced:discord-webhooks:0.8.4")
    paperweight.paperDevBundle("$minecraftVersion-R0.1-SNAPSHOT")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

tasks {
    runServer {
        minecraftVersion(minecraftVersion)
    }

    withType<JavaCompile> {
        options.encoding = "UTF-8"
    }

    compileJava {
        options.encoding = "UTF-8"
    }
}

modrinth {
    token.set(System.getenv("MODRINTH_TOKEN"))
    projectId.set("NgIVqJpi")
    versionNumber.set(pluginVersion)
    val releaseType = if (System.getenv("modrinthrelease") == "release") "release" else "alpha"
    versionType.set(releaseType)
    uploadFile.set(tasks.jar)
    gameVersions.add(minecraftVersion)
    loaders.addAll("paper", "purpur")
    syncBodyFrom = rootProject.file("README.md").readText()
}

publishing {
    repositories {
        maven {
            name = "jfrepository"
            url = uri("https://repo.jonasfranke.xyz/releases")
            credentials {
                username = System.getenv("MAVEN_USERNAME")
                password = System.getenv("MAVEN_PASSWORD")
            }
            authentication {
                create<BasicAuthentication>("basic")
            }
        }
    }
    publications {
        create<MavenPublication>("maven") {
            groupId = "de.j"
            artifactId = "stationofdoom"
            version = pluginVersion
            from(components["java"])
        }
    }
}


bukkit {
    name = "StationOfDoom"
    version = pluginVersion
    description = ""

    main = "de.j.stationofdoom.main.Main"

    generateLibrariesJson = true

    foliaSupported = false

    apiVersion = "1.21"

    load = BukkitPluginDescription.PluginLoadOrder.STARTUP
    authors = listOf("LuckyProgrammer")

}