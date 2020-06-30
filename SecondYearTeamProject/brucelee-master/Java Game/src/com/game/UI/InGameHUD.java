package com.game.UI;

import java.util.ArrayList;

import com.game.UI.HUDComponent;
import com.game.physics.Coord;
import com.game.physics.Robot;
import com.game.weapon.Weapon;

/**
 * Keeps all HUDComponents together in one class, so that they can all be
 * updated easily and kept separate from other classes to prevent making a class
 * too big.
 * 
 * @author Kai Cieplinski
 *
 */
public class InGameHUD {

	private String leftBoxImg = "HUDBox";
	private String leftWeaponImg = "sniper_icon";
	private String currentBoxImg = "HUDCurrentBox";
	private String currentWeaponImg = "sniper_icon";
	private String rightBoxImg = "HUDBox";
	private String rightWeaponImg = "sniper_icon";
	private String robotInfoImg = "HUDRobotInfo";
	private String healthBarImg = "HUDHighBar";
	private String energyBarImg = "HUDHighBar";
	private String powerBarImg = "HUDHighBar";
	private String ammo100Img = "HUD0";
	private String ammo10Img = "HUD0";
	private String ammo1Img = "HUD0";

	private ArrayList<HUDComponent> hud;
	protected ArrayList<HUDComponent> hudRobots;
	private HUDComponent leftBox;
	private HUDComponent leftWeapon;
	private HUDComponent currentBox;
	private HUDComponent currentWeapon;
	private HUDComponent rightBox;
	private HUDComponent rightWeapon;
	private HUDComponent robotInfo;
	private HUDComponent healthBar;
	private HUDComponent energyBar;
	private HUDComponent powerBar;
	private HUDComponent ammo100;
	private HUDComponent ammo10;
	private HUDComponent ammo1;

	/**
	 * Constructor for the InGameHUD. :CAUTION: This will specifically create an
	 * InGameHUD based on pre-determined values, and therefore is only for use for
	 * SolarLegends and needs to be changed completely to be adapted to other games.
	 * 
	 * @param robots
	 *            all robots that will have health bars that show on screen.
	 */
	public InGameHUD(ArrayList<Robot> robots) {

		hud = new ArrayList<HUDComponent>();

		leftBox = new HUDComponent(leftBoxImg, new Coord(653, 0), false);
		leftWeapon = new HUDComponent(leftWeaponImg, new Coord(663, 10), false);
		currentBox = new HUDComponent(currentBoxImg, new Coord(803, 0), false);
		currentWeapon = new HUDComponent(currentWeaponImg, new Coord(813, 22), false);
		rightBox = new HUDComponent(rightBoxImg, new Coord(953, 0), false);
		rightWeapon = new HUDComponent(rightWeaponImg, new Coord(963, 10), false);
		robotInfo = new HUDComponent(robotInfoImg, new Coord(1103, 0), false);
		healthBar = new HUDComponent(healthBarImg, new Coord(1163, 14), true);
		energyBar = new HUDComponent(energyBarImg, new Coord(1163, 51), true);
		powerBar = new HUDComponent(powerBarImg, new Coord(1163, 88), true);
		ammo100 = new HUDComponent(ammo100Img, new Coord(1164, 125), false);
		ammo10 = new HUDComponent(ammo10Img, new Coord(1173, 125), false);
		ammo1 = new HUDComponent(ammo1Img, new Coord(1182, 125), false);

		hud.add(leftBox);
		hud.add(leftWeapon);
		hud.add(currentBox);
		hud.add(currentWeapon);
		hud.add(rightBox);
		hud.add(rightWeapon);
		hud.add(robotInfo);
		hud.add(healthBar);
		hud.add(energyBar);
		hud.add(powerBar);
		hud.add(ammo100);
		hud.add(ammo10);
		hud.add(ammo1);

		hudRobots = new ArrayList<HUDComponent>();

		for (Robot r : robots) {
			HUDComponent robotHUD = new HUDComponent(r);
			hudRobots.add(robotHUD);
		}

	}

	/**
	 * Returns the current HUD as an ArrayList<HUDComponent>.
	 * 
	 * @return returns the HUD as an ArrayList<HUDComponent>.
	 */
	public ArrayList<HUDComponent> getHud() {
		return hud;
	}

	/**
	 * Sets the current HUD to a new HUD.
	 * 
	 * @param hud
	 *            the HUD which will become the new HUD.
	 */
	public void setHud(ArrayList<HUDComponent> hud) {
		this.hud = hud;
	}

	/**
	 * Updates the whole HUD, except the health bars of all non-current robots.
	 * Receives a Robot as an object, which is used for values required to update
	 * the HUD.
	 * 
	 * @param currentCharacter
	 *            the current Robot who's turn it is.
	 */
	public void updateHUD(Robot currentCharacter) {
		// Current Robot
		ArrayList<Weapon> weapons = currentCharacter.getAllWeapons();
		int leftWeaponSlot;
		int currentWeaponSlot = currentCharacter.getSlot();
		int rightWeaponSlot;
		int lastWeapon = (weapons.size() - 1);
		double healthPercentage;
		double energyPercentage;
		double powerPercentage;
		String currentAmmo;
		int currentAmmoLength;
		String currentAmmo100 = "HUD";
		String currentAmmo10 = "HUD";
		String currentAmmo1 = "HUD";

		if (currentWeaponSlot == 0) {
			leftWeaponSlot = lastWeapon;
		} else {
			leftWeaponSlot = currentWeaponSlot - 1;
		}

		if (currentWeaponSlot == lastWeapon) {
			rightWeaponSlot = 0;
		} else {
			rightWeaponSlot = currentWeaponSlot + 1;
		}

		healthPercentage = (double) currentCharacter.getHealth() / (double) currentCharacter.getMaxHealth();
		energyPercentage = (double) currentCharacter.getEnergy() / (double) currentCharacter.getMaxEnergy();
		double minPower = (double) currentCharacter.getMinPower();
		double currentPower = (double) currentCharacter.getPower() - minPower;
		double maxPower = (double) currentCharacter.getMaxPower() - minPower;
		powerPercentage = currentPower / maxPower;
		healthBar.setPercentage(healthPercentage);
		energyBar.setPercentage(energyPercentage);
		powerBar.setPercentage(powerPercentage);

		currentAmmo = Integer.toString(currentCharacter.getWeapon().getAmmo());
		currentAmmoLength = currentAmmo.length();

		if (currentAmmoLength == 3) {
			currentAmmo100 = currentAmmo100.concat(currentAmmo.substring(0, 1));
			currentAmmo10 = currentAmmo10.concat(currentAmmo.substring(1, 2));
			currentAmmo1 = currentAmmo1.concat(currentAmmo.substring(2, 3));
		} else if (currentAmmoLength == 2) {
			currentAmmo100 = currentAmmo100.concat("0");
			currentAmmo10 = currentAmmo10.concat(currentAmmo.substring(0, 1));
			currentAmmo1 = currentAmmo1.concat(currentAmmo.substring(1, 2));
		} else if (currentAmmoLength == 1) {
			currentAmmo100 = currentAmmo100.concat("0");
			currentAmmo10 = currentAmmo10.concat("0");
			currentAmmo1 = currentAmmo1.concat(currentAmmo);
		} else {
			currentAmmo100 = currentAmmo100.concat("0");
			currentAmmo10 = currentAmmo10.concat("0");
			currentAmmo1 = currentAmmo1.concat("0");
		}

		leftWeapon.setHudImage(weapons.get(leftWeaponSlot).getIconName());
		currentWeapon.setHudImage(weapons.get(currentWeaponSlot).getIconName());
		rightWeapon.setHudImage(weapons.get(rightWeaponSlot).getIconName());
		if (healthPercentage >= 0.67) {
			healthBar.setHudImage("HUDHighBar");
		} else if (healthPercentage >= 0.33) {
			healthBar.setHudImage("HUDMediumBar");
		} else {
			healthBar.setHudImage("HUDLowBar");
		}
		if (energyPercentage >= 0.67) {
			energyBar.setHudImage("HUDHighBar");
		} else if (energyPercentage >= 0.33) {
			energyBar.setHudImage("HUDMediumBar");
		} else {
			energyBar.setHudImage("HUDLowBar");
		}
		if (powerPercentage >= 0.67) {
			powerBar.setHudImage("HUDHighBar");
		} else if (powerPercentage >= 0.33) {
			powerBar.setHudImage("HUDMediumBar");
		} else {
			powerBar.setHudImage("HUDLowBar");
		}
		ammo100.setHudImage(currentAmmo100);
		ammo10.setHudImage(currentAmmo10);
		ammo1.setHudImage(currentAmmo1);
	}

	/**
	 * Updates the health bar HUDComponents of all Robots (including current Robot
	 * as well).
	 */
	public void updateHealthBars() {
		double healthPercentage;

		for (HUDComponent hc : hudRobots) {
			healthPercentage = (double) hc.getRobot().getHealth() / (double) hc.getRobot().getMaxHealth();
			hc.setPercentage(healthPercentage);
			if (healthPercentage >= 0.67) {
				hc.setHudImage("HUDHighBar");
			} else if (healthPercentage >= 0.33) {
				hc.setHudImage("HUDMediumBar");
			} else {
				hc.setHudImage("HUDLowBar");
			}
			hc.setXPos(hc.getRobot().getXPos());
			hc.setYPos(hc.getRobot().getYPos() - 20);
		}
	}
}
