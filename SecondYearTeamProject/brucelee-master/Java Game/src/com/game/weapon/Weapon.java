package com.game.weapon;

import com.game.object.Projectile;
import com.game.physics.Coord;
import com.game.sound.SoundEffect;
import javafx.scene.image.Image;

/**
 * An abstract class that stores all the variables and methods that any weapon
 * in the game must have
 * 
 * @author Isaac
 *
 */
public abstract class Weapon {
	protected int weapondId;
	private double maxRange;
	private int ammo;
	private boolean firesBullet;
	private static int dropRarity;

	protected String name;

	/**
	 * Creates a weapon with the given parameters
	 * 
	 * @param maxDamage
	 *            The max amount of damage a projectile this weapon fires will
	 *            do on impact
	 * @param range
	 *            The maximum range of this weapon
	 * @param ammo
	 *            The ammo that this weapon currently has for it
	 * @param bullet
	 *            Does the weapon fire a projectile that experiences gravity
	 * @param name
	 *            The name of the weapon
	 * @param dropRarity
	 *            The rarity of getting ammo for this weapon from a drop
	 */
	public Weapon(double range, int ammo, boolean bullet, String name, int dropRarity) {
		this.ammo = ammo;
		this.maxRange = range;
		this.firesBullet = bullet;
		this.name = name;
		this.dropRarity = dropRarity;
	}

	/**
	 * Decreases the weapons ammo by one
	 */
	public void fired() {
		ammo -= 1;
	}

	/**
	 * Get the current ammo that this weapon has
	 * 
	 * @return The current ammo of this weapon
	 */
	public int getAmmo() {
		return ammo;
	}

	/**
	 * Get the maximum range that this weapon can fire a projectile over
	 * 
	 * @return The maximum range of this weapon
	 */
	public double getRange() {
		return maxRange;
	}

	/**
	 * Refill the ammo of this weapon by the amount given
	 * 
	 * @param pickup
	 *            The amount of ammo to add
	 */
	public void refillAmmo(int pickup) {
		ammo += pickup;
	}

	/**
	 * Do projectiles fired by this weapon feel gravity
	 * 
	 * @return Is this weapons projectiles affected by gravity
	 */
	public boolean firesBullet() {
		return firesBullet;
	}

	/**
	 * Creates a projectile of the type that this weapon fires
	 * 
	 * @param location
	 *            The location the projectile should be created at
	 * @param Angle
	 *            The angle that the projectile is rotated at when launched
	 * @return The projectile fired from this weapon
	 */
	public abstract Projectile createProjectile(Coord location, int angle);

	/**
	 * Gets the icon name for the image displayed in the HUD
	 * 
	 * @return The name of the image that should be displayed in the hud for
	 *         this weapon
	 */
	public String getIconName() {
		return (name.concat("_icon"));
	}

	/**
	 * Gets the name of this weapon
	 * @return This weapons name
	 */
	public String getName() {
		return name;
	}

	/**
	 * How rare it is for ammo for this weapon to drop from supply crates
	 * @return The rarity of this weapons ammo
	 */
	public int rarity() {
		return dropRarity;
	}

	/**
	 * Sets the current ammo in this weapon
	 * @param ammo
	 */
	public void setAmmo(int ammo) {
		this.ammo = ammo;
	}

}
