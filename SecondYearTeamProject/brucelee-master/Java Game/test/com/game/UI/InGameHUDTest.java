package com.game.UI;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

import com.game.physics.Robot;
import com.game.physics.Coord;
import com.game.state.Loadout;

public class InGameHUDTest {

	@Test
	public void testUpdateHUD() {
		ArrayList<Robot> robots = new ArrayList<Robot>();
		Loadout ld = new Loadout();
		Robot andrew = new Robot(0, 0, new Coord(0, 0), ld, 0);
		robots.add(andrew);
		for (int i = 0; i < 3; i++) {
			robots.add(new Robot(0, 0, new Coord(0, 0), ld, 0));
		}
		InGameHUD igh = new InGameHUD(robots);
		andrew.takeDamage(50);
		for (int i = 0; i < 40; i++) {
			andrew.decreaseEnergy();
		}
		andrew.setPower(20);
		andrew.setPlayerSlot(1);
		andrew.setPlayerSlot(1);
		igh.updateHUD(andrew);
		ArrayList<HUDComponent> hud;
		hud = igh.getHud();
		String currWeapon = hud.get(3).getHudImageName();
		String healthBar = hud.get(7).getHudImageName();
		String energyBar = hud.get(8).getHudImageName();
		String powerBar = hud.get(9).getHudImageName();
		assertEquals("teleporter_icon", currWeapon);
		assertEquals("HUDMediumBar", healthBar);
		assertEquals("HUDHighBar", energyBar);
		assertEquals("HUDLowBar", powerBar);
	}

	@Test
	public void testHealthBars() {
		ArrayList<Robot> robots = new ArrayList<Robot>();
		Loadout ld = new Loadout();
		for (int i = 0; i < 3; i++) {
			robots.add(new Robot(0, 0, new Coord(0, 0), ld, 0));
		}
		InGameHUD igh = new InGameHUD(robots);
		for (int i = 0; i < 3; i++) {
			robots.get(i).takeDamage(10 + (i * 30));
		}
		igh.updateHealthBars();
		ArrayList<HUDComponent> robotHuds = igh.hudRobots;
		String healthBar1 = robotHuds.get(0).getHudImageName();
		String healthBar2 = robotHuds.get(1).getHudImageName();
		String healthBar3 = robotHuds.get(2).getHudImageName();
		assertEquals("HUDHighBar", healthBar1);
		assertEquals("HUDMediumBar", healthBar2);
		assertEquals("HUDLowBar", healthBar3);
	}
}
