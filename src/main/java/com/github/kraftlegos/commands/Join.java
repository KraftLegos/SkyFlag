package com.github.kraftlegos.commands;

import com.github.kraftlegos.Main;
import com.github.kraftlegos.managers.GameManager;
import com.github.kraftlegos.object.Game;
import com.github.kraftlegos.object.GamePlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.IOException;

public class Join implements CommandExecutor {

    private int players;
    
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage("You must be a player to run that command!");
            return true;
        }

        //if (players < 16) {
        //    sender.sendMessage(ChatColor.RED + "This minigame is currently full! Please try again later!");
        //}
        Player p = (Player) sender;
        GamePlayer g = new GamePlayer(p);

        GameManager.getGame().joinGame(g);

        //Bukkit.getServer().broadcastMessage(p.getCustomName() + ChatColor.YELLOW + " joined! (" + ChatColor.GREEN + players++ + "/16" + ChatColor.YELLOW + ")");
        return true;
    }
}
