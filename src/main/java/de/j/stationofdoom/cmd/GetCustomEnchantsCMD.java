package de.j.stationofdoom.cmd;

import de.j.stationofdoom.enchants.CustomEnchants;
import de.j.stationofdoom.enchants.CustomEnchantsEnum;
import de.j.stationofdoom.main.Main;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
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
        if (sender instanceof Player player) {
            if (args.length == 1) {
                switch (args[0].toLowerCase()) {
                    case "telepathy" -> {
                        player.sendMessage(ChatColor.RED + "Möchtest du " + ChatColor.AQUA + "Telepathy" + ChatColor.RED + " für " + ChatColor.BOLD + CustomEnchantsEnum.TELEPATHY.getPrice() + " " + ChatColor.RESET + ChatColor.RED + "Dias kaufen");
                        Component f = Component
                                .text("Dann klicke diese Nachricht!")
                                .color(NamedTextColor.RED)
                                .clickEvent(ClickEvent.runCommand("/customenchant telepathy confirm"));
                        player.sendMessage(f);
                    }
                    case "flight" -> {
                        player.sendMessage(ChatColor.RED + "Möchtest du " + ChatColor.AQUA + "Flight" + ChatColor.RED + " für " + ChatColor.BOLD + CustomEnchantsEnum.FLIGHT.getPrice() + " " + ChatColor.RESET + ChatColor.RED + "Dias kaufen");
                        Component f = Component
                                .text("Dann klicke diese Nachricht!")
                                .color(NamedTextColor.RED)
                                .clickEvent(ClickEvent.runCommand("/customenchant flight confirm"));
                        player.sendMessage(f);
                    }
                    case "furnace" -> {
                        player.sendMessage(ChatColor.RED + "Möchtest du " + ChatColor.AQUA + "Furnace" + ChatColor.RED + " für " + ChatColor.BOLD + CustomEnchantsEnum.FURNACE.getPrice() + " " + ChatColor.RESET + ChatColor.RED + "Dias kaufen");
                        Component f = Component
                                .text("Dann klicke diese Nachricht!")
                                .color(NamedTextColor.RED)
                                .clickEvent(ClickEvent.runCommand("/customenchant furnace confirm"));
                        player.sendMessage(f);
                    }
                    default ->
                            Main.getMainLogger().severe("How did we end up here? Please contact the dev or the admin");
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

                        case "furnace":
                        if (args[1].equalsIgnoreCase("confirm")) {
                            Inventory inventory = player.getInventory();
                            if (inventory.contains(Material.DIAMOND)) {
                                for (ItemStack itemStack : inventory.getContents()) {
                                    if (itemStack != null) {
                                        if (itemStack.getType() == Material.DIAMOND) {
                                            if (itemStack.getAmount() >= CustomEnchantsEnum.FURNACE.getPrice()) {
                                                ItemStack item = player.getInventory().getItemInMainHand();
                                                if (item.getType() == Material.DIAMOND_PICKAXE || item.getType() == Material.NETHERITE_PICKAXE) {
                                                    item.addUnsafeEnchantment(CustomEnchants.FURNACE, 1);

                                                    ItemMeta meta = item.getItemMeta();
                                                    List<String> lore = new ArrayList<>();
                                                    lore.add(CustomEnchantsEnum.FURNACE.getLoreName());
                                                    assert meta != null;
                                                    meta.setLore(lore);
                                                    player.getInventory().getItemInMainHand().setItemMeta(meta);
                                                    itemStack.setAmount(itemStack.getAmount() - CustomEnchantsEnum.FURNACE.getPrice());
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
