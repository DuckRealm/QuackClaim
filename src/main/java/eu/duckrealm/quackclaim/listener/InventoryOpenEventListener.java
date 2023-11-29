package eu.duckrealm.quackclaim.listener;

import eu.duckrealm.quackclaim.QuackClaim;
import eu.duckrealm.quackclaim.manager.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;

import java.awt.*;
import java.util.ArrayList;

public class InventoryOpenEventListener implements Listener {

    @EventHandler (ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void InventoryOpen (InventoryOpenEvent inventoryOpenEvent) {
        Chunk chunk = inventoryOpenEvent.getInventory().getLocation().getChunk();
        InventoryType inventoryType = inventoryOpenEvent.getInventory().getType();

        if(isInvAllowed(inventoryType)) return;

        if (!(inventoryOpenEvent.getPlayer() instanceof Player)) return;

        Player player = Bukkit.getPlayer(inventoryOpenEvent.getPlayer().getName());

        if(QuackClaim.ignoringClaims.contains(player.getUniqueId())) return;

        boolean permitted = ClaimManager.isPermitted(player, chunk);
        if(permitted) return;

        inventoryOpenEvent.getPlayer().sendActionBar(Component.text("You are not allowed to do that!", NamedTextColor.RED));
        inventoryOpenEvent.setCancelled(true);
        inventoryOpenEvent.getPlayer().closeInventory();
    }

    private boolean isInvAllowed(InventoryType inventoryType) {
        return switch (inventoryType){
            case LOOM,
                    PLAYER,
                    CRAFTING,
                    CREATIVE,
                    SMITHING,
                    ENCHANTING,
                    GRINDSTONE,
                    CARTOGRAPHY,
                    SMITHING_NEW,
                    ENDER_CHEST,
                    WORKBENCH -> true;
            default -> false;
        };
    }
}
