package com.github.kraftlegos.commands;

import com.github.kraftlegos.managers.GameManager;
import com.github.kraftlegos.utility.StartCountdown;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ForceStart implements CommandExecutor {

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender.hasPermission("skyflag.forcestart")) {
            Bukkit.getScheduler().cancelAllTasks();

            int time = StartCountdown.timeUntilStart - 1;
            Bukkit.getServer().getScoreboardManager().getMainScoreboard().resetScores(StartCountdown.timeUntilStart + "s until start!");
            Bukkit.getServer().getScoreboardManager().getMainScoreboard().resetScores(time + "s until start!");
            Bukkit.getServer().getScoreboardManager().getMainScoreboard().resetScores("Waiting...");
            GameManager.getGame().startGame();
        }
        return true;
    }
}
