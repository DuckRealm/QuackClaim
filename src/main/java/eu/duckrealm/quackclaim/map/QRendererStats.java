package eu.duckrealm.quackclaim.map;

import java.util.ArrayList;
import java.util.List;

public class QRendererStats {
    public static long AverageTime = 0;
    public static long AverageWeight = 0;
    public static long MaxTime = 0;
    public static long MinTime = Long.MAX_VALUE;
    public static long RenderedChunks = 0;

    private static final List<Long> doNotRender = new ArrayList<>();

    public static void addLoadException(long chunkKey) {
        doNotRender.add(chunkKey);
    }

    public static void removeLoadException(long chunkKey) {
        doNotRender.remove(chunkKey);
    }

    public static boolean isLoadExcepted(long chunkKey) {
        return doNotRender.contains(chunkKey);
    }
}
