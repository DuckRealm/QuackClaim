package eu.duckrealm.quackclaim.map;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;

import eu.duckrealm.quackclaim.QuackClaim;
import eu.duckrealm.quackclaim.util.ChunkLoadAnalyzer;
import eu.duckrealm.quackclaim.util.Team;
import eu.duckrealm.quackclaim.util.Teams;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.joml.Vector2d;

public class ChunkRenderer {

    private List<Chunk> chunks;
    private final Map<Long, Integer> chunkHashes = new HashMap<>();
    public ChunkRenderer() {
        this.chunks = new ArrayList<>();
    }

    public void addChunk(Chunk chunk) {
        this.chunks.add(chunk);
    }

    public Chunk getChunk(int index) {
        return this.chunks.get(index);
    }

    public int queueSize() {
        return this.chunks.size();
    }

    public MapTile[] render() {
        MapTile[] tiles = new MapTile[chunks.size()];
        int i = 0;
        if (chunks.isEmpty()) return tiles;

		for (Chunk chunk : chunks) {
			if (chunk == null) continue;
			MapTile mapTile = renderChunk(chunk);
			tiles[i] = mapTile;
			i++;
		}

        chunks = new ArrayList<>();
        return tiles;
    }
    
    private MapTile renderChunk(Chunk chunk) {

        long ucid = chunk.getChunkKey() + chunk.getWorld().getName().hashCode();
        if(chunkHashes.containsKey(ucid)) {
            if(chunkHashes.get(ucid) == chunk.hashCode()) return null;
        }

        BufferedImage image = new BufferedImage(16, 16, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                Location location = new Location(chunk.getWorld(), x + chunk.getX() * 16, 0d, z + chunk.getZ() * 16);
                Block highestBlock = location.getWorld().getHighestBlockAt((int) location.x(), (int) location.z());
                Block highestBlockTop;
                Block highestBlockBottom;
                Block highestBlockRight;
                Block highestBlockLeft;

                if(x < 15) {
                    highestBlockTop = location.getWorld().getHighestBlockAt((int) location.x() + 1, (int) location.z());
                } else {
                    highestBlockTop = highestBlock;
                }

                if(x > 0) {
                    highestBlockBottom = location.getWorld().getHighestBlockAt((int) location.x() - 1, (int) location.z());
                } else {
                    highestBlockBottom = highestBlock;
                }

                if(z < 15) {
                    highestBlockRight = location.getWorld().getHighestBlockAt((int) location.x(), (int) location.z() + 1);
                } else {
                    highestBlockRight = highestBlock;
                }

                if(z > 0) {
                    highestBlockLeft = location.getWorld().getHighestBlockAt((int) location.x(), (int) location.z() - 1);
                } else {
                    highestBlockLeft = highestBlock;
                }

                int top = highestBlockTop.getY();
                int bottom = highestBlockBottom.getY();
                int left = highestBlockLeft.getY();
                int right = highestBlockRight.getY();
                int current = highestBlock.getY();

                int depth = 0;
                //if (isWater(highestBlock.getType())) {
                //    while (isWater(highestBlock.getType())) {
                //        location.setY(location.getY() - 1);
                //        depth++;
                //    }
                //}
                //location.setY(location.getY() + depth);

                Color blockColor = getBlockColor(highestBlock.getType());

                double topShadeFactor = Math.min(Math.max(0, (double) (current - top) / 10.0), 0.5);
                double bottomShadeFactor = Math.min(Math.max(0, (double) (bottom - current) / 10.0), 0.5);
                double leftShadeFactor = Math.min(Math.max(0, (double) (current - left) / 10.0), 0.5);
                double rightShadeFactor = Math.min(Math.max(0, (double) (right - current) / 10.0), 0.5);

                int shadedRed = (int) (blockColor.getRed() - (topShadeFactor + leftShadeFactor) * 64
                        + (bottomShadeFactor + rightShadeFactor) * 64);
                int shadedGreen = (int) (blockColor.getGreen() - (topShadeFactor + leftShadeFactor) * 64
                        + (bottomShadeFactor + rightShadeFactor) * 64);
                int shadedBlue = (int) (blockColor.getBlue() - (topShadeFactor + leftShadeFactor) * 64
                        + (bottomShadeFactor + rightShadeFactor) * 64);

                shadedRed = Math.min(255, Math.max(0, shadedRed));
                shadedGreen = Math.min(255, Math.max(0, shadedGreen));
                shadedBlue = Math.min(255, Math.max(0, shadedBlue));

                image.setRGB(x, z, new Color(shadedRed, shadedGreen, shadedBlue).getRGB());

            }
        }

        int score = ChunkLoadAnalyzer.getChunkScore(chunk);

        chunkHashes.put(ucid, chunk.hashCode());
        Vector2d chunkPos = new Vector2d(chunk.getX(), chunk.getZ());
        Team team = Teams.getTeamByChunk(chunk);
        if(team == null) team = QuackClaim.SERVERTEAM;
        TileInfo info = new TileInfo(chunk.getInhabitedTime(), team.getTeamID(), score, chunkPos, chunk.getWorld().getName(), ucid);
        info.save();
        QuackClaim.tileInfo.put(ucid, info);
        return new MapTile(image, score, chunk.getInhabitedTime(), chunkPos, chunk.getWorld().getName());
    }
    private boolean isWater(Material material) {
        //flags blocks as water when they are effectively water
        return material.equals(Material.WATER);
        //return material.equals(Material.WATER) || ColorConfig.BLOCKS_WATER.contains(material.getKey().asString());
    }
    private Color getBlockColor(Material material) {
        String block = material.getKey().asString();
        if(ColorConfig.BLOCKS_WATER.contains(block)) block = "minecraft:water";
        return new Color(ColorConfig.BLOCK_COLORS.getOrDefault(block, 0x000000));
    }
}
