package eu.duckrealm.quackclaim.util;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import java.util.HashMap;

import static javax.swing.UIManager.put;

public class ChunkLoadAnalyzer {

    private static final HashMap<Material, Integer> blockEntityScore = new HashMap<>(){
        {
            put(Material.CHEST, 1);
            put(Material.TRAPPED_CHEST, 2);
            put(Material.BLAST_FURNACE, 3);
            put(Material.FURNACE, 3);
            put(Material.BEACON, 35);
            put(Material.HOPPER, 6);
            put(Material.PISTON, 5);
            put(Material.DISPENSER, 10);
            put(Material.SPAWNER, 60);
        }
    };

    private static final HashMap<EntityType, Integer> livingEntityScore = new HashMap<>() {
        {
            put(EntityType.PLAYER, 15);
            put(EntityType.ZOMBIE, 2);
            put(EntityType.SPIDER, 2);
            put(EntityType.SKELETON, 2);
            put(EntityType.CREEPER, 3);
            put(EntityType.COW, 1);
            put(EntityType.PIG, 1);
            put(EntityType.SHEEP, 1);
            put(EntityType.ENDER_PEARL, 15);
            put(EntityType.EGG, 3);
            put(EntityType.FISHING_HOOK, 2);
            put(EntityType.SNOWBALL, 2);
            put(EntityType.ARROW, 1);
            put(EntityType.PAINTING, 1);
            put(EntityType.ITEM_FRAME, 3);
            put(EntityType.ARMOR_STAND, 4);
            put(EntityType.DROPPED_ITEM, 2);
            put(EntityType.EXPERIENCE_ORB, 3);
            put(EntityType.FIREWORK, 8);
            put(EntityType.ENDER_DRAGON, 85);
            put(EntityType.ENDER_CRYSTAL, 10);
            put(EntityType.WITHER, 55);
            put(EntityType.WITHER_SKULL, 20);
            put(EntityType.PRIMED_TNT, 7);
            put(EntityType.MINECART_TNT, 15);
            put(EntityType.MINECART_HOPPER, 20);
            put(EntityType.MINECART_MOB_SPAWNER, 80);
            put(EntityType.ENDERMITE, 5);
        }
    };
    public static int getChunkScore(Chunk chunk) {
        int score = 0;
        BlockState[] tileEntities = chunk.getTileEntities();
        for(BlockState tileEntity : tileEntities) {
            score += blockEntityScore.getOrDefault(tileEntity.getType(), 0);
        }
        Entity[] entites = chunk.getEntities();
        for(Entity entity : entites) {
            score += livingEntityScore.getOrDefault(entity.getType(), 0);
        }
        return score;
    }
}
