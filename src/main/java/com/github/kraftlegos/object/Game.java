package com.github.kraftlegos.object;

import com.github.kraftlegos.managers.GameManager;
import com.github.kraftlegos.utility.StartCountdown;
import com.github.kraftlegos.utility.StartGraceCountdown;
import net.minecraft.server.v1_8_R3.PacketPlayOutScoreboardTeam;
import net.minecraft.server.v1_8_R3.ScoreboardTeam;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@SuppressWarnings("unused")
public class Game {

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

    private List<String> redTeam = new ArrayList<>();
    private List<String> blueTeam = new ArrayList<>();

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

    public Player redCarrier;
    public Player blueCarrier;

    public Game(String gameName) {
        //FileConfiguration fileConfiguration = DataHandler.getInstance().getGameInfo();
        this.displayName = gameName;
        this.maxPlayers = 16;
        this.minPlayers = 2;
        this.world = Bukkit.getServer().getWorld("world");
        this.isTeamGame = true;

        double redx = 0.5;
        double redy = 79;
        double redz = 484.5;

        double bluex = -91.5;
        double bluey = 79;
        double bluez = 553.5;

        double lobbyx = -48.5;
        double lobbyy = 153.0;
        double lobbyz = 596.5;
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

            this.line6 = objective.getScore("Kills:" + ChatColor.GREEN + "0");
            line6.setScore(2);

            this.line7 = objective.getScore("Deaths:" + ChatColor.RED + "0");
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

    public void setRedCarrier (Player p) {
        this.redCarrier = p;
    }

    public void setBlueCarrier (Player p) {
        this.blueCarrier = p;
    }

    public Player getBlueCarrier () {
        return this.blueCarrier;
    }

    public Player getRedCarrier () {
        return this.redCarrier;
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

    public void sendMessage(String message) {
        for (Player player : getPlayers()) {
            player.sendMessage(message);
        }
    }

    public enum GameState {
        LOBBY, STARTING, GRACE, ACTIVE, DEATHMATCH, ENDING
    }

    public enum TeamType {
        RED, BLUE
    }

}
