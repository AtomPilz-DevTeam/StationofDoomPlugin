import net.minecrell.pluginyml.bukkit.BukkitPluginDescription

plugins {
    id("java")
    id("xyz.jpenilla.run-paper") version "2.2.3"
    id("net.minecrell.plugin-yml.bukkit") version "0.6.0"
    id("io.papermc.paperweight.userdev") version "1.5.11"
    id("maven-publish")
    id("com.modrinth.minotaur") version "2.+"
}

val minecraftVersion = "1.20.2"
val pluginVersion: String = "1.13" + if (System.getenv("runnumber") != null) "." + System.getenv("runnumber") else ""

group = "org.example"
version = pluginVersion

repositories {
    mavenLocal()
    maven { url = uri("https://papermc.io/repo/repository/maven-public/") }
}

dependencies {
    library("com.google.code.gson:gson:2.10.1")
    library("club.minnced:discord-webhooks:0.8.4")
    paperweightDevelopmentBundle("io.papermc.paper:dev-bundle:$minecraftVersion-R0.1-SNAPSHOT")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
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
    versionType.set("release")
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

    apiVersion = "1.20"

    load = BukkitPluginDescription.PluginLoadOrder.STARTUP
    authors = listOf("12jking", "LuckyProgrammer")

    commands {
        register("afk") {
            description = "Toggle AFK"
        }
        register("plversion") {
            description = "Shows you the plugin version"
        }
        register("lag") {
            description = "Kick players when lag"
        }
        register("sit") {
            description = "Setze dich hin"
        }
        register("deathpoint") {
            description = "Gibt dir deinen Death Point"
        }
        register("voterestart") {
            description = "Vote für restarts"
        }
        register("customenchant") {
            description = ""
        }
        register("ping") {
            description = "Ping"
        }
        register("language") {
            description = "Change language"
        }

    }

}