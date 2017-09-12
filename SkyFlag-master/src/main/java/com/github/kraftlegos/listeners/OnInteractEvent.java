package com.github.kraftlegos.listeners;

import com.github.kraftlegos.Main;
import com.github.kraftlegos.managers.GameManager;
import com.github.kraftlegos.object.Game;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityEquipment;
import org.bukkit.*;
import org.bukkit.block.Banner;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.plugin.Plugin;

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

        if (GameManager.getGame().spectators.contains(p)) {
            e.setCancelled(true);
        }

        if ((GameManager.getGame().getGameState() != Game.GameState.LOBBY) && (GameManager.getGame().getGameState() != Game.GameState.STARTING) && (GameManager.getGame().getGameState() != Game.GameState.ENDING) && (GameManager.getGame().getGameState() != Game.GameState.GRACE)) {
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

                    if (GameManager.getGame().blueFlagDropped == true) {
                        Bukkit.getServer().getScheduler().cancelTask(OnDeath.stopblue);
                        double x = OnDeath.blueFlagDropLocation.getX();
                        double y = OnDeath.blueFlagDropLocation.getY();
                        double z = OnDeath.blueFlagDropLocation.getZ();
                        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "kill @e[type=ArmorStand] " + x + " " + y + " " + z);
                        GameManager.getGame().blueFlagDropped = false;
                        GameManager.getGame().sendTitleMessage("§c" + p.getName() + "§e picked up the §9BLUE §eflag!", "blue");
                        for (Player s : Bukkit.getServer().getOnlinePlayers()) {
                            s.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Blue flag was picked up by " + p.getCustomName());

                        }
                    } else {
                        GameManager.getGame().sendTitleMessage("§c" + p.getName() + "§e stole the §9BLUE §eflag!", "blue");
                        for (Player s : Bukkit.getServer().getOnlinePlayers()) {
                            s.sendMessage(ChatColor.BLUE + "" + ChatColor.BOLD + "Blue flag was stolen by " + p.getCustomName());
                        }
                    }

                    PacketPlayOutEntityEquipment entityEquipment = new PacketPlayOutEntityEquipment(p.getEntityId(), 4, CraftItemStack.asNMSCopy(itemStack));
                    //final Packet packet = new PacketPlayOutWorldParticles(EnumParticle.REDSTONE, true, (float) p.getEyeLocation().getBlockX(), (float) p.getEyeLocation().getBlockY(), (float) p.getEyeLocation().getBlockZ(), (float) 0/255, (float) 0/255, (float) 255/255, (float) 0, 1000, null);

                    for (Player pl : Bukkit.getServer().getOnlinePlayers()) {
                        if (pl != GameManager.getGame().getRedCarrier()) {
                            ((CraftPlayer) pl).getHandle().playerConnection.sendPacket(entityEquipment);
                        }
                    }

                    final int stop = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
                        public void run() {
                            if (GameManager.getGame().getRedCarrier() != null) {
                                //for (Player pl : Bukkit.getServer().getOnlinePlayers()) {
                                    //Packet packet = new PacketPlayOutWorldParticles(EnumParticle.BLOCK_DUST("blockdust_", 38, false, 152), true, (float) p.getEyeLocation().getBlockX(), (float) p.getEyeLocation().getBlockY(), (float) p.getEyeLocation().getBlockZ(), (float) 1, (float) 1, (float) 1, (float) 152, 100, 1);
                                    //((CraftPlayer) pl).getHandle().playerConnection.sendPacket(packet);
                                //}
                                GameManager.getGame().getRedCarrier().getWorld().playEffect(GameManager.getGame().getRedCarrier().getEyeLocation(), Effect.STEP_SOUND, Material.LAPIS_BLOCK);
                                //p.getWorld().spigot().playEffect(p.getEyeLocation(), Effect.TILE_BREAK, 22, 0, ((float) 2), ((float) 2), ((float) 2), 1, 10, 1);
                            }
                        }
                    }, 0L, 20L);
                    Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
                        public void run() {
                            if (GameManager.getGame().getRedCarrier() == null) {
                                Bukkit.getScheduler().cancelTask(stop);
                            }
                        }
                    }, 0L,20L);

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

                    if (GameManager.getGame().redFlagDropped == true) {
                        Bukkit.getServer().getScheduler().cancelTask(OnDeath.stopred);
                        //double x = OnDeath.redFlagDropLocation.getX();
                        //double y = OnDeath.redFlagDropLocation.getY();
                        //double z = OnDeath.redFlagDropLocation.getZ();
                       // Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "kill @e[type=ArmorStand] " + x + " " + y + " " + z);
                        GameManager.getGame().redFlagDropped = false;
                        GameManager.getGame().sendTitleMessage("§9" + p.getName() + "§e picked up the §cRED §eflag!", "blue");

                        for (Player s : Bukkit.getServer().getOnlinePlayers()) {
                            s.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Red flag was picked up by " + p.getCustomName());
                        }
                    } else {
                        GameManager.getGame().sendTitleMessage("§9" + p.getName() + "§e stole the §cRED §eflag!", "blue");
                        for (Player s : Bukkit.getServer().getOnlinePlayers()) {
                            s.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Red flag was stolen by " + p.getCustomName());
                        }
                    }

                    PacketPlayOutEntityEquipment entityEquipment = new PacketPlayOutEntityEquipment(p.getEntityId(), 4, CraftItemStack.asNMSCopy(itemStack));
                    //final Packet packet = new PacketPlayOutWorldParticles(EnumParticle.REDSTONE, true, (float) p.getEyeLocation().getBlockX(), (float) p.getEyeLocation().getBlockY(), (float) p.getEyeLocation().getBlockZ(), (float) 0/255, (float) 0/255, (float) 255/255, (float) 0, 1000, null);

                    for (Player pl : Bukkit.getServer().getOnlinePlayers()) {
                        if (pl != GameManager.getGame().getBlueCarrier()) {
                            ((CraftPlayer) pl).getHandle().playerConnection.sendPacket(entityEquipment);
                        }
                    }
                    final int stop = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
                        public void run() {
                            if (GameManager.getGame().getBlueCarrier() != null) {
                                //for (Player pl : Bukkit.getServer().getOnlinePlayers()) {
                                //Packet packet = new PacketPlayOutWorldParticles(EnumParticle.BLOCK_DUST("blockdust_", 38, false, 152), true, (float) p.getEyeLocation().getBlockX(), (float) p.getEyeLocation().getBlockY(), (float) p.getEyeLocation().getBlockZ(), (float) 1, (float) 1, (float) 1, (float) 152, 100, 1);
                                //((CraftPlayer) pl).getHandle().playerConnection.sendPacket(packet);
                                //}
                                GameManager.getGame().getBlueCarrier().getWorld().playEffect(GameManager.getGame().getBlueCarrier().getEyeLocation(), Effect.STEP_SOUND, Material.REDSTONE_BLOCK);
                                //p.getWorld().spigot().playEffect(p.getEyeLocation(), Effect.TILE_BREAK, 22, 0, ((float) 2), ((float) 2), ((float) 2), 1, 10, 1);
                            }
                        }
                    }, 0L, 20L);

                    Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
                        public void run() {
                            if (GameManager.getGame().getBlueCarrier() == null) {
                                Bukkit.getScheduler().cancelTask(stop);
                            }
                        }
                    }, 0L,20L);

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
                } else if (GameManager.getGame().getBlueTeam().contains(p.getName()) && meta.getBaseColor() == DyeColor.BLUE) {
                    if (GameManager.getGame().isBlueFlagDropped() == true) {
                        if (GameManager.getGame().getBlueCarrier() != p) {
                            GameManager.getGame().sendTitleMessage("§9" + p.getName() + "§e returned the §9BLUE §eflag!", "blue");
                            for (Player s : Bukkit.getServer().getOnlinePlayers()) {
                                s.sendMessage(ChatColor.BLUE + "" + ChatColor.BOLD + "Blue flag was returned to its base by " + p.getCustomName());
                            }
                            Bukkit.getServer().getScheduler().cancelTask(OnDeath.stopblue);
                            double x = OnDeath.blueFlagDropLocation.getX();
                            double y = OnDeath.blueFlagDropLocation.getY();
                            double z = OnDeath.blueFlagDropLocation.getZ();
                            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "kill @e[type=ArmorStand] " + x + " " + y + " " + z);
                            Location defaultBlueFlag = new Location(p.getWorld(), 698.5, 76, -390.5);
                            Block airblock = OnDeath.blueFlagDropLocation.getBlock().getRelative(BlockFace.SELF);
                            airblock.setType(Material.AIR);
                            Block bannerblock = defaultBlueFlag.getBlock().getRelative(BlockFace.SELF);
                            bannerblock.setType(Material.STANDING_BANNER);
                            BlockState bs = bannerblock.getState();
                            Banner b = (Banner) bs;
                            b.setBaseColor(DyeColor.BLUE);
                            bs.setData(b.getData());
                            bs.update();
                            GameManager.getGame().blueFlagDropped = false;
                        }
                        }else {
                        if (GameManager.getGame().getBlueCarrier() == p) {
                            GameManager.getGame().sendTitleMessage("§9" + p.getName() + "§e has captured the §cRED §eflag!", "blue");
                            GameManager.getGame().sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "The red flag was captured by " + p.getCustomName());
                            ItemStack helmet = p.getInventory().getHelmet();

                            PacketPlayOutEntityEquipment entityEquipment = new PacketPlayOutEntityEquipment(p.getEntityId(), 4, CraftItemStack.asNMSCopy(helmet));
                            for (Player playerlist : Bukkit.getServer().getOnlinePlayers()) {
                                ((CraftPlayer) playerlist).getHandle().playerConnection.sendPacket(entityEquipment);
                            }
                            GameManager.getGame().setBlueCarrier(null);

                            GameManager.getGame().sendBlueMessage(ChatColor.GOLD + "(+ 100 TeamPoints)");
                            GameManager.getGame().addBluePoints(100);

                            Location defaultRedFlag = new Location(p.getWorld(), 526.5, 76, -502.5);
                            Block bannerblock = defaultRedFlag.getBlock().getRelative(BlockFace.SELF);
                            bannerblock.setType(Material.STANDING_BANNER);
                            BlockState bs = bannerblock.getState();
                            Banner b = (Banner) bs;
                            b.setBaseColor(DyeColor.RED);
                            bs.setData(b.getData());
                            bs.update();
                            return;
                        }
                    }
                } else if (GameManager.getGame().getRedTeam().contains(p.getName()) && meta.getBaseColor() == DyeColor.RED) {
                    if (GameManager.getGame().isRedFlagDropped() == true) {
                        if (GameManager.getGame().getRedCarrier() != p) {
                            GameManager.getGame().sendTitleMessage("§c" + p.getName() + "§e returned the §cRED §eflag!", "blue");
                            for (Player s : Bukkit.getServer().getOnlinePlayers()) {
                                s.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Red flag was returned to its base by " + p.getCustomName());
                            }
                            Bukkit.getServer().getScheduler().cancelTask(OnDeath.stopred);
                            double x = OnDeath.redFlagDropLocation.getX();
                            double y = OnDeath.redFlagDropLocation.getY();
                            double z = OnDeath.redFlagDropLocation.getZ();
                            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "kill @e[type=ArmorStand] " + x + " " + y + " " + z);
                            Location defaultRedFlag = new Location(p.getWorld(), 526.5, 76, -502.5);
                            Block airblock = OnDeath.redFlagDropLocation.getBlock().getRelative(BlockFace.SELF);
                            airblock.setType(Material.AIR);
                            Block bannerblock = defaultRedFlag.getBlock().getRelative(BlockFace.SELF);
                            bannerblock.setType(Material.STANDING_BANNER);
                            BlockState bs = bannerblock.getState();
                            Banner b = (Banner) bs;
                            b.setBaseColor(DyeColor.RED);
                            bs.setData(b.getData());
                            bs.update();
                            GameManager.getGame().redFlagDropped = false;
                        }
                    } else {
                        if (GameManager.getGame().getRedCarrier() == p) {
                            ItemStack helmet = p.getInventory().getHelmet();

                            PacketPlayOutEntityEquipment entityEquipment = new PacketPlayOutEntityEquipment(p.getEntityId(), 4, CraftItemStack.asNMSCopy(helmet));
                            for (Player playerlist : Bukkit.getServer().getOnlinePlayers()) {
                                ((CraftPlayer) playerlist).getHandle().playerConnection.sendPacket(entityEquipment);
                            }

                            GameManager.getGame().sendTitleMessage("§9" + p.getName() + "§e captured the §cRED §eflag!", "blue");
                            GameManager.getGame().sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "The red flag was captured by " + p.getCustomName());

                            GameManager.getGame().sendRedMessage(ChatColor.GOLD + "(+ 100 TeamPoints)");
                            GameManager.getGame().addRedPoints(100);


                            Location defaultBlueFlag = new Location(p.getWorld(), 698.5, 76, -390.5);
                            Block bannerblock = defaultBlueFlag.getBlock().getRelative(BlockFace.SELF);
                            bannerblock.setType(Material.STANDING_BANNER);
                            BlockState bs = bannerblock.getState();
                            Banner b = (Banner) bs;
                            b.setBaseColor(DyeColor.BLUE);
                            bs.setData(b.getData());
                            bs.update();
                            GameManager.getGame().setRedCarrier(null);
                            return;
                        }
                    }
                }
            }
        }
    }
}
