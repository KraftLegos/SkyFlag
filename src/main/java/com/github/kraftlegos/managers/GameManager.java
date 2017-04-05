package com.github.kraftlegos.managers;

import com.github.kraftlegos.object.Game;
import org.bukkit.scoreboard.Scoreboard;

import java.util.ArrayList;

public class GameManager {

    private static Game game;

    public static Game getGame() {
        return game;
    }

    public static void setGame(Game game) {
        GameManager.game = game;
    }
}
