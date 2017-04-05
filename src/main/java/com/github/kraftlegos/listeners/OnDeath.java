package com.github.kraftlegos.listeners;

import com.github.kraftlegos.Main;
import com.github.kraftlegos.managers.GameManager;
import com.github.kraftlegos.object.Game;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityEquipment;
import org.bukkit.*;
import org.bukkit.block.Banner;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;

import java.util.HashMap;
import java.util.Map;

import static com.github.kraftlegos.listeners.OnPlayerDamage.lastDamager;

public class OnDeath implements Listener {

    private final Map<Game.TeamType, Game.TeamType> reverses = new HashMap<Game.TeamType, Game.TeamType>() {
        {
            put(Game.TeamType.RED, Game.TeamType.BLUE);
            put(Game.TeamType.BLUE, Game.TeamType.RED);
        }
    };

    private HashMap<String, Integer> taskList = new HashMap<>();
    private Player t;
    private Player p;
    private int redtask;
    private int bluetask;
    private int blueleft;
    private int redleft;

    public static int stopred;
    public static int stopblue;

    public static Location redFlagDropLocation;
    public static Location blueFlagDropLocation;

    private Main plugin;
    public OnDeath(Main instance) { plugin = instance; }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {

        this.t = e.getEntity();

        if (GameManager.getGame().getRedCarrier() != null) {
            Game.TeamType teamType = GameManager.getGame().getPlayerTeams().get(t.getUniqueId());

            ItemStack itemStack = new ItemStack(Material.BANNER,1 );
            BannerMeta bannerMeta = (BannerMeta) itemStack.getItemMeta();
            bannerMeta.setBaseColor(DyeColor.valueOf(reverses.get(teamType).name()));
            itemStack.setItemMeta(bannerMeta);
            itemStack.setDurability(DyeColor.valueOf(reverses.get(teamType).name()).getData());

            PacketPlayOutEntityEquipment entityEquipment = new PacketPlayOutEntityEquipment(t.getEntityId(), 4, CraftItemStack.asNMSCopy(itemStack));

            for (Player pl : Bukkit.getServer().getOnlinePlayers()) {
                if (pl != GameManager.getGame().getRedCarrier()) {
                    ((CraftPlayer) pl).getHandle().playerConnection.sendPacket(entityEquipment);
                }
            }
        }

        if (GameManager.getGame().getBlueCarrier() != null) {
            if (GameManager.getGame().getRedCarrier() != null) {
                Game.TeamType teamType = GameManager.getGame().getPlayerTeams().get(t.getUniqueId());

                ItemStack itemStack = new ItemStack(Material.BANNER, 1);
                BannerMeta bannerMeta = (BannerMeta) itemStack.getItemMeta();
                bannerMeta.setBaseColor(DyeColor.valueOf(reverses.get(teamType).name()));
                itemStack.setItemMeta(bannerMeta);
                itemStack.setDurability(DyeColor.valueOf(reverses.get(teamType).name()).getData());

                PacketPlayOutEntityEquipment entityEquipment = new PacketPlayOutEntityEquipment(t.getEntityId(), 4, CraftItemStack.asNMSCopy(itemStack));

                for (Player pl : Bukkit.getServer().getOnlinePlayers()) {
                    if (pl != GameManager.getGame().getRedCarrier()) {
                        ((CraftPlayer) pl).getHandle().playerConnection.sendPacket(entityEquipment);
                    }
                }
            }
        }

        //if (GameManager.getGame().getGameState() != Game.GameState.STARTING && GameManager.getGame().getGameState() != Game.GameState.LOBBY){
            if (GameManager.getGame().spectators.contains(t)) {
                e.setDeathMessage(null);
                return;
            }
        //}

        //int deaths = GameManager.getGame().deathCount.get(t.getName());

        //GameManager.getGame().board.resetScores("Deaths: " + ChatColor.RED + deaths);

        //GameManager.getGame().deathCount.put(t.getName(), deaths+1);

        //GameManager.getGame().line7 = GameManager.getGame().objective.getScore("Deaths:" + ChatColor.RED + deaths);
        //GameManager.getGame().line7.setScore(1);


        if (GameManager.getGame().getRedCarrier() == t) {
            GameManager.getGame().setRedCarrier(null);
            GameManager.getGame().blueFlagDropped = true;
            if (!e.getDeathMessage().contains("fell out of the world")) {
                Block bann = t.getLocation().getBlock().getRelative(BlockFace.SELF);
                bann.setType(Material.STANDING_BANNER);
                BlockState bs = bann.getState();
                Banner banner = (Banner) bs;
                banner.setBaseColor(DyeColor.BLUE);
                bs.setData(banner.getData());
                bs.update();

                this.blueFlagDropLocation = t.getLocation();


                //new Thread(new ReturnBlueFlag()).start();
                this.blueleft = 15;

                stopblue = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
                    public void run() {



                        double x = OnDeath.blueFlagDropLocation.getX();
                        double y = OnDeath.blueFlagDropLocation.getY();
                        double z = OnDeath.blueFlagDropLocation.getZ();
                        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "kill @e[type=ArmorStand] " + x + " " + y + " " + z);

                        if (GameManager.getGame().isBlueFlagDropped() == true) {

                            ArmorStand am = blueFlagDropLocation.getWorld().spawn(blueFlagDropLocation, ArmorStand.class);
                            am.setArms(false);
                            am.setGravity(false);
                            am.setVisible(false);
                            am.setCustomName(ChatColor.RED + "Returning in" + blueleft + "s!");
                            am.setCustomNameVisible(true);
                            //p.getWorld().spigot().playEffect(p.getEyeLocation(), Effect.TILE_BREAK, 22, 0, ((float) 2), ((float) 2), ((float) 2), 1, 10, 1);
                            blueleft--;
                            blueFlagDropLocation.getWorld().playEffect(blueFlagDropLocation, Effect.STEP_SOUND, Material.LAPIS_BLOCK);
                        }
                    }
                }, 0L, 20L);

                if (taskList.containsKey("blueFlag")) {
                    Bukkit.getServer().getScheduler().cancelTask(taskList.get("blueFlag"));
                }

                    this.bluetask = Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                        public void run() {

                            if (GameManager.getGame().isBlueFlagDropped() == true) {
                                double x = OnDeath.blueFlagDropLocation.getX();
                                double y = OnDeath.blueFlagDropLocation.getY();
                                double z = OnDeath.blueFlagDropLocation.getZ();
                                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "kill @e[type=ArmorStand] " + x + " " + y + " " + z);

                                Location defaultBlueFlag = new Location(p.getWorld(), 698.5, 76, -390.5);

                                Block airblock = blueFlagDropLocation.getBlock().getRelative(BlockFace.SELF);
                                airblock.setType(Material.AIR);

                                Block bannerblock = defaultBlueFlag.getBlock().getRelative(BlockFace.SELF);
                                bannerblock.setType(Material.STANDING_BANNER);
                                BlockState bs = bannerblock.getState();
                                Banner banner = (Banner) bs;
                                banner.setBaseColor(DyeColor.BLUE);
                                bs.setData(banner.getData());
                                bs.update();
                                taskList.remove("blueFlag");
                                Bukkit.getServer().getScheduler().cancelTask(stopblue);
                                GameManager.getGame().blueFlagDropped = false;
                            }
                        }
                    }, 15 * 20L);

                taskList.put("blueflag", bluetask);
            } else {

                Location defaultBlueFlag = new Location(p.getWorld(), 698.5, 76, -390.5);

                Block bannerblock = defaultBlueFlag.getBlock().getRelative(BlockFace.SELF);
                bannerblock.setType(Material.STANDING_BANNER);
                BlockState bs = bannerblock.getState();
                Banner banner = (Banner) bs;
                banner.setBaseColor(DyeColor.BLUE);
                bs.setData(banner.getData());
                bs.update();
            }
        }

        if (GameManager.getGame().getBlueCarrier() == t) {
            GameManager.getGame().setBlueCarrier(null);
            GameManager.getGame().redFlagDropped = true;
            if (!e.getDeathMessage().contains("fell out of the world")) {
                Block bann = t.getLocation().getBlock().getRelative(BlockFace.SELF);
                bann.setType(Material.STANDING_BANNER);
                BlockState bs = bann.getState();
                Banner banner = (Banner) bs;
                banner.setBaseColor(DyeColor.RED);
                bs.setData(banner.getData());
                bs.update();

                this.redFlagDropLocation = t.getLocation();

                this.redleft = 15;

                stopred = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
                    public void run() {
                        double x = OnDeath.redFlagDropLocation.getX();
                        double y = OnDeath.redFlagDropLocation.getY();
                        double z = OnDeath.redFlagDropLocation.getZ();
                        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "kill @e[type=ArmorStand] "+ x + " " + y + " " + z);
                        if (GameManager.getGame().isRedFlagDropped() == true) {
                            redFlagDropLocation.getWorld().playEffect(redFlagDropLocation, Effect.STEP_SOUND, Material.REDSTONE_BLOCK);

                            ArmorStand am = redFlagDropLocation.getWorld().spawn(redFlagDropLocation, ArmorStand.class);
                            am.setArms(false);
                            am.setGravity(false);
                            am.setVisible(false);
                            am.setCustomName(ChatColor.RED + "Returning in" + redleft + "s!");
                            am.setCustomNameVisible(true);
                            //p.getWorld().spigot().playEffect(p.getEyeLocation(), Effect.TILE_BREAK, 22, 0, ((float) 2), ((float) 2), ((float) 2), 1, 10, 1);
                            redleft--;
                            blueFlagDropLocation.getWorld().playEffect(blueFlagDropLocation, Effect.STEP_SOUND, Material.REDSTONE_BLOCK);
                        }
                    }
                }, 0L, 20L);

                if (taskList.containsKey("redFlag")) {
                    Bukkit.getServer().getScheduler().cancelTask(taskList.get("redFlag"));
                }
                    this.redtask = Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                        public void run() {

                            if (GameManager.getGame().isBlueFlagDropped() == true) {
                                double x = OnDeath.redFlagDropLocation.getX();
                                double y = OnDeath.redFlagDropLocation.getY();
                                double z = OnDeath.redFlagDropLocation.getZ();
                                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "kill @e[type=ArmorStand] "+ x + " " + y + " " + z);

                                Location defaultRedFlag = new Location(p.getWorld(), 526.5, 76, -502.5);

                                Block airblock = redFlagDropLocation.getBlock().getRelative(BlockFace.SELF);
                                airblock.setType(Material.AIR);

                                Block bannerblock = defaultRedFlag.getBlock().getRelative(BlockFace.SELF);
                                bannerblock.setType(Material.STANDING_BANNER);
                                BlockState bs = bannerblock.getState();
                                Banner banner = (Banner) bs;
                                banner.setBaseColor(DyeColor.RED);
                                bs.setData(banner.getData());
                                bs.update();
                                Bukkit.getServer().getScheduler().cancelTask(stopred);
                                taskList.remove("redFlag");
                            }
                        }
                    }, 15 * 20L);

                taskList.put("redFlag", redtask);

            } else {

                Location defaultRedFlag = new Location(p.getWorld(), 526.5, 76, -502.5);

                Block bannerblock = defaultRedFlag.getBlock().getRelative(BlockFace.SELF);
                bannerblock.setType(Material.STANDING_BANNER);
                BlockState bs = bannerblock.getState();
                Banner banner = (Banner) bs;
                banner.setBaseColor(DyeColor.RED);
                bs.setData(banner.getData());
                bs.update();
            }
        }

        if (e.getDeathMessage().contains("was slain by")) {
            this.p = e.getEntity().getKiller();
            e.setDeathMessage(t.getCustomName() + ChatColor.YELLOW + " had their head chopped off by " + p.getCustomName());

            t.getLocation().getWorld().strikeLightningEffect(t.getLocation());
            p.playSound(p.getLocation(), Sound.ORB_PICKUP, 1.0F, 1.6F);
            if (GameManager.getGame().getRedTeam().contains(t.getName())) {
                GameManager.getGame().sendBlueMessage(ChatColor.GOLD + "(+ 10 TeamPoints)");
                GameManager.getGame().addBluePoints(10);
            } else if (GameManager.getGame().getBlueTeam().contains(t.getName())) {
                GameManager.getGame().sendRedMessage(ChatColor.GOLD + "(+ 10 TeamPoints)");
                GameManager.getGame().addRedPoints(10);
            }
            return;
        }

        else if (e.getDeathMessage().contains("fell out of the world")) {
            if (lastDamager.containsKey(t.getName())) {
                this.p = Bukkit.getServer().getPlayer(lastDamager.get(t.getName()));
                e.setDeathMessage(t.getCustomName() + ChatColor.YELLOW + " was thrown into the abyss by " + p.getCustomName());
                lastDamager.remove(t.getName());
                p.playSound(p.getLocation(), Sound.ORB_PICKUP, 1.0F, 2F);

                if (GameManager.getGame().getRedTeam().contains(t.getName())) {
                    GameManager.getGame().sendBlueMessage(ChatColor.GOLD + "(+ 10 TeamPoints)");
                    GameManager.getGame().addBluePoints(10);
                } else if (GameManager.getGame().getBlueTeam().contains(t.getName())) {
                    GameManager.getGame().sendRedMessage(ChatColor.GOLD + "(+ 10 TeamPoints)");
                    GameManager.getGame().addRedPoints(10);
                }
                return;
            } else {
                e.setDeathMessage(t.getCustomName() + ChatColor.YELLOW + " fell into the void.");
                return;
            }
        }

        else if (e.getDeathMessage().contains("fell from a high place") || e.getDeathMessage().contains("was thrown off a cliff")) {
            if (lastDamager.containsKey(t.getName())) {
                this.p = Bukkit.getServer().getPlayer(lastDamager.get(t.getName()));
                e.setDeathMessage(t.getCustomName() + ChatColor.YELLOW + " was thrown off a cliff by " + p.getCustomName());
                lastDamager.remove(t.getName());
                t.getLocation().getWorld().strikeLightningEffect(t.getLocation());
                p.playSound(p.getLocation(), Sound.ORB_PICKUP, 1.0F, 1.6F);

                if (GameManager.getGame().getRedTeam().contains(t.getName())) {
                    GameManager.getGame().sendBlueMessage(ChatColor.GOLD + "(+ 10 TeamPoints)");
                    GameManager.getGame().addBluePoints(10);
                } else if (GameManager.getGame().getBlueTeam().contains(t.getName())) {
                    GameManager.getGame().sendRedMessage(ChatColor.GOLD + "(+ 10 TeamPoints)");
                    GameManager.getGame().addRedPoints(10);
                }
                return;
            } else {
                e.setDeathMessage(t.getCustomName() + ChatColor.YELLOW + " walked off a cliff.");
                return;
            }
        } else {
            if (lastDamager.containsKey(t.getName())) {
                this.p = Bukkit.getServer().getPlayer(lastDamager.get(t.getName()));
                e.setDeathMessage(t.getCustomName() + ChatColor.YELLOW + " randomly died for some random reason by " + p.getCustomName());
                lastDamager.remove(t.getName());
                t.getLocation().getWorld().strikeLightningEffect(t.getLocation());
                p.playSound(p.getLocation(), Sound.ORB_PICKUP, 1.0F, 1.6F);

                if (GameManager.getGame().getRedTeam().contains(t.getName())) {
                    GameManager.getGame().sendBlueMessage(ChatColor.GOLD + "(+ 10 TeamPoints)");
                    GameManager.getGame().addBluePoints(10);
                } else if (GameManager.getGame().getBlueTeam().contains(t.getName())) {
                    GameManager.getGame().sendRedMessage(ChatColor.GOLD + "(+ 10 TeamPoints)");
                    GameManager.getGame().addRedPoints(10);
                }
                return;
            } else {
                e.setDeathMessage(t.getCustomName() + ChatColor.YELLOW + " randomly died.");
                return;
            }
        }
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent e) {
        Player player = e.getPlayer();

        if (GameManager.getGame().players.contains(player)) {
            if (GameManager.getGame().getGameState() != Game.GameState.STARTING || GameManager.getGame().getGameState() != Game.GameState.LOBBY) {
                if (GameManager.getGame().getRedTeam().contains(player.getName())) {
                    e.setRespawnLocation(GameManager.getGame().redSpawn);
                } else if (GameManager.getGame().getGameState() == Game.GameState.STARTING || GameManager.getGame().getGameState() == Game.GameState.LOBBY){
                    e.setRespawnLocation(GameManager.getGame().lobbyPoint);
                } else {
                    e.setRespawnLocation(GameManager.getGame().blueSpawn);
                }
            } else {
                e.setRespawnLocation(GameManager.getGame().lobbyPoint);
            }
        }
    }
}
