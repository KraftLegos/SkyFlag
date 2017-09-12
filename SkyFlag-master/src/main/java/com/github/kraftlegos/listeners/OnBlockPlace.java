package com.github.kraftlegos.listeners;

import org.bukkit.Material;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Kraft on 4/8/2017.
 */
public class OnBlockPlace implements Listener {
    @EventHandler
    public void onBlockPlace (BlockPlaceEvent e) {
        Material m = e.getBlock().getType();
        if (m == Material.TNT) {
            e.setCancelled(true);
            int amount = e.getPlayer().getItemInHand().getAmount();
            e.getPlayer().getItemInHand().setAmount(amount-1);
            int slot = e.getPlayer().getInventory().getHeldItemSlot();

            if (e.getPlayer().getInventory().getItem(slot).getAmount() == 1) {
                e.getPlayer().getInventory().setItem(slot, new ItemStack(Material.AIR));
                e.getPlayer().updateInventory();
            }

            e.getBlock().getLocation().getWorld().spawn(e.getBlock().getLocation(), TNTPrimed.class);
        }
    }
}
