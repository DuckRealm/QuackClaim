package eu.duckrealm.quackclaim.listener;

import eu.duckrealm.quackclaim.manager.*;
import org.bukkit.Chunk;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;

public class PistonMoveEventListener implements Listener {
    @EventHandler (ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPistonExtend(BlockPistonExtendEvent event) {
        Chunk destinationChunk = event.getBlock().getRelative(event.getDirection(), event.getBlocks().size() + 1).getChunk();
        Chunk currentChunk = event.getBlock().getChunk();
        boolean permitted = ClaimManager.isExtendPermitted(currentChunk, destinationChunk);
        if (permitted) return;
        event.setCancelled(true);
    }

    @EventHandler (ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPistonRetract(BlockPistonRetractEvent event) {
        Chunk destinationChunk = event.getBlock().getRelative(event.getDirection(), event.getBlocks().size() + 1).getChunk();
        Chunk currentChunk = event.getBlock().getChunk();
        boolean permitted = ClaimManager.isExtendPermitted(destinationChunk, currentChunk);
        if (permitted) return;
        event.setCancelled(true);
    }

}
