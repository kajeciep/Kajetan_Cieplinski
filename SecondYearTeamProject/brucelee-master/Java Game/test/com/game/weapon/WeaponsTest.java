package com.game.weapon;

import static org.junit.Assert.*;

import org.junit.Test;

import com.game.object.Projectile;
import com.game.object.SniperBullet;
import com.game.physics.Coord;
import com.game.weapon.Sniper;


public class WeaponsTest {
	
	@Test
	public void testProjectileCreation() {
		Sniper w = new Sniper(5);
		Coord location = new Coord(200,200);
		Projectile p = w.createProjectile(location, 0);
		assertSame(SniperBullet.class, p.getClass());
	}
	
	@Test
	public void testFired() {
		Sniper s = new Sniper(5);
		s.fired();
		int result = s.getAmmo();
		assertEquals(4, result);
	}
	
	@Test
	public void testRefill() {
		Sniper s = new Sniper(0);
		s.refillAmmo(5);
		int result = s.getAmmo();
		assertEquals(5, result);
	}
}
