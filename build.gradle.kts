import net.minecrell.pluginyml.bukkit.BukkitPluginDescription

plugins {
    id("java")
    id("xyz.jpenilla.run-paper") version "2.3.1"
    id("net.minecrell.plugin-yml.bukkit") version "0.6.0"
    id("io.papermc.paperweight.userdev") version "2.0.0-beta.18"
    id("maven-publish")
    id("com.modrinth.minotaur") version "2.+"
}

val minecraftVersion = "1.21.10"
val pluginVersion: String = "1.15.1" + if (System.getenv("runnumber") != null) "." + System.getenv("runnumber") else ""

group = "com.github.atompilz-devteam"
version = pluginVersion

repositories {
    mavenLocal()
    maven { url = uri("https://repo.papermc.io/repository/maven-public/") }
}

dependencies {
    library("com.google.code.gson:gson:2.13.2")
    library("club.minnced:discord-webhooks:0.8.4")
    paperweight.paperDevBundle("$minecraftVersion-R0.1-SNAPSHOT")
    library("org.postgresql:postgresql:42.7.7")
    library("de.chojo.sadu:sadu-postgresql:2.3.3")
    library("de.chojo.sadu:sadu-datasource:2.3.3")
    library("de.chojo.sadu:sadu-queries:2.3.3")
    library("com.zaxxer:HikariCP:6.3.2")
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
    loaders.addAll("paper", "purpur", "folia")
    syncBodyFrom = rootProject.file("README.md").readText()
}

publishing {
    publications {
        create<MavenPublication>("release") {
            groupId = "com.github.atompilz-devteam"
            artifactId = "stationofdoom"
            version = pluginVersion
            from(components["java"])
        }

        create<MavenPublication>("snapshot") {
            groupId = "com.github.atompilz-devteam"
            artifactId = "stationofdoom"
            version = "$pluginVersion-SNAPSHOT"
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

    foliaSupported = true

    apiVersion = "1.20"

    load = BukkitPluginDescription.PluginLoadOrder.STARTUP
    authors = listOf("LuckyProgrammer")

}