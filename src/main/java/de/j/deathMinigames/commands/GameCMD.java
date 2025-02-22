package de.j.deathMinigames.commands;

import de.j.deathMinigames.dmUtil.DmUtil;
import de.j.deathMinigames.main.*;
import de.j.stationofdoom.util.translations.TranslationFactory;
import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import de.j.deathMinigames.minigames.Difficulty;
import de.j.deathMinigames.minigames.Minigame;
import de.j.deathMinigames.settings.MainMenu;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

import static de.j.deathMinigames.main.HandlePlayers.waitingListMinigame;

public class GameCMD implements BasicCommand {

    private final Difficulty difficulty = Difficulty.getInstance();
    private final Minigame minigame = Minigame.getInstance();
    private final Introduction introduction = new Introduction();
    private final MainMenu mainMenu = new MainMenu();
    private final Config config = Config.getInstance();
    private final TranslationFactory tf = new TranslationFactory();

    public enum ArgsLength1 {
        SETTINGS,
        LOWERDIFFICULTY,
        INTROPLAYERDECIDESTOUSEFEATURES,
        INTROPLAYERDECIDESTONOTUSEFEATURES,
        SETNOTINTRODUCED,
        DIFFICULTY,
        SETWAITINGLISTPOSITION,
        DECIDEDNOTTOSETPOSITION,
        STATUS,
        START,
        IGNORE
    }

    /**
     * {@inheritDoc}
     * <p>
     * This command can only be used by {@link Player}s.
     */
    @Override
    public boolean canUse(@NotNull CommandSender sender) {
        return sender instanceof Player;
    }

    /**
     * Handles the execution of the command.
     *
     * <p>
     * Depending on the number of arguments provided, the command is handled differently.
     * If one argument is provided, it is checked if it is a valid argument for length 1.
     * If two arguments are provided, it is checked if they are valid arguments for length 2.
     * If three arguments are provided, it is checked if they are valid arguments for length 3.
     *
     * @param stack the stack of the command sender
     * @param args the arguments provided
     */
    @Override
    public void execute(CommandSourceStack stack, String[] args) {
        if(stack == null) throw new NullPointerException("stack is null!");
        if(args == null) throw new NullPointerException("args is null!");
        Player player = (Player) stack.getSender();
        if(player == null) throw new NullPointerException("player is null!");
        PlayerData playerData = HandlePlayers.getKnownPlayers().get(player.getUniqueId());
        if(playerData == null) throw new NullPointerException("playerData is null!");
        String arg0 = args.length > 0 ? args[0].toLowerCase() : "";
        String arg1 = null;
        if(args.length > 1) {
            arg1 = args[1].toLowerCase();
        }
        String arg2 = null;
        if(args.length > 2) {
            arg2 = args[2].toLowerCase();
        }
        switch (args.length) {
            case 0:
                player.sendMessage(Component.text(tf.getTranslation(player, "cmdUsage")).color(NamedTextColor.RED));
                break;
            case 1:
                handleArgsLength1Execution(playerData, player, arg0);
                break;
            case 2:
                handleArgsLength2Execution(playerData, player, arg0, arg1);
                break;
            case 3:
                handleArgsLength3Execution(player, arg0, arg1, arg2);
                break;
            default:
                player.sendMessage(Component.text(tf.getTranslation(player, "cmdUsage")).color(NamedTextColor.RED));
                break;
        }
    }

    /**
     * Handles the execution of the command when only one argument is provided.
     *
     * <p>
     * Depending on the argument provided, the command is handled differently.
     * If the argument is "settings", the settings inventory is opened.
     * If the argument is "lowerdifficulty", the difficulty of the minigame is lowered.
     * If the argument is "introplayerdecidestousefeatures", the player decides to use features.
     * If the argument is "introplayerdecidestonotusefeatures", the player decides to not use features.
     * If the argument is "setnotintroduced", the player is set to not be introduced.
     * If the argument is "difficulty", the current difficulty of the minigame is sent to the player.
     * If the argument is "setwaitinglistposition", the waiting list position inventory is opened.
     * If the argument is "decidednottosetposition", the player is told that they decided not to set a position.
     * If the argument is "status", the status of the minigame is sent to the player.
     * If the argument is "test", nothing happens.
     *
     * <p>
     * If the player is not in the waiting list, not in an arena and not in the introduction,
     * the command is handled differently.
     * If the argument is "start", the minigame is started.
     * If the argument is "ignore", the player is ignored.
     * If the argument is not one of the above, a usage message is sent to the player.
     *
     * @param playerData the player data of the player
     * @param player the player
     * @param arg0 the argument provided
     */
    private void handleArgsLength1Execution(PlayerData playerData, Player player, String arg0) {
        ArgsLength1 arg0AsEnumType = null;
        try {
            arg0AsEnumType = ArgsLength1.valueOf(arg0.toUpperCase());
        }
        catch (IllegalArgumentException e) {
            player.sendMessage(Component.text(tf.getTranslation(player, "cmdUsageLength1")).color(NamedTextColor.RED));
        }
        if(arg0AsEnumType == null) return;
        switch (arg0AsEnumType) {
            case ArgsLength1.SETTINGS:
                handleArgsLength1SettingsExecution(player);
                break;
            case ArgsLength1.LOWERDIFFICULTY:
                handleArgsLength1LowerDifficultyExecution(playerData, player);
                break;
            case ArgsLength1.INTROPLAYERDECIDESTOUSEFEATURES:
                handleArgsLength1IntroPlayerDecidesToUseFeaturesExecution(playerData, player);
                break;
            case ArgsLength1.INTROPLAYERDECIDESTONOTUSEFEATURES:
                handleArgsLength1IntroPlayerDecidesToNotUseFeaturesExecution(playerData, player);
                break;
            case ArgsLength1.SETNOTINTRODUCED:
                playerData.setIntroduction(false);
                break;
            case ArgsLength1.DIFFICULTY:
                player.sendMessage(Component.text(tf.getTranslation(player, "diffAt")).color(NamedTextColor.GOLD)
                        .append(Component.text(playerData.getDifficulty()).color(NamedTextColor.RED)));
                break;
            case ArgsLength1.SETWAITINGLISTPOSITION:
                handleArgsLength1SetWaitingListPositionExecution(player);
                break;
            case ArgsLength1.DECIDEDNOTTOSETPOSITION:
                player.sendMessage(Component.text(tf.getTranslation(player, "decidedNotToSetPosition")).color(NamedTextColor.RED));
                break;
            case ArgsLength1.STATUS:
                player.sendMessage("Status: " + playerData.getStatus().toString());
                break;
            default:
                handleDecisionCMDsWhenRespawning(playerData, player, arg0AsEnumType);
                break;
        }
    }

    private void handleDecisionCMDsWhenRespawning(PlayerData playerData, Player player, ArgsLength1 arg0) {
        if (!playerData.getLastDeathInventory().isEmpty() && playerData.getStatus() != PlayerMinigameStatus.IN_MINIGAME && playerData.getStatus() != PlayerMinigameStatus.IN_WAITING_LIST) {
            switch (arg0) {
                case ArgsLength1.START:
                    handleArgsLength1StartExecution(player);
                    break;
                case ArgsLength1.IGNORE:
                    handleArgsLength1IgnoreExecution(player);
                    break;
                default:
                    if(!playerData.getIntroduction()) {
                        player.sendMessage(Component.text(tf.getTranslation(player, "cmdUsageLength1")).color(NamedTextColor.RED));
                    }
                    break;
            }
        }
    }

    /**
     * Handles the execution of commands with two arguments.
     *
     * <p>
     * This method processes commands that require two arguments. It checks if the player has
     * operator permissions and then performs actions based on the first argument.
     *
     * <p>
     * If the first argument is "difficulty", it adjusts the player's difficulty settings.
     * If the first argument is "introPlayerDecidesToUseFeatures", it enables the plugin features for the player.
     * If the first argument is "introPlayerDecidesToNotUseFeatures", it disables the plugin features for the player.
     * If the player is not an operator, an error message is sent.
     *
     * @param playerData the player data associated with the current player
     * @param player the player executing the command
     * @param arg0 the first argument provided
     * @param arg1 the second argument provided
     */
    private void handleArgsLength2Execution(PlayerData playerData, Player player, String arg0, String arg1) {
        ArgsLength1 arg0AsEnumType = null;
        try {
            arg0AsEnumType = ArgsLength1.valueOf(arg0.toUpperCase());
        }
        catch (IllegalArgumentException e) {
            player.sendMessage(Component.text(tf.getTranslation(player, "cmdUsageLength1")).color(NamedTextColor.RED));
        }
        if(arg0AsEnumType == null) return;
        if(player.isOp()) {
            switch (arg0AsEnumType) {
                case ArgsLength1.DIFFICULTY:
                    handleArgsLength2DifficultyExecution(playerData, player, arg1);
                    break;
                case ArgsLength1.INTROPLAYERDECIDESTOUSEFEATURES:
                    playerData.setUsesPlugin(true);
                    break;
                case ArgsLength1.INTROPLAYERDECIDESTONOTUSEFEATURES:
                    playerData.setUsesPlugin(false);
                    break;
            }
        }
        else {
            player.sendMessage(Component.text(tf.getTranslation(player, "playerNotOP")).color(NamedTextColor.RED));
        }
    }

    /**
     * Handles the execution of commands with three arguments.
     *
     * <p>This method processes commands that require three arguments. It first checks if the player
     * has operator permissions and then performs actions based on the first argument.
     *
     * <p>If the first argument is "difficulty", it adjusts the player's difficulty settings
     * using the second and third arguments.
     * If the first argument is "introPlayerDecidesToUseFeatures", it enables the plugin features
     * for a specified player.
     * If the first argument is "introPlayerDecidesToNotUseFeatures", it disables the plugin features
     * for a specified player.
     *
     * <p>If the player is not an operator, an error message is sent.
     *
     * @param player the player executing the command
     * @param arg0 the first argument provided
     * @param arg1 the second argument provided
     * @param arg2 the third argument provided
     */
    private void handleArgsLength3Execution(Player player, String arg0, String arg1, String arg2) {
        ArgsLength1 arg0AsEnumType = null;
        try {
            arg0AsEnumType = ArgsLength1.valueOf(arg0.toUpperCase());
        }
        catch (IllegalArgumentException e) {
            player.sendMessage(Component.text(tf.getTranslation(player, "cmdUsageLength1")).color(NamedTextColor.RED));
        }
        if(arg0AsEnumType == null) return;
        if(player.isOp()) {
            switch (arg0AsEnumType) {
                case ArgsLength1.DIFFICULTY:
                    handleArgsLength3DifficultyExecution(player, arg1, arg2);
                    break;
                case ArgsLength1.INTROPLAYERDECIDESTOUSEFEATURES:
                    handleArgsLength3IntroPlayerDecidesToUseFeaturesExecution(player, arg1);
                    break;
                case ArgsLength1.INTROPLAYERDECIDESTONOTUSEFEATURES:
                    handleArgsLength3introPlayerDecidesToNotUseFeaturesExecution(player, arg1);
                    break;
            }
        }
        else {
            player.sendMessage(Component.text(tf.getTranslation(player, "playerNotOP")).color(NamedTextColor.RED));
        }
    }

    /**
     * Handles the execution of commands with one argument, "settings".
     *
     * <p>If the player is an operator, it will open the settings menu for the player.
     * If the player is not an operator, a   message will be sent to the player, that he is not an operator and therefore is not allowed to open the settings menu.
     *
     * @param player the player executing the command
     */
    private void handleArgsLength1SettingsExecution(Player player) {
        if(player.isOp()) {
            mainMenu.showPlayerSettings(player);
        }
        else {
            player.sendMessage(Component.text(tf.getTranslation(player, "playerNotOP")).color(NamedTextColor.RED));
        }
    }

    /**
     * Handles the execution of commands with one argument, "lowerdifficulty".
     *
     * <p>If the player has enough resources, it will lower the player's difficulty.
     * If the player does not have enough resources, an error message will be sent to the player.
     *
     * @param playerData the player data of the player
     * @param player the player executing the command
     */
    private void handleArgsLength1LowerDifficultyExecution(PlayerData playerData, Player player) {
        DmUtil util = DmUtil.getInstance();
        if(difficulty.checkIfPlayerCanPay(player)) {
            int diff = playerData.getDifficulty();
            if(diff > 0) {
                difficulty.removePaymentFromPlayerInventory(player);
                difficulty.lowerDifficulty(player);
                util.playSoundAtLocation(player.getEyeLocation(), 0.5F, Sound.ENTITY_ENDER_EYE_DEATH);
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

    /**
     * Handles the execution of commands with one argument, "introPlayerDecidesToUseFeatures".
     *
     * <p>If the player has not yet been introduced, it will set the player's introduction status
     * to true and enable the plugin features for the player. It will then start the minigame and
     * send a message to the player indicating that they have decided to use the plugin features.
     *
     * <p>If the player has already been introduced, it will send a message to the player
     * indicating that they have already made a decision.
     *
     * @param playerData the player data of the player
     * @param player the player executing the command
     */
    private void handleArgsLength1IntroPlayerDecidesToUseFeaturesExecution(PlayerData playerData, Player player) {
        if (!playerData.getIntroduction()) {
            playerData.setIntroduction(true);
            playerData.setUsesPlugin(true);
            Minigame.getInstance().minigameStart(player);
            player.sendMessage(Component.text(tf.getTranslation(player, "playerDecided")).color(NamedTextColor.GOLD));
        }
        else {
            player.sendMessage(Component.text(tf.getTranslation(player, "playerAlreadyDecided")).color(NamedTextColor.RED));
        }
    }

    /**
     * Handles the execution of commands with one argument, "introPlayerDecidesToNotUseFeatures".
     *
     * <p>If the player has not yet been introduced, it will set the player's introduction status
     * to true and disable the plugin features for the player. It will then drop the player's
     * inventory and send a message to the player indicating that they have decided to not
     * use the plugin features.
     *
     * <p>If the player has already been introduced, it will send a message to the player
     * indicating that they have already made a decision.
     *
     * @param playerData the player data of the player
     * @param player the player executing the command
     */
    private void handleArgsLength1IntroPlayerDecidesToNotUseFeaturesExecution(PlayerData playerData, Player player) {
        DmUtil util = DmUtil.getInstance();
        if (!playerData.getIntroduction()) {
            playerData.setIntroduction(true);
            playerData.setUsesPlugin(false);
            Location lastDeathLocation = playerData.getLastDeathLocation();
            if(lastDeathLocation == null) lastDeathLocation = player.getLastDeathLocation();
            util.dropInv(player, lastDeathLocation);
            minigame.sendLoseMessage(player);
            introduction.teleportPlayerToRespawnLocation(player);
            player.sendMessage(Component.text(tf.getTranslation(player, "playerDecided")).color(NamedTextColor.GOLD));
        }
        else {
            player.sendMessage(Component.text(tf.getTranslation(player, "playerAlreadyDecided")).color(NamedTextColor.RED));
        }
    }

    /**
     * Handles the execution of commands with one argument, "setWaitingListPosition".
     *
     * <p>Sets the waiting list position to the player's current location and sends
     * a message to the player indicating that the waiting list position has been set.
     *
     * @param player the player executing the command
     */
    private void handleArgsLength1SetWaitingListPositionExecution(Player player) {
        Location location = player.getLocation();
        config.setWaitingListPosition(location);
        player.sendMessage(Component.text(tf.getTranslation(player, "setWaitingListPosition")).color(NamedTextColor.GREEN));
    }

    /**
     * Handles the execution of the "start" command with one argument.
     *
     * <p>This method initiates the start of a minigame for the player. It performs the following actions:
     * - Logs the start of the minigame.
     * - Plays a sound effect at the player's location.
     * - Resets the player's title and sends an action bar message indicating the start of the minigame.
     * - Plays a portal travel sound effect.
     * - Adds the player to the waiting list for the minigame.
     * - Sets the player's status to "alive".
     * - Starts the minigame for the player.
     *
     * @param player the player executing the "start" command
     */
    private void handleArgsLength1StartExecution(Player player) {
        DmUtil util = DmUtil.getInstance();
        PlayerData playerData = HandlePlayers.getKnownPlayers().get(player.getUniqueId());
        util.playSoundAtLocation(player.getEyeLocation(), 0.5F, Sound.ENTITY_ENDER_EYE_DEATH);
        player.resetTitle();
        playerData.setStatus(PlayerMinigameStatus.ALIVE);
        Minigame.getInstance().minigameStart(player);
    }

    /**
     * Handles the execution of commands with one argument, "ignore".
     *
     * <p>Resets the player's title, sets the player's status to "alive", and if the player is
     * not in the waiting list and has items in their inventory, it drops the items at the location
     * of death and removes the player from the inventories HashMap. It will then send a message to
     * the player indicating that they have been ignored.
     *
     * @param player the player executing the command
     */
    private void handleArgsLength1IgnoreExecution(Player player) {
        PlayerData playerData = HandlePlayers.getKnownPlayers().get(player.getUniqueId());
        Inventory lastDeathInventory = playerData.getLastDeathInventory();
        Location deathLocation = playerData.getLastDeathLocation();

        minigame.playSoundToPlayer(player, 0.5F, Sound.ENTITY_ITEM_BREAK);
        playerData.setStatus(PlayerMinigameStatus.ALIVE);
        player.resetTitle();
        if (!waitingListMinigame.contains(player) && !lastDeathInventory.isEmpty()) {
            player.sendMessage(Component.text(tf.getTranslation(player, "droppingInvAt")).color(NamedTextColor.GOLD)
                    .append(Component.text("X: " + deathLocation.getBlockX() + " Y: " + deathLocation.getBlockY() + " Z: " + deathLocation.getBlockZ()).color(NamedTextColor.RED))
                    .append(Component.text(")")).color(NamedTextColor.GOLD));
            for (int i = 0; i < lastDeathInventory.getSize(); i++) {
                ItemStack item = lastDeathInventory.getItem(i);
                if(item == null) continue;
                player.getWorld().dropItem(deathLocation, item);
            }
            playerData.getLastDeathInventory().clear();
        }
    }

    /**
     * Handles the execution of commands with two arguments, "difficulty".
     *
     * <p>If the second argument is a valid integer, it will set the player's difficulty
     * to that number and send a message to the player indicating that their difficulty
     * has been set.
     *
     * <p>If the second argument is not a valid integer, it will send a message to the
     * player indicating that they have to enter a number.
     *
     * @param playerData the player data of the player
     * @param player the player executing the command
     * @param arg1 the second argument provided
     */
    private void handleArgsLength2DifficultyExecution(PlayerData playerData, Player player, String arg1) {
        if(arg1 != null) {
            int i;
            try {
                i = Integer.parseInt(arg1);
                if(i < 0 || i > 10) player.sendMessage(Component.text(tf.getTranslation(player, "invalidDifficulty")).color(NamedTextColor.RED));
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

    /**
     * Handles the execution of commands with three arguments for setting player difficulty.
     *
     * <p>This method sets the difficulty level for a specified player based on the provided arguments.
     * It first checks if the third argument is not null and attempts to parse it as an integer, which
     * represents the difficulty level. If the parsing fails, it sends an error message to the player.
     * If successful, it retrieves the player to edit using the second argument and sets their difficulty
     * to the specified level, then confirms the change with a message.
     *
     * <p>If the third argument is null, it sends a message indicating that a number must be entered.
     *
     * @param player the player executing the command
     * @param arg2 the third argument provided, representing the difficulty level
     * @param arg1 the second argument provided, representing the player to edit
     */
    private void handleArgsLength3DifficultyExecution(Player player, String arg1, String arg2) {
        if(arg2 != null) {
            int i;
            try{
                i = Integer.parseInt(arg2);
            }
            catch (NumberFormatException e) {
                player.sendMessage(Component.text(tf.getTranslation(player, "youHaveToEnterANumber")).color(NamedTextColor.RED));
                return;
            }
            Player playerToEdit = Bukkit.getPlayer(arg1);
            assert playerToEdit != null;
            PlayerData playerDataPlayerToEdit = HandlePlayers.getKnownPlayers().get(playerToEdit.getUniqueId());
            if(playerDataPlayerToEdit == null) throw new NullPointerException("playerDataPlayerToEdit is null!");
            playerDataPlayerToEdit.setDifficulty(i);
            player.sendMessage(MiniMessage.miniMessage().deserialize(Component.text(tf.getTranslation(player, "setDiffOfTo", playerToEdit, playerDataPlayerToEdit.getDifficulty())).content()));
        }
        else {
            player.sendMessage(Component.text(tf.getTranslation(player, "youHaveToEnterANumber")).color(NamedTextColor.RED));
        }
    }

    /**
     * Handles the execution of commands with one argument when the player decides to use features.
     *
     * <p>This method enables the plugin features for the specified player.
     * It retrieves the player based on the second argument and sets their plugin usage status to true.
     * If the specified player is not found, it sends a message to the executing player.
     *
     * @param player the player executing the command
     * @param arg1 the second argument provided, representing the player to edit
     */
    private void handleArgsLength3IntroPlayerDecidesToUseFeaturesExecution(Player player, String arg1) {
        Player playerToEdit = Bukkit.getPlayer(arg1);
        if(playerToEdit != null) {
            PlayerData playerDataPlayerToEdit = HandlePlayers.getKnownPlayers().get(playerToEdit.getUniqueId());
            playerDataPlayerToEdit.setUsesPlugin(true);
        }
        else {
            player.sendMessage(Component.text(tf.getTranslation(player, "didNotEnterKnownPlayer")).color(NamedTextColor.RED));
        }
    }

    /**
     * Handles the execution of commands with one argument when the player decides to not use features.
     *
     * <p>This method disables the plugin features for the specified player.
     * It retrieves the player based on the second argument and sets their plugin usage status to false.
     * If the specified player is not found, it sends a message to the executing player.
     *
     * @param player the player executing the command
     * @param arg1 the second argument provided, representing the player to edit
     */
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

    /**
     * Returns a list of suggestions for the given command input.
     *
     * @param commandSourceStack the source of the command
     * @param args the arguments provided
     * @return a list of suggestions
     */
    @Override
    public @NotNull Collection<String> suggest(@NotNull CommandSourceStack commandSourceStack, @NotNull String[] args) {
        Collection<String> suggestions = new ArrayList<>();
        if(commandSourceStack == null || args == null) return suggestions;
        Player player = (Player) commandSourceStack.getSender();
        if(player == null) return suggestions;

        if (args.length == 0) {
            suggestions.add("difficulty");
            suggestions.add("lowerDifficulty");
            return suggestions;
        }
        else if (args.length == 2 && player.isOp()) {
            if("difficulty".equalsIgnoreCase(args[0])) {
                for (UUID uuid : HandlePlayers.getKnownPlayers().keySet()) {
                    Player playerToSuggest = Bukkit.getPlayer(uuid);
                    if(playerToSuggest != null) suggestions.add(playerToSuggest.getName());
                }
                return suggestions;
            }
        }
        return BasicCommand.super.suggest(commandSourceStack, args);
    }
}
