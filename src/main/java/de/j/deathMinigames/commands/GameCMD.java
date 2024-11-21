package de.j.deathMinigames.commands;

import de.j.deathMinigames.listeners.DeathListener;
import de.j.deathMinigames.main.*;
import de.j.stationofdoom.util.translations.TranslationFactory;
import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import de.j.stationofdoom.main.Main;
import de.j.deathMinigames.listeners.RespawnListener;
import de.j.deathMinigames.minigames.Difficulty;
import de.j.deathMinigames.minigames.Minigame;
import de.j.deathMinigames.settings.MainMenu;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

import static de.j.deathMinigames.listeners.DeathListener.*;

public class GameCMD implements BasicCommand {

    private final Difficulty difficulty = Difficulty.getInstance();
    private final Minigame minigame = Minigame.getInstance();
    private final RespawnListener respawnListener = new RespawnListener();
    private final Introduction introduction = Introduction.getInstance();
    private final MainMenu mainMenu = new MainMenu();
    private final Config config = Config.getInstance();
    private final TranslationFactory tf = new TranslationFactory();

    @Override
    public boolean canUse(@NotNull CommandSender sender) {
        return sender instanceof Player;
    }

    @Override
    public void execute(CommandSourceStack stack, String[] args) {
        Player player = (Player) stack.getSender();
        PlayerData playerData = HandlePlayers.getKnownPlayers().get(player.getUniqueId());
        if (args.length == 1) {
            String arg0 = args[0];
            Main.getPlugin().getLogger().info("Handled args.length 1");
            handleArgsLength1Execution(playerData, player, arg0);
        }
        else if (args.length == 2) {
            String arg0 = args[0];
            String arg1 = args[1];
            handleArgsLength2Execution(playerData, player, arg0, arg1);
        }
        else if (args.length == 3) {
            String arg0 = args[0];
            String arg1 = args[1];
            String arg2 = args[2];
            handleArgsLength3Execution(player, arg0, arg1, arg2);
        }
    }

    private void handleArgsLength1Execution(PlayerData playerData, Player player, String arg0) {
        switch (arg0.toLowerCase()) {
            case "settings":
                handleArgsLength1SettingsExecution(player);
                break;
            case "lowerdifficulty":
                handleArgsLength1LowerDifficultyExecution(playerData, player);
                break;
            case "introplayerdecidestousefeatures":
                handleArgsLength1IntroPlayerDecidesToUseFeaturesExecution(playerData, player);
                break;
            case "introplayerdecidestonotusefeatures":
                handleArgsLength1IntroPlayerDecidesToNotUseFeaturesExecution(playerData, player);
                break;
            case "setnotintroduced":
                playerData.setIntroduction(false);
                break;
            case "difficulty":
                player.sendMessage(Component.text(tf.getTranslation(player, "diffAt")).color(NamedTextColor.GOLD)
                        .append(Component.text(playerData.getDifficulty()).color(NamedTextColor.RED)));
                break;
            case "setwaitinglistposition":
                handleArgsLength1SetWaitingListPositionExecution(player);
                break;
            case "decidednottosetposition":
                player.sendMessage(Component.text(tf.getTranslation(player, "decidedNotToSetPosition")).color(NamedTextColor.RED));
                break;
            case "status":
                player.sendMessage("Status: " + playerData.getStatus().toString());
                break;
            case "test":

        }
        if (inventories.containsKey(player.getUniqueId()) && !waitingListMinigame.contains(player) && DeathListener.getPlayerInArena() != player) {
            switch (arg0.toLowerCase()) {
                case "start":
                    Main.getPlugin().getLogger().info("Handled start");
                    handleArgsLength1StartExecution(player);
                    break;
                case "ignore":
                    handleArgsLength1IgnoreExecution(player);
                    break;
                default:
                    if(!playerData.getIntroduction()) {
                        player.sendMessage(Component.text("Usage: /game <start/ignore/difficulty>").color(NamedTextColor.RED));
                    }
                    break;
            }
        }
    }

    private void handleArgsLength2Execution(PlayerData playerData, Player player, String arg0, String arg1) {
        if(player.isOp()) {
            switch (arg0) {
                case "difficulty":
                    handleArgsLength2DifficultyExecution(playerData, player, arg1);
                    break;
                case "introPlayerDecidesToUseFeatures":
                    playerData.setUsesPlugin(true);
                    break;
                case "introPlayerDecidesToNotUseFeatures":
                    playerData.setUsesPlugin(false);
                    break;
            }
        }
        else {
            player.sendMessage(Component.text(tf.getTranslation(player, "playerNotOP")).color(NamedTextColor.RED));
        }
    }

    private void handleArgsLength3Execution(Player player, String arg0, String arg1, String arg2) {
        if(player.isOp()) {
            switch (arg0) {
                case "difficulty":
                    handleArgsLength3DifficultyExecution(player, arg1, arg2);
                    break;
                case "introPlayerDecidesToUseFeatures":
                    handleArgsLength3IntroPlayerDecidesToUseFeaturesExecution(player, arg1);
                    break;
                case "introPlayerDecidesToNotUseFeatures":
                    handleArgsLength3introPlayerDecidesToNotUseFeaturesExecution(player, arg1);
                    break;
            }
        }
        else {
            player.sendMessage(Component.text(tf.getTranslation(player, "playerNotOP")).color(NamedTextColor.RED));
        }
    }

    private void handleArgsLength1SettingsExecution(Player player) {
        if(player.isOp()) {
            mainMenu.showPlayerSettings(player);
        }
        else {
            player.sendMessage(Component.text(tf.getTranslation(player, "playerNotOP")).color(NamedTextColor.RED));
        }
    }

    private void handleArgsLength1LowerDifficultyExecution(PlayerData playerData, Player player) {
        if(difficulty.checkIfPlayerCanPay(player)) {
            int diff = playerData.getDifficulty();
            if(diff > 0) {
                difficulty.playerPay(player);
                difficulty.lowerDifficulty(player);
                minigame.playSoundAtLocation(player.getEyeLocation(), 0.5F, Sound.ENTITY_ENDER_EYE_DEATH);
                player.sendMessage(MiniMessage.miniMessage().deserialize(Component.text(tf.getTranslation(player, "changedDiff", diff)).content()));
            }
            else {
                player.sendMessage(Component.text(tf.getTranslation(player, "diffAlreadyAt")).color(NamedTextColor.GOLD)
                        .append(Component.text(diff).color(NamedTextColor.RED)));
            }
        }
        else {
            player.sendMessage(Component.text(tf.getTranslation(player, "notEnoughResources")).color(NamedTextColor.RED));
        }
    }

    private void handleArgsLength1IntroPlayerDecidesToUseFeaturesExecution(PlayerData playerData, Player player) {
        if (!introduction.checkIfPlayerGotIntroduced(player)) {
            playerData.setIntroduction(true);
            playerData.setUsesPlugin(true);
            Minigame.minigameStart(player);
            player.sendMessage(Component.text(tf.getTranslation(player, "playerDecided")).color(NamedTextColor.GOLD));
        }
        else {
            player.sendMessage(Component.text(tf.getTranslation(player, "playerAlreadyDecided")).color(NamedTextColor.RED));
        }
    }

    private void handleArgsLength1IntroPlayerDecidesToNotUseFeaturesExecution(PlayerData playerData, Player player) {
        if (!playerData.getIntroduction()) {
            playerData.setIntroduction(true);
            playerData.setUsesPlugin(false);
            introduction.dropInv(player);
            player.sendMessage(Component.text(tf.getTranslation(player, "playerDecided")).color(NamedTextColor.GOLD));
        }
        else {
            player.sendMessage(Component.text(tf.getTranslation(player, "playerAlreadyDecided")).color(NamedTextColor.RED));
        }
    }

    private void handleArgsLength1SetWaitingListPositionExecution(Player player) {
        Location location = player.getLocation();
        config.setWaitingListPosition(location);
        player.sendMessage(Component.text(tf.getTranslation(player, "setWaitingListPosition")).color(NamedTextColor.GREEN));
    }

    private void handleArgsLength1StartExecution(Player player) {
        PlayerData playerData = HandlePlayers.getKnownPlayers().get(player.getUniqueId());
        Main.getPlugin().getLogger().info("Started Minigame");
        minigame.playSoundAtLocation(player.getEyeLocation(), 0.5F, Sound.ENTITY_ENDER_EYE_DEATH);
        player.resetTitle();
        player.sendActionBar(Component.text(tf.getTranslation(player, "startingMinigame"))
                .color(NamedTextColor.GOLD)
                .decoration(TextDecoration.ITALIC, true));
        player.playSound(player.getEyeLocation(), Sound.BLOCK_PORTAL_TRAVEL, 0.5F, 1.0F);
        waitingListMinigame.addLast(player);
        playerData.setStatus(PlayerMinigameStatus.alive);
        Minigame.minigameStart(player);
    }

    private void handleArgsLength1IgnoreExecution(Player player) {
        PlayerData playerData = HandlePlayers.getKnownPlayers().get(player.getUniqueId());

        minigame.playSoundToPlayer(player, 0.5F, Sound.ENTITY_ITEM_BREAK);
        playerData.setStatus(PlayerMinigameStatus.alive);
        player.resetTitle();
        if (!waitingListMinigame.contains(player) && inventories.containsKey(player.getUniqueId())) {
            player.sendMessage(Component.text(tf.getTranslation(player, "droppingInvAt")).color(NamedTextColor.GOLD)
                    .append(Component.text("X: " + deaths.get(player.getUniqueId()).getBlockX() + " Y: " + deaths.get(player.getUniqueId()).getBlockY() + " Z: " + deaths.get(player.getUniqueId()).getBlockZ()).color(NamedTextColor.RED))
                    .append(Component.text(")")).color(NamedTextColor.GOLD));
            for (int i = 0; i < inventories.get(player.getUniqueId()).getSize(); i++) {
                if (inventories.get(player.getUniqueId()).getItem(i) == null) continue;
                player.getWorld().dropItem(deaths.get(player.getUniqueId()), inventories.get(player.getUniqueId()).getItem(i));
            }
            inventories.remove(player.getUniqueId());
        }
    }

    private void handleArgsLength2DifficultyExecution(PlayerData playerData, Player player, String arg1) {
        if(arg1 != null) {
            int i;
            try {
                i = Integer.parseInt(arg1);
            } catch (NumberFormatException e) {
                player.sendMessage(Component.text(tf.getTranslation(player, "youHaveToEnterANumber")).color(NamedTextColor.RED));
                return;
            }
            playerData.setDifficulty(i);
            player.sendMessage(MiniMessage.miniMessage().deserialize(Component.text(tf.getTranslation(player, "setDiffTo", playerData.getDifficulty())).content()));
        }
        else {
            player.sendMessage(Component.text(tf.getTranslation(player, "youHaveToEnterANumber")));
        }
    }

    private void handleArgsLength3DifficultyExecution(Player player, String arg2, String arg1) {
        if(arg2 != null) {
            int i = 0;
            try{
                i = Integer.parseInt(arg2);
            }
            catch (NumberFormatException e) {
                player.sendMessage(Component.text(tf.getTranslation(player, "youHaveToEnterANumber")).color(NamedTextColor.RED));
            }
            Player playerToEdit = Bukkit.getPlayer(arg1);
            assert playerToEdit != null;
            PlayerData playerDataPlayerToEdit = HandlePlayers.getKnownPlayers().get(playerToEdit.getUniqueId());
            playerDataPlayerToEdit.setDifficulty(i);
            player.sendMessage(MiniMessage.miniMessage().deserialize(Component.text(tf.getTranslation(player, "setDiffOfTo", playerToEdit, playerDataPlayerToEdit.getDifficulty())).content()));
        }
        else {
            player.sendMessage(Component.text(tf.getTranslation(player, "youHaveToEnterANumber")).color(NamedTextColor.RED));
        }
    }

    private void handleArgsLength3IntroPlayerDecidesToUseFeaturesExecution(Player player, String arg1) {
        Player playerToEdit = Bukkit.getPlayer(arg1);
        if(playerToEdit != null) {
            PlayerData playerDataPlayerToEdit = HandlePlayers.getKnownPlayers().get(playerToEdit.getUniqueId());
            playerDataPlayerToEdit.setUsesPlugin(true);
        }
        else {
            player.sendMessage(Component.text());
        }
    }

    private void handleArgsLength3introPlayerDecidesToNotUseFeaturesExecution(Player player, String arg1) {
        Player playerToEdit = Bukkit.getPlayer(arg1);
        if(playerToEdit != null) {
            PlayerData playerDataPlayerToEdit = HandlePlayers.getKnownPlayers().get(playerToEdit.getUniqueId());
            playerDataPlayerToEdit.setUsesPlugin(false);
        }
        else {
            player.sendMessage(Component.text(tf.getTranslation(player, "didNotEnterKnownPlayer")));
        }
    }

    @Override
    public @NotNull Collection<String> suggest(@NotNull CommandSourceStack commandSourceStack, @NotNull String[] args) {
        if (args.length == 0) {
            Collection<String> suggestions = new ArrayList<>();
            suggestions.add("difficulty");
            suggestions.add("lowerDifficulty");
            return suggestions;
        }
        else if (args.length == 2) {
            if(args[1].equals("difficulty")) {
                Collection<String> suggestions2 = new ArrayList<>();
                for (UUID uuid : HandlePlayers.getKnownPlayers().keySet()) {
                    suggestions2.add(Bukkit.getPlayer(uuid).getName());
                }
                return suggestions2;
            }
        }
        return BasicCommand.super.suggest(commandSourceStack, args);
    }
}
