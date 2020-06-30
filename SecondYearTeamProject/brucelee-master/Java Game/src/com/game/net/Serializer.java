package com.game.net;

import com.game.object.Explosion;
import com.game.physics.Coord;
import com.game.physics.Robot;
import com.game.state.GameState;
import com.game.state.Loadout;
import com.game.weapon.*;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * This class is used for converting to and from bytes the various objects send
 * between the clients and the server.
 *
 * @author Robert Chiper
 */
public class Serializer {

    /**
     * Converts a Loadout to bytes.
     *
     * @param loadout the Loadout to be converted
     * @return array of 12 bytes
     */
    public static byte[] serializeLoadout(Loadout loadout) {
        ByteUtils byteUtils = new ByteUtils();
        byteUtils.write((byte) loadout.getStartHP());
        byteUtils.write((short) loadout.getMaxEnergy());
        for (Weapon weapon : loadout.getInv()) {
            byteUtils.write((byte) weapon.getAmmo());
        }
        return byteUtils.getBuffer();
    }

    /**
     * Converts bytes to Loadout.
     *
     * @param data the array of bytes representing the loadout
     * @return the loadout
     */
    public static Loadout deserializeLoadout(byte[] data) {
        return new Loadout(data[0], ByteUtils.getShort(data, 1),
                new int[]{data[3], data[4], data[5],
                        data[6], data[7], data[8],
                        data[9], data[10], data[11]});
    }

    /**
     * Converts a Collection of Loadout objects to bytes.
     *
     * @param loadouts the ArrayList of loadouts
     * @return array of 12 * number of loadouts bytes
     */
    public static byte[] serializeLoadouts(ArrayList<Loadout> loadouts) {
        ByteUtils byteUtils = new ByteUtils();
        for (Loadout loadout : loadouts) {
            byteUtils.write(serializeLoadout(loadout));
        }
        return byteUtils.getBuffer();
    }

    /**
     * Converts an array of bytes that contains 3 loadouts to a Collection.
     *
     * @param data   the array containing the loadouts
     * @param offset the position where the first loadout starts
     * @return the ArrayList of Loadout objects
     */
    public static ArrayList<Loadout> deserializeLoadouts(byte[] data, int offset) {
        ArrayList<Loadout> loadouts = new ArrayList<>();
        for (int i = offset; i < offset + 36; i += 12) {
            loadouts.add(deserializeLoadout(ByteUtils.shrinkByteArray(data, i, 12)));
        }
        return loadouts;

    }

    /**
     * Converts a Robot object to bytes.
     *
     * @param robot the object to be converted
     * @return array of 24 bytes
     */
    public static byte[] serializeRobot(Robot robot) {
        ByteUtils byteUtils = new ByteUtils();
        byteUtils.write((byte) robot.getRobotId());
        byteUtils.write(robot.isDead());
        byteUtils.write((short) robot.getXPos());
        byteUtils.write((short) robot.getYPos());
        byteUtils.write((byte) robot.getHealth());
        byteUtils.write((short) robot.getEnergy());
        for (Weapon weapon : robot.getAllWeapons()) {
            byteUtils.write((byte) weapon.getAmmo());
        }
        byteUtils.write((byte) robot.getWeaponSlot());
        byteUtils.write((short) robot.getAngle());
        byteUtils.write((byte) robot.getPower());
        byteUtils.write((byte) robot.getImageId());
        byteUtils.write((byte) robot.getTimer());

        return byteUtils.getBuffer();
    }

    /**
     * Converts an array of bytes to a Robot object.
     *
     * @param data the array containing the bytes of the robot
     * @return the Robot object
     */
    public static Robot deserializeRobot(byte[] data) {
        boolean isDead = false;
        if (data[1] == 0x01)
            isDead = true;
        short xPos = ByteUtils.getShort(data, 2);
        short yPos = ByteUtils.getShort(data, 4);
        Robot robot = new Robot(100, 45, new Coord(xPos, yPos), new Loadout(), (int) data[0]);
        robot.setHealth((int) data[6]);
        robot.setEnergy((int) ByteUtils.getShort(data, 7));
        int index = 0;
        for (Weapon weapon : robot.getAllWeapons()) {
            weapon.setAmmo(data[index + 9]);
            index++;
        }
        robot.setIsDead(isDead);
        robot.setSlot((int) data[18]);
        robot.setAngle((int) ByteUtils.getShort(data, 19));
        robot.setPower((int) data[21]);
        robot.setImageId(data[22]);
        robot.setTimer(data[23]);
        return robot;
    }

    /**
     * Converts a Collection of Robot objects to bytes.
     *
     * @param robots the ArrayList of robots
     * @return array of 24 * number of robots bytes
     */
    public static byte[] serializeRobots(ArrayList<Robot> robots) {
        ByteUtils byteUtils = new ByteUtils();
        for (Robot robot : robots)
            byteUtils.write(serializeRobot(robot));
        return byteUtils.getBuffer();
    }

    /**
     * Converts a byte array to a Collection of Robot objects.
     *
     * @param data the byte array containing the robots
     * @return the ArrayList of Robot objects
     */
    public static ArrayList<Robot> deserializeRobots(byte[] data) {
        int offset = 0;
        int robotsSize = GameState.teamSize * GameState.noTeams * 24;
        ArrayList<Robot> robots = new ArrayList<>();
        for (int i = offset; i < robotsSize + offset; i += 24) {
            robots.add(deserializeRobot(ByteUtils.shrinkByteArray(data, i, 24)));
        }
        return robots;
    }

    /**
     * Converts a byte array to GameServer.
     *
     * @param data the byte array containing the GameServer
     * @return the GameServer
     */
    public static GameServer deserializeGameServer(byte[] data) {
        ByteUtils byteUtils = new ByteUtils();
        int index = 1;
        ArrayList<String> strings = new ArrayList<>();
        while (index < data.length) {
            if (data[index] == 0) {
                strings.add(new String(byteUtils.getBuffer()));
                byteUtils.flush();
            } else
                byteUtils.write(data[index]);
            index++;
        }
        return new GameServer(strings.get(0), data[0], strings.get(1));
    }

    /**
     * Converts a byte array to a collection of GameServers.
     *
     * @param data   the byte array containing the Collection of GameServer objects
     * @param offset the start position
     * @return the ArrayList of GameServer objects
     */
    public static ArrayList<GameServer> deserializeGameServers(byte[] data, int offset) {
        ArrayList<GameServer> gameServers = new ArrayList<>();

        int currentSize = 0;
        int listSize = data[offset++];
        int leftIndex = offset;
        int rightIndex = offset;
        int zeros = 0;
        while (currentSize < listSize) {
            while (zeros < 2) {
                if (data[rightIndex] == 0x00) {
                    zeros++;
                }
                rightIndex++;
            }
            zeros = 0;
            gameServers.add(deserializeGameServer(ByteUtils.shrinkByteArray(data, leftIndex, rightIndex - leftIndex)));
            leftIndex = rightIndex;
            currentSize++;
        }
        return gameServers;
    }

    /**
     * Converts a byte array to a Player object.
     *
     * @param data the array of bytes containing the Player object
     * @return the Player object
     */
    public static Player deserializePlayer(byte[] data) {
        ByteUtils byteUtils = new ByteUtils();
        int index = 3;
        while (index < data.length) {
            byteUtils.write(data[index]);
            index++;
        }
        return new Player(new String(byteUtils.getBuffer()), data[0], data[1], data[2]);
    }

    /**
     * Converts a byte array to a Collection of Player objects.
     *
     * @param data the byte array containing the Player objects
     * @return the ArrayList of Player objects
     */
    public static ArrayList<Player> deserializeLeaderboard(byte[] data, int offset) {
        ArrayList<Player> players = new ArrayList<>();

        int currentSize = 0;
        int listSize = data[offset++];
        int leftIndex = offset;
        int rightIndex = offset + 3;
        while (currentSize < listSize) {
            while (data[rightIndex] != 0)
                rightIndex++;

            players.add(deserializePlayer(ByteUtils.shrinkByteArray(data, leftIndex, rightIndex - leftIndex)));
            leftIndex = ++rightIndex;
            rightIndex += 3;
            currentSize++;
        }
        return players;
    }

    public static byte[] serializeExplosions(ArrayList<Explosion> explosions) {
        ByteUtils byteUtils = new ByteUtils();
        byteUtils.write((byte) explosions.size());
        for (Explosion explosion : explosions) {
            byteUtils.write((short) explosion.getXPos());
            byteUtils.write((short) explosion.getYPos());
            byteUtils.write((short) explosion.getRadius());
            System.out.println("Radius: " + (short) explosion.getRadius());
        }
        return byteUtils.getBuffer();
    }
}
