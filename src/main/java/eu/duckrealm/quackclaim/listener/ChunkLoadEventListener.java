package eu.duckrealm.quackclaim.listener;

import eu.duckrealm.quackclaim.QuackClaim;
import eu.duckrealm.quackclaim.map.MapTile;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;


public class ChunkLoadEventListener implements Listener {

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onChunkLoad(ChunkLoadEvent chunkLoadEvent) {
        QuackClaim.chunkRenderer.addChunk(chunkLoadEvent.getChunk());
        MapTile[] mapTiles = QuackClaim.chunkRenderer.render();
        for(MapTile tile : mapTiles) {
            if(tile == null) continue;
            tile.save();
        }
    }
}
