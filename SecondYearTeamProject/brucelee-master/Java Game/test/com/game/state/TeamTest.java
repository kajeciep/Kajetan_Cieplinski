package com.game.state;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.game.physics.Coord;
import com.game.physics.Robot;

public class TeamTest {
	
	Team t;

	@Before
	public void setUp() throws Exception {
		ArrayList<Robot> members = new ArrayList<Robot>();
		members.add(new Robot(50, 23, new Coord(200, 200), new Loadout(), 0));
		members.add(new Robot(50, 23, new Coord(200, 200), new Loadout(), 0));
		members.add(new Robot(50, 23, new Coord(200, 200), new Loadout(), 0));
		t = new Team(members);
	}

	@Test
	public void testGetNextPlayer() {
		Robot robot = t.getRobots().get(1);
		assertEquals(robot, t.getNextPlayer());
	}
	
	@Test
	public void testGetNextPlayerDead() {
		t.getRobots().get(1).setHealth(0);
		Robot robot = t.getRobots().get(2);
		assertEquals(robot, t.getNextPlayer());
	}
	
	@Test
	public void testGetNextPlayerLoop() {
		Robot robot = t.getRobots().get(0);
		t.getNextPlayer();
		t.getNextPlayer();
		assertEquals(robot, t.getNextPlayer());
	}

	@Test
	public void testRemoveRobot() {
		Robot robot = t.getRobots().get(0);
		t.removeRobot(robot);
		assertFalse(t.getRobots().contains(robot));
	}

	@Test
	public void testHasLostTrue() {
		t.removeRobot(t.getRobots().get(0));
		t.removeRobot(t.getRobots().get(0));
		t.removeRobot(t.getRobots().get(0));
		assertTrue(t.hasLost());
	}
	
	@Test
	public void testHasLostFalse() {
		assertFalse(t.hasLost());
	}

}
