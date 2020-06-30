package com.game.net;

import com.game.physics.Coord;
import com.game.physics.Robot;
import com.game.state.Loadout;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class SerializerTest {

    @Test
    public void serializeLoadout() {
        Loadout loadout = new Loadout();
        byte[] serializedLoadout = Serializer.serializeLoadout(loadout);
        byte[] actualResult = new byte[]{100, 0, -106, 3, 3, 1, 3, 1, 1, 1, 1, 1};
        assertArrayEquals(serializedLoadout, actualResult);
    }

    @Test
    public void deserializeLoadout() {
        Loadout deserializedLoadout = Serializer.deserializeLoadout(new byte[]
                {100, 0, -106, 3, 3, 1, 3, 1, 1, 1, 1, 1});
        Loadout actualResult = new Loadout();
        assertEquals(deserializedLoadout.getStartHP(), actualResult.getStartHP());
        assertEquals(deserializedLoadout.getMaxEnergy(), actualResult.getMaxEnergy());
        for (int i = 0; i < 9; i++) {
            assertEquals(deserializedLoadout.getInv().get(0).getAmmo(),
                    actualResult.getInv().get(0).getAmmo());
        }
    }

    @Test
    public void serializeLoadouts() {
        ArrayList<Loadout> loadouts = new ArrayList<>();
        Loadout loadout = new Loadout();
        for (int i = 0; i < 3; i++) {
            loadouts.add(loadout);
        }
        byte[] serializedLoadouts = Serializer.serializeLoadouts(loadouts);
        byte[] actualResult = new byte[]
                {100, 0, -106, 3, 3, 1, 3, 1, 1, 1, 1, 1,
                        100, 0, -106, 3, 3, 1, 3, 1, 1, 1, 1, 1, 100, 0, -106, 3, 3, 1, 3, 1, 1, 1, 1, 1};
        assertArrayEquals(serializedLoadouts, actualResult);
    }

    @Test
    public void deserializeLoadouts() {
        ArrayList<Loadout> loadouts = Serializer.deserializeLoadouts(new byte[]
                {100, 0, -106, 3, 3, 1, 3, 1, 1, 1, 1, 1,
                        100, 0, -106, 3, 3, 1, 3, 1, 1, 1, 1, 1, 100, 0, -106, 3, 3, 1, 3, 1, 1, 1, 1, 1}, 0);
        ArrayList<Loadout> actualResult = new ArrayList<>();
        Loadout loadout = new Loadout();
        for (int i = 0; i < 3; i++) {
            actualResult.add(loadout);
        }
        for (int i = 0; i < 3; i++) {
            for (int j = i; j < 9; j++) {
                assertEquals(loadouts.get(i).getInv().get(0).getAmmo(),
                        actualResult.get(i).getInv().get(0).getAmmo());
            }
        }
    }

    @Test
    public void serializeRobot() {
        Robot robot = new Robot(50, 23, new Coord(400, 0), new Loadout(), 0);
        byte[] serializedRobot = Serializer.serializeRobot(robot);
        byte[] actualResult = new byte[]
                {0, 2, 1, -112, 0, 0, 100, 0, -106, 3, 3, 1, 3, 1, 1, 1, 1, 1, 1, 0, 45, 100, 0, 0};
        assertArrayEquals(serializedRobot, actualResult);

    }

    @Test
    public void deserializeRobot() {
        Robot robot = Serializer.deserializeRobot(new byte[]
                {0, 2, 1, -112, 0, 0, 100, 0, -106, 3, 3, 1, 3, 1, 1, 1, 1, 1, 1, 0, 45, 100, 0, 0});
        Robot actualResult = new Robot(50, 23, new Coord(400, 0), new Loadout(), 0);
        assertEquals(robot.getRobotId(), actualResult.getRobotId());
        assertEquals(robot.isDead(), actualResult.isDead());
        assertEquals((int) robot.getXPos(), (int) actualResult.getXPos());
        assertEquals((int) robot.getYPos(), (int) actualResult.getYPos());
        assertEquals(robot.getHealth(), actualResult.getHealth());
        assertEquals(robot.getEnergy(), actualResult.getEnergy());
        for (int i = 0; i < 9; i++) {
            assertEquals(robot.getAllWeapons().get(i).getAmmo(), actualResult.getAllWeapons().get(i).getAmmo());
        }
        assertEquals(robot.getWeaponSlot(), actualResult.getWeaponSlot());
        assertEquals(robot.getAngle(), actualResult.getAngle());
        assertEquals(robot.getPower(), actualResult.getPower());
        assertEquals(robot.getImageId(), actualResult.getImageId());
        assertEquals(robot.getTimer(), actualResult.getTimer());
    }

    @Test
    public void serializeRobots() {
        ArrayList<Robot> robots = new ArrayList<>();
        Robot robot = new Robot(50, 23, new Coord(400, 0), new Loadout(), 0);
        for (int i = 0; i < 3; i++) {
            robots.add(robot);
        }
        byte[] serializedRobots = Serializer.serializeRobots(robots);
        byte[] actualResult = new byte[]
                {0, 2, 1, -112, 0, 0, 100, 0, -106, 3, 3, 1, 3, 1, 1, 1, 1, 1, 1, 0, 45, 100, 0, 0,
                        0, 2, 1, -112, 0, 0, 100, 0, -106, 3, 3, 1, 3, 1, 1, 1, 1, 1, 1, 0, 45, 100, 0, 0,
                        0, 2, 1, -112, 0, 0, 100, 0, -106, 3, 3, 1, 3, 1, 1, 1, 1, 1, 1, 0, 45, 100, 0, 0};
        assertArrayEquals(serializedRobots, actualResult);
    }

    @Test
    public void deserializeRobots() {
        ArrayList<Robot> robots = Serializer.deserializeRobots(new byte[]
                {0, 2, 1, -112, 0, 0, 100, 0, -106, 3, 3, 1, 3, 1, 1, 1, 1, 1, 1, 0, 45, 100, 0, 0,
                        0, 2, 1, -112, 0, 0, 100, 0, -106, 3, 3, 1, 3, 1, 1, 1, 1, 1, 1, 0, 45, 100, 0, 0,
                        0, 2, 1, -112, 0, 0, 100, 0, -106, 3, 3, 1, 3, 1, 1, 1, 1, 1, 1, 0, 45, 100, 0, 0,
                        0, 2, 1, -112, 0, 0, 100, 0, -106, 3, 3, 1, 3, 1, 1, 1, 1, 1, 1, 0, 45, 100, 0, 0,
                        0, 2, 1, -112, 0, 0, 100, 0, -106, 3, 3, 1, 3, 1, 1, 1, 1, 1, 1, 0, 45, 100, 0, 0,
                        0, 2, 1, -112, 0, 0, 100, 0, -106, 3, 3, 1, 3, 1, 1, 1, 1, 1, 1, 0, 45, 100, 0, 0});
        ArrayList<Robot> actualResult = new ArrayList<>();
        Robot robot = new Robot(50, 23, new Coord(400, 0), new Loadout(), 0);
        for (int i = 0; i < 3; i++) {
            actualResult.add(robot);
        }
        for (int i = 0; i < 3; i++) {
            assertEquals(robots.get(i).getRobotId(), actualResult.get(i).getRobotId());
            assertEquals(robots.get(i).isDead(), actualResult.get(i).isDead());
            assertEquals((int) robots.get(i).getXPos(), (int) actualResult.get(i).getXPos());
            assertEquals((int) robots.get(i).getYPos(), (int) actualResult.get(i).getYPos());
            assertEquals(robots.get(i).getHealth(), actualResult.get(i).getHealth());
            assertEquals(robots.get(i).getEnergy(), actualResult.get(i).getEnergy());
            for (int j = 0; j < 9; j++) {
                assertEquals(robots.get(i).getAllWeapons().get(i).getAmmo(),
                        actualResult.get(i).getAllWeapons().get(i).getAmmo());
            }
            assertEquals(robots.get(i).getWeaponSlot(), actualResult.get(i).getWeaponSlot());
            assertEquals(robots.get(i).getAngle(), actualResult.get(i).getAngle());
            assertEquals(robots.get(i).getPower(), actualResult.get(i).getPower());
            assertEquals(robots.get(i).getImageId(), actualResult.get(i).getImageId());
            assertEquals(robots.get(i).getTimer(), actualResult.get(i).getTimer());
        }
    }

    @Test
    public void deserializeGameServer() {
        GameServer deserializedGameServer = Serializer.deserializeGameServer(new byte[]
                {1, 69, 85, 49, 50, 51, 52, 0, 82, 111, 98, 101, 114, 116, 0});
        GameServer actualResult = new GameServer("EU1234", 1, "Robert");
        assertEquals(deserializedGameServer.getName(), actualResult.getName());
        assertEquals(deserializedGameServer.getCapacity(), actualResult.getCapacity());
        assertEquals(deserializedGameServer.getHostName(), actualResult.getHostName());
    }

    @Test
    public void deserializeGameServers() {
        ArrayList<GameServer> gameServers = Serializer.deserializeGameServers(new byte[]
                {3, 1, 69, 85, 49, 50, 51, 52, 0, 82, 111, 98, 101, 114, 116, 0,
                        2, 85, 83, 55, 55, 55, 55, 0, 75, 97, 105, 0,
                        1, 65, 83, 73, 65, 50, 53, 48, 0, 74, 101, 114, 114, 121, 0}, 0);
        ArrayList<GameServer> actualResult = new ArrayList<>();
        actualResult.add(new GameServer("EU1234", 1, "Robert"));
        actualResult.add(new GameServer("US7777", 2, "Kai"));
        actualResult.add(new GameServer("ASIA250", 1, "Jerry"));
        for (int i = 0; i < 3; i++) {
            assertEquals(gameServers.get(i).getName(), actualResult.get(i).getName());
            assertEquals(gameServers.get(i).getCapacity(), actualResult.get(i).getCapacity());
            assertEquals(gameServers.get(i).getHostName(), actualResult.get(i).getHostName());
        }

    }

    @Test
    public void deserializePlayer() {
        Player player = Serializer.deserializePlayer(new byte[]
                {8, 6, 1, 82, 111, 98, 101, 114, 116});
        Player actualResult = new Player("Robert", 8, 6, 1);
        assertEquals(player.getUsername(), actualResult.getUsername());
        assertEquals(player.getWins(), actualResult.getWins());
        assertEquals(player.getLoses(), actualResult.getLoses());
        assertEquals(player.getDraws(), actualResult.getDraws());
    }

    @Test
    public void deserializeLeaderboard() {
        ArrayList<Player> leaderboard = Serializer.deserializeLeaderboard(new byte[]{
                2, 8, 6, 1, 82, 111, 98, 101, 114, 116, 0, 4, 4, 0, 75, 97, 105, 0}, 0);
        ArrayList<Player> actualResult = new ArrayList<>();
        actualResult.add(new Player("Robert", 8, 6, 1));
        actualResult.add(new Player("Kai", 4, 4, 0));
        for (int i = 0; i < 2; i++) {
            assertEquals(leaderboard.get(i).getUsername(), actualResult.get(i).getUsername());
            assertEquals(leaderboard.get(i).getWins(), actualResult.get(i).getWins());
            assertEquals(leaderboard.get(i).getLoses(), actualResult.get(i).getLoses());
            assertEquals(leaderboard.get(i).getDraws(), actualResult.get(i).getDraws());
        }
    }
}