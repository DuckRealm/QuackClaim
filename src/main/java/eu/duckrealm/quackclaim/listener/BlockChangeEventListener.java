package eu.duckrealm.quackclaim.listener;

import eu.duckrealm.quackclaim.manager.*;
import eu.duckrealm.quackclaim.util.Team;
import eu.duckrealm.quackclaim.util.Teams;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;

import java.util.*;

public class BlockChangeEventListener implements Listener {

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onBlockExplode(EntityExplodeEvent entityExplodeEvent) {
        List<Block> blocksToRemove = new ArrayList<>();
        entityExplodeEvent.blockList().forEach(block -> {
            boolean permitted = ClaimManager.isExplosionPermitted(block.getLocation().getChunk());
            if (!permitted) blocksToRemove.add(block);
        });

        entityExplodeEvent.blockList().removeAll(blocksToRemove);
    }

    private Team lastSpreadFrom = null;
    private Team lastSpreadTo = null;

    @EventHandler (ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onLiquidFlow(BlockFromToEvent event) {
        if (event.getFace() == BlockFace.DOWN) return;

        Location fromLocation = event.getBlock().getLocation();
        Location toLocation = event.getToBlock().getLocation();
        Team fromTeam = Teams.getTeamByChunk(fromLocation.getChunk());
        Team toTeam = Teams.getTeamByChunk(toLocation.getChunk());

        this.lastSpreadFrom = fromTeam;
        this.lastSpreadTo = toTeam;

        if (!ClaimManager.isBlockTransferLegal(fromTeam, toTeam))
        {
            event.setCancelled(true);
        }
    }
}
