package eu.duckrealm.quackclaim.util;

import eu.duckrealm.quackclaim.QuackClaim;
import eu.duckrealm.quackclaim.map.MapTile;
import eu.duckrealm.quackclaim.map.QRendererStats;

import java.util.Date;

public class Renderer {
    public static void RenderQueuedChunks() {
        Date start = new Date();

        MapTile[] mapTiles = QuackClaim.chunkRenderer.render();

        for (MapTile tile : mapTiles) {
            if (tile == null) continue;
            tile.save();
        }

        //incremental average time
        long time = new Date().getTime() - start.getTime();
        long avgtime = QRendererStats.AverageTime;
        QRendererStats.AverageTime = (avgtime * QRendererStats.AverageWeight + time) / (QRendererStats.AverageWeight + mapTiles.length);
        if (time > QRendererStats.MaxTime) QRendererStats.MaxTime = time;
        if (time < QRendererStats.MinTime) QRendererStats.MinTime = time;
        QRendererStats.AverageWeight += mapTiles.length;
        QRendererStats.RenderedChunks += mapTiles.length;
    }

}
