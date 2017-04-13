package com.github.kraftlegos.listeners;

import com.github.kraftlegos.managers.GameManager;
import com.github.kraftlegos.object.Game;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

/**
 * Created by Kraft on 4/3/2017.
 */
public class OnMove implements Listener {

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        if (GameManager.getGame().isState(Game.GameState.ENDING) || GameManager.getGame().isState(Game.GameState.STARTING) || GameManager.getGame().isState(Game.GameState.LOBBY)) {
            if (e.getPlayer().getLocation().getBlockY() <= 0) {
                e.getPlayer().teleport(GameManager.getGame().lobbyPoint);
            }
        } else {
            if (e.getPlayer().getLocation().getBlockY() <= 0) {
                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "kill " + e.getPlayer().getName());
            }
        }
    }
}
