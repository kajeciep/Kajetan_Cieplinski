package com.game.physics;

import static org.junit.Assert.*;

import java.util.Random;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import com.game.UI.JavaFXTestingRule;
import com.game.state.GameWorld;
import com.game.state.Loadout;

public class RobotTest {

	Robot r;
	GameWorld w;

	@Rule public JavaFXTestingRule javafxRule = new JavaFXTestingRule();
	

	@Before
	public void setUp() throws Exception {
		r = new Robot(50, 23, new Coord(200, 200), new Loadout(), 0);
		w = new GameWorld(true, 0, true);
	}

	@Test
	public void testTakeDamage() {
		r.takeDamage(30);
		assertEquals(70, r.getHealth());
	}

	@Test
	public void testMoveLeft() {
		r.moveLeft();
		assertEquals(-120, r.xVelocity, 0.001);
	}

	@Test
	public void testMoveRight() {
		r.moveRight();
		assertEquals(120, r.xVelocity, 0.001);
	}

	@Test
	public void testDecreaseEnergyFull() {
		r.decreaseEnergy();
		assertEquals(149, r.getEnergy(), 0.001);
	}

	@Test
	public void testDecreaseEnergyEmpty() {
		r.setEnergy(0);
		r.decreaseEnergy();
		assertEquals(0, r.getEnergy());
	}

	@Test
	public void testDecreaseEnergyJumpFull() {
		r.decreaseEnergyJump();
		assertEquals(140, r.getEnergy(), 0.01);
	}

	@Test
	public void testDecreaseEnergyJumpEmpty() {
		r.setEnergy(0);
		r.decreaseEnergyJump();
		assertEquals(0, r.getEnergy(), 0.01);
	}

	@Test
	public void testSetPlayerSlotIncrease() {
		r.setPlayerSlot(1);
		assertEquals(2, r.getSlot());
	}

	@Test
	public void testSetPlayerSlotDecrease() {
		r.setPlayerSlot(0);
		assertEquals(0, r.getSlot());
	}

	@Test
	public void testSetPlayerSlotLoopIncrease() {
		r.setSlot(8);
		r.setPlayerSlot(1);
		assertEquals(0, r.getSlot());
	}

	@Test
	public void testSetPlayerSlotLoopDecrease() {
		r.setSlot(0);
		r.setPlayerSlot(0);
		assertEquals(8, r.getSlot());
	}

	@Test
	public void testChangeAngleRightInc() {
		r.changeAngle(1);
		assertEquals(46, r.getAngle(), 0.001);
	}

	@Test
	public void testChangeAngleRightDec() {
		r.changeAngle(-1);
		assertEquals(44, r.getAngle(), 0.001);
	}

	@Test
	public void testChangeAngleLeftInc() {
		r.swapAngleLeft();
		r.setFacingLeft(true);
		r.changeAngle(1);
		assertEquals(134, r.getAngle(), 0.001);
	}

	@Test
	public void testChangeAngleLeftDec() {
		r.swapAngleLeft();
		r.setFacingLeft(true);
		r.changeAngle(-1);
		assertEquals(136, r.getAngle(), 0.001);
	}

	@Test
	public void testSwapAngleLeft() {
		r.swapAngleLeft();
		assertEquals(135, r.getAngle(), 0.001);
	}

	@Test
	public void testSwapAngleRight() {
		r.swapAngleRight();
		assertEquals(45, r.getAngle(), 0.001);
	}

	@Test
	public void testChangePowerSniper() {
		r.changePower(-10);
		assertEquals(r.getMaxPower(), r.getPower());
	}
	
	@Test
	public void testChangePowerInc() {
		r.setSlot(0);
		r.setPower(50);
		r.changePower(1);
		assertEquals(51, r.getPower());
	}
	
	@Test
	public void testChangePowerMax() {
		r.setSlot(0);
		r.changePower(10);
		assertEquals(r.getMaxPower(), r.getPower());
	}
	
	@Test
	public void testChangePowerMin() {
		r.setSlot(0);
		r.setPower(r.getMinPower());
		r.changePower(-10);
		assertEquals(r.getMinPower(), r.getPower());
	}

	@Test
	public void testPickupSupplies() {
		Random rand = new Random(0);
		r.r = rand;
		r.setHealth(50);
		r.pickupSupplies();
		r.setSlot(6);
		assertEquals(r.getWeapon().getAmmo(), 2);
		assertEquals(75, r.getHealth());
	}

	@Test
	public void testStartTurn() {
		r.setEnergy(0);
		r.startTurn();
		assertEquals(150, r.getEnergy());
	}

	@Test
	public void testStepX() {
		r.xVelocity = 200;
		r.stepX(w, 1);
		assertEquals(400, r.getXPos(), 0.001);
	}

	@Test
	public void testHasCollisionXTrue() {
		r.setYPos(900);
		boolean result = r.hasCollisionX(w);
		assertTrue(result);
	}
	
	@Test
	public void testHasCollisionXFalse() {
		boolean result = r.hasCollisionX(w);
		assertFalse(result);
	}

	@Test
	public void testStepY() {
		r.yVelocity = 100;
		r.stepY(w, 1);
		assertEquals(100, r.yPos, 0.001);
		assertEquals(90.19, r.yVelocity, 0.001);
	}

	@Test
	public void testHasCollisionYFalse() {
		boolean result = r.hasCollisionY(w);
		assertFalse(result);
	}
	
	@Test
	public void testHasCollisionYTrue() {
		r.setYPos(900);
		boolean result = r.hasCollisionX(w);
		assertTrue(result);
	}

	@Test
	public void testTakeFallDamage() {
		r.setYVelocity(-300);
		r.takeFallDamage();
		assertEquals(80, r.getHealth());
	}

}
