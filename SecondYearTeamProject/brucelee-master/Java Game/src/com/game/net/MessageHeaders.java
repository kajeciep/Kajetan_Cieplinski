package com.game.net;

/**
 * A data class for all the possible message headers.
 *
 * @author Robert Chiper
 */
public class MessageHeaders {
    public final static byte[] PACKET_HEADER = new byte[]{0x12, 0x11};

    public final static byte CREATE_SERVER = 0x01;
    public final static byte JOIN_SERVER = 0x02;
    public final static byte SERVER_NAME_IN_USE = 0x03;
    public final static byte SERVER_NOT_FOUND = 0x04;
    public final static byte SERVER_FULL = 0x05;
    public final static byte INVALID_PASSWORD = 0x06;

    public final static byte LEAVE_GAME = 0x07;

    public final static byte CONNECTION = 0x11;
    public final static byte SERVERS_LIST = 0x12;
    public final static byte LEADERBOARD = 0x13;
    public final static byte PORT_PACKET = 0x14;
    public final static byte INPUT_PACKET = 0x15;

    public final static byte BEGIN_GAME = 0x16;
    public final static byte GAME_STATE = 0x17;
    public final static byte TURN_CHANGE = 0x19;
    public final static byte WEAPON_FIRED = 0x1A;


    public final static byte FORCE_QUIT = 0x1B;
    public final static byte GAME_ENDED = 0x1C;

}
