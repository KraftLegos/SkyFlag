package com.github.kraftlegos.listeners;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class onJoin implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        //e.getPlayer().setCustomName(ChatColor.GRAY + e.getPlayer().getName());
    }
}
