package com.github.kraftlegos.managers;

import com.github.kraftlegos.object.Game;

import java.util.ArrayList;

public class GameManager {

    private static ArrayList<Game> games = new ArrayList<>();

    public static void addGame(Game game) {
        games.add(game);
    }

    public static void removeGame(Game game) {
        games.remove(game);
    }

    public static ArrayList<Game> getGames() {
        return games;
    }

    public static Game getGame() {
        for(Game game : games) {
            if (game != null) {
                return game;
            }

        }
        return null;
    }

}
