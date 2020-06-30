package com.game.object;

import com.game.physics.Coord;
import com.game.physics.PhysicsObject;
import com.game.physics.Robot;
import com.game.sound.SoundEffect;
import com.game.state.GameState;

import javafx.scene.image.Image;

/**
 * This abstract class is extended by any object that's considered a projectile fired by a weapon
 * @author Isaac
 *
 */
public abstract class Projectile extends PhysicsObject {
	protected double maxVelocity;
	protected int maxDam;
	protected int minDam;
	protected int fireAngle;
	protected double radius;
	protected double explosiveRadius = 300;
	protected SoundEffect fireSound;
	protected SoundEffect impactSound;
	protected int imgIndex;
	protected boolean isRotational = false;
	private Robot creator;
	
	/**
	 * Creates a projectile with the given attributes
	 * @param radius The radius of the projectile
	 * @param location The position of the projectiles hitbox in the world(The top left corner)
	 * @param b Does this projectile experience gravity
	 * @param eRadius The radius of the explosion when the projectile explodes
	 * @param name The name of this projectile
	 * @param angle The angle of rotation of this projectile
	 */
	public Projectile(double radius, Coord location, boolean b, double eRadius, String name, int angle) {
		super((int) radius *2, (int) radius * 2,location, b, name);
		this.radius = radius;
		this.explosiveRadius = eRadius;
		this.fireAngle = angle;
		fireSound = new SoundEffect(name.concat("_fire"));
		impactSound = new SoundEffect(name.concat("_impact"));
		imgIndex = 0;
	}
	
	/**
	 * Get the radius of this projectile
	 * @return The projectiles radius
	 */
	public double getRadius() {
		return radius;
	}
	
	/**
	 * Get the maximum damage the projectile deals 
	 * @return The max damage
	 */
	public int getDamage() {
		return maxDam;
	}
	
	/**
	 * Get the radius of the explosion this projectile makes
	 * @return The explosions radius
	 */
	public double getExplosiveRadius() {
		return explosiveRadius;
	}
	
	/**
	 * Set the robot who fired this projectile
	 * @param creator The projectiles creator
	 */
	public void setCreator(Robot creator) {
		this.creator = creator;
	}
	
	/**
	 * Get the robot that fired this projectile
	 * @return The projectiles creator
	 */
	public Robot getCreator() {
		return creator;
	}
	
	/**
	 * Override this to get the projectile to do something when colliding with terrain
	 * @param state The state of the world
	 * @return Should the projectile explode and be removed
	 */
	public boolean collided(GameState state) {
		return true;
	}

	/**
	 * Get the sound made when this projectile is fired
	 * @return The projectiles fire sound
	 */
	public SoundEffect getFireSound() {
		return fireSound;
	}

	/**
	 * Get the sound the projectile makes when it hits something
	 * @return The impact sound
	 */
	public SoundEffect getImpactSound() {
		return impactSound;
	}

	/**
	 * Get the angle that the projectile is currently at
	 * @return The angle of rotation of the projectile
	 */
	public int getFireAngle() {
		return fireAngle;
	}

	/**
	 * Check if this projectile changes angle as it moves through the air
	 * @return True if the projectile is angled
	 */
	public boolean isAngled() {
		return isBullet;
	};

	/**
	 * Get the counter for what image this projectile curently has
	 * @return The projectiles image index
	 */
	public int getCounter() {
		return imgIndex;
	}

	/**
	 * Increase the images counter 
	 */
	public void incrementCounter() {
		imgIndex = imgIndex + 1;
	}
}
