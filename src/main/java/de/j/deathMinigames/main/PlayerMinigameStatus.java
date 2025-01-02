package de.j.deathMinigames.main;

/**
 * Represents the various states a player can be in during a minigame.
 * This enum is designed for single-threaded usage within the Minecraft server environment.
*/
public enum PlayerMinigameStatus {
    ALIVE,
    DEAD,
    IN_MINIGAME,
    IN_WAITING_LIST,
    DECIDING,
    INTRODUCTION,
    OFFLINE
}
