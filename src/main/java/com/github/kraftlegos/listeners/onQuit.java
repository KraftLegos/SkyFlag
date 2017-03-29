package com.github.kraftlegos.listeners;

import com.github.kraftlegos.managers.GameManager;
import com.github.kraftlegos.object.Game;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class onQuit implements Listener {

    @EventHandler
    public void onPlayerDisconnect(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        Game game = GameManager.getGame();
        e.setQuitMessage(null);
        if (game.getGameState() == Game.GameState.LOBBY || game.getGameState() == Game.GameState.STARTING) {
            if (game.players.contains(p) || game != null) {
                game.players.remove(p);
                game.sendMessage(p.getCustomName() + ChatColor.YELLOW + " quit! (" + game.players.size() + "/" + game.getMaxPlayers() + ")");
                return;
            }
        } else {
            p.getLocation().getWorld().strikeLightningEffect(p.getLocation());
            game.sendMessage(p.getCustomName() + ChatColor.YELLOW + " was killed when they disconnected!");
        }
        //TODO Fix double leave messages
        //TODO Fix bugged message
    }
}
