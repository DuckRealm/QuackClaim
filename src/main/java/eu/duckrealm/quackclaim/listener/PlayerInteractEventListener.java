package eu.duckrealm.quackclaim.listener;

import eu.duckrealm.quackclaim.QuackClaim;
import eu.duckrealm.quackclaim.util.Team;
import eu.duckrealm.quackclaim.util.Teams;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.spigotmc.event.entity.EntityMountEvent;

import static java.util.Objects.isNull;
public class PlayerInteractEventListener implements Listener {

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onInteract(PlayerInteractEvent event) {
        if(event.getAction().equals(Action.RIGHT_CLICK_AIR)) return;
        if(event.getAction().equals(Action.LEFT_CLICK_AIR)) return;

        Block block = event.getClickedBlock();
        if(block == null) return;

        Player player = event.getPlayer();

        if(QuackClaim.ignoringClaims.contains(player.getUniqueId())) return;

        Team playerTeam = Teams.getTeamByPlayer(player.getUniqueId());
        Team claimTeam = Teams.getTeamByChunk(block.getChunk());

        if(isBlockAllowed(block) && !event.getAction().equals(Action.LEFT_CLICK_BLOCK)) return;
        if(isNull(claimTeam)) return;
        if(isNull(playerTeam)) playerTeam = QuackClaim.SERVERTEAM;

        if(!playerTeam.equals(claimTeam)) {
            player.sendActionBar(Component.text("You are not allowed to do that!", NamedTextColor.RED));
            event.setCancelled(true);
        }
    }

    private boolean isBlockAllowed(Block block) {
        return switch(block.getType()) {
            case CRAFTING_TABLE, LOOM, ENDER_CHEST, SMITHING_TABLE, ENCHANTING_TABLE, STONECUTTER, FLETCHING_TABLE, CARTOGRAPHY_TABLE, GRINDSTONE -> true;
            default -> false;
        };
    }
}
