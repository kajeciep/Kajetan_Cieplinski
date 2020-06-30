package com.solarlegendsserver;

import com.game.net.ByteUtils;
import com.game.net.MessageHeaders;
import com.game.net.Serializer;
import com.game.state.Loadout;
import com.solarlegendsserver.utils.Player;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Predicate;

/**
 * This class is responsible for creating the main server. The main server is the one
 * that the clients connect to first, keeps track of the online games and clients, is
 * able to create new games, or communicate with the database to get or update players
 * in the leaderboard.
 *
 * @author Robert Chiper
 */
public class SolarLegendsServer {

    public static boolean DATABASE = false;
    public static boolean DEBUG = true;
    public static DatabaseManager databaseManager;

    private int port;
    private static HashMap<String, GameServer> gameServers = new HashMap<>();
    private HashMap<String, Client> onlineClients = new HashMap<>();

    /**
     * Creates the DatabaseManager and then uses it to connect to the database.
     * Sets the port number for the Server Socket.
     *
     * @param port the port number of the ServerSocket
     */
    private SolarLegendsServer(int port) {
        this.port = port;
        if (DATABASE) {
            databaseManager = new DatabaseManager();
            databaseManager.connectToDB();
        }
    }

    /**
     * Opens the ServerSocket which accepts connections from clients and launches a listen thread for each of them.
     */
    private void start() {
        try {
            ServerSocket tcpSocket = new ServerSocket(port);
            if (DEBUG) System.out.println("Started main server on port " + port + "...");
            while (true) {
                Socket clientSocket = tcpSocket.accept();
                (new Thread(() -> listen(clientSocket), "ListenThread for " + clientSocket.getInetAddress() + ":" + clientSocket.getPort())).start();
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        } finally {
            if (DATABASE) {
                databaseManager.closeDBConnection();
            }
        }
    }

    /**
     * Checks the status of every game server and removes the ones which are not online anymore
     */
    public static void serverChecker() {
        Predicate<GameServer> gameServerPredicate = server -> !server.online;
        gameServers.values().removeIf(gameServerPredicate);
    }

    /**
     * While the Socket is not closed, it waits for it to receive input, it reads the input
     * and calls the method to process it.
     *
     * @param clientSocket the Socket specific for the listen thread
     */
    private void listen(Socket clientSocket) {
        InputStream inputStream;
        try {
            inputStream = clientSocket.getInputStream();
            byte[] data = new byte[128];
            while (!clientSocket.isClosed()) {
                inputStream.read(data);
                process(clientSocket, data);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    /**
     * Processes the input. First it validates the package header and then checks to see
     * which type of message it is. The possible types are: CONNECTION, SERVERS_LIST,
     * LEADERBOARD, FORCE_QUIT, CREATE_SERVER, JOIN_SERVER. Based on the received message,
     * it changes its state accordingly and then send a message back to the client.
     *
     * @param clientSocket the Socket that sent the message
     * @param data         the content of the message
     */
    private void process(Socket clientSocket, byte[] data) {
        ByteUtils byteUtils = new ByteUtils();

        InetAddress clientAddress = clientSocket.getInetAddress();
        int clientPort = clientSocket.getPort();
        Client client = new Client(clientAddress, clientPort, null);

        // Validate packet header
        if (!(data[0] == 0x12 && data[1] == 0x11))
            return;

        if (data[2] > 0x10) {

            ArrayList<Byte> nameList = new ArrayList<>();
            int index = 3;
            while (data[index] != (byte) 0) {
                nameList.add(data[index]);
                index++;
            }

            String playerName = new String(byteUtils.toByteArray(nameList));

            switch (data[2]) {
                // Connection request
                case MessageHeaders.CONNECTION:
                    if (DATABASE) {
                        client.player = databaseManager.getPlayer(playerName);
                    } else {
                        client.player = new Player(playerName);
                    }
                    onlineClients.put(clientAddress.toString() + ":" + clientPort, client);

                    byteUtils.flush();
                    byteUtils.write(MessageHeaders.PACKET_HEADER);
                    byteUtils.write(MessageHeaders.CONNECTION);

                    send(clientSocket, byteUtils.getBuffer());
                    if (DEBUG)
                        System.out.println(clientAddress.toString() + ":" + clientPort + " connected: " + playerName);
                    break;

                // Server list request
                case MessageHeaders.SERVERS_LIST:
                    serverChecker();
                    byteUtils.flush();
                    byteUtils.write(MessageHeaders.PACKET_HEADER);
                    byteUtils.write(MessageHeaders.SERVERS_LIST);
                    byteUtils.write(getServerList());
                    send(clientSocket, byteUtils.getBuffer());
                    break;

                // Leaderboard request
                case MessageHeaders.LEADERBOARD:
                    System.out.println("Received leaderboard request");
                    byteUtils.flush();
                    byteUtils.write(MessageHeaders.PACKET_HEADER);
                    byteUtils.write(MessageHeaders.LEADERBOARD);
                    if (DATABASE) {
                        byteUtils.write(getLeaderboard());
                    }
                    send(clientSocket, byteUtils.getBuffer());
                    break;

                case MessageHeaders.FORCE_QUIT:
                    String clientName = clientAddress.toString() + ":" + clientPort;
                    if (onlineClients.containsKey(clientName)) {
                        String serverName = onlineClients.get(clientName).serverName;
                        if (serverName != null && gameServers.containsKey(serverName)) {
                            gameServers.get(serverName).forceCloseServer(onlineClients.get(clientName).player.id);
                        }
                        onlineClients.remove(clientName);
                    }
                    try {
                        clientSocket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;

                default:
                    break;
            }

        } else {
            ArrayList<Loadout> loadouts = Serializer.deserializeLoadouts(data, 4);

            ArrayList<Byte> nameList = new ArrayList<>();
            int index = 40;
            while (data[index] != (byte) 0) {
                nameList.add(data[index]);
                index++;
            }
            String gameServerName = new String(byteUtils.toByteArray(nameList));

            index++;
            ArrayList<Byte> passList = new ArrayList<>();
            while (data[index] != (byte) 0) {
                passList.add(data[index]);
                index++;
            }
            String gameServerPassword = new String(byteUtils.toByteArray(passList));

            switch (data[2]) {
                // Create server request
                case MessageHeaders.CREATE_SERVER:

                    if (gameServers.containsKey(gameServerName)) {
                        byteUtils.flush();
                        byteUtils.write(MessageHeaders.PACKET_HEADER);
                        byteUtils.write(MessageHeaders.SERVER_NAME_IN_USE);
                        send(clientSocket, byteUtils.getBuffer());
                        if (DEBUG)
                            System.out.println("Main Server: sent SERVER_NAME_IN_USE message to " + clientAddress + ":" + clientPort);

                    } else {
                        GameServer gameServer = new GameServer(gameServerName, gameServerPassword);
                        onlineClients.get(clientAddress.toString() + ":" + clientPort).serverName = gameServerName;
                        onlineClients.get(clientAddress.toString() + ":" + clientPort).player.id = 0;
                        gameServer.addClient(onlineClients.get(clientAddress.toString() + ":" + clientPort));
                        gameServer.addLoadouts(loadouts);
                        gameServer.setRandomGravity(data[3] == 1);
                        gameServer.start();
                        gameServers.put(gameServerName, gameServer);

                        byteUtils.flush();
                        byteUtils.write(MessageHeaders.PACKET_HEADER);
                        byteUtils.write(MessageHeaders.CREATE_SERVER);

                        byteUtils.write(gameServer.getPort());
                        send(clientSocket, byteUtils.getBuffer());
                        if (DEBUG)
                            System.out.println("Main Server: sent SERVER_CREATED message to " + clientAddress + ":" + clientPort);

                    }
                    break;

                // Join request
                case MessageHeaders.JOIN_SERVER:
                    if (!gameServers.containsKey(gameServerName)) {
                        byteUtils.flush();
                        byteUtils.write(MessageHeaders.PACKET_HEADER);
                        byteUtils.write(MessageHeaders.SERVER_NOT_FOUND);
                        send(clientSocket, byteUtils.getBuffer());
                        if (DEBUG)
                            System.out.println("Main Server: sent SERVER_NOT_FOUND message to " + clientAddress + ":" + clientPort);


                    } else if (gameServers.get(gameServerName).getPlayerCount() >= 2) {
                        byteUtils.flush();
                        byteUtils.write(MessageHeaders.PACKET_HEADER);
                        byteUtils.write(MessageHeaders.SERVER_FULL);
                        send(clientSocket, byteUtils.getBuffer());
                        if (DEBUG)
                            System.out.println("Main Server: sent SERVER_FULL message to " + clientAddress + ":" + clientPort);

                    } else if (!gameServers.get(gameServerName).getServerPassword().equals(gameServerPassword)) {
                        byteUtils.flush();
                        byteUtils.write(MessageHeaders.PACKET_HEADER);
                        byteUtils.write(MessageHeaders.INVALID_PASSWORD);
                        send(clientSocket, byteUtils.getBuffer());
                        if (DEBUG)
                            System.out.println("Main Server: sent INVALID_PASSWORD message to " + clientAddress + ":" + clientPort);


                    } else {
                        GameServer gameServer = gameServers.get(gameServerName);
                        onlineClients.get(clientAddress.toString() + ":" + clientPort).serverName = gameServerName;
                        onlineClients.get(clientAddress.toString() + ":" + clientPort).player.id = 1;
                        gameServer.addClient(onlineClients.get(clientAddress.toString() + ":" + clientPort));
                        gameServer.addLoadouts(loadouts);

                        byteUtils.flush();
                        byteUtils.write(MessageHeaders.PACKET_HEADER);
                        byteUtils.write(MessageHeaders.JOIN_SERVER);
                        byteUtils.write(gameServer.getPort());
                        send(clientSocket, byteUtils.getBuffer());
                        if (DEBUG)
                            System.out.println("Main Server: sent SERVER_JOINED message to " + clientAddress + ":" + clientPort);
                    }
                    break;

                default:
                    break;
            }
        }
    }

    /**
     * Converts to bytes the player count, the server name and the host name of each game server.
     *
     * @return the byte array of the game servers
     */
    private byte[] getServerList() {
        ByteUtils byteUtils = new ByteUtils();
        byteUtils.write((byte) gameServers.size());
        for (GameServer server : gameServers.values()) {
            byteUtils.write((byte) server.getPlayerCount());
            byteUtils.write(server.getServerName());
            byteUtils.write(server.getHostName());
        }
        return byteUtils.getBuffer();
    }

    /**
     * Gets the Player objects from the DatabaseManager and converts the usernames, number of wins
     * number of loses, and draws to bytes.
     *
     * @return the byte array of the players
     */
    private byte[] getLeaderboard() {
        ByteUtils byteUtils = new ByteUtils();
        ArrayList<Player> players = databaseManager.getPlayers();
        byteUtils.write((byte) players.size());
        for (Player p : players) {
            byteUtils.write((byte) p.wins);
            byteUtils.write((byte) p.loses);
            byteUtils.write((byte) p.draws);
            byteUtils.write(p.username);
        }
        return byteUtils.getBuffer();
    }

    /**
     * Send a message to the specified Socket
     *
     * @param clientSocket the receiving Socket
     * @param data         the bytes to be sent
     */
    private void send(Socket clientSocket, byte[] data) {
        try {
            OutputStream outputStream = clientSocket.getOutputStream();
            outputStream.write(data);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Creates the main server and starts it.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        SolarLegendsServer server = new SolarLegendsServer(4445);
        server.start();
    }
}
