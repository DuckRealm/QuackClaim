package eu.duckrealm.quackclaim.listener;

import eu.duckrealm.quackclaim.QuackClaim;
import eu.duckrealm.quackclaim.util.Team;
import eu.duckrealm.quackclaim.util.Teams;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.UUID;

public class MovementEventListener implements Listener {
    @EventHandler(ignoreCancelled = true)
    public void movementEvent (PlayerMoveEvent playerMoveEvent) {
        if(playerMoveEvent.getPlayer().getGameMode().equals(GameMode.SPECTATOR)) return;

        long chunkKey = playerMoveEvent.getTo().getChunk().getChunkKey();
        if(!QuackClaim.claims.containsKey(chunkKey)) return;

        boolean sameChunk = playerMoveEvent.getFrom().getChunk().equals(playerMoveEvent.getTo().getChunk());

        Player player = playerMoveEvent.getPlayer();
        if(QuackClaim.ignoringClaims.contains(player.getUniqueId())) return;

        UUID playerUUID = playerMoveEvent.getPlayer().getUniqueId();
        Team team = Teams.getTeam(QuackClaim.claims.get(chunkKey));

        if(team == null) return;

        if(team.isTeamBanned(playerUUID)) {
            if(sameChunk) {

                Location spawn = player.getBedSpawnLocation();

                if(spawn == null) spawn = player.getWorld().getSpawnLocation();

                if(!QuackClaim.claims.containsKey(spawn.getChunk().getChunkKey())) {
                    player.teleport(spawn);
                    player.sendActionBar(Component.text("You are banned from this team!", NamedTextColor.RED));
                    return;
                }

                if(Teams.getTeam(QuackClaim.claims.get(spawn.getChunk().getChunkKey())).isTeamBanned(playerUUID)) {
                    spawn = player.getWorld().getSpawnLocation();
                    player.setBedSpawnLocation(spawn);
                }

                player.teleport(spawn);
                player.sendActionBar(Component.text("You are banned from this team!", NamedTextColor.RED));
                return;
            }

            player.teleport(playerMoveEvent.getFrom());
            player.sendActionBar(Component.text("You are banned from this team!", NamedTextColor.RED));

        }
    }
}
