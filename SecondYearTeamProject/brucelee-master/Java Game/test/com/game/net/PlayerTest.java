package com.game.net;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class PlayerTest {

    private Player player;

    @Before
    public void init() {
        player = new Player("Robert", 6, 3, 1);
    }

    @Test
    public void compareTo() {
        Player betterPlayer = new Player("Kai", 8, 1, 1);
        assertEquals(player.compareTo(betterPlayer), 20);
    }

    @Test
    public void getWinRatio() {
        assertEquals(player.getWinRatio(), 60);
    }
}