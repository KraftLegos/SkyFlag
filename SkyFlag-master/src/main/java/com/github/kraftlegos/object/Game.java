package com.github.kraftlegos.object;

import com.github.kraftlegos.Main;
import com.github.kraftlegos.managers.GameManager;
import com.github.kraftlegos.utility.EndCounter;
import com.github.kraftlegos.utility.StartCountdown;
import com.github.kraftlegos.utility.StartGraceCountdown;

import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.async.Callback;
import com.mashape.unirest.request.HttpRequest;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.exceptions.UnirestException;

import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle;
import org.apache.commons.lang.StringUtils;
import org.bukkit.*;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.bukkit.scoreboard.*;
import org.json.JSONObject;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@SuppressWarnings("unused")
public class Game {

    private Main plugin;

    //**************Countdown Timers*****************
    private int firstRefillCountdownTimer;
    private int lastRefillCountdownTimer;
    private int deathmatchCountdownTimer;
    private int endCountdownTimer;
    private int firstRefillCountdown;
    private int secondRifillCountdown;
    private int thirdRifillCountdown;
    private int fourthRifillCountdown;
    private int deathmatchCountdown;
    private int endCountdown;

    //***********Pre-Defined Game Objects***********

    private String displayName;
    private String actionBarMessage;

    private boolean isTeamGame;

    private int maxPlayers;
    private int minPlayers;

    private World world;

    public Location lobbyPoint;

    public Location redSpawn;
    public Location blueSpawn;

    //*************Active Game Objects**************

    //Lists
    public ArrayList<Player> players = new ArrayList<>();
    public ArrayList<Player> spectators = new ArrayList<>();
    private ArrayList<GameTeam> team = new ArrayList<>();
    private ArrayList<BlockState> chestList = new ArrayList<>();
    private List<String> redTeam = new ArrayList<>();
    private List<String> blueTeam = new ArrayList<>();


    //HashMaps
    //private HashMap<String, Integer> coins = new HashMap<>();
    //private HashMap<String, Integer> killAmount = new HashMap<>();
    //private HashMap<String, Integer> deathCount = new HashMap<>();
    private HashMap<String, Scoreboard> scoreboardManager = new HashMap<>();
    private HashMap<String, Integer> skyflagkills = new HashMap<>();
    private HashMap<String, Integer> skyflagwins = new HashMap<>();
    private HashMap<String, Integer> skyflagcoins = new HashMap<>();
    private HashMap<UUID, TeamType> playerTeams = new HashMap<>();
    public HashMap<String, Integer> killAmount = new HashMap<>();

    //Ints
    private int bluePoints;
    private int redPoints;
    private int timeUntilStart;
    private long top1Points = -1;
    private long top2Points = -1;
    private long top3Points = -1;

    //Booleans
    public boolean redFlagDropped;
    public boolean blueFlagDropped;

    //UUIDs
    private UUID redCarrier;
    private UUID blueCarrier;

    //Others
    private GameState gameState;
    private GameType gameType;

    private String top1Player;
    private String top2Player;
    private String top3Player;

    public Scoreboard board = Bukkit.getServer().getScoreboardManager().getMainScoreboard();

    public Objective objective;

    private Score line1;
    public Score line2;
    private Score line3;
    public Score line4;
    private Score line5;
    private Score line6;
    private Score line7;

    public Team redScoreTeam = Bukkit.getServer().getScoreboardManager().getMainScoreboard().getTeam("RED");
    public Team blueScoreTeam = Bukkit.getServer().getScoreboardManager().getMainScoreboard().getTeam("BLUE");

    public Game(Main plugin, String gameName) {
        this.plugin = plugin;
        //FileConfiguration fileConfiguration = DataHandler.getInstance().getGameInfo();
        this.displayName = gameName;
        this.maxPlayers = 16;
        this.minPlayers = 13;
        this.world = Bukkit.getServer().getWorld("world");
        this.isTeamGame = true;
        this.gameType = GameType.NORMAL;

        double redx = 567.5;
        double redy = 83;
        double redz = -412.5;

        double bluex = 657.5;
        double bluey = 83;
        double bluez = -480.5;

        double lobbyx = 609.5;
        double lobbyy = 153;
        double lobbyz = -372.5;
        //TODO Locations of spawnpoints in the world

        Location redSpawn = new Location(world, redx, redy, redz);

        redSpawn.setPitch((float)0.0);
        redSpawn.setYaw((float)180.0);

        Location blueSpawn = new Location(world, bluex, bluey, bluez);

        blueSpawn.setPitch((float) 0.0);
        blueSpawn.setYaw((float) 0.0);

        Location lobbySpawn = new Location(world, lobbyx, lobbyy, lobbyz);

        lobbySpawn.setPitch((float) 0.0);
        lobbySpawn.setYaw((float) 180.0);

        this.redSpawn = redSpawn;
        this.blueSpawn = blueSpawn;
        this.lobbyPoint = lobbySpawn;
    }

    public void joinGame(GamePlayer gamePlayer) {
        //if (gamePlayer.isTeamClass() && !isTeamGame) {
        //    gamePlayer.sendMessage("TEST");
        //    return false;

        //if (getPlayers().contains(gamePlayer)) {
        //    return false;
        //}

        //DEBUG: Bukkit.getServer().broadcastMessage(players.toString());

        if (board == null) {
            this.board = Bukkit.getServer().getScoreboardManager().getNewScoreboard();
            board.registerNewTeam("RED");
            board.registerNewTeam("BLUE");
            board.registerNewTeam("SPEC");

            //Line 1
            board.registerNewObjective("line", "dummy");
            board.getObjective("line").setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + "SKYFLAG");
            board.getObjective("line").setDisplaySlot(DisplaySlot.SIDEBAR);

        }

        if (board.getObjective("line") == null) {
            board.registerNewObjective("line", "dummy");
            board.getObjective("line").setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + "SKYFLAG");
            board.getObjective("line").setDisplaySlot(DisplaySlot.SIDEBAR);
        }

        if (board.getTeam("RED") == null && board.getTeam("BLUE") == null && board.getTeam("SPEC") == null) {
            board.registerNewTeam("RED");
            board.registerNewTeam("BLUE");
            board.registerNewTeam("SPEC");
        }

        board = Bukkit.getServer().getScoreboardManager().getMainScoreboard();
        redScoreTeam = board.getTeam("RED");
        blueScoreTeam = board.getTeam("BLUE");

        board.getObjective("line").setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + "SKYFLAG");
        board.getObjective("line").setDisplaySlot(DisplaySlot.SIDEBAR);


        for (Player online : Bukkit.getOnlinePlayers()) {
            online.setScoreboard(board);
        }

        Player p = gamePlayer.getPlayer();

        p.setHealth(20);
        p.setSaturation(20);
        p.setGameMode(GameMode.SURVIVAL);
        p.removePotionEffect(PotionEffectType.INVISIBILITY);
        if (isState(GameState.LOBBY) || isState(GameState.STARTING)) {
            if (players.contains(p)) {
                p.sendMessage(ChatColor.RED + "You are already in the game!");
                return;
            }

            if (getPlayers().size() == getMaxPlayers()) {
                p.sendMessage("&cThis game has already started! Please try again in a few minutes!");
                return;
            }
            players.add(p);

            this.bluePoints = 0;
            this.redPoints = 0;

            //deathCount.put(p.getName(), 0);
            //killAmount.put(p.getName(), 0);
            //coins.put(p.getName(), 0);

            this.objective = Bukkit.getServer().getScoreboardManager().getMainScoreboard().getObjective("line");

            this.line1 = objective.getScore(" ");
            line1.setScore(7);

            board.resetScores(ChatColor.GREEN + "Players: " + (players.size() - 1) + "/" + getMaxPlayers());
            this.line2 = objective.getScore(ChatColor.GREEN + "Players: " + players.size() + "/" + getMaxPlayers());
            line2.setScore(6);

            this.line3 = objective.getScore("  ");
            line3.setScore(5);

            this.line5 = objective.getScore("   ");
            line5.setScore(3);

            this.line6 = objective.getScore("Red Score: " + ChatColor.RED + redPoints);
            line6.setScore(2);

            this.line7 = objective.getScore("Blue Score: " + ChatColor.BLUE + bluePoints);
            line7.setScore(1);

            gamePlayer.teleport(lobbyPoint, gamePlayer);
            Bukkit.getServer().broadcastMessage(gamePlayer.getPlayer().getDisplayName() + ChatColor.YELLOW + " joined! (" + getPlayers().size() + "/" + getMaxPlayers() + ")");

            if (getPlayers().size() == getMinPlayers() && !isState(GameState.STARTING)) {
                setState(GameState.STARTING);
                Bukkit.getServer().broadcastMessage(ChatColor.GREEN + "Minimum players reached! The game will now start in 30 seconds...");

                sendTitleMessage("30s", "red");
                //for (Player pl : players) {
                 //   Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "title " + pl.getName() + " title {\"text\":\"30s\",\"color\":\"red\"}"); //JSON formatting is invalid!
                //    Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "title " + pl.getName() + " times 0 20 0");
               // }
                startCount();
            } else {
                this.line4 = objective.getScore("Waiting...");
                line4.setScore(4);
            }
        } else if (isState(gameState.ACTIVE) || isState(gameState.DEATHMATCH) || isState(gameState.ENDING) || isState(gameState.GRACE)) {
            gamePlayer.teleport(lobbyPoint, gamePlayer);
            p.sendMessage("You are now a spectator!");
            makeSpectator(p);
            return;
        } else {
            p.sendMessage("You are already in the game!");
        }
        return;
    }


    private void sendTeamSpecificActionBarRepeating() {
        Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
            @Override
            public void run() {
                for (Player p : players) {
                    Game.TeamType teamType = GameManager.getGame().getPlayerTeams().get(p.getUniqueId());
                    actionBarMessage = ChatColor.valueOf(teamType.name()) + "YOU ARE ON THE " + teamType.name() + " TEAM!" + ChatColor.GREEN + " Kills: " + killAmount.get(p.getName());
                    ActionBar actionBar = new ActionBar(actionBarMessage);
                    actionBar.sendToPlayer(p);
                }
            }
        }, 1, 5L);
    }

    public void spawnDragon() {
        Bukkit.getServer().getWorld("world").spawn(new Location(Bukkit.getServer().getWorld("world"), 614.5, 116, -446.5), EnderDragon.class);
    }

    public void removeSpectator (Player p, TeamType teamType) {
        p.setFlying(false);
        p.setAllowFlight(false);
        spectators.remove(p);
        p.removePotionEffect(PotionEffectType.INVISIBILITY);

        spectators.remove(p);
        players.add(p);

        if (teamType == TeamType.BLUE) {
            blueTeam.add(p.getName());
            p.teleport(blueSpawn);
        } else if (teamType == TeamType.RED) {
            redTeam.add(p.getName());
            p.teleport(redSpawn);
        }

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

        for (Player pl : players) {
            pl.showPlayer(p);
        }

    }

    private void makeSpectator(Player p) {
        p.setAllowFlight(true);
        p.setFlying(true);
        spectators.add(p);
        p.setPlayerListName(ChatColor.GRAY + p.getName());
        p.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 15));
        for (Player pl : players) {
            pl.hidePlayer(p);
        }
    }

    public void sendTitleMessage(String msg, String color) {
        PacketPlayOutTitle title = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE, IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + msg + "\",\"color\":\"" + color + "\"}"));
        final PacketPlayOutTitle titleend = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE, IChatBaseComponent.ChatSerializer.a("{\"text\":\" \",\"color\":\"" + color + "\"}"));

        for (Player all : Bukkit.getOnlinePlayers()) {
            ((CraftPlayer) all).getHandle().playerConnection.sendPacket(title);
        }

        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
            @Override
            public void run() {
                for (Player all : Bukkit.getOnlinePlayers()) {
                    ((CraftPlayer) all).getHandle().playerConnection.sendPacket(titleend);
                }
            }
        }, 20L);
        return;
    }

    public void startCount() {

        new Thread(new StartCountdown()).start();
    }

    public void startGame() {

        GameManager.getGame().sendTitleMessage("GO! You can find supplies in chests!", "green");

        if (gameType != GameType.GODGAME) {
            for (Player player : players) {
                ItemStack stoneSword = new ItemStack(Material.STONE_SWORD, 1);
                stoneSword.addEnchantment(Enchantment.DAMAGE_ALL, 1);
                player.getInventory().addItem(stoneSword);

                ItemStack woodenPickaxe = new ItemStack(Material.WOOD_PICKAXE, 1);
                player.getInventory().addItem(woodenPickaxe);

                ItemStack blocks = new ItemStack(Material.WOOD, 8);
                player.getInventory().addItem(blocks);

                //player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 10, 1));

                player.getInventory().setHelmet(new ItemStack(Material.LEATHER_HELMET));
                player.getInventory().setChestplate(new ItemStack(Material.LEATHER_CHESTPLATE, 1));
                player.getInventory().setLeggings(new ItemStack(Material.LEATHER_LEGGINGS));
                player.getInventory().setBoots(new ItemStack(Material.LEATHER_BOOTS));
                killAmount.put(player.getName(), 0);
                loadSkyflagInfo(player.getName());
            }

            int i = 0;
            Scoreboard b = Bukkit.getServer().getScoreboardManager().getMainScoreboard();

            this.line2 = objective.getScore(ChatColor.GREEN + "Players: " + players.size());
            line2.setScore(6);

            board.resetScores(ChatColor.GREEN + "Players: " + players.size() + "/" + getMaxPlayers());
            board.resetScores("Waiting...");

            redScoreTeam = Bukkit.getServer().getScoreboardManager().getMainScoreboard().getTeam("RED");
            blueScoreTeam = Bukkit.getServer().getScoreboardManager().getMainScoreboard().getTeam("BLUE");
            setState(GameState.GRACE);
            startGracePeriod();
            addNormalChestItems();
            sendTeamSpecificActionBarRepeating();



            for (Player p : players) {

                //if(p.getGameMode() != GameMode.SURVIVAL) p.setGameMode(GameMode.SURVIVAL);

                if (i < players.size() / 2) {
                    addToTeam(TeamType.RED, p);

                    //TODO PacketPlayOutScoreboardTeam packetPlayOutScoreboardTeam = new PacketPlayOutScoreboardTeam();

                    redScoreTeam.addEntry(p.getName());
                    redScoreTeam.setPrefix("§c[R] ");
                    for (Player online : Bukkit.getOnlinePlayers()) {
                        online.setScoreboard(b);
                    }
                    p.teleport(redSpawn);
                    p.sendMessage(ChatColor.RED + "You are now on the RED team!");

                    p.playSound(p.getLocation(), Sound.NOTE_PLING, 1.0F, 1.200F);

                } else {
                    addToTeam(TeamType.BLUE, p);
                    blueScoreTeam.addEntry(p.getName());
                    blueScoreTeam.setPrefix("§9[B] ");
                    for (Player online : Bukkit.getOnlinePlayers()) {
                        online.setScoreboard(b);
                    }
                    p.teleport(blueSpawn);
                    p.sendMessage(ChatColor.BLUE + "You are now on the blue team!");
                    p.playSound(p.getLocation(), Sound.NOTE_PLING, 1.0F, 1.200F);

                }
                i++;
            }
        } else if (gameType == GameType.GODGAME) {
            //TODO Start the game in GODGAME mode
        }
    }

    public void addToTeam(TeamType type, Player player) {
        playerTeams.put(player.getUniqueId(), type);
        switch (type) {
            case RED:
                redTeam.add(player.getName());
                player.setPlayerListName(ChatColor.RED + "[R] " + player.getName());
                break;
            case BLUE:
                blueTeam.add(player.getName());
                player.setPlayerListName(ChatColor.BLUE + "[B] " + player.getName());
                break;
        }
    }

    private void startGracePeriod() {   
        board.resetScores("Waiting...");
        board.resetScores( "0s until start!");
        board.resetScores("0 until start!");
        new Thread(new StartGraceCountdown()).start();
    }

    public void startActive() {
        sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "The grace period has now ended! You can PvP!");
        setState(GameState.ACTIVE);
        //new Thread(new ActiveCountdown()).start();

        this.firstRefillCountdownTimer = 150;
        board.resetScores("PvP Enables: " + ChatColor.YELLOW + "0s");
        firstRefillCountdown = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
            public void run() {
                int time = firstRefillCountdownTimer + 1;
                board.resetScores(ChatColor.GREEN + "Players: " + players.size() + "/" + getMaxPlayers());
                board.resetScores("Waiting...");

                board.resetScores("Chest Reset: " + ChatColor.YELLOW + "0s");
                board.resetScores("Chest Reset: " + ChatColor.YELLOW + time + "s");

                line4 = objective.getScore("Chest Reset: " + ChatColor.YELLOW + firstRefillCountdownTimer + "s");
                line4.setScore(4);

                firstRefillCountdownTimer--;
            }
        }, 0, 20L);

        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {

            public void run() {
                board.resetScores("Chest Reset: " + ChatColor.YELLOW + "1s");
                board.resetScores("Chest Reset: " + ChatColor.YELLOW + "0s");
                fiveMinRefill();
                Bukkit.getServer().getScheduler().cancelTask(firstRefillCountdown);
            }
        }, 150 * 20);
    }

    public void fiveMinRefill() {

        sendMessage(ChatColor.GREEN + "All chests have been refilled");
        addFirstRefillItems();
        this.lastRefillCountdownTimer = 150;
        secondRifillCountdown = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
            public void run() {
                int time = lastRefillCountdownTimer + 1;

                board.resetScores("Chest Reset: " + ChatColor.YELLOW + "0s");
                board.resetScores("Chest Reset: " + ChatColor.YELLOW + time + "s");

                line4 = objective.getScore("Chest Reset: " + ChatColor.YELLOW + lastRefillCountdownTimer + "s");
                line4.setScore(4);

                lastRefillCountdownTimer--;
            }
        }, 0, 20L);

        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {

            public void run() {
                board.resetScores("Chest Reset: " + ChatColor.YELLOW + "1s");
                board.resetScores("Chest Reset: " + ChatColor.YELLOW + "0s");
                sevenAndAHalfMinRefill();
                Bukkit.getServer().getScheduler().cancelTask(secondRifillCountdown);
            }
        }, 150 * 20);
    }

    public void sevenAndAHalfMinRefill() {

        sendMessage(ChatColor.GREEN + "All chests have been refilled");
        addFirstRefillItems();
        this.lastRefillCountdownTimer = 150;
        thirdRifillCountdown = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
            public void run() {
                int time = lastRefillCountdownTimer + 1;
                board.resetScores("Chest Reset: " + ChatColor.YELLOW + "1s");
                board.resetScores("Chest Reset: " + ChatColor.YELLOW + "0s");

                board.resetScores("Last Chest Reset: " + ChatColor.YELLOW + "0s");
                board.resetScores("Last Chest Reset: " + ChatColor.YELLOW + time + "s");

                line4 = objective.getScore("Last Chest Reset: " + ChatColor.YELLOW + lastRefillCountdownTimer + "s");
                line4.setScore(4);

                lastRefillCountdownTimer--;
            }
        }, 0, 20L);

        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {

            public void run() {
                board.resetScores("Last Chest Reset: " + ChatColor.YELLOW + "1s");
                board.resetScores("Last Chest Reset: " + ChatColor.YELLOW + "0s");
                tenMinRefill();
                Bukkit.getServer().getScheduler().cancelTask(thirdRifillCountdown);
            }
        }, 150 * 20);
    }

    public void tenMinRefill() {
        sendMessage(ChatColor.GREEN + "All chests have been refilled");
        addLastRefillItems();

        this.deathmatchCountdownTimer = 300;
        deathmatchCountdown = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
            public void run() {
                int time = deathmatchCountdownTimer + 1;

                board.resetScores("Last Chest Reset: " + ChatColor.YELLOW + "0s");
                board.resetScores("Last Chest Reset: " + ChatColor.YELLOW + time + "s");

                board.resetScores("Deathmatch in: " + ChatColor.YELLOW + "0s");
                board.resetScores("Deathmatch in: " + ChatColor.YELLOW + time + "s");

                line4 = objective.getScore("Deathmatch in: " + ChatColor.YELLOW + deathmatchCountdownTimer + "s");
                line4.setScore(4);

                deathmatchCountdownTimer--;
            }
        }, 0, 20L);

        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {

            public void run() {
                board.resetScores("Deathmatch in: " + ChatColor.YELLOW + "1s");
                board.resetScores("Deathmatch in: " + ChatColor.YELLOW + "0s");
                deathMatch();
                Bukkit.getServer().getScheduler().cancelTask(deathmatchCountdown);
            }
        }, 300 * 20);
        //TODO 10m Chest refill
    }

    public void deathMatch() {
        sendMessage(ChatColor.RED + "DEATHMATCH STARTED!");
        //sendMessage(ChatColor.YELLOW + "+1 " + ChatColor.BLUE + "Dragon!");
        GameManager.getGame().sendTitleMessage("Watch out for the dragon! He bites ;D", "red");
        spawnDragon();

        this.endCountdownTimer = 300;
        endCountdown = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
            public void run() {

                int time = endCountdownTimer + 1;

                board.resetScores("Deathmatch in: " + ChatColor.YELLOW + "1s");
                board.resetScores("Deathmatch in: " + ChatColor.YELLOW + "0s");

                board.resetScores("Game End: " + ChatColor.YELLOW + "0s");
                board.resetScores("Game End: " + ChatColor.YELLOW + time + "s");

                line4 = objective.getScore("Game End: " + ChatColor.YELLOW + endCountdownTimer + "s");
                line4.setScore(4);

                endCountdownTimer--;
            }
        }, 0, 20L);

        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {

            public void run() {
                board.resetScores("Game End: " + ChatColor.YELLOW + "1s");
                board.resetScores("Game End: " + ChatColor.YELLOW + "0s");
                end();
                Bukkit.getServer().getScheduler().cancelTask(endCountdown);
            }
        }, 300 * 20);
    }

    public void end() {

        for(Player p : players) {//loop threw all players that have ever played
            String playername = p.getName();//get the player's UUID
            long points = killAmount.get(playername);//get the player's points
            if (points >= top3Points) { //if the player has more points than the top3Points
                if (points >= top2Points) { //if the player has more points than the top2Points
                    if (points >= top1Points) { //if the player has more points than the top1Points
                        //store that this player has the #1 points as of now
                        if (top1Player != null) {
                            if (top2Player != null) {
                                top3Player = top2Player;
                            }
                            top2Player = top1Player;
                        }
                        top1Points = points;
                        top1Player = p.getName();
                        continue;
                    }
                    //store that this player has the #2 points as of now
                    if (top2Player != null) {
                        top3Player = top2Player;
                    }
                    top2Points = points;
                    top2Player = p.getName();
                    continue;
                }
                //store that this player has the #3 points as of now
                top3Points = points;
                top3Player = p.getName();
                continue;
            }
        }
        setState(GameState.ENDING);
        if (bluePoints == redPoints) {

            GameManager.getGame().sendTitleMessage("DRAW!", "white");
            //DRAW
            sendMessage("-----------------------------------------------------");
            sendMessage("");
            String winmessage = ChatColor.GREEN + "" + ChatColor.BOLD + "Winner: " + ChatColor.RESET +"DRAW!";
            sendMessage(StringUtils.center(winmessage, winmessage.length()));
            sendMessage("");

            String topKillerOne = ChatColor.YELLOW + "1st Killer: " + Bukkit.getServer().getPlayer(top1Player).getCustomName() + ChatColor.YELLOW + " - " + killAmount.get(top1Player) + " kills";
            String topKillerTwo = ChatColor.YELLOW + "2nd Killer: " + Bukkit.getServer().getPlayer(top2Player).getCustomName() + ChatColor.YELLOW + " - " + killAmount.get(top2Player) + " kills";
            String topKillerThree = ChatColor.YELLOW + "3rd Killer: " + Bukkit.getServer().getPlayer(top3Player).getCustomName() + ChatColor.YELLOW + " - " + killAmount.get(top3Player) + " kills";
            sendMessage(StringUtils.center(topKillerOne, topKillerOne.length()));
            sendMessage(StringUtils.center(topKillerTwo, topKillerTwo.length()));
            sendMessage(StringUtils.center(topKillerThree, topKillerThree.length()));
            sendMessage("");
            sendMessage("-----------------------------------------------------");
            sendBlueMessage(ChatColor.GOLD + "Your team's " + bluePoints + " points were transfused into coins!");
            sendRedMessage(ChatColor.GOLD + "Your teams's " + redPoints + " points were transfused into coins!");
            //TODO Add Coins, Wins, And kills to the database

            plugin.openConnection();
            try {
                for (String name : blueTeam) {
                    String uuid = "";
                    if (Bukkit.getServer().getPlayer(name) != null) {
                        uuid = Bukkit.getServer().getPlayer(name).getUniqueId().toString();
                    } else {
                        uuid = Bukkit.getServer().getOfflinePlayer(name).getUniqueId().toString();
                    }

                    PreparedStatement winsUpdate = plugin.connection.prepareStatement("UPDATE `player_data` SET skyflag_wins=? WHERE player=?;");
                    winsUpdate.setInt(1, this.skyflagwins.get(name) + 0);
                    winsUpdate.setString(2, uuid);
                    winsUpdate.executeUpdate();

                    PreparedStatement killsUpdate = plugin.connection.prepareStatement("UPDATE `player_data` SET skyflag_kills=? WHERE player=?;");
                    killsUpdate.setInt(1, this.skyflagkills.get(name) + killAmount.get(name));
                    killsUpdate.setString(2, uuid);
                    killsUpdate.executeUpdate();

                    PreparedStatement coinsUpdate = plugin.connection.prepareStatement("UPDATE `player_data` SET skyflag_coins=? WHERE player=?;");
                    coinsUpdate.setInt(1, this.skyflagcoins.get(name) + bluePoints);
                    coinsUpdate.setString(2, uuid);
                    coinsUpdate.executeUpdate();

                    killsUpdate.close();
                    winsUpdate.close();
                    coinsUpdate.close();
                }
                for (String name : redTeam) {
                    String uuid = "";
                    if (Bukkit.getServer().getPlayer(name) != null) {
                        uuid = Bukkit.getServer().getPlayer(name).getUniqueId().toString();
                    } else {
                        uuid = Bukkit.getServer().getOfflinePlayer(name).getUniqueId().toString();
                    }

                    PreparedStatement winsUpdate = plugin.connection.prepareStatement("UPDATE `player_data` SET skyflag_wins=? WHERE player=?;");
                    winsUpdate.setInt(1, this.skyflagwins.get(name) + 0);
                    winsUpdate.setString(2, uuid);
                    winsUpdate.executeUpdate();

                    PreparedStatement killsUpdate = plugin.connection.prepareStatement("UPDATE `player_data` SET skyflag_kills=? WHERE player=?;");
                    killsUpdate.setInt(1, this.skyflagkills.get(name) + killAmount.get(name));
                    killsUpdate.setString(2, uuid);
                    killsUpdate.executeUpdate();

                    PreparedStatement coinsUpdate = plugin.connection.prepareStatement("UPDATE `player_data` SET skyflag_coins=? WHERE player=?;");
                    coinsUpdate.setInt(1, this.skyflagcoins.get(name) + redPoints);
                    coinsUpdate.setString(2, uuid);
                    coinsUpdate.executeUpdate();

                    killsUpdate.close();
                    winsUpdate.close();
                    coinsUpdate.close();
                }
            }catch (Exception e) {
                e.printStackTrace();
            } finally {
                plugin.closeConnection();
            }

        } else if (bluePoints < redPoints) {
            //RED WON
            GameManager.getGame().sendTitleMessage("RED TEAM WON!", "red");
            sendMessage("----------------------------------------------------------------");
            sendMessage("");
            String winmessage = ChatColor.GREEN + "" + ChatColor.BOLD + "Winner: " + ChatColor.RED +"RED TEAM!";
            sendMessage(StringUtils.center(winmessage, winmessage.length()));
            sendMessage("");

            String topKillerOne = ChatColor.YELLOW + "1st Killer: " + Bukkit.getServer().getPlayer(top1Player).getCustomName() + ChatColor.YELLOW + " - " + killAmount.get(top1Player) + " kills";
            String topKillerTwo = ChatColor.YELLOW + "2nd Killer: " + Bukkit.getServer().getPlayer(top2Player).getCustomName() + ChatColor.YELLOW + " - " + killAmount.get(top2Player) + " kills";
            String topKillerThree = ChatColor.YELLOW + "3rd Killer: " + Bukkit.getServer().getPlayer(top3Player).getCustomName() + ChatColor.YELLOW + " - " + killAmount.get(top3Player) + " kills";
            sendMessage(StringUtils.center(topKillerOne, topKillerOne.length()));
            sendMessage(StringUtils.center(topKillerTwo, topKillerTwo.length()));
            sendMessage(StringUtils.center(topKillerThree, topKillerThree.length()));
            sendMessage("");
            sendMessage("----------------------------------------------------------------");
            addRedPoints(500);
            sendRedMessage(ChatColor.GOLD + "+ 500 TeamPoints (Win Bonus)");
            sendBlueMessage(ChatColor.GOLD + "Your team's " + bluePoints + " points were transfused into coins!");
            sendRedMessage(ChatColor.GOLD + "Your teams's " + redPoints + " points were transfused into coins!");
            //TODO Add Coins, Wins, And kills to the database

            plugin.openConnection();
            try {
                for (String name : blueTeam) {
                    String uuid = "";
                    if (Bukkit.getServer().getPlayer(name) != null) {
                        uuid = Bukkit.getServer().getPlayer(name).getUniqueId().toString();
                    } else {
                        uuid = Bukkit.getServer().getOfflinePlayer(name).getUniqueId().toString();
                    }

                    PreparedStatement winsUpdate = plugin.connection.prepareStatement("UPDATE `player_data` SET skyflag_wins=? WHERE player=?;");
                    winsUpdate.setInt(1, this.skyflagwins.get(name));
                    winsUpdate.setString(2, uuid);
                    winsUpdate.executeUpdate();

                    PreparedStatement killsUpdate = plugin.connection.prepareStatement("UPDATE `player_data` SET skyflag_kills=? WHERE player=?;");
                    killsUpdate.setInt(1, this.skyflagkills.get(name) + killAmount.get(name));
                    killsUpdate.setString(2, uuid);
                    killsUpdate.executeUpdate();

                    PreparedStatement coinsUpdate = plugin.connection.prepareStatement("UPDATE `player_data` SET skyflag_coins=? WHERE player=?;");
                    coinsUpdate.setInt(1, this.skyflagcoins.get(name) + bluePoints);
                    coinsUpdate.setString(2, uuid);
                    coinsUpdate.executeUpdate();

                    killsUpdate.close();
                    winsUpdate.close();
                    coinsUpdate.close();
                }
                for (String name : redTeam) {
                    String uuid = "";
                    if (Bukkit.getServer().getPlayer(name) != null) {
                        uuid = Bukkit.getServer().getPlayer(name).getUniqueId().toString();
                    } else {
                        uuid = Bukkit.getServer().getOfflinePlayer(name).getUniqueId().toString();
                    }

                    PreparedStatement winsUpdate = plugin.connection.prepareStatement("UPDATE `player_data` SET skyflag_wins=? WHERE player=?;");
                    winsUpdate.setInt(1, this.skyflagwins.get(name) + 1);
                    winsUpdate.setString(2, uuid);
                    winsUpdate.executeUpdate();

                    PreparedStatement killsUpdate = plugin.connection.prepareStatement("UPDATE `player_data` SET skyflag_kills=? WHERE player=?;");
                    killsUpdate.setInt(1, this.skyflagkills.get(name) + killAmount.get(name));
                    killsUpdate.setString(2, uuid);
                    killsUpdate.executeUpdate();

                    PreparedStatement coinsUpdate = plugin.connection.prepareStatement("UPDATE `player_data` SET skyflag_coins=? WHERE player=?;");
                    coinsUpdate.setInt(1, this.skyflagcoins.get(name) + redPoints);
                    coinsUpdate.setString(2, uuid);
                    coinsUpdate.executeUpdate();

                    killsUpdate.close();
                    winsUpdate.close();
                    coinsUpdate.close();
                }
            }catch (Exception e) {
                e.printStackTrace();
            } finally {
                plugin.closeConnection();
            }
        } else if (bluePoints > redPoints) {
            //BLUE WON
            GameManager.getGame().sendTitleMessage("BLUE TEAM WON!", "blue");
            sendMessage("----------------------------------------------------------------");
            sendMessage("");
            String winmessage = ChatColor.GREEN + "" + ChatColor.BOLD + "Winner: " + ChatColor.BLUE +"BLUE TEAM!";
            sendMessage(StringUtils.center(winmessage, winmessage.length()));
            sendMessage("");

            String topKillerOne = ChatColor.YELLOW + "1st Killer: " + Bukkit.getServer().getPlayer(top1Player).getCustomName() + ChatColor.YELLOW + " - " + killAmount.get(top1Player) + " kills";
            String topKillerTwo = ChatColor.YELLOW + "2nd Killer: " + Bukkit.getServer().getPlayer(top2Player).getCustomName() + ChatColor.YELLOW + " - " + killAmount.get(top2Player) + " kills";
            String topKillerThree = ChatColor.YELLOW + "3rd Killer: " + Bukkit.getServer().getPlayer(top3Player).getCustomName() + ChatColor.YELLOW + " - " + killAmount.get(top3Player) + " kills";
            sendMessage(StringUtils.center(topKillerOne, topKillerOne.length()));
            sendMessage(StringUtils.center(topKillerTwo, topKillerTwo.length()));
            sendMessage(StringUtils.center(topKillerThree, topKillerThree.length()));
            sendMessage("");
            sendMessage("----------------------------------------------------------------");
            addBluePoints(500);
            sendBlueMessage(ChatColor.GOLD + "+ 500 TeamPoints (Win Bonus)");
            sendBlueMessage(ChatColor.GOLD + "Your team's " + bluePoints + " points were transfused into coins!");
            sendRedMessage(ChatColor.GOLD + "Your teams's " + redPoints + " points were transfused into coins!");
            //TODO Add Coins, Wins, And kills to the database
            plugin.openConnection();
            try {
                for (String name : blueTeam) {
                    String uuid = "";
                    if (Bukkit.getServer().getPlayer(name) != null) {
                        uuid = Bukkit.getServer().getPlayer(name).getUniqueId().toString();
                    } else {
                        uuid = Bukkit.getServer().getOfflinePlayer(name).getUniqueId().toString();
                    }

                    PreparedStatement winsUpdate = plugin.connection.prepareStatement("UPDATE `player_data` SET skyflag_wins=? WHERE player=?;");
                    winsUpdate.setInt(1, this.skyflagwins.get(name) + 1);
                    winsUpdate.setString(2, uuid);
                    winsUpdate.executeUpdate();

                    PreparedStatement killsUpdate = plugin.connection.prepareStatement("UPDATE `player_data` SET skyflag_kills=? WHERE player=?;");
                    killsUpdate.setInt(1, this.skyflagkills.get(name) + killAmount.get(name));
                    killsUpdate.setString(2, uuid);
                    killsUpdate.executeUpdate();

                    PreparedStatement coinsUpdate = plugin.connection.prepareStatement("UPDATE `player_data` SET skyflag_coins=? WHERE player=?;");
                    coinsUpdate.setInt(1, this.skyflagcoins.get(name) + bluePoints);
                    coinsUpdate.setString(2, uuid);
                    coinsUpdate.executeUpdate();

                    killsUpdate.close();
                    winsUpdate.close();
                    coinsUpdate.close();
                }
                for (String name : redTeam) {
                    String uuid = null;
                    if (Bukkit.getServer().getPlayer(name) != null) {
                        uuid = Bukkit.getServer().getPlayer(name).getUniqueId().toString();
                    } else {
                        uuid = Bukkit.getServer().getOfflinePlayer(name).getUniqueId().toString();
                    }

                    PreparedStatement winsUpdate = plugin.connection.prepareStatement("UPDATE `player_data` SET skyflag_wins=? WHERE player=?;");
                    winsUpdate.setInt(1, this.skyflagwins.get(name) + 0);
                    winsUpdate.setString(2, uuid);
                    winsUpdate.executeUpdate();

                    PreparedStatement killsUpdate = plugin.connection.prepareStatement("UPDATE `player_data` SET skyflag_kills=? WHERE player=?;");
                    killsUpdate.setInt(1, this.skyflagkills.get(name) + killAmount.get(name));
                    killsUpdate.setString(2, uuid);
                    killsUpdate.executeUpdate();

                    PreparedStatement coinsUpdate = plugin.connection.prepareStatement("UPDATE `player_data` SET skyflag_coins=? WHERE player=?;");
                    coinsUpdate.setInt(1, this.skyflagcoins.get(name) + redPoints);
                    coinsUpdate.setString(2, uuid);
                    coinsUpdate.executeUpdate();

                    killsUpdate.close();
                    winsUpdate.close();
                    coinsUpdate.close();
                }
            }catch (Exception e) {
                e.printStackTrace();
            } finally {
                plugin.closeConnection();
            }
        }

        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
            public void run() {
                //Send them to the lobby
                kickAllPlayers();
                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "restart");
            }
        }, 13 * 20L);
    }

    public synchronized void loadSkyflagInfo(String name) {
        UUID playerUUID = Bukkit.getServer().getPlayer(name).getUniqueId();

        try {
            if (plugin.playerDataContainsPlayer(Bukkit.getServer().getPlayer(name))) {
                plugin.openConnection();
                PreparedStatement skyflagwinsStatement = plugin.connection.prepareStatement("SELECT skyflag_wins FROM `player_data` WHERE player=?;");
                skyflagwinsStatement.setString(1, playerUUID.toString());

                ResultSet resultWinsSet = skyflagwinsStatement.executeQuery();
                resultWinsSet.next();

                skyflagwins.put(name, resultWinsSet.getInt("skyflag_wins"));

                PreparedStatement skyflagkillsStatement = plugin.connection.prepareStatement("SELECT skyflag_kills FROM `player_data` WHERE player=?;");
                skyflagkillsStatement.setString(1, playerUUID.toString());

                ResultSet resultKillsSet = skyflagkillsStatement.executeQuery();
                resultKillsSet.next();

                skyflagkills.put(name, resultKillsSet.getInt("skyflag_kills"));


                PreparedStatement skyflagCoinsStatement = plugin.connection.prepareStatement("SELECT skyflag_coins FROM `player_data` WHERE player=?;");
                skyflagCoinsStatement.setString(1, playerUUID.toString());

                ResultSet resultCoinsSet = skyflagCoinsStatement.executeQuery();
                resultCoinsSet.next();

                skyflagcoins.put(name, resultCoinsSet.getInt("skyflag_coins"));

                skyflagwinsStatement.close();
                resultWinsSet.close();
                skyflagkillsStatement.close();
                resultKillsSet.close();
                skyflagCoinsStatement.close();
                resultCoinsSet.close();
            } else {
                skyflagcoins.put(name, 1000);
                skyflagkills.put(name, 0);
                skyflagwins.put(name, 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void endGame() {
        new Thread(new EndCounter()).start();
        setState(GameState.ENDING);
        sendMessage(ChatColor.GREEN + "TODO: END MESSAGE!");
        sendMessage(ChatColor.GREEN + "THANK YOU FOR PLAYING " + ChatColor.BOLD + "" + ChatColor.ITALIC + "" + ChatColor.GOLD + "SkyFlag");
        sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Please report any bugs to Kraft on slack!");

    }

    public void setScoreboard(Player p) {
        String name = p.getName();
        if (scoreboardManager.containsKey(p.getName())) {
            scoreboardManager.remove(p.getName());
        }
        Scoreboard privateBoard = Bukkit.getServer().getScoreboardManager().getNewScoreboard();

        setScoreboardObjective(p, "line");
        p.setScoreboard(privateBoard);
        scoreboardManager.put(p.getName(), privateBoard);
    }

    public Scoreboard getScoreboard(Player p) {
        return scoreboardManager.get(p.getName());
    }

    private void setScoreboardObjective(Player p, String objectName) {
        if (scoreboardManager.get(p.getName()).getObjective(objectName) == null) {
            scoreboardManager.get(p.getName()).registerNewObjective(objectName, "dummy");
            scoreboardManager.get(p.getName()).getObjective("line").setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + "SKYFLAG");
            scoreboardManager.get(p.getName()).getObjective("line").setDisplaySlot(DisplaySlot.SIDEBAR);
        }
    }

    public HashMap<UUID, TeamType> getPlayerTeams() {
        return playerTeams;
    }

    private void addLastRefillItems() {
        GameManager.getGame().sendTitleMessage("Chests have been refilled!", "yellow");
        for (BlockState b : chestList) {
            if (b != null) {
                Inventory inv = ((Chest) b).getBlockInventory();

                Material[] randomItems = {
                        Material.STONE_BUTTON,

                        Material.TNT,
                        Material.TNT,
                        Material.TNT,
                        Material.TNT,
                        Material.TNT,
                        Material.TNT,
                        Material.TNT,
                        Material.TNT,
                        Material.TNT,
                        Material.TNT,
                        Material.TNT,
                        Material.TNT,

                        Material.DIAMOND_CHESTPLATE,
                        Material.DIAMOND_CHESTPLATE,
                        Material.DIAMOND_CHESTPLATE,
                        Material.DIAMOND_CHESTPLATE,
                        Material.DIAMOND_CHESTPLATE,
                        Material.DIAMOND_CHESTPLATE,
                        Material.DIAMOND_CHESTPLATE,
                        Material.DIAMOND_CHESTPLATE,
                        Material.DIAMOND_CHESTPLATE,

                        Material.DIAMOND_HELMET,
                        Material.DIAMOND_HELMET,
                        Material.DIAMOND_HELMET,
                        Material.DIAMOND_HELMET,
                        Material.DIAMOND_HELMET,
                        Material.DIAMOND_HELMET,
                        Material.DIAMOND_HELMET,
                        Material.DIAMOND_HELMET,
                        Material.DIAMOND_HELMET,

                        Material.DIAMOND_LEGGINGS,
                        Material.DIAMOND_LEGGINGS,
                        Material.DIAMOND_LEGGINGS,
                        Material.DIAMOND_LEGGINGS,
                        Material.DIAMOND_LEGGINGS,
                        Material.DIAMOND_LEGGINGS,
                        Material.DIAMOND_LEGGINGS,
                        Material.DIAMOND_LEGGINGS,
                        Material.DIAMOND_LEGGINGS,
                        Material.DIAMOND_LEGGINGS,

                        Material.DIAMOND_BOOTS,
                        Material.DIAMOND_BOOTS,
                        Material.DIAMOND_BOOTS,
                        Material.DIAMOND_BOOTS,
                        Material.DIAMOND_BOOTS,
                        Material.DIAMOND_BOOTS,
                        Material.DIAMOND_BOOTS,
                        Material.DIAMOND_BOOTS,
                        Material.DIAMOND_BOOTS,
                        Material.DIAMOND_BOOTS,

                        Material.GOLDEN_APPLE,
                        Material.GOLDEN_APPLE,
                        Material.GOLDEN_APPLE,
                        Material.GOLDEN_APPLE,
                        Material.GOLDEN_APPLE,
                        Material.GOLDEN_APPLE,
                        Material.GOLDEN_APPLE,
                        Material.GOLDEN_APPLE,
                        Material.GOLDEN_APPLE,

                        Material.DIAMOND_PICKAXE,
                        Material.DIAMOND_PICKAXE,
                        Material.DIAMOND_PICKAXE,
                        Material.DIAMOND_PICKAXE,
                        Material.DIAMOND_PICKAXE,
                        Material.DIAMOND_PICKAXE,
                        Material.DIAMOND_PICKAXE,
                        Material.DIAMOND_PICKAXE,
                        Material.DIAMOND_PICKAXE,
                        Material.DIAMOND_PICKAXE,
                        Material.DIAMOND_PICKAXE,
                        Material.DIAMOND_PICKAXE,
                        Material.DIAMOND_PICKAXE,
                        Material.DIAMOND_PICKAXE,
                        Material.DIAMOND_PICKAXE,

                        Material.DIAMOND_SWORD,
                        Material.DIAMOND_SWORD,
                        Material.DIAMOND_SWORD,
                        Material.DIAMOND_SWORD,
                        Material.DIAMOND_SWORD,
                        Material.DIAMOND_SWORD,
                        Material.DIAMOND_SWORD,
                        Material.DIAMOND_SWORD,
                        Material.DIAMOND_SWORD,
                        Material.DIAMOND_SWORD,
                        Material.DIAMOND_SWORD,

                        Material.POTION,
                        Material.POTION,
                        Material.POTION,
                        Material.POTION,
                        Material.POTION,
                        Material.POTION,
                        Material.POTION,
                        Material.POTION,
                        Material.POTION,

                        Material.STONE_HOE,


                        Material.DIAMOND_BLOCK,
                        Material.DIAMOND_BLOCK,
                        Material.DIAMOND_BLOCK,
                        Material.DIAMOND_BLOCK,
                        Material.DIAMOND_BLOCK,
                        Material.DIAMOND_BLOCK,
                        Material.DIAMOND_BLOCK,
                        Material.DIAMOND_BLOCK,

                        Material.ENDER_PEARL,
                        Material.ENDER_PEARL,
                        Material.ENDER_PEARL,
                        Material.ENDER_PEARL,


                        Material.WOOD,
                        Material.WOOD,
                        Material.WOOD,
                        Material.WOOD,
                        Material.WOOD,
                        Material.WOOD,
                        Material.WOOD,
                        Material.WOOD,
                        Material.WOOD,
                        Material.WOOD,
                        Material.WOOD,
                        Material.WOOD,
                        Material.WOOD,
                        Material.WOOD,
                        Material.WOOD,
                        Material.WOOD,
                        Material.WOOD,
                        Material.WOOD,
                        Material.WOOD,
                        Material.WOOD,


                        Material.EXP_BOTTLE,
                        Material.EXP_BOTTLE,
                        Material.EXP_BOTTLE,
                        Material.EXP_BOTTLE,
                        Material.EXP_BOTTLE,
                        Material.EXP_BOTTLE,
                        Material.EXP_BOTTLE,
                        Material.EXP_BOTTLE,
                        Material.EXP_BOTTLE,
                        Material.EXP_BOTTLE,
                        Material.EXP_BOTTLE,
                        Material.EXP_BOTTLE,
                        Material.EXP_BOTTLE,
                        Material.EXP_BOTTLE,
                };

                Chest chest = (Chest) b;
                int randomNumber = (int) (Math.random() * 15 + 8);
                for (int i = 0; i < randomNumber; i++) {
                    int intRandom = (int) (Math.random() * 26 + 0);
                    int intItems = (int) (Math.random() * randomItems.length + 0);
                    int randomAmount = (int) (Math.random() * 13 + 5);

                    Material newitem = randomItems[intItems];
                    if (newitem.equals(Material.STONE_BUTTON)) {
                        randomAmount = 1;

                        ItemStack item = new ItemStack(newitem, 1);
                        ItemMeta im = item.getItemMeta();
                        im.setDisplayName("KReFTS HAIRY FRECKLE");
                        item.setItemMeta(im);
                        inv.setItem(intRandom, item);
                    } else if (newitem == Material.STONE_HOE ) {
                        ItemStack item = new ItemStack(newitem, 1);
                        ItemMeta im = item.getItemMeta();
                        im.setDisplayName("Don't keep em, just chuck em");
                        im.addEnchant(Enchantment.KNOCKBACK, 4, true);
                        item.setItemMeta(im);
                        inv.setItem(intRandom, item);
                    } else if (newitem == Material.POTION) {
                        Potion potion = new Potion(PotionType.SPEED,2);
                        potion.setSplash(true);
                        ItemStack iStack = new ItemStack(Material.POTION);
                        potion.apply(iStack);
                        inv.setItem(intRandom, iStack);
                    } else if (newitem == Material.DIAMOND_SWORD) {
                        ItemStack iStack = new ItemStack(Material.DIAMOND_SWORD);
                        iStack.addEnchantment(Enchantment.DAMAGE_ALL, 1);
                        inv.setItem(intRandom, iStack);
                    } else if (newitem == Material.DIAMOND_PICKAXE ||
                            newitem == Material.DIAMOND_BOOTS ||
                            newitem == Material.DIAMOND_HELMET ||
                            newitem == Material.DIAMOND_CHESTPLATE ||
                            newitem == Material.DIAMOND_LEGGINGS ||
                            newitem == Material.ENDER_PEARL ||
                            newitem == Material.DIAMOND_BLOCK ||
                            newitem == Material.TNT) {
                        randomAmount = 1;
                        ItemStack item = new ItemStack(newitem, randomAmount);

                        inv.setItem(intRandom, item);
                    } else {
                        ItemStack item = new ItemStack(newitem, randomAmount);

                        inv.setItem(intRandom, item);
                    }
                }
            }
        }
    }

    private void addFirstRefillItems() {
        GameManager.getGame().sendTitleMessage("Chests have been refilled!", "yellow");
        for (BlockState b : chestList) {
            if (b != null) {
                Inventory inv = ((Chest) b).getBlockInventory();

                Material[] randomItems = {
                        Material.STONE_BUTTON,

                        Material.TNT,
                        Material.TNT,
                        Material.TNT,
                        Material.TNT,
                        Material.TNT,
                        Material.TNT,

                        Material.IRON_CHESTPLATE,
                        Material.IRON_CHESTPLATE,
                        Material.IRON_CHESTPLATE,
                        Material.IRON_CHESTPLATE,
                        Material.IRON_CHESTPLATE,
                        Material.IRON_CHESTPLATE,
                        Material.IRON_CHESTPLATE,
                        Material.IRON_CHESTPLATE,

                        Material.IRON_HELMET,
                        Material.IRON_HELMET,
                        Material.IRON_HELMET,
                        Material.IRON_HELMET,
                        Material.IRON_HELMET,
                        Material.IRON_HELMET,
                        Material.IRON_HELMET,
                        Material.IRON_HELMET,

                        Material.IRON_LEGGINGS,
                        Material.IRON_LEGGINGS,
                        Material.IRON_LEGGINGS,
                        Material.IRON_LEGGINGS,
                        Material.IRON_LEGGINGS,
                        Material.IRON_LEGGINGS,
                        Material.IRON_LEGGINGS,

                        Material.IRON_BOOTS,
                        Material.IRON_BOOTS,
                        Material.IRON_BOOTS,
                        Material.IRON_BOOTS,
                        Material.IRON_BOOTS,
                        Material.IRON_BOOTS,
                        Material.IRON_BOOTS,
                        Material.IRON_BOOTS,
                        Material.IRON_BOOTS,
                        Material.IRON_BOOTS,

                        Material.GOLDEN_APPLE,
                        Material.GOLDEN_APPLE,
                        Material.GOLDEN_APPLE,
                        Material.GOLDEN_APPLE,
                        Material.GOLDEN_APPLE,
                        Material.GOLDEN_APPLE,
                        Material.GOLDEN_APPLE,
                        Material.GOLDEN_APPLE,
                        Material.GOLDEN_APPLE,

                        Material.DIAMOND_PICKAXE,
                        Material.DIAMOND_PICKAXE,
                        Material.DIAMOND_PICKAXE,
                        Material.DIAMOND_PICKAXE,
                        Material.DIAMOND_PICKAXE,
                        Material.DIAMOND_PICKAXE,
                        Material.DIAMOND_PICKAXE,
                        Material.DIAMOND_PICKAXE,
                        Material.DIAMOND_PICKAXE,
                        Material.DIAMOND_PICKAXE,
                        Material.DIAMOND_PICKAXE,
                        Material.DIAMOND_PICKAXE,
                        Material.DIAMOND_PICKAXE,
                        Material.DIAMOND_PICKAXE,
                        Material.DIAMOND_PICKAXE,

                        Material.DIAMOND_SWORD,
                        Material.DIAMOND_SWORD,
                        Material.DIAMOND_SWORD,
                        Material.DIAMOND_SWORD,
                        Material.DIAMOND_SWORD,
                        Material.DIAMOND_SWORD,

                        Material.STONE_HOE,
                        Material.STONE_HOE,

                        Material.DIAMOND_BLOCK,
                        Material.DIAMOND_BLOCK,
                        Material.DIAMOND_BLOCK,
                        Material.DIAMOND_BLOCK,

                        Material.ENDER_PEARL,

                        Material.WOOD,
                        Material.WOOD,
                        Material.WOOD,
                        Material.WOOD,
                        Material.WOOD,
                        Material.WOOD,
                        Material.WOOD,
                        Material.WOOD,
                        Material.WOOD,
                        Material.WOOD,
                        Material.WOOD,
                        Material.WOOD,
                        Material.WOOD,
                        Material.WOOD,
                        Material.WOOD,
                        Material.WOOD,
                        Material.WOOD,
                        Material.WOOD,
                        Material.WOOD,
                        Material.WOOD,


                        Material.EXP_BOTTLE,
                        Material.EXP_BOTTLE,
                        Material.EXP_BOTTLE,
                        Material.EXP_BOTTLE,
                        Material.EXP_BOTTLE,
                        Material.EXP_BOTTLE,
                        Material.EXP_BOTTLE,
                        Material.EXP_BOTTLE,
                        Material.EXP_BOTTLE,
                        Material.EXP_BOTTLE,
                        Material.EXP_BOTTLE,
                        Material.EXP_BOTTLE,
                        Material.EXP_BOTTLE,
                        Material.EXP_BOTTLE,
                };

                Chest chest = (Chest) b;
                int randomNumber = (int) (Math.random() * 11 + 5);
                for (int i = 0; i < randomNumber; i++) {
                    int intRandom = (int) (Math.random() * 26 + 0);
                    int intItems = (int) (Math.random() * randomItems.length + 0);
                    int randomAmount = (int) (Math.random() * 12 + 1);

                    Material newitem = randomItems[intItems];
                    if (newitem.equals(Material.STONE_BUTTON)) {
                        randomAmount = 1;

                        ItemStack item = new ItemStack(newitem, 1);
                        ItemMeta im = item.getItemMeta();
                        im.setDisplayName("KReFTS HAIRY FRECKLE");
                        item.setItemMeta(im);
                        inv.setItem(intRandom, item);
                    } else if (newitem == Material.STONE_HOE ) {
                        ItemStack item = new ItemStack(newitem, 1);
                        ItemMeta im = item.getItemMeta();
                        im.setDisplayName("Don't keep em, just chuck em");
                        im.addEnchant(Enchantment.KNOCKBACK, 2, true);
                        item.setItemMeta(im);
                        inv.setItem(intRandom, item);
                    } else if (newitem == Material.DIAMOND_PICKAXE ||
                            newitem == Material.IRON_BOOTS ||
                            newitem == Material.IRON_HELMET ||
                            newitem == Material.IRON_CHESTPLATE ||
                            newitem == Material.IRON_LEGGINGS ||
                            newitem == Material.ENDER_PEARL ||
                            newitem == Material.DIAMOND_BLOCK ||
                            newitem == Material.DIAMOND_SWORD ||
                            newitem == Material.TNT) {
                        randomAmount = 1;
                        ItemStack item = new ItemStack(newitem, randomAmount);

                        inv.setItem(intRandom, item);
                    } else {
                        ItemStack item = new ItemStack(newitem, randomAmount);

                        inv.setItem(intRandom, item);
                    }
                }
            } else {
                chestList.remove(b);
            }
        }
    }

    private void addNormalChestItems() {
        for (Chunk c : world.getLoadedChunks()) {
            for (BlockState b : c.getTileEntities()) {
                if (b instanceof Chest) {

                    chestList.add(b);

                    //sendMessage("FILLED: " + b.getLocation());
                    Inventory inv = ((Chest) b).getBlockInventory();

                    Material[] randomItems = {
                            Material.STONE_BUTTON,

                            Material.STONE_PICKAXE,
                            Material.STONE_PICKAXE,
                            Material.STONE_PICKAXE,
                            Material.STONE_PICKAXE,
                            Material.STONE_PICKAXE,
                            Material.STONE_PICKAXE,
                            Material.STONE_PICKAXE,
                            Material.STONE_PICKAXE,
                            Material.STONE_PICKAXE,
                            Material.STONE_PICKAXE,

                            Material.DIAMOND_PICKAXE,
                            Material.DIAMOND_PICKAXE,

                            Material.APPLE,
                            Material.APPLE,
                            Material.APPLE,
                            Material.APPLE,
                            Material.APPLE,
                            Material.APPLE,
                            Material.APPLE,
                            Material.APPLE,
                            Material.APPLE,

                            Material.FISHING_ROD,
                            Material.FISHING_ROD,
                            Material.FISHING_ROD,
                            Material.FISHING_ROD,
                            Material.FISHING_ROD,

                            Material.GOLDEN_APPLE,

                            Material.IRON_CHESTPLATE,
                            Material.IRON_CHESTPLATE,
                            Material.IRON_CHESTPLATE,

                            Material.BOW,
                            Material.BOW,
                            Material.BOW,
                            Material.BOW,

                            Material.COOKED_BEEF,
                            Material.COOKED_BEEF,
                            Material.COOKED_BEEF,
                            Material.COOKED_BEEF,
                            Material.COOKED_BEEF,
                            Material.COOKED_BEEF,
                            Material.COOKED_BEEF,
                            Material.COOKED_BEEF,
                            Material.COOKED_BEEF,
                            Material.COOKED_BEEF,
                            Material.COOKED_BEEF,
                            Material.COOKED_BEEF,
                            Material.COOKED_BEEF,
                            Material.COOKED_BEEF,
                            Material.COOKED_BEEF,

                            Material.WOOD,
                            Material.WOOD,
                            Material.WOOD,
                            Material.WOOD,
                            Material.WOOD,
                            Material.WOOD,
                            Material.WOOD,
                            Material.WOOD,
                            Material.WOOD,
                            Material.WOOD,
                            Material.WOOD,
                            Material.WOOD,
                            Material.WOOD,
                            Material.WOOD,
                            Material.WOOD,
                            Material.WOOD,
                            Material.WOOD,
                            Material.WOOD,

                            Material.COBBLESTONE,
                            Material.COBBLESTONE,
                            Material.COBBLESTONE,
                            Material.COBBLESTONE,
                            Material.COBBLESTONE,
                            Material.COBBLESTONE,
                            Material.COBBLESTONE,
                            Material.COBBLESTONE,
                            Material.COBBLESTONE,
                            Material.COBBLESTONE,
                            Material.COBBLESTONE,
                            Material.COBBLESTONE,
                            Material.COBBLESTONE,
                            Material.COBBLESTONE,
                            Material.COBBLESTONE,
                            Material.COBBLESTONE,

                            Material.STRING,
                            Material.STRING,
                            Material.STRING,

                            Material.ARROW,
                            Material.ARROW,
                            Material.ARROW,
                            Material.ARROW,
                            Material.ARROW,
                            Material.ARROW,
                            Material.ARROW,
                            Material.ARROW,
                            Material.ARROW,
                            Material.ARROW,
                            Material.ARROW,
                            Material.ARROW,
                            Material.ARROW,
                            Material.ARROW,

                            Material.STICK,
                            Material.STICK,
                            Material.STICK,
                            Material.STICK,
                            Material.STICK,
                            Material.STICK};

                    Chest chest = (Chest) b;
                    int randomNumber = (int) (Math.random() * 9 + 3);
                    for (int i = 0; i < randomNumber; i++) {
                        int intRandom = (int) (Math.random() * 26 + 0);
                        int intItems = (int) (Math.random() * randomItems.length + 0);
                        int randomAmount = (int) (Math.random() * 8 + 1);

                        Material newitem = randomItems[intItems];
                        if (newitem.equals(Material.STONE_BUTTON)) {
                            randomAmount = 1;

                            ItemStack item = new ItemStack(newitem, 1);
                            ItemMeta im = item.getItemMeta();
                            im.setDisplayName("KReFTS HAIRY FRECKLE");
                            item.setItemMeta(im);
                            inv.setItem(intRandom, item);
                        } else if (newitem == Material.DIAMOND_PICKAXE ||
                                newitem == Material.GOLDEN_APPLE ||
                                newitem == Material.FISHING_ROD ||
                                newitem == Material.IRON_CHESTPLATE ||
                                newitem == Material.STONE_PICKAXE ||
                                newitem == Material.ENDER_PEARL ||
                                newitem == Material.BOW) {
                            randomAmount = 1;
                            ItemStack item = new ItemStack(newitem, randomAmount);

                            inv.setItem(intRandom, item);
                        } else {
                            ItemStack item = new ItemStack(newitem, randomAmount);

                            inv.setItem(intRandom, item);
                        }
                    }
                }
            }
        }
    }

    private void kickAllPlayers() {
        for (Player p : Bukkit.getServer().getOnlinePlayers()) {
            p.kickPlayer(ChatColor.RED + "GameServer restarting! If you would like to play again, join back in a few seconds!" /*TODO Say who won*/);
        }
    }

    public void addBluePoints(int points) {

        this.bluePoints = bluePoints + points;
        int oldScore = bluePoints - points;

        Bukkit.getServer().getScoreboardManager().getMainScoreboard().resetScores("Blue Score: " + ChatColor.BLUE + "0");
        Bukkit.getServer().getScoreboardManager().getMainScoreboard().resetScores("Blue Score: " + ChatColor.BLUE + oldScore);
        this.line7 = objective.getScore("Blue Score: " + ChatColor.BLUE + bluePoints);
        line7.setScore(1);

    }

    public void addRedPoints(int points) {

        this.redPoints = redPoints + points;
        int oldScore = redPoints - points;

        Bukkit.getServer().getScoreboardManager().getMainScoreboard().resetScores("Red Score: " + ChatColor.BLUE + "0");
        Bukkit.getServer().getScoreboardManager().getMainScoreboard().resetScores("Red Score: " + ChatColor.RED + oldScore);
        this.line6 = objective.getScore("Red Score: " + ChatColor.RED + redPoints);
        line6.setScore(2);

    }

    public void setRedCarrier(Player p) {
        this.redCarrier = p == null ? null : p.getUniqueId();
    }

    public void setBlueCarrier(Player p) {
        this.blueCarrier = p == null ? null : p.getUniqueId();
    }

    public Player getBlueCarrier() {
        return Bukkit.getPlayer(this.blueCarrier);
    }

    public boolean isBlueCarrier(Player p) {
        return this.blueCarrier.equals(p.getUniqueId());
    }

    public boolean isRedCarrier(Player p) {
        return this.redCarrier.equals(p.getUniqueId());
    }

    public boolean isCarrier(Player p) {
        if (this.blueCarrier.equals(p.getUniqueId()) || this.redCarrier.equals(p.getUniqueId())) {
            return true;
        } else {
            return false;
        }
    }

    public Player getRedCarrier() {
        return Bukkit.getPlayer(this.redCarrier);
    }

    public Game getGame() {
        return this;
    }

    public void setState(GameState state) {
        this.gameState = state;
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }

    public List<String> getRedTeam() {
        return redTeam;
    }

    public List<String> getBlueTeam() {
        return blueTeam;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public ArrayList<Player> getSpectators() {
        return spectators;
    }

    public int getMinPlayers() {
        return minPlayers;
    }

    public boolean isTeamGame() {
        return isTeamGame;
    }

    public String getDisplayName() {
        return displayName;
    }

    public GameState getGameState() {
        return gameState;
    }

    public boolean isState(GameState state) {
        return getGameState() == state;
    }

    public boolean isRedFlagDropped() {
        return redFlagDropped;
    }

    public boolean isBlueFlagDropped() {
        return blueFlagDropped;
    }

    public void sendMessage(String message) {
        for (Player player : getPlayers()) {
            player.sendMessage(message);
        }
    }

    public void sendBlueMessage(String message) {
        for (String s : getBlueTeam()) {
            Player p = Bukkit.getPlayer(s);
            p.sendMessage(message);
        }
    }

    public void sendRedMessage(String message) {
        for (String s : getRedTeam()) {
            Player p = Bukkit.getPlayer(s);
            p.sendMessage(message);
        }
    }

    public void setGameType(GameType type) {

        gameType = type;
        return;
    }

    public enum GameState {
        LOBBY, STARTING, GRACE, ACTIVE, DEATHMATCH, ENDING
    }

    public enum TeamType {
        RED, BLUE
    }

    public enum GameType {
        GODGAME, NORMAL, EASY
    }

    public enum BlueFlagState {
        DROPPED, SAFE, STOLEN
    }

    public enum RedFlagState {
        DROPPED, SAFE, STOLEN
    }


}
