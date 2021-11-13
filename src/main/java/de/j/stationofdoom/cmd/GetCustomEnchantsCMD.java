package de.j.stationofdoom.cmd;

import de.j.stationofdoom.enchants.CustomEnchants;
import de.j.stationofdoom.enchants.CustomEnchantsEnum;
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
                        player.sendMessage(ChatColor.RED + "Möchtest du " + ChatColor.AQUA + "Telepathy" + ChatColor.RED +  " für " + ChatColor.BOLD + CustomEnchantsEnum.TELEPATHY.getPrice() + " " + ChatColor.RESET + ChatColor.RED + "Dias kaufen");
                        TextComponent t = new TextComponent("Dann klicke diese Nachricht!");
                        t.setColor(net.md_5.bungee.api.ChatColor.RED);
                        t.setClickEvent(new ClickEvent(
                                ClickEvent.Action.RUN_COMMAND, "/customenchant telepathy confirm"
                        ));
                        player.spigot().sendMessage(t);
                        break;
                    case "flight":
                        player.sendMessage(ChatColor.RED + "Möchtest du " + ChatColor.AQUA + "Flight" + ChatColor.RED +  " für " + ChatColor.BOLD + CustomEnchantsEnum.FLIGHT.getPrice() + " " + ChatColor.RESET + ChatColor.RED + "Dias kaufen");
                        TextComponent f = new TextComponent("Dann klicke diese Nachricht!");
                        f.setColor(net.md_5.bungee.api.ChatColor.RED);
                        f.setClickEvent(new ClickEvent(
                                ClickEvent.Action.RUN_COMMAND, "/customenchant flight confirm"
                        ));
                        player.spigot().sendMessage(f);
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
                                                if (item.getType() == Material.DIAMOND_PICKAXE || item.getType() == Material.NETHERITE_PICKAXE || item.getType() == Material.DIAMOND_AXE || item.getType() == Material.NETHERITE_AXE || item.getType() == Material.DIAMOND_SHOVEL || item.getType() == Material.NETHERITE_SHOVEL) {
                                                    item.addUnsafeEnchantment(CustomEnchants.TELEPATHY, 1);

                                                    ItemMeta meta = item.getItemMeta();
                                                    List<String> lore = new ArrayList<>();
                                                    lore.add(CustomEnchantsEnum.TELEPATHY.getLoreName());
                                                    assert meta != null;
                                                    meta.setLore(lore);
                                                    player.getInventory().getItemInMainHand().setItemMeta(meta);
                                                    itemStack.setAmount(itemStack.getAmount() - CustomEnchantsEnum.TELEPATHY.getPrice());
                                                } else
                                                    player.sendMessage(ChatColor.RED + "Bitte nehme ein Dia oder Netherite Tool in die Hand!");
                                                break;
                                            }
                                        }
                                    }

                                }
                            } else
                                player.sendMessage(ChatColor.RED + "Du hast keine Dias in deinem Inventar!");
                            break;
                        }

                    case "flight":
                        if (args[1].equalsIgnoreCase("confirm")) {
                            Inventory inventory = player.getInventory();
                            if (inventory.contains(Material.DIAMOND)) {
                                for (ItemStack itemStack : inventory.getContents()) {
                                    if (itemStack != null) {
                                        if (itemStack.getType() == Material.DIAMOND) {
                                            if (itemStack.getAmount() >= 10) {
                                                ItemStack item = player.getInventory().getItemInMainHand();
                                                if (item.getType() == Material.DIAMOND_SWORD || item.getType() == Material.NETHERITE_SWORD) {
                                                    item.addUnsafeEnchantment(CustomEnchants.FLIGHT, 1);

                                                    ItemMeta meta = item.getItemMeta();
                                                    List<String> lore = new ArrayList<>();
                                                    lore.add(CustomEnchantsEnum.FLIGHT.getLoreName());
                                                    assert meta != null;
                                                    meta.setLore(lore);
                                                    player.getInventory().getItemInMainHand().setItemMeta(meta);
                                                    itemStack.setAmount(itemStack.getAmount() - CustomEnchantsEnum.FLIGHT.getPrice());
                                                } else
                                                    player.sendMessage(ChatColor.RED + "Bitte nehme eine Dia oder Netherite Schwert in die Hand!");
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
