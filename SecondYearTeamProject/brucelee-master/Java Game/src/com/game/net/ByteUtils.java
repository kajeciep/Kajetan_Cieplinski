package com.game.net;

import javafx.scene.input.KeyCode;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is used a lot when dealing with bytes. It stores a collection of bytes
 * in buffer which can grow by writing to the buffer or it can be extracted. It also provides
 * method to convert from bytes to primitive types.
 *
 * @author Robert Chiper
 */
public class ByteUtils {

    private List<Byte> buffer;

    /**
     * Initializes the collection of bytes.
     */
    public ByteUtils() {
        buffer = new ArrayList<>();
    }

    /**
     * @return the array which contains all of the bytes in the buffer
     */
    public byte[] getBuffer() {
        byte[] data = new byte[buffer.size()];
        for (int i = 0; i < buffer.size(); i++) {
            data[i] = buffer.get(i);
        }
        return data;
    }

    /**
     * Converts an ArrayList of bytes to an array of bytes.
     *
     * @param buffer the ArrayList of bytes
     * @return the array of bytes
     */
    public byte[] toByteArray(ArrayList<Byte> buffer) {
        byte[] data = new byte[buffer.size()];
        for (int i = 0; i < buffer.size(); i++) {
            data[i] = buffer.get(i);
        }
        return data;
    }

    /**
     * Creates a new buffer.
     */
    public void flush() {
        buffer = new ArrayList<>();
    }

    /**
     * @param data the array of bytes to be shrinked
     * @param offset the position where the shrinking needs to start
     * @param length the length of the new array
     * @return the shrinked array of bytes
     */
    public static byte[] shrinkByteArray(byte[] data, int offset, int length) {
        byte[] result = new byte[length];
        System.arraycopy(data, offset, result, 0, length);
        return result;
    }

    /**
     * @param data the byte to be written at the end of the buffer
     */
    public void write(byte data) {
        buffer.add(data);
    }

    /**
     * @param data the bytes to be written at the end of the buffer
     */
    public void write(byte[] data) {
        for (byte b : data) {
            buffer.add(b);
        }
    }

    /**
     * @param data the integer to be written at the end of the buffer
     */
    public void write(int data) {
        byte[] b = ByteBuffer.allocate(4).putInt(data).array();
        write(b);
    }

    /**
     * @param data the short to be written at the end of the buffer
     */
    public void write(short data) {
        byte[] b = ByteBuffer.allocate(2).putShort(data).array();
        write(b);
    }

    /**
     * Writes the bytes of the String to the end of the buffer and adds a byte of 0 at the end.
     *
     * @param data the String to be written at the end of the buffer
     */
    public void write(String data) {
        byte[] b = data.getBytes();
        write(b);
        write((byte) 0);
    }

    /**
     * @param data the long to be written at the end of the buffer
     */
    public void write(long data) {
        byte[] b = ByteBuffer.allocate(8).putLong(data).array();
        write(b);
    }

    /**
     * Writes a byte of 1 for true or a byte of 2 for false.
     *
     * @param data the boolean to be written at the end of the buffer
     */
    public void write(boolean data) {
        write(data ? (byte) 1 : (byte) 2);
    }

    /**
     * Writes the ASCII value of the KeyCode.
     *
     * @param data the KeyCode to be written at the of the buffer
     */
    public void write(KeyCode data) {
        switch (data) {
            case ENTER:
                write((byte) 0x0A);
                break;
            case SPACE:
                write((byte) 0x20);
                break;
            case LEFT:
                write((byte) 0x25);
                break;
            case UP:
                write((byte) 0x26);
                break;
            case RIGHT:
                write((byte) 0x27);
                break;
            case DOWN:
                write((byte) 0x28);
                break;
            case DIGIT1:
                write((byte) 0x31);
                break;
            case DIGIT2:
                write((byte) 0x32);
                break;
            case DIGIT3:
                write((byte) 0x33);
                break;
            case A:
                write((byte) 0x41);
                break;
            case D:
                write((byte) 0x44);
                break;
            case E:
                write((byte) 0x45);
                break;
            case Q:
                write((byte) 0x51);
                break;
            case S:
                write((byte) 0x53);
                break;
            case W:
                write((byte) 0x57);
                break;
            default:
                break;
        }
    }

    /**
     * @param data the bytes of the KeyCode
     * @return the KeyCode obtained from the bytes
     */
    public KeyCode getKeyCode(byte data) {
        switch (data) {
            case 0x0A:
                return KeyCode.ENTER;
            case 0x20:
                return KeyCode.SPACE;
            case 0x25:
                return KeyCode.LEFT;
            case 0x26:
                return KeyCode.UP;
            case 0x27:
                return KeyCode.RIGHT;
            case 0x28:
                return KeyCode.DOWN;
            case 0x31:
                return KeyCode.DIGIT1;
            case 0x32:
                return KeyCode.DIGIT2;
            case 0x33:
                return KeyCode.DIGIT3;
            case 0x41:
                return KeyCode.A;
            case 0x44:
                return KeyCode.D;
            case 0x45:
                return KeyCode.E;
            case 0x51:
                return KeyCode.Q;
            case 0x53:
                return KeyCode.S;
            case 0x57:
                return KeyCode.W;
            default:
                return null;
        }
    }

    /**
     * @param data the array of bytes which contains an int
     * @param offset the position where the bytes of the int start
     * @return the int
     */
    public static int getInt(byte[] data, int offset) {
        byte[] result = new byte[4];
        System.arraycopy(data, offset, result, 0, 4);
        ByteBuffer buffer = ByteBuffer.wrap(result);
        return buffer.getInt();
    }

    /**
     * @param data the array of bytes which contains an short
     * @param offset the position where the bytes of the short start
     * @return the short
     */
    public static short getShort(byte[] data, int offset) {
        byte[] result = new byte[2];
        System.arraycopy(data, offset, result, 0, 2);
        ByteBuffer buffer = ByteBuffer.wrap(result);
        return buffer.getShort();
    }

    /**
     * @param data the array of bytes which contains an long
     * @param offset the position where the bytes of the long start
     * @return the long
     */
    public static long getLong(byte[] data, int offset) {
        byte[] result = new byte[8];
        System.arraycopy(data, offset, result, 0, 8);
        ByteBuffer buffer = ByteBuffer.wrap(result);
        return buffer.getLong();
    }
}
