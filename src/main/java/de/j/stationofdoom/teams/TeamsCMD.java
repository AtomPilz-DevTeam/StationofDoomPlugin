package de.j.stationofdoom.teams;

import de.j.deathMinigames.main.HandlePlayers;
import de.j.stationofdoom.main.Main;
import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class TeamsCMD implements BasicCommand {
    public TeamsMainMenuGUI teamsMainMenuGUI = new TeamsMainMenuGUI();

    @Override
    public boolean canUse(@NotNull CommandSender sender) {
        return sender instanceof Player;
    }

    @Override
    public void execute(CommandSourceStack stack, String[] args) {
        Player player = (Player) stack.getSender();
        if(args.length == 0) {
            teamsMainMenuGUI.showPage(1, player);
        }
        else if(args.length == 1 && (args[0].equalsIgnoreCase("e") || args[0].equalsIgnoreCase("enderchest"))) {
            Team team = TeamsMainMenuGUI.getTeam(HandlePlayers.getInstance().getPlayerData(player.getUniqueId()));
            if(team == null || !team.isMember(player.getUniqueId())) return;
            team.accessEnderChest(player);
        }
    }
}
