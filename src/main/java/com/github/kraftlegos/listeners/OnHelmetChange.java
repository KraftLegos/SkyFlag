package com.github.kraftlegos.listeners;

import com.github.kraftlegos.managers.GameManager;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityEquipment;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.Banner;
import org.bukkit.block.BlockState;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.material.MaterialData;

/**
 * Created by kgils on 4/1/2017.
 */
public class OnHelmetChange implements Listener{

    @EventHandler
    public void onHelmetChange (InventoryClickEvent e) {

        Player p = (Player) e.getWhoClicked();

        if (e.getSlot() == 103) {
            if (GameManager.getGame().getBlueCarrier() == p) {


                ItemStack itemStack = new ItemStack(Material.BANNER,1 );
                BannerMeta bannerMeta = (BannerMeta) itemStack.getItemMeta();
                bannerMeta.setBaseColor(DyeColor.RED);
                itemStack.setItemMeta(bannerMeta);
                itemStack.setDurability((DyeColor.RED).getData());

                PacketPlayOutEntityEquipment entityEquipment = new PacketPlayOutEntityEquipment(p.getEntityId(), 4, CraftItemStack.asNMSCopy(itemStack));

                for (Player pl : Bukkit.getServer().getOnlinePlayers()) {
                    if (pl != GameManager.getGame().getRedCarrier()) {
                        ((CraftPlayer) pl).getHandle().playerConnection.sendPacket(entityEquipment);
                    }
                }
            } else if (GameManager.getGame().getRedCarrier() == p) {
                ItemStack itemStack = new ItemStack(Material.BANNER,1 );
                BannerMeta bannerMeta = (BannerMeta) itemStack.getItemMeta();
                bannerMeta.setBaseColor(DyeColor.BLUE);
                itemStack.setItemMeta(bannerMeta);
                itemStack.setDurability((DyeColor.BLUE).getData());

                PacketPlayOutEntityEquipment entityEquipment = new PacketPlayOutEntityEquipment(p.getEntityId(), 4, CraftItemStack.asNMSCopy(itemStack));

                for (Player pl : Bukkit.getServer().getOnlinePlayers()) {
                    if (pl != GameManager.getGame().getBlueCarrier()) {
                        ((CraftPlayer) pl).getHandle().playerConnection.sendPacket(entityEquipment);
                    }
                }
            }
        }
    }
}
