package com.github.kraftlegos.commands;

import com.github.kraftlegos.managers.GameManager;
import com.github.kraftlegos.object.Game;
import com.github.kraftlegos.utility.StartCountdown;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ForceStart implements CommandExecutor {

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender.hasPermission("skyflag.forcestart")) {
            Bukkit.getScheduler().cancelAllTasks();

            //Bukkit.getServer().getScoreboardManager().getMainScoreboard().resetScores(time + "s until start!");
            //Bukkit.getServer().getScoreboardManager().getMainScoreboard().resetScores("Waiting...");
            GameManager.getGame().setState(Game.GameState.STARTING);
            Bukkit.getServer().broadcastMessage(ChatColor.GREEN + "Minimum players reached! The game will now start in 30 seconds...");
            for (Player pl : GameManager.getGame().players) {
                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "title " + pl.getName() + " title {\"text\":\"30s\",\"color\":\"red\"}"); //JSON formatting is invalid!
                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "title " + pl.getName() + " times 0 20 0");
                pl.playSound(pl.getLocation(), Sound.NOTE_PLING, 1.0F, 0.6F);
            }
            GameManager.getGame().startCount();
        }
        return true;
    }
}
