package com.game.net;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * A data class for the GameServer object which reflects the one in the server project.
 * This one also interacts with the UI.
 *
 * @author Robert Chiper
 */
public class GameServer {
    private String name;
    private StringProperty nameProperty;
    private int capacity;
    private String hostName;

    /**
     * @param name the name of the game
     * @param capacity the player count
     * @param hostName the name of the player who created the game
     */
    public GameServer(String name, int capacity, String hostName) {
        this.name = name;
        nameProperty = new SimpleStringProperty(name);
        this.capacity = capacity;
        this.hostName = hostName;
    }

    /**
     * @return the name of the game
     */
    public String getName() {
        return name;
    }

    /**
     * @return the StringProperty for the name that UI uses
     */
    public StringProperty getNameProperty() {
    	return nameProperty;
    }

    /**
     * @return the player count of the game
     */
    public int getCapacity() {
        return capacity;
    }

    /**
     * @return the name of the player who created the game
     */
    public String getHostName() {
        return hostName;
    }
}
