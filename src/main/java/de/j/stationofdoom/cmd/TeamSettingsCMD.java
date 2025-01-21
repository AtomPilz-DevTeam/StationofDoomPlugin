package de.j.stationofdoom.cmd;

import de.j.stationofdoom.main.Main;
import de.j.stationofdoom.teams.TeamSettingsInventoryListener;
import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class TeamSettingsCMD implements BasicCommand {

    @Override
    public boolean canUse(@NotNull CommandSender sender) {
        return sender instanceof Player;
    }

    @Override
    public void execute(CommandSourceStack stack, String[] args) {
        Player player = (Player) stack.getSender();
        TeamSettingsInventoryListener.teamsMainMenu.showInventory(player);
    }
}
