import net.minecrell.pluginyml.bukkit.BukkitPluginDescription

plugins {
    id("java")
    id("xyz.jpenilla.run-paper") version "2.1.0"
    id("net.minecrell.plugin-yml.bukkit") version "0.6.0"
    id("io.papermc.paperweight.userdev") version "1.5.5"
}

val minecraftVersion = "1.20"
val pluginVersion = "1.12.1"

group = "org.example"
version = pluginVersion

repositories {
    mavenLocal()
    maven { url = uri("https://papermc.io/repo/repository/maven-public/") }
}

dependencies {
    library("com.google.code.gson:gson:2.10.1")
    library("club.minnced:discord-webhooks:0.8.2")
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
