package com.game.net;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * A data class for the Player object which reflects the one in the server project.
 * This one also interacts with the UI.
 *
 * @author Robert Chiper
 */
public class Player implements Comparable<Player> {
    private String username;
    private int wins;
    private int loses;
    private int draws;
    private int winRatio;
    private int position;

    /**
     * @param username the name of the player
     * @param wins the number of wins
     * @param loses the number of loses
     * @param draws the number of draws
     */
    public Player(String username, int wins, int loses, int draws) {
        this.username = username;
        this.wins = wins;
        this.loses = loses;
        this.draws = draws;
        if (getGamesPlayed() != 0) {
        	winRatio = (100 * this.wins) / getGamesPlayed();
        } else {
        	winRatio = 0;
        }
        
    }

    /**
     * @param comparePlayer the player that this one is compared with
     * @return the player with the better win ratio
     */
    @Override
    public int compareTo(Player comparePlayer) {
    	int compareWinRatio = comparePlayer.getWinRatio();
    	return compareWinRatio - this.winRatio;
    }

    /**
     * @return the total number of played games
     */
    private int getGamesPlayed() {
        return wins + loses + draws;
    }

    /**
     * @return the StringProperty for the username that UI uses
     */
	public StringProperty getUsernameProperty() {
		return new SimpleStringProperty(username);
	}

    /**
     * @return the StringProperty for the number of games played that UI uses
     */
	public StringProperty getGamesPlayedProperty() {
		int played = getGamesPlayed();
		String playedString = Integer.toString(played);
		return new SimpleStringProperty(playedString);
	}

    /**
     * @return the StringProperty for the number of wins that UI uses
     */
	public StringProperty getWinsProperty() {
		String winsString = Integer.toString(wins);
		return new SimpleStringProperty(winsString);
	}

    /**
     * @return the StringProperty for the number of loses that UI uses
     */
	public StringProperty getLosesProperty() {
		String losesString = Integer.toString(loses);
		return new SimpleStringProperty(losesString);
	}

    /**
     * @return the StringProperty for the number of draws that UI uses
     */
	public StringProperty getDrawsProperty() {
		String drawsString = Integer.toString(draws);
		return new SimpleStringProperty(drawsString);
	}

    /**
     * @return the win ratio
     */
	public int getWinRatio() {
		return winRatio;
	}

    /**
     * @return the position in the leaderboard
     */
	public int getPosition() {
		return position;
	}

	public StringProperty getPositionProperty() {
		String positionString = Integer.toString(position);
		return new SimpleStringProperty(positionString);
	}

    /**
     * @return the username of the player
     */
	public String getUsername() {
	    return username;
    }

    /**
     * @return the number of wins
     */
    public int getWins() {
	    return wins;
    }

    /**
     * @return the number of draws
     */
    public int getDraws() {
	    return draws;
    }

    /**
     * @return the number of loses
     */
    public int getLoses() {
	    return loses;
    }

    /**
     * @param position the position in the leaderboard that the player occupies
     */
	public void setPosition(int position) {
		this.position = position;
	}

    /**
     * @return a String with all the relevant information about the object
     */
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("Username: ");
        buffer.append(username);
        buffer.append("\n");
        buffer.append("Wins: ");
        buffer.append(wins);
        buffer.append("\n");
        buffer.append("Loses: ");
        buffer.append(loses);
        buffer.append("\n");
        buffer.append("Draws: ");
        buffer.append(draws);
        buffer.append("\n");
        return buffer.toString();
    }
	
}
