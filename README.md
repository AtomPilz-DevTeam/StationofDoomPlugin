Stationofdoom Plugin [![Discord](https://img.shields.io/discord/827941357824770098?label=Discord&logo=Discord)](https://discord.gg/uYwAKpRyak) [![Time spent](https://wakatime.com/badge/github/12jking/StationofDoomPlugin.svg)](https://wakatime.com/badge/github/12jking/StationofDoomPlugin) [![CodeFactor](https://www.codefactor.io/repository/github/atompilz-devteam/stationofdoomplugin/badge)](https://www.codefactor.io/repository/github/atompilz-devteam/stationofdoomplugin)
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
To compile the plugin, you need JDK 17 and an internet connection.

Clone this repo and run `gradle jar` from your terminal. You can find the compiled jar in the project root's `build/libs` directory.

Support Me
------
Contributions, issues, and feature requests are welcome!
Give a ⭐️ if you like this project!
<!-- modrinth_exclude.end -->