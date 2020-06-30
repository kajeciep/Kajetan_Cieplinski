package com.game.net;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class GameServerTest {

    private GameServer gameServer;

    @Before
    public void init() {
        gameServer = new GameServer("EU1234", 2, "Robert");
    }

    @Test
    public void getName() {
        assertEquals(gameServer.getName(), "EU1234");
    }

    @Test
    public void getCapacity() {
        assertEquals(gameServer.getCapacity(), 2);
    }

    @Test
    public void getHostName() {
        assertEquals(gameServer.getHostName(), "Robert");
    }
}