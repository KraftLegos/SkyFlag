package com.github.kraftlegos.commands;

import com.github.kraftlegos.managers.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created by Kraft on 4/9/2017.
 */

public class Shout implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Player p = (Player) sender;
        if (!GameManager.getGame().players.contains(p)) {
            p.sendMessage(ChatColor.RED + "You must be a member of the game to use that command!");
            return false;
        }
        if (args.length == 0)
            // Premature return instead of using if-else
            // to reduce cyclomatic complexity.
            return false;
        if (args.length == 1) {
            // fast path
            Bukkit.getServer().broadcastMessage(ChatColor.GOLD + "[SHOUT] " + ChatColor.valueOf(GameManager.getGame().getPlayerTeams().get(p.getUniqueId()).name()) + "["  + GameManager.getGame().getPlayerTeams().get(p.getUniqueId()).name() + "] " + p.getCustomName() + ": " + ChatColor.GRAY + args[0]);
            return false;
        }

        StringBuilder builder = new StringBuilder();
        for(int i = 0; i < args.length; i++) {
            builder.append(args[i] + " ");
        }

        String msg = builder.toString();
        Bukkit.getServer().broadcastMessage(ChatColor.GOLD + "[SHOUT] " + ChatColor.valueOf(GameManager.getGame().getPlayerTeams().get(p.getUniqueId()).name()) + "["  + GameManager.getGame().getPlayerTeams().get(p.getUniqueId()).name() + "] " + p.getCustomName() + ": " + ChatColor.GRAY + msg);


        return true;
    }
}
