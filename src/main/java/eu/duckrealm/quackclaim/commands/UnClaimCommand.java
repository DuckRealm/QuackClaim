package eu.duckrealm.quackclaim.commands;

import eu.duckrealm.quackclaim.QuackClaim;
import eu.duckrealm.quackclaim.util.Team;
import eu.duckrealm.quackclaim.util.Teams;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class UnClaimCommand implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Player player = (Player) commandSender;
        if(!Teams.isPlayerInTeam(player.getUniqueId())) {
            player.sendMessage(Component.text("You aren't in a team yet", NamedTextColor.RED));
            return true;
        }

        Team team = Teams.getTeamByPlayer(player.getUniqueId());
        Long chunkKey = player.getChunk().getChunkKey();

        if(!player.getUniqueId().equals(team.getOwner())) {
            player.sendMessage(Component.text("You can not unclaim ", NamedTextColor.GRAY)
                    .append(team.getTeamComponent())
                    .append(Component.text(" as a member!", NamedTextColor.GRAY)));
            return true;
        }

        if(!QuackClaim.claims.containsKey(chunkKey)) {
            player.sendMessage(Component.text("Chunk not claimed", NamedTextColor.RED));
            return true;
        }

        Team claimedByTeam = Teams.getTeam(QuackClaim.claims.get(player.getChunk().getChunkKey()));
        if(!claimedByTeam.getTeamID().equals(team.getTeamID())) {
            player.sendMessage(Component.text("Chunk not claimed by you", NamedTextColor.RED));
            return true;
        }

        team.subtractClaimedChunk();
        QuackClaim.claims.remove(chunkKey);
        player.sendMessage(Component.text("This is from no longer property of ", NamedTextColor.GRAY).append(team.getTeamComponent()));

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return null;
    }
}
