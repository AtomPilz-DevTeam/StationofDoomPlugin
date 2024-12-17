package de.j.deathMinigames.commands;

import de.j.deathMinigames.main.HandlePlayers;
import de.j.deathMinigames.main.PlayerData;
import de.j.stationofdoom.util.translations.TranslationFactory;
import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class LeaderboardCMD implements BasicCommand {

    /**
     * Determines if the given command sender can use this command.
     *
     * <p>This command can only be used by {@link Player}s.
     *
     * @param sender the command sender to check
     * @return true if the command sender can use this command, false otherwise
     */
    @Override
    public boolean canUse(@NotNull CommandSender sender) {
        return sender instanceof Player;
    }

    /**
     * Executes the command for the given command source stack.
     *
     * <p>
     * This method retrieves the player from the command source stack and sends a test message followed by the
     * database connection information. Each key-value pair in the connection info is sent as a message to the player.
     *
     * @param stack the command source stack
     * @param args the arguments provided with the command
     */
    @Override
    public void execute(@NotNull CommandSourceStack stack, @NotNull String[] args) {
        Player player = (Player) stack.getSender();
        TranslationFactory tf = new TranslationFactory();
        HandlePlayers handlePlayers = HandlePlayers.getInstance();
        List<PlayerData> leaderboard = handlePlayers.getLeaderBoard();
        if(args.length == 0) {
            if(leaderboard.isEmpty()) {
                player.sendMessage(Component.text(tf.getTranslation(player, "leaderboardEmpty")).color(NamedTextColor.RED));
                return;
            }
            player.sendMessage(tf.getTranslation(player, "leaderboard"));
            player.sendMessage("------------------");
            for (int i = 0; i < leaderboard.size(); i++) {
                PlayerData playerData = leaderboard.get(i);
                player.sendMessage(Component.text(i + 1 + ". " + playerData.getName() + " - " + playerData.getBestParkourTime() + "s"));
            }
            player.sendMessage("------------------");
        }
        else if (args.length == 1 && player.isOp()) {
            if(args[0].equalsIgnoreCase("reset")) {
                handlePlayers.resetLeaderboardAndTimesOfPlayers();
                player.sendMessage(Component.text(tf.getTranslation(player, "leaderboardReset")));
            }
            else {
                player.sendMessage(Component.text(tf.getTranslation(player, "invalidArgument")).color(NamedTextColor.RED));
            }
        }
    }

    @Override
    public @NotNull Collection<String> suggest(@NotNull CommandSourceStack stack, @NotNull String[] args) {
        Collection<String> suggestions = new ArrayList<>();
        Player player = (Player) stack.getSender();
        if(player.isOp()) {
            suggestions.add("reset");
        }
        return suggestions;
    }
}
