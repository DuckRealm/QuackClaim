package eu.duckrealm.quackclaim.util;

import org.bukkit.RegionAccessor;
import org.bukkit.World;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.bukkit.Bukkit.getServer;

public class RenderTask {
    public static void RenderWorld (World world) {

    }

    public static List<Region> getAllRegions(World world){
        List<Region> regions = new ArrayList<>();
        Path directory = switch (world.getEnvironment()) {
            case NETHER -> Path.of(world.getWorldFolder().getAbsolutePath(), "DIM-1", "region");
            case THE_END -> Path.of(world.getWorldFolder().getAbsolutePath(), "DIM1", "region");
            default -> Path.of(world.getWorldFolder().getAbsolutePath(), "region");
        };
        File[] files = directory.toFile().listFiles((dir, name) -> name.endsWith(".mca"));
        if (files == null) {
            files = new File[0];
        }
        for (File file : files) {
            if (file.length() == 0) continue;
            try {
                String[] split = file.getName().split("\\.");
                int x = Integer.parseInt(split[1]);
                int z = Integer.parseInt(split[2]);

                Region region = new Region(x, z);

                regions.add(region);

            } catch (NumberFormatException ignore) {
            }
        }
        return regions;
    }
}
