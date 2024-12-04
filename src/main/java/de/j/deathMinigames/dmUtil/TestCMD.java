package de.j.deathMinigames.dmUtil;

import de.j.deathMinigames.database.Database;
import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class TestCMD implements BasicCommand {

    @Override
    public boolean canUse(@NotNull CommandSender sender) {
        return sender instanceof Player;
    }

    @Override
    public void execute(@NotNull CommandSourceStack stack, @NotNull String[] args) {
        Player player = (Player) stack.getSender();
        player.sendMessage("test");
        HashMap<String, Object> info = Database.getInstance().getConnectionInfo();
        player.sendMessage("Connection info:");
        for (String string : info.keySet()) {
            player.sendMessage(string + ": " + info.get(string).toString());
        }
    }
}
