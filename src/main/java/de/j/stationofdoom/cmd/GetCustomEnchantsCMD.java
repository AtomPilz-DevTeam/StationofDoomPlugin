package de.j.stationofdoom.cmd;

import de.j.stationofdoom.enchants.CustomEnchants;
import de.j.stationofdoom.main.Main;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class GetCustomEnchantsCMD implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (args.length == 1) {
                switch (args[0].toLowerCase()) {
                    case "telepathy":
                        player.sendMessage(ChatColor.RED + "Möchtest du " + ChatColor.AQUA + "Telepathy" + ChatColor.RED +  "für " + ChatColor.BOLD + "10" + ChatColor.RESET + ChatColor.RED + "Dias kaufen");
                        TextComponent message = new TextComponent("Dann klicke diese Nachricht!");
                        message.setColor(net.md_5.bungee.api.ChatColor.RED);
                        message.setClickEvent(new ClickEvent(
                                ClickEvent.Action.RUN_COMMAND, "/customenchant telepathy confirm"
                        ));
                        player.spigot().sendMessage(message);
                        break;
                    default:
                        Main.getMainLogger().severe("How did we end up here? Please contact the dev or the admin");
                        break;
                }
            } else if (args.length == 2) {
                switch (args[0].toLowerCase()) {
                    case "telepathy":
                        if (args[1].equalsIgnoreCase("confirm")) {
                            Inventory inventory = player.getInventory();
                            if (inventory.contains(Material.DIAMOND)) {
                                for (ItemStack itemStack : inventory.getContents()) {
                                    if (itemStack != null) {
                                        if (itemStack.getType() == Material.DIAMOND) {
                                            if (itemStack.getAmount() >= 10) {
                                                ItemStack item = player.getInventory().getItemInMainHand();
                                                if (item.getType() == Material.DIAMOND_PICKAXE || item.getType() == Material.NETHERITE_PICKAXE) {
                                                    item.addUnsafeEnchantment(CustomEnchants.TELEPATHY, 1);

                                                    ItemMeta meta = item.getItemMeta();
                                                    List<String> lore = new ArrayList<>();
                                                    lore.add(ChatColor.GRAY + "Telepathy I");
                                                    assert meta != null;
                                                    meta.setLore(lore);
                                                    player.getInventory().getItemInMainHand().setItemMeta(meta);
                                                    itemStack.setAmount(itemStack.getAmount() - 10);
                                                } else
                                                    player.sendMessage(ChatColor.RED + "Bitte nehme eine Dia oder Netherite Pickaxe in die Hand!");
                                                break;
                                            }
                                        }
                                    }

                                }
                            } else
                                player.sendMessage(ChatColor.RED + "Du hast keine Dias in deinem Inventar!");
                            break;
                        }
                    default:
                        Main.getMainLogger().severe("How did we end up here? Please contact the dev or the admin");
                }
            }
        }
        return false;
    }
}
