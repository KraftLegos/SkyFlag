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
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.github.kraftlegos.listeners.OnPlayerDamage.lastDamager;

public class OnDeath implements Listener {

    private final Map<Game.TeamType, Game.TeamType> reverses = new HashMap<Game.TeamType, Game.TeamType>() {
        {
            put(Game.TeamType.RED, Game.TeamType.BLUE);
            put(Game.TeamType.BLUE, Game.TeamType.RED);
        }
    };

    public static ArrayList<String> spawnProt = new ArrayList<>();

    private final HashMap<String, Integer> taskList = new HashMap<>();
    private final ArrayList<Integer> respawnTaskList = new ArrayList<>();
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

    public static float redFlagDropYaw;
    public static float blueFlagDropYaw;

    private Main plugin;
    public OnDeath(Main instance) { plugin = instance; }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {

        this.t = e.getEntity();

        for (ItemStack i : e.getDrops()) {
            if (i.getType().equals(Material.STONE_SWORD) || i.getType().equals(Material.LEATHER_HELMET) || i.getType().equals(Material.LEATHER_CHESTPLATE) || i.getType().equals(Material.LEATHER_LEGGINGS) || i.getType().equals(Material.LEATHER_BOOTS) || i.getType().equals(Material.WOOD_PICKAXE)) {
                i.setType(Material.AIR);
            }
        }

        t.getInventory().remove(Material.LEATHER_BOOTS);
        t.getInventory().remove(Material.LEATHER_CHESTPLATE);
        t.getInventory().remove(Material.LEATHER_HELMET);
        t.getInventory().remove(Material.LEATHER_LEGGINGS);
        t.getInventory().remove(Material.STONE_SWORD);
        t.getInventory().remove(Material.WOOD_PICKAXE);
        t.updateInventory();

        t.setExhaustion(20);

        t.setHealth(20.0D);
        respawnPlayer(t);

        /*if (GameManager.getGame().getRedCarrier() != null) {
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
        }*/

        /*if (GameManager.getGame().getBlueCarrier() != null) {
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
        }*/

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
            if (!e.getDeathMessage().contains("fell out of the world")) {
                GameManager.getGame().blueFlagDropped = true;
                GameManager.getGame().sendTitleMessage("§c" + t.getName() + "§e dropped the §9BLUE §eflag!", "blue");
                for (Player s : Bukkit.getServer().getOnlinePlayers()) {
                    s.sendMessage(ChatColor.BLUE + "" + ChatColor.BOLD + "Blue flag was dropped by " + t.getDisplayName());
                }
                Block bann = t.getLocation().getBlock().getRelative(BlockFace.SELF);
                bann.setType(Material.STANDING_BANNER);
                BlockState bs = bann.getState();
                Banner banner = (Banner) bs;
                org.bukkit.material.Banner bannerData = (org.bukkit.material.Banner) banner.getData();
                bannerData.setFacingDirection(yawToFace(blueFlagDropYaw, false));
                banner.setBaseColor(DyeColor.BLUE);
                bs.setData(banner.getData());
                bs.update();

                this.blueFlagDropLocation = t.getLocation();
                this.blueFlagDropYaw = t.getLocation().getYaw();


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
                            am.setCustomName(ChatColor.RED + "Returning in: " + blueleft + "s!");
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

                                Location defaultBlueFlag = new Location(t.getWorld(), 698.5, 76, -390.5);
                                defaultBlueFlag.setYaw((float)135.5);

                                Block airblock = blueFlagDropLocation.getBlock().getRelative(BlockFace.SELF);
                                airblock.setType(Material.AIR);

                                Block bannerblock = defaultBlueFlag.getBlock().getRelative(BlockFace.SELF);
                                bannerblock.setType(Material.STANDING_BANNER);
                                BlockState bs = bannerblock.getState();
                                Banner banner = (Banner) bs;
                                org.bukkit.material.Banner bannerData = (org.bukkit.material.Banner) banner.getData();
                                bannerData.setFacingDirection(yawToFace(defaultBlueFlag.getYaw(), false));
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
                GameManager.getGame().sendTitleMessage("§eThe §bBLUE §eflag was returned to its base", "blue");

                for (Player s : Bukkit.getServer().getOnlinePlayers()) {
                    s.sendMessage(ChatColor.BLUE + "" + ChatColor.BOLD + "Blue flag was returned to its base");
                }
                Location defaultBlueFlag = new Location(t.getWorld(), 698.5, 76, -390.5);

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
            if (!e.getDeathMessage().contains("fell out of the world")) {
                GameManager.getGame().redFlagDropped = true;
                GameManager.getGame().sendTitleMessage("§9" + t.getName() + "§e dropped the §cRED §eflag!", "blue");

                for (Player s : Bukkit.getServer().getOnlinePlayers()) {
                    s.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Red flag was dropped by " + t.getDisplayName());
                }
                Block bann = t.getLocation().getBlock().getRelative(BlockFace.SELF);
                bann.setType(Material.STANDING_BANNER);
                BlockState bs = bann.getState();
                Banner banner = (Banner) bs;
                org.bukkit.material.Banner bannerData = (org.bukkit.material.Banner) banner.getData();
                bannerData.setFacingDirection(yawToFace(redFlagDropYaw, false));
                banner.setBaseColor(DyeColor.RED);
                bs.setData(banner.getData());
                bs.update();

                this.redFlagDropLocation = t.getLocation();
                this.redFlagDropYaw = t.getLocation().getYaw();

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
                            am.setCustomName(ChatColor.RED + "Returning in: " + redleft + "s!");
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

                            if (GameManager.getGame().isRedFlagDropped() == true) {
                                GameManager.getGame().redFlagDropped = false;
                                double x = OnDeath.redFlagDropLocation.getX();
                                double y = OnDeath.redFlagDropLocation.getY();
                                double z = OnDeath.redFlagDropLocation.getZ();
                                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "kill @e[type=ArmorStand] "+ x + " " + y + " " + z);

                                Location defaultRedFlag = new Location(t.getWorld(), 526.5, 76, -502.5);
                                defaultRedFlag.setYaw((float) 44.5);

                                Block airblock = redFlagDropLocation.getBlock().getRelative(BlockFace.SELF);
                                airblock.setType(Material.AIR);

                                Block bannerblock = defaultRedFlag.getBlock().getRelative(BlockFace.SELF);
                                bannerblock.setType(Material.STANDING_BANNER);
                                BlockState bs = bannerblock.getState();
                                Banner banner = (Banner) bs;
                                banner.setBaseColor(DyeColor.RED);
                                org.bukkit.material.Banner bannerData = (org.bukkit.material.Banner) banner.getData();
                                bannerData.setFacingDirection(yawToFace(defaultRedFlag.getYaw(), false));
                                bs.setData(banner.getData());
                                bs.update();
                                Bukkit.getServer().getScheduler().cancelTask(stopred);
                                taskList.remove("redFlag");
                                GameManager.getGame().redFlagDropped = false;
                            }
                        }
                    }, 15 * 20L);

                taskList.put("redFlag", redtask);

            } else {
                GameManager.getGame().sendTitleMessage("§eThe §cRED §eflag was returned to its base", "blue");
                for (Player s : Bukkit.getServer().getOnlinePlayers()) {
                    s.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Red flag was returned to it's base");
                }

                Location defaultRedFlag = new Location(t.getWorld(), 526.5, 76, -502.5);

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
            if (e.getDeathMessage().contains("was slain by Ender")) {
                e.setDeathMessage(t.getDisplayName() + ChatColor.YELLOW + " had their head chopped off by an Ender Dragon!");
                t.getLocation().getWorld().strikeLightningEffect(t.getLocation());
                return;
            }
            this.p = e.getEntity().getKiller();
            e.setDeathMessage(t.getDisplayName() + ChatColor.YELLOW + " had their head chopped off by " + p.getDisplayName());

            t.getLocation().getWorld().strikeLightningEffect(t.getLocation());
            p.playSound(p.getLocation(), Sound.ORB_PICKUP, 1.0F, 1.6F);
            if (GameManager.getGame().killAmount.containsKey(p.getName())) {
                GameManager.getGame().killAmount.put(p.getName(), (GameManager.getGame().killAmount.get(p.getName())+1));
            } else {
                GameManager.getGame().killAmount.put(p.getName(), 1);
            }
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
                e.setDeathMessage(t.getDisplayName() + ChatColor.YELLOW + " was thrown into the abyss by " + p.getDisplayName());
                lastDamager.remove(t.getName());
                p.playSound(p.getLocation(), Sound.ORB_PICKUP, 1.0F, 2F);

                if (GameManager.getGame().killAmount.containsKey(p.getName())) {
                    GameManager.getGame().killAmount.put(p.getName(), (GameManager.getGame().killAmount.get(p.getName())+1));
                } else {
                    GameManager.getGame().killAmount.put(p.getName(), 1);
                }
                if (GameManager.getGame().getRedTeam().contains(t.getName())) {
                    GameManager.getGame().sendBlueMessage(ChatColor.GOLD + "(+ 10 TeamPoints)");
                    GameManager.getGame().addBluePoints(10);
                } else if (GameManager.getGame().getBlueTeam().contains(t.getName())) {
                    GameManager.getGame().sendRedMessage(ChatColor.GOLD + "(+ 10 TeamPoints)");
                    GameManager.getGame().addRedPoints(10);
                }
                return;
            } else {
                e.setDeathMessage(t.getDisplayName() + ChatColor.YELLOW + " fell into the void.");
                return;
            }
        }

        else if (e.getDeathMessage().contains("fell from a high place") || e.getDeathMessage().contains("was thrown off a cliff")) {
            if (lastDamager.containsKey(t.getName())) {
                this.p = Bukkit.getServer().getPlayer(lastDamager.get(t.getName()));
                e.setDeathMessage(t.getDisplayName() + ChatColor.YELLOW + " was thrown off a cliff by " + p.getDisplayName());
                lastDamager.remove(t.getName());
                t.getLocation().getWorld().strikeLightningEffect(t.getLocation());
                p.playSound(p.getLocation(), Sound.ORB_PICKUP, 1.0F, 1.6F);

                if (GameManager.getGame().killAmount.containsKey(p.getName())) {
                    GameManager.getGame().killAmount.put(p.getName(), (GameManager.getGame().killAmount.get(p.getName())+1));
                } else {
                    GameManager.getGame().killAmount.put(p.getName(), 1);
                }
                if (GameManager.getGame().getRedTeam().contains(t.getName())) {
                    GameManager.getGame().sendBlueMessage(ChatColor.GOLD + "(+ 10 TeamPoints)");
                    GameManager.getGame().addBluePoints(10);
                } else if (GameManager.getGame().getBlueTeam().contains(t.getName())) {
                    GameManager.getGame().sendRedMessage(ChatColor.GOLD + "(+ 10 TeamPoints)");
                    GameManager.getGame().addRedPoints(10);
                }
                return;
            } else {
                e.setDeathMessage(t.getDisplayName() + ChatColor.YELLOW + " walked off a cliff.");
                return;
            }
        } else {
            if (lastDamager.containsKey(t.getName())) {
                this.p = Bukkit.getServer().getPlayer(lastDamager.get(t.getName()));
                e.setDeathMessage(t.getDisplayName() + ChatColor.YELLOW + " randomly died for some random reason by " + p.getDisplayName());
                lastDamager.remove(t.getName());
                t.getLocation().getWorld().strikeLightningEffect(t.getLocation());
                p.playSound(p.getLocation(), Sound.ORB_PICKUP, 1.0F, 1.6F);

                if (GameManager.getGame().killAmount.containsKey(p.getName())) {
                    GameManager.getGame().killAmount.put(p.getName(), (GameManager.getGame().killAmount.get(p.getName())+1));
                } else {
                    GameManager.getGame().killAmount.put(p.getName(), 1);
                }
                if (GameManager.getGame().getRedTeam().contains(t.getName())) {
                    GameManager.getGame().sendBlueMessage(ChatColor.GOLD + "(+ 10 TeamPoints)");
                    GameManager.getGame().addBluePoints(10);
                } else if (GameManager.getGame().getBlueTeam().contains(t.getName())) {
                    GameManager.getGame().sendRedMessage(ChatColor.GOLD + "(+ 10 TeamPoints)");
                    GameManager.getGame().addRedPoints(10);
                }
                return;
            } else {
                e.setDeathMessage(t.getDisplayName() + ChatColor.YELLOW + " randomly died.");
                return;
            }
        }
    }

    public void respawnPlayer(final Player p) {
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
            @Override
            public void run() {
                p.setFoodLevel(20);

                if (GameManager.getGame().players.contains(p)) {
                if (!GameManager.getGame().isState(Game.GameState.STARTING) || !GameManager.getGame().isState(Game.GameState.LOBBY)) {

                    ItemStack stoneSword = new ItemStack(Material.STONE_SWORD,1 );
                    stoneSword.addEnchantment(Enchantment.DAMAGE_ALL, 1);
                    p.getInventory().addItem(stoneSword);

                    ItemStack woodenPickaxe = new ItemStack(Material.WOOD_PICKAXE, 1);
                    p.getInventory().addItem(woodenPickaxe);

                    ItemStack blocks = new ItemStack(Material.WOOD, 8);
                    p.getInventory().addItem(blocks);

                    p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 10, 1));

                    p.getInventory().setHelmet(new ItemStack(Material.LEATHER_HELMET));
                    p.getInventory().setChestplate(new ItemStack(Material.LEATHER_CHESTPLATE, 1));
                    p.getInventory().setLeggings(new ItemStack(Material.LEATHER_LEGGINGS));
                    p.getInventory().setBoots(new ItemStack(Material.LEATHER_BOOTS));


                    if (GameManager.getGame().getRedTeam().contains(p.getName())) {
                        p.teleport(GameManager.getGame().redSpawn);
                    } else if (GameManager.getGame().isState(Game.GameState.STARTING) || GameManager.getGame().isState(Game.GameState.LOBBY)) {
                        p.teleport(GameManager.getGame().lobbyPoint);
                    } else {
                        p.teleport(GameManager.getGame().blueSpawn);
                    }
                } else {
                    p.teleport(GameManager.getGame().lobbyPoint);
                }
            }
            spawnProt.add(p.getName());
            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                public void run() {
                    spawnProt.remove(p.getName());
                }
            }, 5 * 20L);

                }
        }, 10L);

    }


    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent e) {
        final Player player = e.getPlayer();

        if (GameManager.getGame().players.contains(player)) {
            if (!GameManager.getGame().isState(Game.GameState.STARTING) || !GameManager.getGame().isState(Game.GameState.LOBBY)) {

                ItemStack stoneSword = new ItemStack(Material.STONE_SWORD,1 );
                stoneSword.addEnchantment(Enchantment.DAMAGE_ALL, 1);
                player.getInventory().addItem(stoneSword);

                ItemStack woodenPickaxe = new ItemStack(Material.WOOD_PICKAXE, 1);
                player.getInventory().addItem(woodenPickaxe);

                ItemStack blocks = new ItemStack(Material.WOOD, 8);
                player.getInventory().addItem(blocks);

                player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 10, 1));

                player.getInventory().setHelmet(new ItemStack(Material.LEATHER_HELMET));
                player.getInventory().setChestplate(new ItemStack(Material.LEATHER_CHESTPLATE, 1));
                player.getInventory().setLeggings(new ItemStack(Material.LEATHER_LEGGINGS));
                player.getInventory().setBoots(new ItemStack(Material.LEATHER_BOOTS));


                if (GameManager.getGame().getRedTeam().contains(player.getName())) {
                    e.setRespawnLocation(GameManager.getGame().redSpawn);
                } else if (GameManager.getGame().isState(Game.GameState.STARTING) || GameManager.getGame().isState(Game.GameState.LOBBY)) {
                    e.setRespawnLocation(GameManager.getGame().lobbyPoint);
                } else {
                    e.setRespawnLocation(GameManager.getGame().blueSpawn);
                }
            } else {
                e.setRespawnLocation(GameManager.getGame().lobbyPoint);
            }
        }
        spawnProt.add(player.getName());
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
            public void run() {
                spawnProt.remove(player.getName());
            }
        }, 5 * 20L);
    }

    public BlockFace[] axis = { BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST };
    public BlockFace[] radial = { BlockFace.NORTH, BlockFace.NORTH_EAST, BlockFace.EAST, BlockFace.SOUTH_EAST, BlockFace.SOUTH, BlockFace.SOUTH_WEST, BlockFace.WEST, BlockFace.NORTH_WEST };

    public BlockFace yawToFace(float yaw) {
        return yawToFace(yaw, true);
    }

    public BlockFace yawToFace(float yaw, boolean useSubCardinalDirections) {
        if (useSubCardinalDirections) {
            return radial[Math.round(yaw / 45f) & 0x7];
        } else {
            return axis[Math.round(yaw / 90f) & 0x3];
        }
    }
}
