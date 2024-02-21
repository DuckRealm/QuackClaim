package eu.duckrealm.quackclaim.listener;

import eu.duckrealm.quackclaim.QuackClaim;
import eu.duckrealm.quackclaim.manager.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
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
            Bukkit.broadcast(Component.text("[QuackClaim] ERROR: Attacking Player is not a Player!", NamedTextColor.RED)
                    .append(Component.newline())
                    .append(Component.text("Report this: ", NamedTextColor.RED))
                    .append(Component.text("here", NamedTextColor.RED, TextDecoration.UNDERLINED).clickEvent(ClickEvent.openUrl("https://github.com/DuckRealm/QuackClaim/issues"))));
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
}
