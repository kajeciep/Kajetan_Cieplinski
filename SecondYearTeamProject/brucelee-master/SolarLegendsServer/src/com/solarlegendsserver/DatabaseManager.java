package com.solarlegendsserver;

import com.solarlegendsserver.utils.Player;

import java.sql.*;
import java.util.ArrayList;

/**
 * The class is responsible for all the communication with the database hosted on
 * the machine that the server is running.
 *
 * @author Robert Chiper
 */
public class DatabaseManager {
    private Connection dbConnection = null;
    private static final String url = "jdbc:postgresql://localhost/solarlegends";

    /**
     * Establishes the connection to the database.
     */
    public void connectToDB() {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        System.out.println("PostgreSQL driver registered");

        try {
            dbConnection = DriverManager
                    .getConnection(url, "robert", "robert");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (dbConnection != null) {
            if (SolarLegendsServer.DEBUG) {
                System.out.println("Database accessed.");
            }
        }
    }

    /**
     * Updates the values of an entry in the leaderboard based on the game result. 1 means a win,
     * 0 is draw, and -1 is loss.
     *
     * @param username the name of the player
     * @param result   the result of the game
     */
    public void updatePlayer(String username, int result) {

        try {
            if (isRegistered(username))
                switch (result) {
                    case 1:
                        PreparedStatement getWins = dbConnection.prepareStatement("SELECT wins FROM leaderboard " +
                                "WHERE name = ?");
                        getWins.setString(1, username);
                        ResultSet resultSet = getWins.executeQuery();
                        resultSet.next();
                        int currentWins = resultSet.getInt(1) + 1;
                        PreparedStatement updateWins = dbConnection.prepareStatement("UPDATE leaderboard SET wins = ? " +
                                "WHERE name = ?");
                        updateWins.setInt(1, currentWins);
                        updateWins.setString(2, username);
                        updateWins.executeUpdate();
                        break;

                    case 0:
                        PreparedStatement getDraws = dbConnection.prepareStatement("SELECT draws FROM leaderboard " +
                                "WHERE name = ?");
                        getDraws.setString(1, username);
                        resultSet = getDraws.executeQuery();
                        resultSet.next();
                        int currentDraws = resultSet.getInt(1) + 1;
                        PreparedStatement updateDraws = dbConnection.prepareStatement("UPDATE leaderboard SET draws = ?" +
                                "WHERE name = ?");
                        updateDraws.setInt(1, currentDraws);
                        updateDraws.setString(2, username);
                        updateDraws.executeUpdate();
                        break;

                    case -1:
                        PreparedStatement getLoses = dbConnection.prepareStatement("SELECT loses FROM leaderboard " +
                                "WHERE name = ?");
                        getLoses.setString(1, username);
                        resultSet = getLoses.executeQuery();
                        resultSet.next();
                        int currentLoses = resultSet.getInt(1) + 1;
                        PreparedStatement updateLoses = dbConnection.prepareStatement("UPDATE leaderboard SET loses = ?" +
                                "WHERE name = ?");
                        updateLoses.setInt(1, currentLoses);
                        updateLoses.setString(2, username);
                        updateLoses.executeUpdate();
                        break;

                    default:
                        break;
                }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Checks if a given player is registered (present in the leaderboard already).
     *
     * @param username the name of the player.
     * @return the status of whether is registered
     */
    private boolean isRegistered(String username) {
        boolean result = false;
        try {
            PreparedStatement checkIfRegistered = dbConnection.prepareStatement("SELECT EXISTS (SELECT name FROM " +
                    "leaderboard WHERE name = ?);");
            checkIfRegistered.setString(1, username);
            ResultSet validRs = checkIfRegistered.executeQuery();
            validRs.next();
            result = validRs.getBoolean(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Inserts a new player in the table. Is only called when the connected user is not already
     * registered. The default values for games played are all 0.
     *
     * @param username the name of the new player
     */
    private void insertPlayer(String username) {
        try {
            PreparedStatement insert = dbConnection.prepareStatement("INSERT INTO leaderboard VALUES (?, 0, 0, " +
                    "0);");
            insert.setString(1, username);
            insert.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * @return a collection of all the players in the leaderboard
     */
    public ArrayList<Player> getPlayers() {
        ArrayList<Player> players = new ArrayList<>();
        try {
            PreparedStatement getQuery = dbConnection.prepareStatement("SELECT * FROM leaderboard");
            ResultSet resultSet = getQuery.executeQuery();

            while (resultSet.next()) {
                Player player = new Player();
                player.username = resultSet.getString(1);
                player.wins = resultSet.getInt(2);
                player.draws = resultSet.getInt(3);
                player.loses = resultSet.getInt(4);
                players.add(player);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return players;
    }

    /**
     * @param username the name of the player
     * @return returns a specific player
     */
    public Player getPlayer(String username) {
        Player player = null;
        try {
            if (isRegistered(username)) {
                player = new Player();

                PreparedStatement getQuery = dbConnection.prepareStatement("SELECT * FROM leaderboard WHERE name = ?");
                getQuery.setString(1, username);
                ResultSet resultSet = getQuery.executeQuery();
                resultSet.next();
                player.username = resultSet.getString(1);
                player.wins = resultSet.getInt(2);
                player.draws = resultSet.getInt(3);
                player.loses = resultSet.getInt(4);
            } else {
                insertPlayer(username);
                player = getPlayer(username);

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return player;
    }

    /**
     * Closes the connection to the database.
     */
    public void closeDBConnection() {
        try {
            dbConnection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
