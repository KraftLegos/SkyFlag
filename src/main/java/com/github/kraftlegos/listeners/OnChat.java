package com.github.kraftlegos.listeners;

import com.github.kraftlegos.managers.GameManager;
import com.github.kraftlegos.object.Game;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerChatEvent;

/**
 * Created by Kraft on 4/9/2017.
 */
public class OnChat implements Listener {

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent e) {
        e.setCancelled(true);

        Player p = e.getPlayer();
        if (GameManager.getGame().isState(Game.GameState.STARTING) || GameManager.getGame().isState(Game.GameState.LOBBY)) {
            Bukkit.getServer().broadcastMessage(p.getCustomName() + ": " + ChatColor.GRAY + e.getMessage());
        } else if(GameManager.getGame().getBlueTeam().contains(p.getName())) {
            for (String s : GameManager.getGame().getBlueTeam()) {
                Player player = Bukkit.getServer().getPlayer(s);
                player.sendMessage(ChatColor.BLUE + "[TEAM] "  + p.getCustomName() + ": " + ChatColor.GRAY + e.getMessage());
            }
        } else if (GameManager.getGame().getRedTeam().contains(p.getName())) {
            for (String s : GameManager.getGame().getRedTeam()) {
                Player player = Bukkit.getServer().getPlayer(s);
                player.sendMessage(ChatColor.RED + "[TEAM] "  + p.getCustomName() + ": " + ChatColor.GRAY + e.getMessage());
            }
        } else {
            for (Player player : GameManager.getGame().spectators) {
                player.sendMessage(ChatColor.GRAY + "[SPECTATOR] " + p.getCustomName() + ": " + ChatColor.GRAY + e.getMessage());
            }
        }
    }
}
