package com.github.kraftlegos.utility;

import com.github.kraftlegos.listeners.OnDeath;
import com.github.kraftlegos.listeners.OnPlayerDamage;
import com.github.kraftlegos.managers.GameManager;
import org.bukkit.*;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

/**
 * Created by kgils on 4/2/2017.
 */
public class ReturnBlueFlag implements Runnable {
    private static int timeUntilStart;

    public void run() {

        timeUntilStart = 15;
        for (; timeUntilStart >= 0; timeUntilStart--) {


            OnDeath.blueFlagDropLocation.getWorld().playEffect(OnDeath.blueFlagDropLocation, Effect.STEP_SOUND, Material.REDSTONE_BLOCK);

            double x = OnDeath.blueFlagDropLocation.getX();
            double y = OnDeath.blueFlagDropLocation.getY();
            double z = OnDeath.blueFlagDropLocation.getZ();
            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "kill @e[type=ArmorStand,r=1,x=" + x + ",y=" + y + ",z=" + z + ",r=0]");
            createHologram(OnDeath.blueFlagDropLocation, ChatColor.RED + "" + timeUntilStart + "s");
            //if (timeUntilStart == 0 || OnDeath.blueFlagDropped == false) {
            //    break;
            //}

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

    public void createHologram(Location l, String name) {
        ArmorStand am = l.getWorld().spawn(l, ArmorStand.class);
        am.setArms(false);
        am.setGravity(false);
        am.setVisible(false);
        am.setCustomName(name);
        am.setCustomNameVisible(true);
    }
}
