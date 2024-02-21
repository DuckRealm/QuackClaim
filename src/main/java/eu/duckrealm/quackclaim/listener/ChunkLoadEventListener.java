package eu.duckrealm.quackclaim.listener;

import eu.duckrealm.quackclaim.QuackClaim;
import eu.duckrealm.quackclaim.map.QRendererStats;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;


public class ChunkLoadEventListener implements Listener {

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onChunkLoad(ChunkLoadEvent chunkLoadEvent) {

        if (QRendererStats.isLoadExcepted(chunkLoadEvent.getChunk().getChunkKey())) {
           QRendererStats.removeLoadException(chunkLoadEvent.getChunk().getChunkKey());
           return;
        }

        QuackClaim.chunkRenderer.addChunk(chunkLoadEvent.getChunk());
    }
}
