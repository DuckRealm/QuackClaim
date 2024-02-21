package eu.duckrealm.quackclaim.commands;

import java.util.List;

import eu.duckrealm.quackclaim.util.Team;
import eu.duckrealm.quackclaim.util.Teams;
import eu.duckrealm.quackclaim.QuackClaim;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.command.TabCompleter;
import org.bukkit.command.CommandSender;
import org.bukkit.command.CommandExecutor;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public class ClaimCommand implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Player player = (Player) commandSender;
        if(!Teams.isPlayerInTeam(player.getUniqueId())) {
            player.sendMessage(Component.text("You aren't in a team yet", NamedTextColor.RED));
            return true;
        }

        if(player.getLocation().distance(player.getWorld().getSpawnLocation()) < 100) {
            player.sendMessage(Component.text("Too close to the world spawn!", NamedTextColor.RED));
            return true;
        }

        int chunks = 1;
        if(args.length > 0) {
            chunks = Integer.parseInt(args[0]);
        }

        Team team = Teams.getTeamByPlayer(player.getUniqueId());

        if(!player.getUniqueId().equals(team.getOwner())) {
            player.sendMessage(Component.text("You can not claim ", NamedTextColor.GRAY)
                    .append(team.getTeamComponent())
                    .append(Component.text(" as a member!", NamedTextColor.GRAY)));
            return true;
        }

        if(chunks == 1) {
            Long chunkKey = player.getChunk().getChunkKey();

            if (QuackClaim.claims.containsKey(chunkKey)) {
                Team claimedByTeam = Teams.getTeam(QuackClaim.claims.get(player.getChunk().getChunkKey()));
                player.sendMessage(Component.text("Already claimed by ", NamedTextColor.RED).append(claimedByTeam.getTeamComponent()));
                return true;
            }

            if (team.getMaxClaimChunks() <= team.getClaimedChunks() + 1) {
                player.sendMessage(Component.text("Too little chunks", NamedTextColor.RED));
                return true;
            }

            team.addClaimedChunk();
            QuackClaim.claims.put(chunkKey, team.getTeamID());
        } else {
            if (team.getMaxClaimChunks() <= team.getClaimedChunks() + Math.pow(2 * chunks, 2)) {
                player.sendMessage(Component.text("Too little chunks!", NamedTextColor.RED)
                        .append(Component.newline())
                        .append(Component.text("You need ", NamedTextColor.RED))
                        .append(Component.text(Math.pow(2 * chunks, 2) - (team.getMaxClaimChunks() - team.getClaimedChunks()), NamedTextColor.GREEN))
                        .append(Component.text(" more", NamedTextColor.RED)));
                return true;
            }

            for (int i = -chunks; i <= chunks; i++) {
                for (int j = -chunks; j <= chunks; j++) {
                    Location location = player.getLocation();
                    location.add(i * 16, 0, j * 16);
                    long chunkKey = location.getChunk().getChunkKey();

                    if (QuackClaim.claims.containsKey(chunkKey) && !QuackClaim.claims.get(chunkKey).equals(team.getTeamID())) {
                        Team claimedByTeam = Teams.getTeam(QuackClaim.claims.get(player.getChunk().getChunkKey()));
                        player.sendMessage(Component.text(location.getChunk().getX())
                                .append(Component.text(" "))
                                .append(Component.text(location.getChunk().getZ()))
                                .append(Component.text(" is already claimed by ", NamedTextColor.RED))
                                .append(claimedByTeam.getTeamComponent()));
                        return true;
                    }

                    team.addClaimedChunk();
                    QuackClaim.claims.put(chunkKey, team.getTeamID());

                }
            }
        }
        player.sendMessage(Component.text("This is from now on property of ", NamedTextColor.GRAY).append(team.getTeamComponent()));

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return null;
    }
}
