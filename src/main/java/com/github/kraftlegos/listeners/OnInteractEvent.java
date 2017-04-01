package com.github.kraftlegos.listeners;

import com.github.kraftlegos.Main;
import com.github.kraftlegos.managers.GameManager;
import com.github.kraftlegos.object.Game;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityEquipment;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.block.Banner;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;

public class OnInteractEvent implements Listener {

    private Main plugin;
    public OnInteractEvent(Main instance) { plugin = instance; }
    public static Player player;

    @EventHandler
    public void onInteract (final PlayerInteractEvent e) {
        Player p = e.getPlayer();

        if (e.getClickedBlock() == null) {
            return;
        }

        if ((GameManager.getGame().getGameState() != Game.GameState.LOBBY) && (GameManager.getGame().getGameState() != Game.GameState.STARTING) && (GameManager.getGame().getGameState() != Game.GameState.ENDING)) {
            if (e.getClickedBlock().getType() == Material.STANDING_BANNER) {
                Banner meta = (Banner) e.getClickedBlock().getState();
                Material banner = e.getClickedBlock().getType();

               //Bukkit.getServer().broadcastMessage(GameManager.getGame().getRedTeam().toString() + meta.getBaseColor().toString() + "");
                //WoolM meta = (BannerMeta) e.getClickedBlock().getState();
                if (GameManager.getGame().getRedTeam().contains(p.getName()) && (meta.getBaseColor() == DyeColor.BLUE)) {
                    //meta.setBaseColor(DyeColor.BLUE);
                    ItemStack itemStack = new ItemStack(Material.BANNER,1 );
                    BannerMeta bannerMeta = (BannerMeta) itemStack.getItemMeta();
                    bannerMeta.setBaseColor(DyeColor.BLUE);
                    itemStack.setItemMeta(bannerMeta);
                    itemStack.setDurability(meta.getBaseColor().getDyeData());
                    //p.getInventory().setHelmet(itemStack);
                    e.getClickedBlock().setType(Material.AIR);
                    GameManager.getGame().setRedCarrier(p);

                    for (String s : GameManager.getGame().getBlueTeam()) {
                        Bukkit.getServer().getPlayer(s).sendMessage("YOUR FLAG WAS STOLEN BY " + p.getCustomName());
                    }

                    PacketPlayOutEntityEquipment entityEquipment = new PacketPlayOutEntityEquipment(p.getEntityId(), 4, CraftItemStack.asNMSCopy(itemStack));
                    //final Packet packet = new PacketPlayOutWorldParticles(EnumParticle.REDSTONE, true, (float) p.getEyeLocation().getBlockX(), (float) p.getEyeLocation().getBlockY(), (float) p.getEyeLocation().getBlockZ(), (float) 0/255, (float) 0/255, (float) 255/255, (float) 0, 1000, null);

                    for (Player pl : Bukkit.getServer().getOnlinePlayers()) {
                        if (pl != GameManager.getGame().getRedCarrier()) {
                            ((CraftPlayer) pl).getHandle().playerConnection.sendPacket(entityEquipment);
                        }
                    }
                    int stop = 0;
                    final int finalStop = stop;
                    stop = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
                        public void run() {
                            if (GameManager.getGame().getRedCarrier() != null) {
                                //for (Player pl : Bukkit.getServer().getOnlinePlayers()) {
                                    //Packet packet = new PacketPlayOutWorldParticles(EnumParticle.BLOCK_DUST("blockdust_", 38, false, 152), true, (float) p.getEyeLocation().getBlockX(), (float) p.getEyeLocation().getBlockY(), (float) p.getEyeLocation().getBlockZ(), (float) 1, (float) 1, (float) 1, (float) 152, 100, 1);
                                    //((CraftPlayer) pl).getHandle().playerConnection.sendPacket(packet);
                                //}
                                GameManager.getGame().getRedCarrier().getWorld().playEffect(GameManager.getGame().getRedCarrier().getEyeLocation(), Effect.STEP_SOUND, Material.LAPIS_BLOCK);
                                //p.getWorld().spigot().playEffect(p.getEyeLocation(), Effect.TILE_BREAK, 22, 0, ((float) 2), ((float) 2), ((float) 2), 1, 10, 1);
                            } else {
                                Bukkit.getScheduler().cancelTask(finalStop);
                            }
                        }
                    }, 0L, 20L);

                    /*while (GameManager.getGame().getRedCarrier() == p) {
                       /* PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles(
                                EnumParticle.BLOCK_DUST,
                                true,
                                p.getLocation().getBlockX(),
                                p.getLocation().getBlockY(),
                                p.getLocation().getBlockZ(),
                                1,
                                1,
                                1,
                                10,
                                1000,
                                null);

                        for (Player pl : Bukkit.getServer().getOnlinePlayers()) {
                            ((CraftPlayer) pl).getHandle().playerConnection.sendPacket(packet);
                        p.getWorld().spigot().playEffect(p.getEyeLocation(), Effect.COLOURED_DUST, 0, 1, ((float) 17 / 255), ((float) 32 / 255), ((float) 242 / 255), 1, 100, 1);

                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e1) {
                            e1.printStackTrace();
                        }
                    }*/
                    //TODO if they die, drop the flag
                } else if (GameManager.getGame().getBlueTeam().contains(p.getName()) && meta.getBaseColor() == DyeColor.RED) {
                    //meta.setBaseColor(DyeColor.BLUE);
                    ItemStack itemStack = new ItemStack(Material.BANNER,1 );
                    BannerMeta bannerMeta = (BannerMeta) itemStack.getItemMeta();
                    bannerMeta.setBaseColor(DyeColor.RED);
                    itemStack.setItemMeta(bannerMeta);
                    itemStack.setDurability(meta.getBaseColor().getDyeData());
                    //p.getInventory().setHelmet(itemStack);
                    e.getClickedBlock().setType(Material.AIR);
                    GameManager.getGame().setBlueCarrier(p);

                    for (String s : GameManager.getGame().getRedTeam()) {
                        Bukkit.getServer().getPlayer(s).sendMessage("YOUR FLAG WAS STOLEN BY " + p.getCustomName());
                    }

                    PacketPlayOutEntityEquipment entityEquipment = new PacketPlayOutEntityEquipment(p.getEntityId(), 4, CraftItemStack.asNMSCopy(itemStack));
                    //final Packet packet = new PacketPlayOutWorldParticles(EnumParticle.REDSTONE, true, (float) p.getEyeLocation().getBlockX(), (float) p.getEyeLocation().getBlockY(), (float) p.getEyeLocation().getBlockZ(), (float) 0/255, (float) 0/255, (float) 255/255, (float) 0, 1000, null);

                    for (Player pl : Bukkit.getServer().getOnlinePlayers()) {
                        if (pl != GameManager.getGame().getRedCarrier()) {
                            ((CraftPlayer) pl).getHandle().playerConnection.sendPacket(entityEquipment);
                        }
                    }
                    int stop = 0;
                    final int finalStop = stop;
                    stop = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
                        public void run() {
                            if (GameManager.getGame().getBlueCarrier() != null) {
                                //for (Player pl : Bukkit.getServer().getOnlinePlayers()) {
                                //Packet packet = new PacketPlayOutWorldParticles(EnumParticle.BLOCK_DUST("blockdust_", 38, false, 152), true, (float) p.getEyeLocation().getBlockX(), (float) p.getEyeLocation().getBlockY(), (float) p.getEyeLocation().getBlockZ(), (float) 1, (float) 1, (float) 1, (float) 152, 100, 1);
                                //((CraftPlayer) pl).getHandle().playerConnection.sendPacket(packet);
                                //}
                                GameManager.getGame().getBlueCarrier().getWorld().playEffect(GameManager.getGame().getBlueCarrier().getEyeLocation(), Effect.STEP_SOUND, Material.REDSTONE_BLOCK);
                                //p.getWorld().spigot().playEffect(p.getEyeLocation(), Effect.TILE_BREAK, 22, 0, ((float) 2), ((float) 2), ((float) 2), 1, 10, 1);
                            } else {
                                Bukkit.getScheduler().cancelTask(finalStop);
                            }
                        }
                    }, 0L, 20L);

                    /*while (GameManager.getGame().getBlueCarrier() == p) {
                        PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles(
                                EnumParticle.BLOCK_DUST,
                                true,
                                p.getLocation().getBlockX(),
                                p.getLocation().getBlockY(),
                                p.getLocation().getBlockZ(),
                                1,
                                1,
                                1,
                                10,
                                1000,
                                null);

                        for (Player pl : Bukkit.getServer().getOnlinePlayers()) {
                            ((CraftPlayer) pl).getHandle().playerConnection.sendPacket(packet);
                        p.getWorld().spigot().playEffect(p.getEyeLocation(), Effect.COLOURED_DUST, 0, 1, ((float) 251 / 255), ((float) 42 / 255), ((float) 5 / 255), 1, 100, 1);

                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e1) {
                            e1.printStackTrace();
                        }
                    }*/
                    //TODO if they die, drop the flag
                }
            }
        }
    }
}
