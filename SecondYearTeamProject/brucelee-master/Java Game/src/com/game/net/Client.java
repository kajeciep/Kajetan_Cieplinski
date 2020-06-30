package com.game.net;

import com.game.UI.InputForController;
import com.game.UI.InputForNetwork;
import com.game.object.Explosion;
import com.game.physics.Robot;
import com.game.state.GameState;
import com.game.state.Loadout;
import com.game.weapon.Weapon;
import javafx.scene.input.KeyCode;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;


/**
 * This class is used to communicate with the main and game servers and informs the UI about what
 * its state is with the 3 status enums.
 *
 * @author Robert Chiper
 */
public class Client {

    /**
     * Enum that reflects the connection status of the client. Used by UI.
     */
    public enum ConnectionStatus {
        CONNECTED, NOT_CONNECTED
    }

    /**
     * Enum that reflects the game status of the client. Used by UI.
     */
    public enum GameStatus {
        IN_GAME, NOT_IN_GAME, START_GAME
    }

    /**
     * Enum that reflects the menu status of the client. Used by UI.
     */
    public enum MenuStatus {
        DEFAULT, SERVERS_LIST, LEADERBOARD, SERVER_NAME_IN_USE, INVALID_PASSWORD, FULL_SERVER, SERVER_NOT_FOUND
    }

    public volatile ConnectionStatus connectionStatus;
    public volatile GameStatus gameStatus;
    public volatile MenuStatus menuStatus;
    public volatile boolean hasTurn = false;

    private int mainServerPort;
    private InetAddress serverAddress;

    private Socket tcpSocket;
    private OutputStream outputStream;
    private InputStream inputStream;
    private boolean listening = false;

    private boolean listeningGame = false;
    private Socket gameSocket;
    private OutputStream gameOutputStream;
    private InputStream gameInputStream;

    private ArrayList<GameServer> gameServers;
    private ArrayList<Player> leaderboard;
    private GameState gameState;
    private Loadout[] loadouts;
    private InputForController ifc;

    /**
     * Gets the IP address and the port of the main server from InputForNetwork. Sets the statuses accordingly.
     */
    public Client() {
        connectionStatus = ConnectionStatus.NOT_CONNECTED;
        gameStatus = GameStatus.NOT_IN_GAME;
        menuStatus = MenuStatus.DEFAULT;

        try {
            serverAddress = InetAddress.getByName(InputForNetwork.hostIP);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        this.mainServerPort = InputForNetwork.hostPort;
    }

    /**
     * Tries to open a socket to connect to the server, starts a thread to listen for messages
     * if it is successful, and sends a connection message to the server.
     */
    public void connect() {
        try {
            tcpSocket = new Socket(serverAddress, mainServerPort);
            inputStream = tcpSocket.getInputStream();
            outputStream = tcpSocket.getOutputStream();
            Runtime.getRuntime().addShutdownHook(new Terminate());
        } catch (IOException e) {
            e.printStackTrace();
        }

        listening = true;
        (new Thread(() -> listenTCP(), "Main Server ListenThread")).start();
        sendConnectionPacket();
    }

    /**
     * Calls the method that sends a request to the main server to create a game.
     *
     * @param serverName     the name of the game
     * @param serverPassword the password fo the game
     * @param randomGravity  boolean representing the gravity status
     */
    public void createGame(String serverName, String serverPassword, boolean randomGravity) {
        sendCreateServerPacket(serverName, serverPassword, randomGravity);
    }

    /**
     * Calls the method that sends a request to the main server to join a game.
     *
     * @param serverName     the name of the game
     * @param serverPassword the password of the game
     */
    public void joinGame(String serverName, String serverPassword) {
        sendJoinServerPacket(serverName, serverPassword);
    }

    /**
     * Listens for messages from the main server and calls the method to process them.
     */
    private void listenTCP() {
        try {
            byte data[] = new byte[1024];
            while (listening) {
                inputStream.read(data);
                process(data);
            }
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Listens for messages from the game server and calls the method to process them.
     */
    private void listenGame() {
        try {
            byte data[] = new byte[256];
            while (listeningGame) {
                gameInputStream.read(data);
                processGameData(data);
            }
            gameInputStream.close();
            gameStatus = GameStatus.NOT_IN_GAME;
        } catch (IOException e) {
            e.printStackTrace();

        }
    }

    /**
     * Processes the messages from the main server. The possible types of messages are:
     * CREATE_SERVER, JOIN_SERVER, SERVER_NAME_IN_USE, SERVER_NOT_FOUND, SERVER_FULL,
     * INVALID_PASSWORD, CONNECTION, SERVERS_LIST, LEADERBOARD. Based on the received message,
     * the menu and game statuses are set accordingly.
     *
     * @param data the byte array with the received message
     */
    private void process(byte[] data) {
        // Validate packet header
        if (!(data[0] == 0x12 && data[1] == 0x11))
            return;

        switch (data[2]) {
            case MessageHeaders.CREATE_SERVER:
            case MessageHeaders.JOIN_SERVER:
                int gameServerPort = ByteUtils.getInt(data, 3);
                try {
                    gameSocket = new Socket(serverAddress, gameServerPort);
                    System.out.println("Game socket port: " + gameSocket.getLocalPort());
                    gameInputStream = gameSocket.getInputStream();
                    gameOutputStream = gameSocket.getOutputStream();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                gameStatus = GameStatus.IN_GAME;
                menuStatus = MenuStatus.DEFAULT;
                if (InputForNetwork.playerId == 0)
                    hasTurn = true;
                (new Thread(() -> listenGame(), "Game ListenThread")).start();
                listeningGame = true;
                System.out.println("Now connected to " + serverAddress + ":" + gameServerPort);
                sendPortToGameServer();
                break;

            case MessageHeaders.SERVER_NAME_IN_USE:
                menuStatus = MenuStatus.SERVER_NAME_IN_USE;
                break;
            case MessageHeaders.SERVER_NOT_FOUND:
                menuStatus = MenuStatus.SERVER_NOT_FOUND;
                break;
            case MessageHeaders.SERVER_FULL:
                menuStatus = MenuStatus.FULL_SERVER;
                break;
            case MessageHeaders.INVALID_PASSWORD:
                menuStatus = MenuStatus.INVALID_PASSWORD;
                break;
            case MessageHeaders.CONNECTION:
                connectionStatus = ConnectionStatus.CONNECTED;
                break;
            case MessageHeaders.SERVERS_LIST:
                gameServers = Serializer.deserializeGameServers(data, 3);
                menuStatus = MenuStatus.SERVERS_LIST;
                break;
            case MessageHeaders.LEADERBOARD:
                leaderboard = Serializer.deserializeLeaderboard(data, 3);
                menuStatus = MenuStatus.LEADERBOARD;
                break;
            default:
                break;
        }
    }

    /**
     * Sends a message with the port that the client opens for the Socket used in games.
     * It also says if the player is hosting or joining.
     */
    private void sendPortToGameServer() {
        ByteUtils byteUtils = new ByteUtils();
        byteUtils.write(MessageHeaders.PACKET_HEADER);
        byteUtils.write(MessageHeaders.PORT_PACKET);
        byteUtils.write((byte) InputForNetwork.playerId);
        sendGame(byteUtils.getBuffer());

    }

    /**
     * Processes the messages received during games. The possible types of messages are:
     * BEGIN_GAME, GAME_STATE, TURN_CHANGE, WEAPON_FIRED, GAME_ENDED. Based on the message,
     * the game state of the client is updated and the various client statuses can also change.
     *
     * @param data the byte array with the received message
     */
    private void processGameData(byte[] data) {
        if (!(data[0] == 0x12 && data[1] == 0x11))
            return;

        switch (data[2]) {
            case MessageHeaders.BEGIN_GAME:
                boolean randomGravity = data[3] == 1;
                long seed = ByteUtils.getLong(data, 4);
                gameState.createGameWorld(seed);
                gameState.setRobots(Serializer.deserializeRobots(ByteUtils.shrinkByteArray(data, 12, data.length - 12)));
                gameState.initOnline(randomGravity);
                gameStatus = GameStatus.START_GAME;
                break;

            case MessageHeaders.GAME_STATE:
                updateRobots(data);
                break;

            case MessageHeaders.TURN_CHANGE:
                int turnNumber = ByteUtils.getShort(data, 6);
                int whosTurn = data[8];
                if (data[3] == 0x01) {
                    int supplyDropXPos = ByteUtils.getShort(data, 4);
                    gameState.endTurn(true, supplyDropXPos, turnNumber, whosTurn);
                } else {
                    gameState.endTurn(false, 0, turnNumber, whosTurn);
                }

                if (whosTurn == InputForNetwork.playerId) {
                    ifc.clearInputList();
                    hasTurn = true;
                } else {
                    hasTurn = false;
                }
                break;

            case MessageHeaders.WEAPON_FIRED:
                gameState.onlineFireWeapon();
                break;

            case MessageHeaders.LEAVE_GAME:
                gameState.gameResult = 3;
                listeningGame = false;
                gameStatus = GameStatus.NOT_IN_GAME;
                try {
                    gameOutputStream.close();
                    gameInputStream.close();
                    gameSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;

            case MessageHeaders.GAME_ENDED:
                gameState.gameResult = data[3];
                listeningGame = false;
                gameStatus = GameStatus.NOT_IN_GAME;
                try {
                    gameOutputStream.close();
                    gameInputStream.close();
                    gameSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            default:
                break;
        }
    }


    /**
     * Send a connection package to the main server which includes the username.
     */
    private void sendConnectionPacket() {
        ByteUtils byteUtils = new ByteUtils();
        byteUtils.flush();
        byteUtils.write(MessageHeaders.PACKET_HEADER);
        byteUtils.write(MessageHeaders.CONNECTION);
        byteUtils.write(InputForNetwork.username);
        sendTCP(byteUtils.getBuffer());
    }


    /**
     * Sends a request to the main server to create a game. It includes information about whether
     * gravity is random, the selected loadout, the name and password of the game.
     *
     * @param name          the name of the game
     * @param password      the password of the game
     * @param randomGravity true if random, false otherwise
     */
    private void sendCreateServerPacket(String name, String password, boolean randomGravity) {
        ByteUtils byteUtils = new ByteUtils();

        byteUtils.flush();
        byteUtils.write(MessageHeaders.PACKET_HEADER);
        byteUtils.write(MessageHeaders.CREATE_SERVER);
        byteUtils.write(randomGravity);
        for (Loadout loadout : loadouts) {
            byteUtils.write(Serializer.serializeLoadout(loadout));
        }
        byteUtils.write(name);
        byteUtils.write(password);
        sendTCP(byteUtils.getBuffer());
    }

    /**
     * Sends a request to the main server to join a specific game. It includes the loadout,
     * name of the game and password.
     *
     * @param name     the name of the game
     * @param password the password of the game
     */
    private void sendJoinServerPacket(String name, String password) {
        ByteUtils byteUtils = new ByteUtils();

        byteUtils.flush();
        byteUtils.write(MessageHeaders.PACKET_HEADER);
        byteUtils.write(MessageHeaders.JOIN_SERVER);
        byteUtils.write((byte) 0);
        for (Loadout loadout : loadouts) {
            byteUtils.write(Serializer.serializeLoadout(loadout));
        }
        byteUtils.write(name);
        byteUtils.write(password);
        sendTCP(byteUtils.getBuffer());
    }

    /**
     * Sends a request to the main server to get the list of active games.
     */
    public void sendServersListPacket() {
        ByteUtils byteUtils = new ByteUtils();
        byteUtils.write(MessageHeaders.PACKET_HEADER);
        byteUtils.write(MessageHeaders.SERVERS_LIST);
        sendTCP(byteUtils.getBuffer());
    }

    /**
     * Sends a request to the main server to get the leaderboard.
     */
    public void sendLeaderboardPacket() {
        ByteUtils byteUtils = new ByteUtils();
        byteUtils.write(MessageHeaders.PACKET_HEADER);
        byteUtils.write(MessageHeaders.LEADERBOARD);
        sendTCP(byteUtils.getBuffer());
    }

    /**
     * Sends a byte array to the main server.
     *
     * @param data the byte array
     */
    private void sendTCP(byte[] data) {
        try {
            outputStream.write(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sends a byte array to the game server
     *
     * @param data the byte array
     */
    private void sendGame(byte[] data) {
        try {
            gameOutputStream.write(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * During games, sends the user input to the game server.
     *
     * @param keyCode the code of the pressed key
     */
    public void sendInput(KeyCode keyCode) {
        if (gameStatus == GameStatus.NOT_IN_GAME)
            return;
        ByteUtils byteUtils = new ByteUtils();
        byteUtils.flush();
        byteUtils.write(MessageHeaders.PACKET_HEADER);
        byteUtils.write(MessageHeaders.INPUT_PACKET);
        byteUtils.write(keyCode);
        sendGame(byteUtils.getBuffer());

    }

    /**
     * Sends a request to the game server to leave.
     */
    public void sendLeaveGamePacket() {
        ByteUtils byteUtils = new ByteUtils();

        byteUtils.flush();
        byteUtils.write(MessageHeaders.PACKET_HEADER);
        byteUtils.write(MessageHeaders.LEAVE_GAME);
        sendGame(byteUtils.getBuffer());
        gameStatus = GameStatus.NOT_IN_GAME;
    }

    /**
     * @return the ArrayList of active games
     */
    public ArrayList<GameServer> getGameServers() {
        return gameServers;
    }

    /**
     * @return the ArrayList of Player objects in the leadearboard table
     */
    public ArrayList<Player> getLeaderboard() {
        return leaderboard;
    }


    /**
     * @param gameState the GameState object that the UI generates
     */
    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

    /**
     * Updates all the information about the robots and who's turn it is.
     *
     * @param data the byte array containing the data
     */
    private void updateRobots(byte[] data) {
        for (Robot cRobot : gameState.getRobots()) {
            if (cRobot.getRobotId() == data[3]) {
                gameState.setCurrentCharacter(cRobot);
            }
        }
        if (data[4] != gameState.whosTurn) {
            hasTurn = !hasTurn;
        }
        int offset = 5;
        int bytesForRobots = GameState.noTeams * GameState.teamSize * 24;
        for (int i = offset; i < offset + bytesForRobots; i += 24) {
            short xPos = ByteUtils.getShort(data, i + 2);
            short yPos = ByteUtils.getShort(data, i + 4);
            for (Robot robot : gameState.getRobots()) {
                if (robot.getRobotId() == data[i]) {
                    robot.setIsDead(data[i + 1] == 1);
                    robot.setXPos(xPos);
                    robot.setYPos(yPos);
                    robot.setHealth(data[i + 6]);
                    robot.setEnergy(ByteUtils.getShort(data, i + 7));
                    int index = 0;
                    for (Weapon weapon : robot.getAllWeapons()) {
                        weapon.setAmmo(data[i + 9 + index]);
                        index++;
                    }
                    robot.setSlot(data[i + 18]);
                    robot.setAngle(ByteUtils.getShort(data, i + 19));
                    robot.setPower(data[i + 21]);
                    robot.setImageId(data[i + 22]);
                    robot.setTimer(data[i + 23]);
                }
            }
        }
        offset += bytesForRobots;
        int explosionsSize = data[offset++];
        for (int i = offset; i < offset + 6 * explosionsSize; i += 6) {
            gameState.getWorld().destructTerrain(ByteUtils.getShort(data, i), ByteUtils.getShort(data, i + 2), ByteUtils.getShort(data, i + 4));
        }
    }

    /**
     * @param ifc the InputForController that UI creates
     */
    public void setIFC(InputForController ifc) {
        this.ifc = ifc;
    }

    /**
     * Sends a disconnect message to the main server and closes the running threads.
     */
    public void disconnect() {
        ByteUtils byteUtils = new ByteUtils();
        byteUtils.write(MessageHeaders.PACKET_HEADER);
        byteUtils.write(MessageHeaders.FORCE_QUIT);
        if (tcpSocket == null || outputStream == null) {
            return;
        }
        try {
            outputStream.write(byteUtils.getBuffer());
            listening = false;
            tcpSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param loadouts the Loadout objects generated before creating/joining a game.
     */
    public void setLoadout(Loadout[] loadouts) {
        this.loadouts = loadouts;
    }


    /**
     * A class that starts a thread to send a message to the server when the client force closes.
     */
    private class Terminate extends Thread {

        /**
         * Tries to send a force quit message to the server and terminate the execution of all the threads
         * started by the client.
         */
        public void run() {
            ByteUtils byteUtils = new ByteUtils();
            byteUtils.write(MessageHeaders.PACKET_HEADER);
            byteUtils.write(MessageHeaders.FORCE_QUIT);
            try {
                outputStream.write(byteUtils.getBuffer());
                listeningGame = false;
                listening = false;
                tcpSocket.close();
                if (gameSocket != null) {
                    gameSocket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
