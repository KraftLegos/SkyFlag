package com.github.kraftlegos.listeners;

import com.github.kraftlegos.managers.GameManager;
import com.github.kraftlegos.object.GamePlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.scoreboard.*;

public class OnJoin implements Listener {

    @SuppressWarnings("unused")

    public Scoreboard board = Bukkit.getServer().getScoreboardManager().getMainScoreboard();
    public Team redTeam;
    public Team blueTeam;
    public Team spectator;

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        GamePlayer g = new GamePlayer(p);

        GameManager.getGame().joinGame(g);
        e.setJoinMessage(null);

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

        this.board = Bukkit.getServer().getScoreboardManager().getMainScoreboard();
        this.spectator = board.getTeam("SPEC");
        this.redTeam = board.getTeam("RED");
        this.blueTeam = board.getTeam("BLUE");

        spectator.setPrefix("§7");
        spectator.addEntry(p.getName());
        spectator.setCanSeeFriendlyInvisibles(true);

        board.getObjective("line").setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + "SKYFLAG");
        board.getObjective("line").setDisplaySlot(DisplaySlot.SIDEBAR);


        for (Player online : Bukkit.getOnlinePlayers()) {
            online.setScoreboard(board);
        }

        p.getInventory().clear();
        p.getInventory().setArmorContents(null);


    }

    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent e) {
    }

    @EventHandler
    public void onServerPing(ServerListPingEvent event) {
        event.setMotd(GameManager.getGame().getGameState().toString());
    }
}
