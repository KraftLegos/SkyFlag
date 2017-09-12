package com.github.kraftlegos.utility;

import com.github.kraftlegos.managers.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;

/**
 * Created by Kraft on 4/4/2017.
 */
public class DeathMatchCountdown implements Runnable {
    public void run() {

        int timeUntilStart = 300;
        for (; timeUntilStart >= 0; timeUntilStart--) {

            Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
            int time = timeUntilStart + 1;

            scoreboard.resetScores("Deathmatch: " + ChatColor.YELLOW + "0s");
            scoreboard.resetScores("Deathmatch: " + ChatColor.YELLOW + time + "s");
            GameManager.getGame().line4 = GameManager.getGame().objective.getScore("Deathmatch: " + ChatColor.YELLOW + timeUntilStart + "s");
            GameManager.getGame().line4.setScore(4);

            if (timeUntilStart == 0) {
                //GameManager.getGame().sendMessage(ChatColor.GREEN + "DEBUG: STARTED");
                GameManager.getGame().sendMessage(ChatColor.GREEN + "All chests have been reset!");
                scoreboard.resetScores("Deathmatch: " + ChatColor.YELLOW + "0s");
                GameManager.getGame().line4.setScore(4);
                GameManager.getGame().deathMatch();
                break;

            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
                Bukkit.getLogger().severe("GAME CRASHED: READ ERROR ABOVE!");
                for (Player player : GameManager.getGame().players) {
                    player.kickPlayer("A fatal error occured, please report this to Kraft!");
                }
            }
        }
    }
}
