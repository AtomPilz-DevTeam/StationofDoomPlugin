package de.j.deathMinigames.database;

import de.chojo.sadu.queries.api.call.Call;
import de.chojo.sadu.queries.api.query.Query;
import de.chojo.sadu.queries.call.adapter.UUIDAdapter;
import de.j.stationofdoom.main.Main;
import de.j.stationofdoom.teams.Team;
import de.j.stationofdoom.teams.TeamsMainMenuGUI;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.UUID;

public class TeamEnderchestsDatabase {
    private static volatile TeamEnderchestsDatabase instance;

    public TeamEnderchestsDatabase() {}

    public static TeamEnderchestsDatabase getInstance() {
        if (instance == null) {
            synchronized (TeamEnderchestsDatabase.class) {
                if (instance == null) {
                    instance = new TeamEnderchestsDatabase();
                }
            }
        }
        return instance;
    }

    public void createTable() {
        if(!Database.getInstance().isConnected) return;
        Query.query("CREATE TABLE IF NOT EXISTS teamEnderchests (uuidOfTeam VARCHAR(255), name VARCHAR(255), amount INTEGER, material VARCHAR(255));")
                .single()
                .insert();
    }

    public void removeTeamEnderchest(UUID uuidOfTeam) {
        if(!Database.getInstance().isConnected) return;
        Query.query("DELETE FROM teamEnderchests WHERE uuidOfTeam = ?;")
                .single(Call.of()
                        .bind(uuidOfTeam, UUIDAdapter.AS_STRING))
                .delete();
        Main.getMainLogger().info("Removed Enderchest of team " + TeamsMainMenuGUI.getTeam(uuidOfTeam).getName() + " from database");
    }

    public Inventory getTeamEnderchest(UUID uuidOfTeam) {
        Inventory inv = Bukkit.createInventory(null, 27, "Team Enderchest");
        if(!Database.getInstance().isConnected) return inv;
        Query.query("SELECT * FROM teamEnderchests WHERE uuidOfTeam = ?;")
                .single(Call.of()
                        .bind(uuidOfTeam, UUIDAdapter.AS_STRING))
                .map(row -> {
                    ItemStack itemStack = new ItemStack(Material.valueOf(row.getString("material")), row.getInt("amount"));
                    ItemMeta itemMeta = itemStack.getItemMeta();
                    if(row.getString("name") != null && !row.getString("name").isEmpty()) itemMeta.displayName(Component.text(row.getString("name")));
                    itemStack.setItemMeta(itemMeta);
                    inv.addItem(itemStack);
                    return itemStack;
                })
                .all();
        Main.getMainLogger().info("Got Enderchest of team " + uuidOfTeam + " from DB");
        return inv;
    }

    public void updateTeamEnderchestsOfAllTeams() {
        if(!Database.getInstance().isConnected) return;
        for (Team team : TeamsMainMenuGUI.teams) {
            UUID uuidOfTeam = team.getUuid();
            Query.query("DELETE FROM teamEnderchests WHERE uuidOfTeam = ?;")
                    .single(Call.of()
                            .bind(uuidOfTeam, UUIDAdapter.AS_STRING))
                    .delete();
            for (ItemStack itemStack : team.inventory.getContents()) {
                if(itemStack == null) continue;
                String enchants = itemStack.getItemMeta().getEnchants().toString();
                Main.getMainLogger().warning(enchants);
                Query.query("INSERT INTO teamEnderchests (uuidOfTeam, name, amount, material) VALUES (?, ?, ?, ?);")
                        .single(Call.of()
                                .bind(uuidOfTeam, UUIDAdapter.AS_STRING)
                                .bind(itemStack.getItemMeta().displayName() != null ? itemStack.getItemMeta().getDisplayName() : null)
                                .bind(itemStack.getAmount())
                                .bind(itemStack.getType().toString()))
                        .insert();
                Main.getMainLogger().info("Added Item " + itemStack.getItemMeta().getDisplayName() + " of team " + team.getName() + " to database");
            }
        }
    }
}
