package com.game.AI;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.game.physics.Coord;

import javafx.scene.input.KeyCode;

public class PlanTest {

	Plan p;

	@Before
	public void setUp() throws Exception {
		p = new Plan(60, 45);
		ArrayList<Coord> mov = new ArrayList<Coord>();
		mov.add(new Coord(200,200));
		mov.add(new Coord(250,200));
		mov.add(new Coord(300,200));
		p.setMovement(mov);
	}

	@Test
	public void testHasMoreCommandsTrue() {
		assertTrue(p.hasMoreCommands());
	}
	
	@Test
	public void testHasMoreCommandsFalse() {
		p.getNextCode(new Coord(200,200));
		p.getNextCode(new Coord(250,200));
		p.getNextCode(new Coord(300,200));
		assertFalse(p.hasMoreCommands());
	}

	@Test
	public void testGetNextCodeRight() {
		KeyCode code = p.getNextCode(new Coord(150, 200));
		assertEquals(KeyCode.D, code);
		assertEquals(0, p.moveIterator);
	}
	
	@Test
	public void testGetNextCodeLeft() {
		KeyCode code = p.getNextCode(new Coord(210, 200));
		assertEquals(KeyCode.A, code);
		assertEquals(0, p.moveIterator);
	}
	
	@Test
	public void testGetNextCodeRightInc() {
		KeyCode code = p.getNextCode(new Coord(198, 200));
		assertEquals(KeyCode.D, code);
		assertEquals(1, p.moveIterator);
	}
	
	@Test
	public void testGetNextCodeLeftInc() {
		KeyCode code = p.getNextCode(new Coord(202, 200));
		assertEquals(KeyCode.A, code);
		assertEquals(1, p.moveIterator);
	}

}
