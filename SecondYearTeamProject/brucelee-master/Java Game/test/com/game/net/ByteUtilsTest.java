package com.game.net;

import javafx.scene.input.KeyCode;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class ByteUtilsTest {

    private ByteUtils byteUtils;

    @Before
    public void init() {
        byteUtils = new ByteUtils();
    }

    @Test
    public void getBuffer() {
        byteUtils.flush();
        byteUtils.write((byte) 1);
        byteUtils.write((byte) 2);
        byte[] buffer = byteUtils.getBuffer();
        byte[] actualResult = new byte[]{1, 2};
        assertArrayEquals(buffer, actualResult);
    }

    @Test
    public void toByteArray() {
    }

    @Test
    public void flush() {
        byteUtils.flush();
        assertEquals(byteUtils.getBuffer().length, 0);
    }

    @Test
    public void shrinkByteArray() {
        byte[] shrinkedArray = ByteUtils.shrinkByteArray(new byte[]{1, 2, 3, 4, 5}, 2, 2);
        byte[] actualResult = new byte[]{3, 4};
        assertArrayEquals(shrinkedArray, actualResult);
    }

    @Test
    public void write() {
        byteUtils.flush();
        byteUtils.write((byte) 1);
        byte data = byteUtils.getBuffer()[0];
        byte actualResult = 1;
        assertEquals(data, actualResult);
    }

    @Test
    public void write1() {
        byteUtils.flush();
        byteUtils.write(new byte[]{1, 2});
        byte[] data = byteUtils.getBuffer();
        byte[] actualResult = new byte[]{1, 2};
        assertArrayEquals(data, actualResult);
    }

    @Test
    public void write2() {
        byteUtils.flush();
        byteUtils.write(1);
        byte[] data = byteUtils.getBuffer();
        byte[] actualResult = new byte[]{0, 0, 0, 1};
        assertArrayEquals(data, actualResult);
    }

    @Test
    public void write3() {
        byteUtils.flush();
        byteUtils.write((short) 1);
        byte[] data = byteUtils.getBuffer();
        byte[] actualResult = new byte[]{0, 1};
        assertArrayEquals(data, actualResult);
    }

    @Test
    public void write4() {
        byteUtils.flush();
        byteUtils.write((long) 1);
        byte[] data = byteUtils.getBuffer();
        byte[] actualResult = new byte[]{0, 0, 0, 0, 0, 0, 0, 1};
        assertArrayEquals(data, actualResult);
    }

    @Test
    public void write5() {
        byteUtils.flush();
        byteUtils.write("Robert");
        byte[] data = byteUtils.getBuffer();
        byte[] actualResult = new byte[]{82, 111, 98, 101, 114, 116, 0};
        assertArrayEquals(data, actualResult);
    }

    @Test
    public void write6() {
        byteUtils.flush();
        byteUtils.write(true);
        byte[] data = byteUtils.getBuffer();
        byte[] actualResult = new byte[]{1};
        assertArrayEquals(data, actualResult);
    }

    @Test
    public void write7() {
        byteUtils.flush();
        byteUtils.write(KeyCode.A);
        byte[] data = byteUtils.getBuffer();
        byte[] actualResult = new byte[]{0x41};
        assertArrayEquals(data, actualResult);
    }

    @Test
    public void getKeyCode() {
        KeyCode keyCode = byteUtils.getKeyCode((byte) 0x41);
        KeyCode actualResult = KeyCode.A;
        assertEquals(keyCode, actualResult);

    }

    @Test
    public void getInt() {
        int data = ByteUtils.getInt(new byte[]{5, 0, 0, 0, 1}, 1);
        int actualResult = 1;
        assertEquals(data, actualResult);
    }

    @Test
    public void getShort() {
        short data = ByteUtils.getShort(new byte[]{5, 0, 0, 0, 1}, 3);
        short actualResult = 1;
        assertEquals(data, actualResult);
    }

    @Test
    public void getLong() {
        long data = ByteUtils.getLong(new byte[]{5, 0, 0, 0, 0, 0, 0, 0, 1}, 1);
        long actualResult = 1;
        assertEquals(data, actualResult);
    }
}