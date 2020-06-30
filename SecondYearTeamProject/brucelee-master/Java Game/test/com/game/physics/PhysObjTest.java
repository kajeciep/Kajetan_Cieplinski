package com.game.physics;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.game.state.GameState;

import javafx.scene.shape.Rectangle;

public class PhysObjTest {

	PhysicsObject obj;

	@Before
	public void setUp() throws Exception {
		obj = new PhysicsObject(6, 6, new Coord(200, 200), false, "testProjectile");
	}

	@Test
	public void testGetPosition() {
		assertEquals(200, obj.xPos, 0.001);
		assertEquals(200, obj.yPos, 0.001);
	}

	@Test
	public void testStopNFalling() {
		obj.setVelocity(100);
		obj.setFalling(false);
		obj.stop();
		assertEquals(0, obj.xVelocity, 0.001);
	}

	@Test
	public void testStopFalling() {
		obj.setVelocity(100);
		obj.setFalling(true);
		obj.stop();
		assertEquals(100, obj.xVelocity, 0.001);
	}

	@Test
	public void testMoveObject() {
		obj.launch(90, 300);
		obj.moveObject(1);
		assertEquals(200, obj.getXPos(), 0.01);
		assertEquals(-100, obj.getYPos(), 0.01);
		assertEquals(290.19, obj.getYVelocity(), 0.01);
		assertEquals(0,  obj.getXVelocity(), 0.01);	
	}

	@Test
	public void testLaunch() {
		obj.launch(90, 300);
		assertEquals(300, obj.getYVelocity(), 0.01);
		assertEquals(0,obj.getXVelocity(), 0.01);
	}

	@Test
	public void testGetCentre() {
		Coord result = obj.getCentre();
		assertEquals(203, result.getX());
		assertEquals(203, result.getY());
	}


	@Test
	public void testFrictionFalling() {
		obj.isFalling = true;
		obj.xVelocity = 500;
		obj.friction();
		assertEquals(500, obj.xVelocity, 0.001);
	}
	
	@Test
	public void testFrictionNFalling() {
		obj.isFalling = false;
		obj.xVelocity = 500;
		obj.friction();
		assertEquals(400, obj.xVelocity, 0.001);
	}

	@Test
	public void testResetValues() {
		obj.xVelocity = 100;
		obj.yVelocity = 100;
		obj.resetValues();
		double result = obj.xVelocity + obj.yVelocity;
		assertEquals(0, result, 0.001);
	}
	
	@Test
	public void testCollisionBox() {
		obj.yPos = 0;
		Rectangle result = obj.getCollisionBox();
		assertEquals(200, result.getX(), 0.001);
		assertEquals(0, result.getY(), 0.001);
	}

}
