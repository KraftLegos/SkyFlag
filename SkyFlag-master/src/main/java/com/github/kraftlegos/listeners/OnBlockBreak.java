package com.github.kraftlegos.listeners;

import net.minecraft.server.v1_8_R3.EntityComplexPart;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Kraft on 4/9/2017.
 */
public class OnBlockBreak implements Listener {

    @EventHandler
    public void onBlockBreak (BlockBreakEvent e) {
        Block block = e.getBlock();
        Player p = e.getPlayer();

        if (block.getType() == Material.ENCHANTMENT_TABLE || block.getType() == Material.ANVIL || block.getType() == Material.WOOL || block.getType() == Material.STANDING_BANNER || block.getType() == Material.ENDER_CHEST) {
            e.setCancelled(true);
        }

        if (block.getType() == Material.IRON_ORE) {
            block.setType(Material.AIR);

            ItemStack i = new ItemStack(Material.IRON_INGOT, 1);
            p.getWorld().dropItemNaturally(p.getLocation(), i);
        }
    }

    @EventHandler
    public void onTntExplode (EntityExplodeEvent e) {
        if (e.getEntity().getType() == EntityType.PRIMED_TNT && e.blockList().size() > 0) {
            for (Block block : e.blockList()) {
                if (block.getType() == Material.ENCHANTMENT_TABLE || block.getType() == Material.ANVIL || block.getType() == Material.WOOL || block.getType() == Material.STANDING_BANNER || block.getType() == Material.ENDER_CHEST || block.getType() == Material.BANNER) {
                    e.blockList().remove(block);
                }
            }
        } else if (e.getEntity() instanceof ComplexEntityPart) {
            Entity entity = e.getEntity();
            entity = ((ComplexEntityPart) entity).getParent();

            if (entity instanceof EnderDragon || entity instanceof EnderDragonPart) {
                if (e.blockList().size() > 0) {
                    for (Block block : e.blockList()) {
                        if (block.getType() == Material.ENCHANTMENT_TABLE || block.getType() == Material.ANVIL || block.getType() == Material.WOOL || block.getType() == Material.STANDING_BANNER || block.getType() == Material.ENDER_CHEST || block.getType() == Material.BANNER) {
                            e.blockList().remove(block);
                        }
                    }
                }
            }
        }
    }
}
