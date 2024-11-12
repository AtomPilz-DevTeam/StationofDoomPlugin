Stationofdoom Plugin [![Discord](https://img.shields.io/discord/827941357824770098?label=Discord&logo=Discord)](https://discord.gg/uYwAKpRyak)  [![wakatime](https://wakatime.com/badge/user/49ee5b93-5588-4f44-a2a6-bceec1836f4a/project/74ea39da-4817-4a56-b2b0-9bd35bb4ef71.svg)](https://wakatime.com/badge/user/49ee5b93-5588-4f44-a2a6-bceec1836f4a/project/74ea39da-4817-4a56-b2b0-9bd35bb4ef71) [![CodeFactor](https://www.codefactor.io/repository/github/atompilz-devteam/stationofdoomplugin/badge)](https://www.codefactor.io/repository/github/atompilz-devteam/stationofdoomplugin) [![Reposilte](https://repo.jonasfranke.xyz/api/badge/latest/releases/com/github/atompilz-devteam/stationofdoom?color=40c14a&name=Reposilite&prefix=v)](https://repo.jonasfranke.xyz/#/releases/com/github/atompilz-devteam/stationofdoom)
===========
A minecraft paper survival plugin for private servers

**List of features**
- <details>
    <summary>Custom enchantments</summary>
    Flight -> Entities you hit fly up and receive fall damage as they fall down
    <br>
    Furnace -> Ore are smelted directly when you break them
    <br>
    Telepathy -> Items you break go directly to your inventory
  </details>
- <details>
    <summary>Support for multiple languages</summary>
    <br>
    Currently, German and English are supported. Use /language to change your language
  </details>
- <details>
    <summary>Quality of life improvements</summary>
      - /ping -> Get your current ping
      <br>
      - /voterestart -> Vote for a serverrestart - if the majority of online players vote for a restart the server will be restartet
      <br>
      - /sit -> Allows you to sit
      <br>
      - /afk -> Show in the tablist that you are afk
      <br>
      - Custom tab list
      <br>
      - Custom join/quit messages
      <br>
      - Support for MiniMessage in chat messages
  </details>
- <details>
  <summary>Bow combo</summary>
  Deal more damage with your bow when you hit a combo
  </details>

- <details>
  <summary>DeathMinigame</summary>
  This system lets you play a minigame of Jump ’n’ Run when dying in game. If you can get to the end of it, you will get your items back. This system is dynamik, there is difficulty that gets higher when winning. If you can't get to the end, your items will be dropped normally, and you will respawn normally also. If you want to lower your difficulty or reached the max difficulty, you can pay with diamonds, to lower it.
  </details>
- <details>
  <summary>In game settings menu</summary>
  This plugin features an in game settings menu to every operator of the server. It can be reached through the command /game settings. In the settings many important values can be changed to fix problems or set up the plugin.
  <br> Menus inside the settings:
  <br>

  - setUp: hosts all settings to set up the plugin before a playthrough <br>
    - parkourStartHeight: sets at which height the parkour is starting to generate <br>
    - parkourLength: sets how long the parkour should be <br>
    - costToLowerTheDifficulty: sets how many diamonds lowering the difficulty should cost <br>
    - timeToDecideWhenRespawning: sets how long a player has to decide, if he wants to try playing the minigame or if he wants to just let his items drop <br>
  - introduction: in here you can change which player was already introduced to the plugin, normaly everyone starts with false and gets a short introduction at the first death
  - difficulty: in here every player is listet to change their difficulty in the parkour
    - difficultyPlayerSettings: this is just for you to enter the difficulty for the player clicked
  - usesPlugin: in here you can change if the player is using the plugin or not, this changes if the player gets a change to play the parkour or not
  </details>

**Support and Project Discussion:**
- [Discord](https://discord.gg/uYwAKpRyak)

Installation and Usage
------
First download the latest version and move it to your server's plugins folder.
> Make sure to have a server based on the paper software

Finding the configuration file
------
First make sure that the jar is in the correct directory, then start the server.
If everything works there should be a `config.yml` file in the `StationOfDoom` directory.

Enabling discord webhooks
------
Go to your Discord channel where you want to receive the messages and create a new webhook. Copy the url, paste it into the `config.yml` and set enabled to `true`.

<!-- modrinth_exclude.start -->
How To (GitHub download)
------
Download the plugin from the [releases tab](https://github.com/12jking/StationofDoomPlugin/releases) and copy the plugin to your plugins folder.

How To (Compiling Jar From Source)
------
To compile the plugin, you need JDK 21 and an internet connection.

Clone this repo and run `gradle jar` from your terminal. You can find the compiled jar in the project root's `build/libs` directory.

# API Usage
Add the plugin as a dependency to your plugin
<details>
<summary>Gradle (Groovy dsl)</summary>

```groovy
maven {
    name "reposiliteRepositoryReleases"
    url "https://repo.jonasfranke.xyz/releases"
}


dependencies {
    implementation "com.github.atompilz-devteam:TAG"
}
```
</details>
<details>
<summary>Gradle (Kotlin dsl)</summary>

```kotlin
maven {
    name = "reposiliteRepositoryReleases"
    url = uri("https://repo.jonasfranke.xyz/releases")
}

dependencies {
    implementation("com.github.atompilz-devteam:TAG")
}
```
</details>
<details>
<summary>Maven</summary>

```xml
<repository>
    <id>reposilite-repository</id>
    <name>Jonas Franke Repository</name>
    <url>https://repo.jonasfranke.xyz/<repository></url>
</repository>

<dependency>
    <groupId>com.github.atompilz-devteam</groupId>
    <artifactId>stationofdoom</artifactId>
    <version>TAG</version>
</dependency>

```
</details>


Support Me
------
Contributions, issues, and feature requests are welcome!
Give a ⭐️ if you like this project!
<!-- modrinth_exclude.end -->
