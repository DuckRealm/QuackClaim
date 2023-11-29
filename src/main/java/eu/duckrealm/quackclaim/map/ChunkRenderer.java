package eu.duckrealm.quackclaim.map;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;

import eu.duckrealm.quackclaim.QuackClaim;
import eu.duckrealm.quackclaim.util.ChunkLoadAnalyzer;
import eu.duckrealm.quackclaim.util.Team;
import eu.duckrealm.quackclaim.util.Teams;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.joml.Vector2d;

import static java.util.Objects.isNull;

public class ChunkRenderer {

    private List<Chunk> chunks;
    private Map<Long, Integer> chunkHashes = new HashMap<>();
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
    double lastFactor = 0.0;
    private MapTile renderChunk(Chunk chunk) {

        long ucid = chunk.getChunkKey() + chunk.getWorld().getName().hashCode();
        if(chunkHashes.containsKey(ucid)) {
            if(chunkHashes.get(ucid) == chunk.hashCode()) return null;
        }

        BufferedImage image = new BufferedImage(16, 16, BufferedImage.TYPE_4BYTE_ABGR);
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                Location location = new Location(chunk.getWorld(), x + chunk.getX() * 16, 0d, z + chunk.getZ() * 16);
                Block highestBlock = location.getWorld().getHighestBlockAt((int) location.x(), (int) location.z());

                double factor = (double) (highestBlock.getY() + Math.abs(chunk.getWorld().getMinHeight())) / (chunk.getWorld().getMaxHeight() + Math.abs(chunk.getWorld().getMinHeight()));
                factor *= 255;

                if(factor != lastFactor) {
                    Bukkit.broadcast(Component.text(factor));
                    lastFactor = factor;
                }

                int alpha = (int) Math.round(factor) << 24;

                int blockColor = alpha | getBlockColor(highestBlock.getType());

                image.setRGB(x, z, blockColor);
            }
        }

        int score = ChunkLoadAnalyzer.getChunkScore(chunk);

        chunkHashes.put(ucid, chunk.hashCode());
        Vector2d chunkPos = new Vector2d(chunk.getX(), chunk.getZ());
        Team team = Teams.getTeamByChunk(chunk);
        if(team == null) team = QuackClaim.SERVERTEAM;
        QuackClaim.tileInfo.put(ucid, new TileInfo(chunk.getInhabitedTime(), team.getTeamID(), score, chunkPos, chunk.getWorld().getName()));
        return new MapTile(image, score, chunk.getInhabitedTime(), chunkPos, chunk.getWorld().getName());
    }

    private int getBlockColor(Material material) {
        return ColorConfig.BLOCK_COLORS.getOrDefault(material.getKey().asString(), 0x000000);
    }
}
