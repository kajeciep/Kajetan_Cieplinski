package com.solarlegendsserver;

import com.solarlegendsserver.utils.Player;

import java.net.InetAddress;
import java.net.Socket;

/**
 * Data class used for storing information about the client. The InetAddress and port were used
 * at some point for identification. The server name is the name of the last game that the client
 * was part of.
 *
 * @author Robert Chiper
 */
public class Client {
    public InetAddress address;
    public int port;
    public Player player;
    public Socket socket;
    public String serverName;

    /**
     * @param address the InetAddress of the client's Socket
     * @param port    the port of the client's Socket
     * @param player  the Player object of the client
     */
    public Client(InetAddress address, int port, Player player) {
        this.address = address;
        this.port = port;
        this.player = player;
    }
}
