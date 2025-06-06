package de.j.stationofdoom.teams;

import de.j.stationofdoom.util.translations.TranslationFactory;
import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;


public class TeamCMD implements BasicCommand {
    @Override
    public void execute(CommandSourceStack stack, String[] args) {
        if (!(stack.getSender() instanceof Player player)) {
            return;
        }
        Team team = HandleTeams.getTeamFromPlayerUUID(player.getUniqueId());
        if (!team.isMember(player.getUniqueId())) {
            player.sendMessage(Component.text(new TranslationFactory().getTranslation(player, "TeamCMDNoTeam"))
                    .color(NamedTextColor.RED));
            return;
        }
        new TeamSettingsGUI(team).showPage(1, player);
    }
}
