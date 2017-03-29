package com.github.kraftlegos.commands;

import com.github.kraftlegos.managers.GameManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ForceStart implements CommandExecutor {

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender.hasPermission("skyflag.forcestart")) {
            GameManager.getGame().startGame();
        }
        return true;
    }
}
