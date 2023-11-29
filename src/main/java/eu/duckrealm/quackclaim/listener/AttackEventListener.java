package eu.duckrealm.quackclaim.listener;

import eu.duckrealm.quackclaim.QuackClaim;
import eu.duckrealm.quackclaim.manager.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Enemy;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;

public class AttackEventListener implements Listener {

    @EventHandler (ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void AttackEvent (EntityDamageByEntityEvent entityDamageByEntityEvent) {
        boolean pvp = ClaimManager.isPVPOn(entityDamageByEntityEvent.getEntity().getChunk());
        boolean damagerIsPlayer = entityDamageByEntityEvent.getDamager().getType() == EntityType.PLAYER;

        if(!damagerIsPlayer) return;

        Player player = Bukkit.getPlayer(entityDamageByEntityEvent.getDamager().getUniqueId());
        if(player == null) {
            Bukkit.broadcast(Component.text("Something went wrong! I can feel it", NamedTextColor.RED));
            return;
        }

        if(QuackClaim.ignoringClaims.contains(player.getUniqueId())) return;

        boolean permitted = ClaimManager.isPermitted(player, entityDamageByEntityEvent.getEntity().getChunk());

        boolean damagedIsHostile = entityDamageByEntityEvent.getEntity() instanceof Enemy;
        if(damagedIsHostile) return;

        boolean damagedIsPlayer = entityDamageByEntityEvent.getEntity().getType() == EntityType.PLAYER;
        if(permitted && !damagedIsPlayer) return;
        if(pvp && damagedIsPlayer) return;

        player.sendActionBar(Component.text("You are not allowed to do that!", NamedTextColor.RED));
        entityDamageByEntityEvent.setCancelled(true);
    }

    @EventHandler (ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void projectileAttackEvent(ProjectileHitEvent projectileHitEvent) {
        if(projectileHitEvent.getEntity().getShooter() instanceof Player player) {
            boolean pvp = ClaimManager.isPVPOn(projectileHitEvent.getEntity().getChunk());

            if(QuackClaim.ignoringClaims.contains(player.getUniqueId())) return;

            boolean permitted = ClaimManager.isPermitted(player, projectileHitEvent.getEntity().getChunk());

            boolean damagedIsHostile = projectileHitEvent.getEntity() instanceof Enemy;
            if(damagedIsHostile) return;

            boolean damagedIsPlayer = projectileHitEvent.getEntity().getType() == EntityType.PLAYER;
            if(permitted && !damagedIsPlayer) return;
            if(pvp && damagedIsPlayer) return;

            player.sendActionBar(Component.text("You are not allowed to do that!", NamedTextColor.RED));
            projectileHitEvent.setCancelled(true);
        }
    }
}
