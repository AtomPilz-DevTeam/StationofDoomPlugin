package de.j.stationofdoom.teams;

import de.j.deathMinigames.main.HandlePlayers;
import de.j.stationofdoom.util.translations.TranslationFactory;
import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class TeamCMD implements BasicCommand {
    public TeamsMainMenuGUI teamsMainMenuGUI = new TeamsMainMenuGUI();

    @Override
    public boolean canUse(@NotNull CommandSender sender) {
        return sender instanceof Player;
    }

    @Override
    public void execute(CommandSourceStack stack, String[] args) {
        Player player = (Player) stack.getSender();
        Team team = HandleTeams.getTeamFromPlayerUUID(player.getUniqueId());
        if(!team.isMember(player.getUniqueId())) {
            player.sendMessage(Component.text(new TranslationFactory().getTranslation(player, "TeamCMDNoTeam")).color(NamedTextColor.RED));
            return;
        }
        new TeamSettingsGUI(team).showPage(1, player);
    }
}
