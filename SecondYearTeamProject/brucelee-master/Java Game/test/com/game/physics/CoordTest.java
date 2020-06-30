package com.game.physics;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class CoordTest {

	static Coord c1;
	static Coord c2;
	
	@BeforeClass
	public static void setUp() throws Exception {
		c1 = new Coord(200,200);
		c2 = new Coord(400,400);
	}

	@Test
	public void testCoord() {
		Coord test = new Coord(500,500);
		assertEquals(500, test.getX());
		assertEquals(500, test.getY());
	}

	@Test
	public void testGetDistance1() {
		double distance = c1.getDistance(c2);
		assertEquals(282.842,distance,0.01);
	}

}
