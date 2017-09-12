package com.github.kraftlegos.listeners;

import com.github.kraftlegos.managers.GameManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;

/**
 * Created by Kraft on 4/4/2017.
 */
public class OnItemPickup implements Listener {

    @EventHandler
    public void onItemPickup (PlayerPickupItemEvent e) {
        if (GameManager.getGame().spectators.contains(e.getPlayer())) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onItemDrop (PlayerDropItemEvent e) {
        if (GameManager.getGame().spectators.contains(e.getPlayer())) {
            e.setCancelled(true);
        }
    }
}
