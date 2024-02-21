package eu.duckrealm.quackclaim.map.tasks;

import eu.duckrealm.quackclaim.QuackClaim;
import eu.duckrealm.quackclaim.map.MapTile;
import eu.duckrealm.quackclaim.map.QRendererStats;
import eu.duckrealm.quackclaim.map.tasks.Task;

import java.util.Date;

public class Renderer implements Task {
    @Override
    public void run() {
        Date start = new Date();
        if(QuackClaim.chunkRenderer.queueSize() < 1) return;
        MapTile[] mapTiles = QuackClaim.chunkRenderer.render();

        for (MapTile tile : mapTiles) {
            if (tile == null) continue;
            tile.save();
        }

        //incremental average time
        long time = new Date().getTime() - start.getTime();
        long avgtime = QRendererStats.AverageTime;
        if(QRendererStats.AverageWeight + mapTiles.length < 1) return;
        QRendererStats.AverageTime = (avgtime * QRendererStats.AverageWeight + time) / (QRendererStats.AverageWeight + mapTiles.length);
        if (time > QRendererStats.MaxTime) QRendererStats.MaxTime = time;
        if (time < QRendererStats.MinTime) QRendererStats.MinTime = time;
        QRendererStats.AverageWeight += mapTiles.length;
        QRendererStats.RenderedChunks += mapTiles.length;
    }
}
