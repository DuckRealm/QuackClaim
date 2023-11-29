package eu.duckrealm.quackclaim.listener;

import eu.duckrealm.quackclaim.QuackClaim;
import eu.duckrealm.quackclaim.manager.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Chunk;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;

public class PlayerBucketEventListener implements Listener {
    @EventHandler(ignoreCancelled = true)
    public void onPlayerBucketEmpty(PlayerBucketEmptyEvent playerBucketEmptyEvent) {
        Player player = playerBucketEmptyEvent.getPlayer();
        Block block = playerBucketEmptyEvent.getBlock().getRelative(playerBucketEmptyEvent.getBlockFace(), 1);
        Chunk chunk = block.getChunk();

        if(QuackClaim.ignoringClaims.contains(player.getUniqueId())) return;

        boolean permitted = ClaimManager.isPermitted(player, chunk);
        if(permitted) return;

        player.sendActionBar(Component.text("You are not allowed to do that!", NamedTextColor.RED));
        playerBucketEmptyEvent.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerBucketFill(PlayerBucketFillEvent playerBucketFillEvent) {
        Player player = playerBucketFillEvent.getPlayer();
        Block block = playerBucketFillEvent.getBlock().getRelative(playerBucketFillEvent.getBlockFace(), 1);
        Chunk chunk = block.getChunk();

        if(QuackClaim.ignoringClaims.contains(player.getUniqueId())) return;

        boolean permitted = ClaimManager.isPermitted(player, chunk);
        if(permitted) return;
        player.sendActionBar(Component.text("You are not allowed to do that!", NamedTextColor.RED));
        playerBucketFillEvent.setCancelled(true);
    }
}
