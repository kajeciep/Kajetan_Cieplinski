package com.solarlegendsserver;

import com.game.net.ByteUtils;
import com.game.net.MessageHeaders;
import com.game.net.Serializer;
import com.game.state.GameState;
import com.game.state.Loadout;
import javafx.scene.input.KeyCode;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;


/**
 * The class responsible for starting and maintaining a multiplayer game. It's used to communicate with the
 * 2 players by receiving their input and informing them with the current game state of the server.
 *
 * @author Robert Chiper
 */
public class GameServer {
    private int port;
    private boolean listening = false;
    private ArrayList<Client> clients = new ArrayList<>();
    private String serverName;
    private String serverPassword;
    private GameState gameState;
    private int playersConnected;
    private ArrayDeque<KeyCode> userInput;
    private String hostName;
    private ArrayList<Loadout> loadouts;
    private boolean randomGravity;
    private boolean receivedEnter = false;
    public volatile boolean online = true;


    /**
     * The constructor sets the name and password for the game. It initializes the ArrayList
     * of loadouts and sets the number of connected players to 0.
     *
     * @param serverName     the name of the game which a client started
     * @param serverPassword the password of that game
     */
    public GameServer(String serverName, String serverPassword) {
        this.serverName = serverName;
        this.serverPassword = serverPassword;
        loadouts = new ArrayList<>();
        playersConnected = 0;

    }

    /**
     * Launches a new thread used for listening for client messages and selects a port number
     * for the ServerSocket.
     */
    public void start() {
        (new Thread(() -> listen())).start();

        while (port == 0) {
            Thread.yield();
        }
    }

    /**
     * Initializes the ServerSocket that accepts connections from the 2 players, initializes the
     * ArrayList for user input and starts a new listen thread for every client that connects.
     */
    private void listen() {
        try {
            port = ThreadLocalRandom.current().nextInt(40000, 59999);
            ServerSocket tcpSocket = new ServerSocket(port);
            if (SolarLegendsServer.DEBUG)
                System.out.println("Started game server (" + serverName + ") on port " + port + "...");
            listening = true;
            userInput = new ArrayDeque<>();
            while (listening) {
                Socket clientSocket = tcpSocket.accept();
                Thread listenThread = new Thread(() -> listen(clientSocket), serverName + " ListenThread for " +
                        clientSocket.getInetAddress() + ":" + clientSocket.getPort());
                listenThread.start();
            }
            tcpSocket.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * While the game is underway, the game state is updated at rate of 30 times a second. When
     * boolean for ending the turn becomes true, a turn message is sent if the game is not completed
     * or, if it is, the server closes down. While the end turn boolean is false, the game state
     * is sent to the clients.
     */
    private void update() {
        long lastLoopTime = System.nanoTime();
        final int TARGET_FPS = 30;
        final long OPTIMAL_TIME = 1000000000 / TARGET_FPS;
        int lastFpsTime = 0;

        while (gameState.gameResult == -1) {
            long now = System.nanoTime();
            long updateLength = now - lastLoopTime;
            lastLoopTime = now;

            // update the frame counter
            lastFpsTime += updateLength;

            // update our FPS counter if a second has passed since we last recorded
            if (lastFpsTime >= 1000000000) {
                lastFpsTime = 0;

            }

            updateGameState();
            gameState.step(0.03);

            if (receivedEnter) {
                switch (gameState.gameResult) {
                    case -1:
                        sendTurnChange();
                        break;
                    default:
                        closeServer();
                        break;
                }
                receivedEnter = false;
            } else {
                sendGameState();
            }

            try {
                long sleepTime = (lastLoopTime - System.nanoTime() + OPTIMAL_TIME) / 1000000;
                if (sleepTime > 0)
                    Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        closeServer();

    }

    /**
     * Listens for messages for a particular client, stores the message and calls the method to process it.
     *
     * @param clientSocket the Socket of the client
     */
    private void listen(Socket clientSocket) {
        InputStream inputStream;
        try {
            inputStream = clientSocket.getInputStream();
            byte[] data = new byte[16];
            while (listening) {
                inputStream.read(data);
                process(clientSocket, data);
            }
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Sends data to the client
     *
     * @param clientSocket the Socket of the client
     * @param data         the content of the message
     */
    private synchronized void send(Socket clientSocket, byte[] data) {
        try {
            OutputStream outputStream = clientSocket.getOutputStream();
            outputStream.write(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Processes the data sent by the client. First it validates the packet header, then
     * checks the type of the received message. The possible types are: PORT_PACKET, INPUT_PACKET,
     * LEAVE_GAME. One port packet is sent by each client at the start of the game in which
     * they specify whether they hosted or joined (id 0 - host, id 1 - join). When two players connect,
     * the GameState is initialized and a message is sent to each client with its details.
     * <p>
     * An input packet only contains the bytes of the KeyCode pressed by the client. This KeyCode
     * is added to the Collection of input. A leave game packet removes the leaving client from
     * the collection of clients and initializes server shutdown.
     * </p>
     *
     * @param clientSocket the Socket of the client
     * @param data         the content of the message
     */
    private void process(Socket clientSocket, byte[] data) {
        ByteUtils byteUtils = new ByteUtils();

        // Validate packet header
        if (!(data[0] == 0x12 && data[1] == 0x11))
            return;

        switch (data[2]) {
            case MessageHeaders.PORT_PACKET:
                int id = data[3];
                for (Client client : clients) {
                    if (client.player.id == id) {
                        client.socket = clientSocket;
                        playersConnected++;
                    }
                    if (client.player.id == 0) {
                        hostName = client.player.username;
                    }
                }
                if (playersConnected == 2) {
                    Loadout[] loadoutsArray = new Loadout[6];
                    for (int i = 0; i < 6; i++) {
                        loadoutsArray[i] = loadouts.get(i);
                    }
                    gameState = new GameState(false, false, loadoutsArray);
                    gameState.createGameWorld(System.currentTimeMillis());
                    gameState.setRandomGravity(randomGravity);
                    gameState.init();
                    byteUtils.flush();
                    byteUtils.write(MessageHeaders.PACKET_HEADER);
                    byteUtils.write(MessageHeaders.BEGIN_GAME);
                    byteUtils.write(randomGravity ? (byte) 1 : (byte) 2);
                    byteUtils.write(gameState.getSeed());
                    byteUtils.write(Serializer.serializeRobots(gameState.getRobots()));
                    for (Client client : clients) {
                        if (SolarLegendsServer.DEBUG)
                            System.out.println("Sent BEGIN_GAME to " + client.address + ":" + client.socket.getPort());
                        send(client.socket, byteUtils.getBuffer());
                    }
                    (new Thread(() -> update(), serverName + " GameState Thread")).start();
                }
                break;

            case MessageHeaders.INPUT_PACKET:
                KeyCode input = byteUtils.getKeyCode(data[3]);
                userInput.add(input);
                break;
            case MessageHeaders.LEAVE_GAME:
                gameState.gameResult = 3;
                int clientsSize = clients.size();
                for (int i = 0; i < clientsSize; i++) {
                    if (clients.get(i).port == clientSocket.getPort()) {
                        clients.remove(i);
                        break;
                    }
                }
                closeServer();
                break;
            default:
                break;
        }
    }

    /**
     * Sends the game state to both players. This contains the bytes for the robots, the id of the current
     * character and the id of the current player.
     */
    private void sendGameState() {
        ByteUtils byteUtils = new ByteUtils();
        byteUtils.write(MessageHeaders.PACKET_HEADER);
        byteUtils.write(MessageHeaders.GAME_STATE);
        byteUtils.write((byte) gameState.getCurrentChar().getRobotId());
        byteUtils.write((byte) gameState.whosTurn);
        byteUtils.write(Serializer.serializeRobots(gameState.getRobots()));
        byteUtils.write(Serializer.serializeExplosions(gameState.getExplosions()));
        gameState.clearExplosions();

        for (Client c : clients) {
            send(c.socket, byteUtils.getBuffer());
        }
    }

    /**
     * Sends a message to both clients that notifies them that it is time to end the turn.
     * It checks if a supply drop has been created by the game state of the server and informs
     * them of its coordinate. It also includes the turn number of the servers game state
     * used for controlling the level of lava and the id of the players whos turn it is.
     */
    private void sendTurnChange() {
        ByteUtils byteUtils = new ByteUtils();
        byteUtils.flush();
        byteUtils.write(MessageHeaders.PACKET_HEADER);
        byteUtils.write(MessageHeaders.TURN_CHANGE);
        if (gameState.dropCreated) {
            byteUtils.write(true);
            byteUtils.write((short) gameState.getDrops().get(gameState.getDrops().size() - 1).getXPos());
        } else {
            byteUtils.write(false);
            byteUtils.write((short) 0);
        }
        byteUtils.write((short) gameState.turnNumber);
        byteUtils.write((byte) gameState.whosTurn);
        byte[] data = byteUtils.getBuffer();

        for (Client c : clients) {
            send(c.socket, data);
            if (SolarLegendsServer.DEBUG)
                System.out.println("Sent turn change message to id " + c.player.id + "; the turn is " + gameState.whosTurn);
        }
    }

    /**
     * Sends a message to both clients that notifies them that the current character has
     * fired its weapon.
     */
    private void sendWeaponFired() {
        ByteUtils byteUtils = new ByteUtils();
        byteUtils.write(MessageHeaders.PACKET_HEADER);
        byteUtils.write(MessageHeaders.WEAPON_FIRED);
        byteUtils.write((byte) gameState.getCurrentChar().getRobotId());
        for (Client c : clients) {
            send(c.socket, byteUtils.getBuffer());
        }
    }

    /**
     * Tries to get the first input in the Collection of user input and if it is not null
     * then it uses it to update the game state.
     */
    private void updateGameState() {
        KeyCode input = userInput.poll();
        if (input != null) {
            if (input == KeyCode.ENTER) {
                if (gameState.endTurn()) {
                    receivedEnter = true;
                }
            }
            if (input == KeyCode.W) {
                gameState.jump(gameState.whosTurn);
            }
            if (input == KeyCode.A) {
                gameState.moveLeft(gameState.whosTurn);
            }
            if (input == KeyCode.D) {
                gameState.moveRight(gameState.whosTurn);
            }
            if (input == KeyCode.Q) {
                gameState.jump(gameState.whosTurn);
                gameState.moveLeft(gameState.whosTurn);
            }
            if (input == KeyCode.E) {
                gameState.jump(gameState.whosTurn);
                gameState.moveRight(gameState.whosTurn);
            }
            if (input == KeyCode.UP) {
                gameState.increaseAngle(gameState.whosTurn);
            }
            if (input == KeyCode.DOWN) {
                gameState.decreaseAngle(gameState.whosTurn);
            }
            if (input == KeyCode.LEFT) {
                gameState.decreasePower(gameState.whosTurn);
            }
            if (input == KeyCode.RIGHT) {
                gameState.increasePower(gameState.whosTurn);
            }
            if (input == KeyCode.SPACE) {
                gameState.fireWeapon(gameState.whosTurn);
                sendWeaponFired();
            }
            if (input == KeyCode.DIGIT1) {
                gameState.setCurrentSlot(0, gameState.whosTurn);
            }
            if (input == KeyCode.DIGIT2) {
                gameState.setCurrentSlot(1, gameState.whosTurn);
            }
        }
    }

    /**
     * Stops all the threads running on the server and notifies the players with the
     * result of the game. It makes sure that the server is also removed from the collection
     * of active games that the main server has.
     */
    private void closeServer() {
        listening = false;
        ByteUtils byteUtils = new ByteUtils();
        byteUtils.write(MessageHeaders.PACKET_HEADER);
        byteUtils.write(MessageHeaders.GAME_ENDED);
        switch (gameState.gameResult) {
            case 0:
            case 1:
                for (Client c : clients) {
                    if (c.player.id == gameState.gameResult) {
                        c.player.addWin();
                        byteUtils.write((byte) 0);
                    } else {
                        c.player.addLoss();
                        byteUtils.write((byte) 1);
                    }
                    send(c.socket, byteUtils.getBuffer());
                    byteUtils.flush();
                    byteUtils.write(MessageHeaders.PACKET_HEADER);
                    byteUtils.write(MessageHeaders.GAME_ENDED);
                }
                break;
            case 2:
                byteUtils.write((byte) 2);
                for (Client c : clients) {
                    c.player.addDraw();
                    send(c.socket, byteUtils.getBuffer());
                }
                break;
            case 3:
                byteUtils.write((byte) 3);
                for (Client c : clients) {
                    send(c.socket, byteUtils.getBuffer());
                }
                break;
            default:
                break;
        }
        clients.clear();
        online = false;
        System.out.println("Game " + serverName + " ended");
        SolarLegendsServer.serverChecker();
    }

    /**
     * Terminates the threads running on the server and notifies the player who hasn't force closed
     * that the game has ended.
     *
     * @param id the id of the player who forcefully closed his/hers client
     */
    public void forceCloseServer(int id) {
        listening = false;

        ByteUtils byteUtils = new ByteUtils();
        byteUtils.write(MessageHeaders.PACKET_HEADER);
        byteUtils.write(MessageHeaders.LEAVE_GAME);
        byte[] message = byteUtils.getBuffer();
        for (Client c : clients) {
            if (c.player.id != id) {
                send(c.socket, message);
                if (SolarLegendsServer.DEBUG)
                    System.out.println(serverName + ": sent termination message to " + c.socket.getPort());
            }
        }
        clients.clear();
        online = false;
        System.out.println(serverName + ": game ended");
        SolarLegendsServer.serverChecker();
    }

    /**
     * Adds a client to the collection of clients
     *
     * @param client the client to be added
     */
    public void addClient(Client client) {
        clients.add(client);
        if (SolarLegendsServer.DEBUG)
            System.out.println(serverName + ": added client " + client.address + ":" + client.port);
    }

    /**
     * @param loadouts the collection of loadouts to be added
     */
    public void addLoadouts(ArrayList<Loadout> loadouts) {
        this.loadouts.addAll(loadouts);
    }

    /**
     * @return the number of connected players
     */
    public int getPlayerCount() {
        return clients.size();
    }

    /**
     * @return the name of the server
     */
    public String getServerName() {
        return serverName;
    }

    /**
     * @return the password of the server
     */
    public String getServerPassword() {
        return serverPassword;
    }

    /**
     * @return the name of the hosting player
     */
    public String getHostName() {
        return hostName;
    }

    /**
     * @return the port number of the ServerSocket
     */
    public int getPort() {
        return port;
    }

    /**
     * @param b the boolean for random gravity
     */
    public void setRandomGravity(boolean b) {
        this.randomGravity = b;
    }
}
