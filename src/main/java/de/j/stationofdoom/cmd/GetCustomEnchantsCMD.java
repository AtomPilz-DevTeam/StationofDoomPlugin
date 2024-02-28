package de.j.stationofdoom.cmd;

import de.j.stationofdoom.enchants.CustomEnchants;
import de.j.stationofdoom.enchants.CustomEnchantsEnum;
import de.j.stationofdoom.main.Main;
import de.j.stationofdoom.util.translations.TranslationFactory;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class GetCustomEnchantsCMD implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (sender instanceof Player player) {
            TranslationFactory translations = new TranslationFactory();
            if (args.length == 1) {
                MiniMessage mm = MiniMessage.miniMessage();
                switch (args[0].toLowerCase()) {
                    case "telepathy" -> {
                        player.sendMessage(mm.deserialize(translations.getTranslation(player, "BuyCustomEnchant", "<red>", "<aqua>", "Telepathy", "<red>", "<bold>", CustomEnchantsEnum.TELEPATHY.getPrice(), "<reset>", "<red>")));
                        Component f = Component
                                .text("Dann klicke diese Nachricht!")
                                .color(NamedTextColor.RED)
                                .clickEvent(ClickEvent.runCommand("/customenchant telepathy confirm"));
                        player.sendMessage(f);
                    }
                    case "flight" -> {
                        player.sendMessage(mm.deserialize(translations.getTranslation(player, "BuyCustomEnchant", "<red>", "<aqua>", "Flight", "<red>", "<bold>", CustomEnchantsEnum.FLIGHT.getPrice(), "<reset>", "<red>")));
                        Component f = Component
                                .text("Dann klicke diese Nachricht!")
                                .color(NamedTextColor.RED)
                                .clickEvent(ClickEvent.runCommand("/customenchant flight confirm"));
                        player.sendMessage(f);
                    }
                    case "furnace" -> {
                        player.sendMessage(mm.deserialize(translations.getTranslation(player, "BuyCustomEnchant", "<red>", "<aqua>", "Furnace", "<red>", "<bold>", CustomEnchantsEnum.FURNACE.getPrice(), "<reset>", "<red>")));
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

                                                    CustomEnchants.enchant(item, CustomEnchantsEnum.TELEPATHY);

                                                    itemStack.setAmount(itemStack.getAmount() - CustomEnchantsEnum.TELEPATHY.getPrice());
                                                } else
                                                    player.sendMessage(Component.text(translations.getTranslation(player, "TakeDiaOrNetheriteToolInHand"))
                                                            .color(NamedTextColor.RED));
                                                break;
                                            }
                                        }
                                    }

                                }
                            } else
                                player.sendMessage(Component.text(translations.getTranslation(player, "NoDia")));
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
                                                    CustomEnchants.enchant(item, CustomEnchantsEnum.FLIGHT);
                                                    itemStack.setAmount(itemStack.getAmount() - CustomEnchantsEnum.FLIGHT.getPrice());
                                                } else
                                                    player.sendMessage(Component.text(translations.getTranslation(player, "TakeDiaOrNetheriteSwordInHand"))
                                                            .color(NamedTextColor.RED));
                                                break;
                                            }
                                        }
                                    }

                                }
                            } else
                                player.sendMessage(Component.text(translations.getTranslation(player, "NoDia")));
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
                                                    CustomEnchants.enchant(item, CustomEnchantsEnum.FURNACE);
                                                    itemStack.setAmount(itemStack.getAmount() - CustomEnchantsEnum.FURNACE.getPrice());
                                                } else
                                                    player.sendMessage(Component.text(translations.getTranslation(player, "TakeDiaOrNetheritePickaxeInHand"))
                                                            .color(NamedTextColor.RED));
                                                break;
                                            }
                                        }
                                    }

                                }
                            } else
                                player.sendMessage(Component.text(translations.getTranslation(player, "NoDia")));
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
