package com.github.kraftlegos.commands;

import com.github.kraftlegos.managers.GameManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 * Created by kgils on 4/3/2017.
 */
public class End implements CommandExecutor {

    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (commandSender.hasPermission("skyflag.end")) {

            GameManager.getGame().end();
            commandSender.sendMessage(ChatColor.GREEN + "Ending game...");
            return true;

        } else {
            commandSender.sendMessage(ChatColor.RED + "You do not have permission to do that!");
            return true;
        }
    }
}
