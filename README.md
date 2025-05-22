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
  A Jump 'n' Run minigame that triggers upon player death: <br>
  
  **How it works:**
  - Upon death, players get a chance to recover their items by completing a parkour challenge
    - the experience will still be dropped to balance the plugin
  - Successfully completing the challenge returns all items to the player
  - Failing the challenge drops items at the death location

  **Difficulty System:**
  - Difficulty increases with each successful completion
  - Players can spend diamonds to lower their difficulty
  - Maximum difficulty: default is `10`
  - Diamond cost: default is `6`

  **Commands:**
  - Settings menu: `/game settings`

  **Configuration:**
  - Use `/game settings` to configure parkour parameters
  - See "In game settings menu" section for details
  </details>
- <details>
  <summary>In game settings menu</summary>
  
  This plugin features an in game settings menu to every operator of the server. It can be reached through the command `/game settings`. In the settings many important values can be changed to fix problems or set up the plugin.<br>
  - **Menus inside the settings:** <br>
    - **setUp**: hosts all settings to set up the plugin before a playthrough <br>
      - **parkourStartHeight**: Starting height for parkour generation (Default: `100`) <br>
      - **parkourLength**: Length of the parkour course (Default: `10`) <br>
      - **costToLowerTheDifficulty**: Diamond cost to reduce difficulty (Default: `6`) <br>
      - **timeToDecideWhenRespawning**: Time limit for minigame decision in seconds (Default: `10`) <br>
    - **introduction**: Manage player introduction status
      - Players start with 'false' and receive a brief introduction on first death<br>
    - **difficulty**: Manage individual player parkour difficulty
      - **difficultyPlayerSettings**: Configure difficulty level per player<br>
    - **usesPlugin**: Toggle whether players can access the parkour minigame
  </details>
- <details> 
  <summary>Database Connection</summary>
  
  - Usage of the plugin is possible with or without a PostgreSQL database, without a database all stats and info created by the plugin are only saved until the next server restart.
  - **Connecting a database**
    1. Start the server and plugin once
    2. Enter your database-parameters into the `config.yml` file from this plugin
    3. finished
  </details>

- <details> 
  <summary>Team System</summary>

  - Another feature of this plugin is its team system, which lets players create teams, to access it enter the command `/teams`. The teams are saved in the database if one is connected
  - **List of Features**
    1. create Teams
    2. change appearance of the team (color & name)
    3. rank system inside the teams to lock important settings of it
    4. team enderchest
    5. shortcut command to the players team menu (`/team`)
    6. protect a location from interactions of players outside the team (the radius of the location can be configured in game settings or via the config file)
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
