package de.j.stationofdoom.cmd;

import de.j.stationofdoom.main.Main;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;

public class ServerCMD implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (player.isOp()) {
                if (args.length == 2) {
                    if (args[0].equalsIgnoreCase("download")) {
                        update(player, args);
                    }
                }
            }
        } else if (sender instanceof ConsoleCommandSender) {
            if (args.length == 2) {
                if (args[0].equalsIgnoreCase("download")) {
                    update(sender, args);
                }
            }
        }
        return false;
    }

    private void update(CommandSender sender, String[] args) {
        if (args[0].equalsIgnoreCase("download")) {
            try {
                int versionToDownload = Integer.parseInt(args[1]);
                Thread downloadThread = new Thread(() -> {
                    Main.getPlugin().getLogger().info("Starting download Thread...");
                    try {
                        Process p = Runtime.getRuntime().exec("curl -O https://papermc.io/api/v2/projects/paper/versions/1.16.5/builds/" + versionToDownload + "/downloads/paper-1.16.5-" + versionToDownload + ".jar");
                        p.waitFor();
                    } catch (IOException | InterruptedException e) {
                        e.printStackTrace();
                    } finally {
                        Main.getPlugin().getLogger().info("Closing download Thread...");
                    }
                });
                Thread deleteThread = new Thread(() -> {
                    Main.getPlugin().getLogger().info("Starting download Thread...");
                    try {
                        File oldFile = new File("paper-1.16.5-565.jar");
                        assert oldFile.delete();
                    } catch (AssertionError e) {
                        e.printStackTrace();
                    } finally {
                        Main.getPlugin().getLogger().info("Closing delete Thread...");
                    }
                });

                downloadThread.start();
                deleteThread.start();

                File oldName = new File("paper-1.16.5-" + versionToDownload + ".jar");
                File newName = new File("paper-1.16.5-565.jar");
                assert oldName.renameTo(newName);
            } catch (NumberFormatException e) {
                sender.sendMessage(ChatColor.RED + "Wrong usage!");
            } catch (AssertionError e) {
                e.printStackTrace();
            }
        }
    }
}
