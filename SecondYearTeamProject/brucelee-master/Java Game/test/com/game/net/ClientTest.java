package com.game.net;

import com.game.UI.InputForNetwork;
import com.game.state.Loadout;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

import static org.junit.Assert.assertTrue;

public class ClientTest {

    private Client client;
    private boolean connected;
    private boolean gameCreated;
    private boolean gameJoined;
    private boolean serversList;
    private boolean leaderboard;
    private boolean leaveGame;

    class Server {
        Server() {
            (new Thread(() -> accept())).start();
        }

        private void accept() {
            try {
                ServerSocket tcpSocket = new ServerSocket(4445);
                while (true) {
                    Socket clientSocket = tcpSocket.accept();
                    (new Thread(() -> listen(clientSocket))).start();
                }
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }

        private void listen(Socket clientSocket) {
            InputStream inputStream;
            connected = true;
            try {
                inputStream = clientSocket.getInputStream();
                byte[] data = new byte[32];
                while (!clientSocket.isClosed()) {
                    inputStream.read(data);
                    switch (data[2]) {
                        case MessageHeaders.CONNECTION:
                            connected = true;
                            break;
                        case MessageHeaders.CREATE_SERVER:
                            gameCreated = true;
                            break;
                        case MessageHeaders.JOIN_SERVER:
                            gameJoined = true;
                            break;
                        case MessageHeaders.LEADERBOARD:
                            leaderboard = true;
                            break;
                        case MessageHeaders.LEAVE_GAME:
                            leaveGame = true;
                            break;
                        case MessageHeaders.SERVERS_LIST:
                            serversList = true;
                            System.out.println("hererere");
                            break;
                        default:
                            break;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Before
    public void init() {
        new Server();
        InputForNetwork.username = "Robert";
        client = new Client();
        client.setLoadout(new Loadout[]{new Loadout(), new Loadout(), new Loadout()});
    }

    @Test
    public void connect() {
        client.connect();
        assertTrue(connected);
    }

    @Test
    public void createGame() {
        client.connect();
        client.createGame("EU1234", "password", false);
        try {
            Thread.sleep(400);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertTrue(gameCreated);
    }

    @Test
    public void joinGame() {
        client.connect();
        client.joinGame("EU1234", "password");
        try {
            Thread.sleep(400);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertTrue(gameJoined);
    }

    @Test
    public void sendServersListPacket() {
        client.connect();
        client.sendServersListPacket();
        try {
            Thread.sleep(400);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertTrue(serversList);
    }

    @Test
    public void sendLeaderboardPacket() {
        client.connect();
        client.sendLeaderboardPacket();
        try {
            Thread.sleep(400);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertTrue(leaderboard);
    }

    @Test
    public void sendLeaveGamePacket() {
        client.connect();
        client.sendLeaveGamePacket();
        try {
            Thread.sleep(400);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertTrue(leaveGame);
    }
}