package com.github.kraftlegos.utility;

import com.github.kraftlegos.listeners.OnDeath;
import com.github.kraftlegos.listeners.OnPlayerDamage;
import com.github.kraftlegos.managers.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

/**
 * Created by kgils on 3/27/2017.
 */
public class RemoveLastDamager implements Runnable{
    private static int timeUntilStart;

    public void run() {

        timeUntilStart = 20;
        for (; timeUntilStart >= 0; timeUntilStart--) {
            if (timeUntilStart == 0) {
                OnPlayerDamage.lastDamager.remove("");
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
