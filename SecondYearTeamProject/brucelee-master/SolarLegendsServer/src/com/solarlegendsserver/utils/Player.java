package com.solarlegendsserver.utils;

import com.solarlegendsserver.SolarLegendsServer;

/**
 * A data class for storing information about the player. The id represents whether the player is hosting
 * or joining (0 means host, 1 join).
 *
 * @author Robert Chiper
 */
public class Player {
    public int id;
    public String username;
    public int wins;
    public int loses;
    public int draws;

    /**
     * Default constructor
     */
    public Player() {

    }

    /**
     * The default constructor is used when the player is not found in the leaderboard
     * table of the database. it initializes everything to 0.
     */
    public Player(String username) {
        this.username = username;
        wins = 0;
        loses = 0;
        draws = 0;
    }

    /**
     * Updates the database entry of the player with a win.
     */
    public void addWin() {
        if (SolarLegendsServer.DATABASE) {
            SolarLegendsServer.databaseManager.updatePlayer(username, 1);
        }
    }

    /**
     * Updates the database entry of the player with a draw.
     */
    public void addDraw() {
        if (SolarLegendsServer.DATABASE) {
            SolarLegendsServer.databaseManager.updatePlayer(username, 0);
        }
    }

    /**
     * Updates the database entry of the player with a loss.
     */
    public void addLoss() {
        if (SolarLegendsServer.DATABASE) {
            SolarLegendsServer.databaseManager.updatePlayer(username, -1);
        }
    }
}
