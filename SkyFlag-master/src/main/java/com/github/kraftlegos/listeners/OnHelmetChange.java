package com.github.kraftlegos.listeners;

import com.github.kraftlegos.managers.GameManager;
import com.github.kraftlegos.object.Game;
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

import java.util.HashMap;
import java.util.Map;

public class OnHelmetChange implements Listener{

    private final Map<Game.TeamType, Game.TeamType> reverses = new HashMap<Game.TeamType, Game.TeamType>() {
        {
            put(Game.TeamType.RED, Game.TeamType.BLUE);
            put(Game.TeamType.BLUE, Game.TeamType.RED);
        }
    };

    @EventHandler
    public void onHelmetChange (InventoryClickEvent e) {

        Player player = (Player) e.getWhoClicked();

        //if (e.getSlot() == 103) {
            if(GameManager.getGame().isRedCarrier(player) == true || GameManager.getGame().isBlueCarrier(player) == true) {
                Game.TeamType teamType = GameManager.getGame().getPlayerTeams().get(player.getUniqueId());

                ItemStack itemStack = new ItemStack(Material.BANNER,1 );
                BannerMeta bannerMeta = (BannerMeta) itemStack.getItemMeta();
                bannerMeta.setBaseColor(DyeColor.valueOf(reverses.get(teamType).name()));
                itemStack.setItemMeta(bannerMeta);
                itemStack.setDurability(DyeColor.valueOf(reverses.get(teamType).name()).getData());

                PacketPlayOutEntityEquipment entityEquipment = new PacketPlayOutEntityEquipment(player.getEntityId(), 4, CraftItemStack.asNMSCopy(itemStack));

                for (Player p : Bukkit.getServer().getOnlinePlayers()) {
                    if (p != GameManager.getGame().getRedCarrier()) {
                        ((CraftPlayer) p).getHandle().playerConnection.sendPacket(entityEquipment);
                    }
                }

            //}
        }
    }
}
