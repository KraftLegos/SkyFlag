package com.github.kraftlegos.object;

import com.github.kraftlegos.Main;
import com.github.kraftlegos.utility.*;
import org.bukkit.*;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.*;

import java.util.*;
import java.util.logging.Logger;

@SuppressWarnings("unused")
public class Game {

    private Main plugin;
    public Game(Main instance) { plugin = instance; }

    //Active Game Objects
    public ArrayList<Player> players = new ArrayList<>();
    public ArrayList<Player> spectators = new ArrayList<>();
    private ArrayList<GameTeam> team = new ArrayList<>();
    private String displayName;
    private int maxPlayers;
    private int minPlayers;
    private World world;
    //private ArrayList<Location> spawnPoints = new ArrayList<>();
    private boolean isTeamGame;
    public Location lobbyPoint;
    private GameState gameState;
    public Location redSpawn;
    public Location blueSpawn;
    public HashMap<String, Integer> coins = new HashMap<>();
    public HashMap<String, Integer> killAmount = new HashMap<>();
    public HashMap<String, Integer> deathCount = new HashMap<>();
    public HashMap<String, Scoreboard> scoreboardManager = new HashMap<>();

    public int bluePoints;
    public int redPoints;

    public List<Chest> chestList;

    private List<String> redTeam = new ArrayList<>();
    private List<String> blueTeam = new ArrayList<>();

    private HashMap<UUID, TeamType> playerTeams = new HashMap<>();

    public HashMap<UUID, TeamType> getPlayerTeams() {
        return playerTeams;
    }

    private int timeUntilStart;

    public Scoreboard board = Bukkit.getServer().getScoreboardManager().getMainScoreboard();
    public Objective objective;
    public Score line1;
    public Score line2;
    public Score line3;
    public Score line4;
    public Score line5;
    public Score line6;
    public Score line7;
    public Team redScoreTeam = Bukkit.getServer().getScoreboardManager().getMainScoreboard().getTeam("RED");
    public Team blueScoreTeam = Bukkit.getServer().getScoreboardManager().getMainScoreboard().getTeam("BLUE");

    public UUID redCarrier;
    public UUID blueCarrier;

    public boolean redFlagDropped;
    public boolean blueFlagDropped;

    public Game(String gameName) {
        //FileConfiguration fileConfiguration = DataHandler.getInstance().getGameInfo();
        this.displayName = gameName;
        this.maxPlayers = 16;
        this.minPlayers = 13;
        this.world = Bukkit.getServer().getWorld("world");
        this.isTeamGame = true;

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
        Location blueSpawn = new Location(world, bluex, bluey, bluez);
        Location lobbySpawn = new Location(world, lobbyx, lobbyy, lobbyz);
        this.redSpawn = redSpawn;
        this.blueSpawn = blueSpawn;
        this.lobbyPoint = lobbySpawn;
    }

    public boolean joinGame(GamePlayer gamePlayer) {
        //if (gamePlayer.isTeamClass() && !isTeamGame) {
        //    gamePlayer.sendMessage("TEST");
        //    return false;
        //}

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
                return false;
            }

            if (getPlayers().size() == getMaxPlayers()) {
                p.sendMessage("&cThis game has already started! Please try again in a few minutes!");
                return false;
            }
            players.add(p);

            this.bluePoints = 0;
            this.redPoints = 0;

            deathCount.put(p.getName(), 0);
            killAmount.put(p.getName(), 0);
            coins.put(p.getName(), 0);

            this.objective = Bukkit.getServer().getScoreboardManager().getMainScoreboard().getObjective("line");

            this.line1 = objective.getScore(" ");
            line1.setScore(7);

            board.resetScores(ChatColor.GREEN + "Players: " + (players.size()-1) + "/" + getMaxPlayers());
            this.line2 = objective.getScore( ChatColor.GREEN + "Players: " + players.size() + "/" + getMaxPlayers());
            line2.setScore(6);

            this.line3 = objective.getScore("  ");
            line3.setScore(5);

            this.line5 = objective.getScore("   ");
            line5.setScore(3);

            this.line6 = objective.getScore("Red Score:" + ChatColor.RED + "0");
            line6.setScore(2);

            this.line7 = objective.getScore("Blue Score:" + ChatColor.BLUE + "0");
            line7.setScore(1);

            gamePlayer.teleport(lobbyPoint, gamePlayer);
            Bukkit.getServer().broadcastMessage(gamePlayer.getPlayer().getCustomName() + ChatColor.YELLOW + " joined! (" + getPlayers().size() + "/" + getMaxPlayers() + ")");

            if (getPlayers().size() == getMinPlayers() && !isState(GameState.STARTING)) {
                setState(GameState.STARTING);
                Bukkit.getServer().broadcastMessage(ChatColor.GREEN + "Minimum players reached! The game will now start in 30 seconds...");
                for (Player pl : players) {
                    Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "title " + pl.getName() +" title {\"text\":\"30s\",\"color\":\"red\"}"); //JSON formatting is invalid!
                    Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "title " + pl.getName() + " times 0 20 0");
                }
                startCount();
            } else {
                board.resetScores("Waiting...");
                this.line4 = objective.getScore("Waiting...");
                line4.setScore(4);
            }
        } else if (isState(gameState.ACTIVE) || isState(gameState.DEATHMATCH) || isState(gameState.ENDING) || isState(gameState.GRACE)) {
            gamePlayer.teleport(lobbyPoint, gamePlayer);
            p.sendMessage("You are now a spectator!");
            makeSpectator(p);
            return false;
        } else {
            p.sendMessage("You are already in the game!");
        }
        return true;
    }

    public void spawnDragon () {
        Bukkit.getServer().getWorld("world").spawnEntity(new Location(Bukkit.getServer().getWorld("world"), -45.5, 97, 523.5), EntityType.ENDER_DRAGON);
    }

    public void makeSpectator(Player p) {
        p.setAllowFlight(true);
        p.setFlying(true);
        spectators.add(p);
        p.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 15));
        for (Player pl : players) {
            pl.hidePlayer(p);
        }
    }

    public void startCount() {
        new Thread(new StartCountdown()).start();
    }

    public void startGame() {

        int i = 0;
        Scoreboard b = Bukkit.getServer().getScoreboardManager().getMainScoreboard();

        redScoreTeam = Bukkit.getServer().getScoreboardManager().getMainScoreboard().getTeam("RED");
        blueScoreTeam = Bukkit.getServer().getScoreboardManager().getMainScoreboard().getTeam("BLUE");
        setState(GameState.GRACE);
        startGracePeriod();
        addNormalChestItems();

        for (Player p : players) {

            //if(p.getGameMode() != GameMode.SURVIVAL) p.setGameMode(GameMode.SURVIVAL);

            if(i < players.size()/2) {
                addToTeam(TeamType.RED, p);

                //TODO PacketPlayOutScoreboardTeam packetPlayOutScoreboardTeam = new PacketPlayOutScoreboardTeam();

                redScoreTeam.addEntry(p.getName());
                redScoreTeam.setPrefix("§c[R] ");
                for (Player online : Bukkit.getOnlinePlayers()) {
                    online.setScoreboard(b);
                }
                p.teleport(redSpawn);
                p.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "Please wait while we move players! You will have a 2 minute grace period when the game begins!");
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
                p.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "Please wait while we move players! You will have a 2 minute grace period when the game begins!");
                p.sendMessage(ChatColor.BLUE + "You are now on the blue team!");
                p.playSound(p.getLocation(), Sound.NOTE_PLING, 1.0F, 1.200F);

            }
            i++;
        }
    }

    public void addToTeam(TeamType type, Player player) {
        playerTeams.put(player.getUniqueId(), type);
        switch (type) {
            case RED:
                redTeam.add(player.getName());
                break;
            case BLUE:
                blueTeam.add(player.getName());
                break;
        }
    }

    public void startGracePeriod() {
        new Thread(new StartGraceCountdown()).start();
    }

    public void startActive() {
        sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "The grace period has now ended! You can PvP!");
        setState(GameState.ACTIVE);
        new Thread(new ActiveCountdown()).start();
        //TODO 25m End
        //TODO 20m Deathmatch




    }

    public void fiveMinRefill() {

        new Thread(new FiveMinCountdown()).start();
        //TODO 5m Chest refill (Better)
    }

    public void tenMinRefill() {

        new Thread(new DeathMatchCountdown()).start();
        //TODO 10m Chest refill
    }

    public void deathMatch() {
        sendMessage(ChatColor.RED + "DEATHMATCH STARTED!");
        sendMessage(ChatColor.YELLOW + "+1 " + ChatColor.BLUE + "Dragon!");
        spawnDragon();
        new Thread(new EndCountdown()).start();
    }

    public void end () {

        if (bluePoints == redPoints) {
            //DRAW
            sendMessage("TODO: DRAW MESSAGE");
        } else if (bluePoints < redPoints) {
            //RED WON
            sendMessage("TODO: RED WON MESSAGE");
        } else if (bluePoints > redPoints) {
            //BLUE WON
            sendMessage("TODO: BLUE WON MESSAGE");

        }
        new Thread(new EndCounter()).start();
    }

    public void endGame () {
        new Thread(new EndCounter()).start();
        setState(GameState.ENDING);
        sendMessage(ChatColor.GREEN + "TODO: END MESSAGE!");
        sendMessage(ChatColor.GREEN + "THANK YOU FOR PLAYING " + ChatColor.BOLD + "" + ChatColor.ITALIC + "" + ChatColor.GOLD + "SkyFlag");
        sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Please report any bugs to Kraft on slack!");

    }

    public void setScoreboard (Player p) {
        String name = p.getName();
        if (scoreboardManager.containsKey(p.getName())) {
            scoreboardManager.remove(p.getName());
        }
        Scoreboard privateBoard = Bukkit.getServer().getScoreboardManager().getNewScoreboard();

        setScoreboardObjective(p, "line");
        p.setScoreboard(privateBoard);
        scoreboardManager.put(p.getName(), privateBoard);
    }

    public Scoreboard getScoreboard (Player p) {
        return scoreboardManager.get(p.getName());
    }

    public void setScoreboardObjective (Player p, String objectName) {
        if (scoreboardManager.get(p.getName()).getObjective(objectName) == null) {
            scoreboardManager.get(p.getName()).registerNewObjective(objectName, "dummy");
            scoreboardManager.get(p.getName()).getObjective("line").setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + "SKYFLAG");
            scoreboardManager.get(p.getName()).getObjective("line").setDisplaySlot(DisplaySlot.SIDEBAR);
        }
    }

    public void addNormalChestItems () {
        for (Chunk c : world.getLoadedChunks()) {
            for (BlockState b : c.getTileEntities()) {
                if (b instanceof Chest) {

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

                            Material.STICK,
                            Material.STICK,
                            Material.STICK,
                            Material.STICK,
                            Material.STICK,
                            Material.STICK};

                    Chest chest = (Chest) b;
                    int randomNumber = (int )(Math.random() * 9 + 3);
                    for (int i = 0; i < randomNumber; i++) {
                        int intRandom = (int)(Math.random() * 26 + 0);
                        int intItems = (int) (Math.random() * randomItems.length + 0);
                        int randomAmount = (int)(Math.random() * 8 + 1);

                        Material newitem = randomItems[intItems];
                        if (newitem.equals(Material.STONE_BUTTON)) {
                            randomAmount = 1;

                            ItemStack item = new ItemStack(newitem, 1);
                            ItemMeta im = item.getItemMeta();
                            im.setDisplayName("KReFTS HAIRY FRECKLE");
                            item.setItemMeta(im);
                            inv.setItem(intRandom, item);
                        }else if (newitem == Material.DIAMOND_PICKAXE ||
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

    public void kickAllPlayers () {
        for (Player p : Bukkit.getServer().getOnlinePlayers()) {
            p.kickPlayer(ChatColor.RED + "GameServer restarting! If you would like to play again, join back in a few seconds!" /*TODO Say who won*/);
        }
    }

    public void addBluePoints (int points) {

        this.bluePoints = bluePoints + points;
        int oldScore = bluePoints - points;

        Bukkit.getServer().getScoreboardManager().getMainScoreboard().resetScores("Blue Score:" + ChatColor.BLUE + oldScore);
        this.line7 = objective.getScore("Blue Score:" + ChatColor.BLUE + bluePoints);
        line7.setScore(1);

    }

    public void addRedPoints (int points) {

        this.redPoints = redPoints + points;
        int oldScore = redPoints - points;

        Bukkit.getServer().getScoreboardManager().getMainScoreboard().resetScores("Red Score:" + ChatColor.RED + oldScore);
        this.line6 = objective.getScore("Red Score:" + ChatColor.RED + redPoints);
        line6.setScore(2);

    }

    public void setRedCarrier (Player p) {
        this.redCarrier = p == null ? null : p.getUniqueId();
    }

    public void setBlueCarrier (Player p) {
        this.blueCarrier = p == null ? null : p.getUniqueId();
    }

    public Player getBlueCarrier () {
        return Bukkit.getPlayer(this.blueCarrier);
    }

    public boolean isBlueCarrier(Player p) {
        return this.blueCarrier.equals(p.getUniqueId());
    }

    public boolean isRedCarrier(Player p) {
        return this.redCarrier.equals(p.getUniqueId());
    }

    public boolean isCarrier(Player p) {
        if (this.blueCarrier.equals(p.getUniqueId()) || this.redCarrier.equals(p.getUniqueId())) { return true; } else {
            return false;
        }
    }

    public Player getRedCarrier () {
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

    public boolean isRedFlagDropped() {return redFlagDropped;}

    public boolean isBlueFlagDropped() {return blueFlagDropped;}

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

    public enum GameState {
        LOBBY, STARTING, GRACE, ACTIVE, DEATHMATCH, ENDING
    }

    public enum TeamType {
        RED, BLUE
    }

}
