package eu.duckrealm.quackclaim.listener;

import eu.duckrealm.quackclaim.manager.ClaimManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Enemy;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.projectiles.ProjectileSource;

import java.util.UUID;

public class ProjectileListener implements Listener {
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onProjectileHit(ProjectileHitEvent event) {
        UUID owner = event.getEntity().getOwnerUniqueId();

        Player ownerPlayer = Bukkit.getPlayer(owner);
        if(ownerPlayer == null || !ownerPlayer.isOnline()) return;

        if(event.getHitEntity() instanceof Enemy) return;
        boolean permitted = ClaimManager.isPermitted(ownerPlayer, event.getEntity().getChunk());
        boolean damagedIsPlayer = event.getHitEntity().getType() == EntityType.PLAYER;
        if(permitted && !damagedIsPlayer) return;

        if(ClaimManager.isPVPOn(event.getHitEntity().getChunk())) return;

        ownerPlayer.sendActionBar(Component.text("You are not allowed to do that!", NamedTextColor.RED));
        event.setCancelled(true);
    }
}
