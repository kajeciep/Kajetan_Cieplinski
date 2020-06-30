package com.game.physics;

import com.game.AI.HighHealthStrat;
import com.game.AI.Strategy;
import com.game.state.GameState;
import com.game.state.Loadout;
import com.game.state.Team;
import com.game.state.GameWorld;
import com.game.weapon.Weapon;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;
import java.util.Random;

/**
 * These represent individual characters in the games world and store things
 * like their health, energy and ammo. It also stores where the robot is aiming
 * and it's position in the world
 * 
 * @author Isaac
 *
 */
public class Robot extends PhysicsObject {
	private int maxHealth;
	private int health;
	private ArrayList<Weapon> weapons;
	private boolean isDead;
	private int maxEnergy;
	private int energy;
	private int weaponSlot;
	private int angle;
	private final int MAX_POWER = 100;
	private final int MIN_POWER = 40;
	private int power;
	// These two params are used to figure out where the projectile should
	// appear when fired
	private final int ARM_LENGTH = 15;
	private Coord armLocation;
	private boolean facingLeft;
	private int timer;
	private boolean onSlope = false;
	private int imageId;
	private int robotId;
	private int imageOffset = 0;
	protected Random r;

	/**
	 * Creates a new robot character
	 * 
	 * @param height
	 *            The height of the robots hitbox(Should be the same as the
	 *            image)
	 * @param width
	 *            The width of the robots hitbox (Should be the same as the
	 *            image)
	 * @param position
	 *            The coordinate of the robot in the world
	 * @param ldOut
	 *            The loudout that this robot is using which tells it how much
	 *            ammo it has for each weapon
	 * @param id
	 *            An id for this specific robot
	 */
	public Robot(int height, int width, Coord position, Loadout ldOut, int id) {
		super(height, width, position, false, "legends");
		maxHealth = ldOut.getStartHP();
		health = maxHealth;
		weapons = ldOut.getInv();
		isDead = false;
		maxEnergy = ldOut.getMaxEnergy();
		energy = maxEnergy;
		weaponSlot = 1;
		power = MAX_POWER;
		angle = 45;
		this.robotId = id;
		if (robotId >= 10)
			imageOffset = 7;
		imageId = imageOffset;
		r = new Random();
	}

	/**
	 * Deal a certain amount of damage to this robots health
	 * 
	 * @param damage
	 *            The damage to deal
	 */
	public void takeDamage(int damage) {
		if (damage >= health) {
			health = 0;
			isDead = true;
		} else {
			health -= damage;
		}
	}

	/**
	 * Returns all the weapons the AI has as an array list
	 * 
	 * @return All the weapons used by the character
	 */
	public ArrayList<Weapon> getAllWeapons() {
		return weapons;
	}

	/**
	 * Sets the characters velocity causing them to move left The stop method
	 * should be called to set the velocity back to 0
	 */
	public void moveLeft() {
		facingLeft = true;
		if (!onSlope) {
			this.setVelocity(-120);
		} else {
			this.setVelocity(-100);
		}
		timer++;
		if (timer >= 0 && timer < 10) {
			imageId = imageOffset + 1;
		} else if (timer >= 10 && timer < 20) {
			imageId = imageOffset + 2;
		} else if (timer >= 20 && timer < 30) {
			imageId = imageOffset + 3;
		} else {
			timer = 0;
		}
	}

	/**
	 * Sets the characters velocity causing them to move right The stop method
	 * should be called to set the velocity back to 0
	 */
	public void moveRight() {
		facingLeft = false;
		if (!onSlope) {
			this.setVelocity(120);
		} else {
			this.setVelocity(100);
		}
		timer++;
		if (timer >= 0 && timer < 10) {
			imageId = imageOffset + 4;
		} else if (timer >= 10 && timer < 20) {
			imageId = imageOffset + 5;
		} else if (timer >= 20 && timer < 30) {
			imageId = imageOffset + 6;
		} else {
			timer = 0;
		}
	}

	/**
	 * Stops this robot by setting it's x velocity to 0
	 */
	public void stop() {
		this.setVelocity(0);
	}

	/**
	 * Get the robots current health
	 * 
	 * @return The robots health
	 */
	public int getHealth() {
		return health;
	}

	/**
	 * Check if this robot is currently dead and has no health
	 * 
	 * @return Is the robot alive
	 */
	public boolean isDead() {
		return isDead;
	}

	/**
	 * Reduce the energy of the robot if it's not already 0. Used when the robot
	 * moves left or right
	 */
	public void decreaseEnergy() {
		if (energy != 0) {
			this.energy -= 1;
		}
	}

	/**
	 * Decrease the energy by if it's not 0. This is used when the robot jumps
	 * to ensure jumping and walking use a similair amount of energy
	 */
	public void decreaseEnergyJump() {
		this.energy -= 10;
		if (energy < 0) {
			energy = 0;
		}
	}

	/**
	 * Check if the robot currently has energy and is able to move
	 * 
	 * @return Does the robot have energy
	 */
	public boolean hasEnergy() {
		return energy > 0;
	}

	/**
	 * Get the weapon that the robot currently has equipped
	 * 
	 * @return The currently equipped weapon
	 */
	public Weapon getWeapon() {
		return weapons.get(weaponSlot);
	}

	/**
	 * Set the current weapon slot that the robot has equipped. This is used by
	 * the AI to select the eapon it wants to fire
	 * 
	 * @param i
	 *            The sot of the weapon the robot should equip
	 */
	public void setSlot(int i) {
		weaponSlot = i;

	}

	/**
	 * Increases or decreases the slot of the robot to change what weapon it has
	 * equipped at the moment. It takes a 0 to decrease the slot and 1 to
	 * increase the slot. If it reaches 0 or the highest weapon then it loops
	 * back to the other side
	 * 
	 * @param i
	 *            Which weapon should be switched to the current slot. Either 0
	 *            or 1
	 */
	public void setPlayerSlot(int i) {
		int lastWeapon = weapons.size() - 1;
		if (i == 0) {
			if (weaponSlot == 0) {
				weaponSlot = lastWeapon;
			} else {
				weaponSlot = weaponSlot - 1;
			}
		} else if (i == 1) {
			if (weaponSlot == lastWeapon) {
				weaponSlot = 0;
			} else {
				weaponSlot = weaponSlot + 1;
			}
		}
		// Set power to max if it fires a bullet
		if (weapons.get(weaponSlot).firesBullet()) {
			power = MAX_POWER;
		}
	}

	/**
	 * Gets the current weapon slot
	 * 
	 * @return The slot thats currently equipped by this robot
	 */
	public int getSlot() {
		return weaponSlot;
	}

	/**
	 * Adjust the angle that the robot is currently aiming at. 0 to -90 if it's
	 * aiming left and 0 to 90 if it's aiming right
	 * 
	 * @param change
	 *            The number of degrees the angle should be changed by
	 */
	public void changeAngle(int change) {
		if (facingLeft) {// the angle can go from 0 to -90. Up should bring it
							// down. Down should bring it up to 0.
			if (angle > -90 && change > 0) {
				angle = angle - change;
			}
			if (angle <= -90 && change > 0) {
				return;
			}
			if (angle < -1 && change < 0) {
				angle = angle - change;
			}
			if (angle >= -1 && change < 0) {
				return;
			}
		} else {
			if (angle > 0 && change < 0) {
				angle = angle + change;
			}
			if (angle <= 0 && change < 0) {
				return;
			}
			if (angle < 90 && change > 0) {
				angle = angle + change;
			}
			if (angle >= 90 && change > 0) {
				return;
			}
		}
		return;
	}

	/**
	 * Swaps the angle to be on the left. This should be called when the robot
	 * goes from facing right to left
	 */
	public void swapAngleLeft() {
		if (angle > 0) {
			angle *= -1;
		}
		if (angle == 0) {
			angle = -1;
		}
		if (energy == 0) {
			imageId = imageOffset + 1;
			facingLeft = true;
		}
	}

	/**
	 * Swaps the angle from left to right. This should be called when the robot
	 * goes from facing let to right
	 */
	public void swapAngleRight() {
		if (angle < 0) {
			angle *= -1;
		}
		if (energy == 0) {
			imageId = imageOffset + 6;
			// setCurrentImage("legendr");
			facingLeft = false;
		}
	}

	/**
	 * Adjusts the power the robot launches projectiles at
	 * 
	 * @param change
	 *            The amount the power should be adjusted by
	 */
	public void changePower(int change) {
		// If it's a bullet then don't change the power
		if (weapons.get(weaponSlot).firesBullet()) {
			return;
		}
		if ((change < 0) && ((power + change) < MIN_POWER + 1)) {
			// Can't have power less than minPower
		} else if ((change > 0) && ((change + power) > MAX_POWER)) {
			// Can't have power greater than maxPower
		} else {
			power += change;
		}
	}

	/**
	 * Get the current power that the robot will launch a projectile with
	 * 
	 * @return The robots power
	 */
	public int getPower() {
		return power;
	}

	/**
	 * Get the robots current energy
	 * 
	 * @return the robots energy
	 */
	public int getEnergy() {
		return energy;
	}

	/**
	 * return the maximum health of this robot
	 * 
	 * @return The robots highest health value
	 */
	public int getMaxHealth() {
		return maxHealth;
	}

	/**
	 * Get the maximum power of the robot
	 * 
	 * @return The maximum possible power
	 */
	public int getMaxPower() {
		return MAX_POWER;
	}

	/**
	 * The lowest value the power can go. This is in place to prevent the player
	 * from launching at very small values as there is little difference between
	 * different power values this low
	 * 
	 * @return The minimum power of the robot
	 */
	public int getMinPower() {
		return MIN_POWER;
	}

	/**
	 * Get the maximum energy of the robot
	 * 
	 * @return The robots max energy
	 */
	public int getMaxEnergy() {
		return maxEnergy;
	}

	/**
	 * Converts the angle that the robot is aiming at into an angle that the
	 * physics system can use
	 * 
	 * @return The angle in terms of 360 degrees
	 */
	public int getAngle() {
		if (angle < 0) {
			return 180 + angle;
		}
		return angle;
	}

	@Override
	public String getName() {
		return name;
	}

	/**
	 * Get the current image of this robot based on it's direction and if it's
	 * moving
	 * 
	 * @param image
	 *            The image of the robot
	 */
	public void setCurrentImage(String image) {
		this.name = image;
	}

	/**
	 * Gets the current strategy used by the robot based on factors like it's
	 * health
	 * 
	 * @return The strategy this robot should employ if the AI makes a move
	 *         playing as it
	 */
	public Strategy getStrategy() {
		// if(health < 50) {
		// return new LowHealthStrat();
		// }
		return new HighHealthStrat(this);
	}

	/**
	 * Tell the robot to pickup some supplies for it's weapons. This gives it
	 * random ammo for it's weapons based on their rarity
	 */
	public void pickupSupplies() {
		int ammoCount = 0;
		for (Weapon w : weapons) {
			if (ammoCount >= 3) {
				return;
			}
			if (r.nextInt(100) > w.rarity()) {
				w.refillAmmo(1);
				ammoCount++;
			}
		}
		if (ammoCount < 3) {
			health += 25;
			if (health > maxHealth) {
				maxHealth = health;
			}
		}
	}

	/**
	 * Resets the image when the turn ends
	 */
	public void endTurn() {
		imageId = imageOffset;
	}

	/**
	 * Resets values for the start of the characters turn
	 */
	public void startTurn() {
		energy = maxEnergy;
		if (facingLeft) {
			imageId = imageOffset + 1;
		} else {
			imageId = imageOffset + 4;
		}
	}

	/**
	 * Checks what direction the character is facing
	 * 
	 * @return True if the character is facing left
	 */
	public boolean isFacingLeft() {
		return facingLeft;
	}

	/**
	 * Set the direction the character is currently facing
	 * 
	 * @param facingLeft
	 *            Is the robot currently facing left
	 */
	public void setFacingLeft(boolean facingLeft) {
		this.facingLeft = facingLeft;
	}

	/**
	 * Move the robot based on it's x velocity. This method steps the robot one
	 * pixel at a time on the x axis until it reaches however far it should move
	 * in the given time. If it collides with something then it moves the y
	 * co-ordinate up slightly to allow the robot to move up slopes. If the
	 * robot is still colliding after this then it moves the robot back to it's
	 * original position as it's collided with a wall
	 * 
	 * @param world
	 *            The game world
	 * @param time
	 *            The time to calculate the movement for
	 */
	public void stepX(GameWorld world, double time) {
		double endPoint = xPos + xVelocity * time;
		double xStart = xPos;
		double yStart = yPos;
		// Step until it reaches the right place
		while ((int) xPos != (int) endPoint) {
			// Step the position pixel by pixel
			if (xVelocity > 0) {
				xPos += 1;
			} else {
				xPos -= 1;
			}

			// return if it's out of bounds
			if (xPos <= 0 || xPos + width >= world.worldWidth) {
				xPos = xStart;
				return;
			}

			// If there's a collision stop
			if (hasCollisionX(world)) {
				onSlope = true;
				for (int i = 1; i < 5; i++) {
					yPos--;
					if (!hasCollisionX(world)) {
						return;
					}
				}
				// If there's still a collision, undo any movement in the x axis
				xPos = xStart;
				yPos = yStart;
				return;
			}
		}
		onSlope = false;
	}

	/**
	 * Checks if the robot is currently colliding with any of the terrain
	 * 
	 * @param world
	 *            The current world
	 * @return True if the robot is currently colliding with the world
	 */
	public boolean hasCollisionX(GameWorld world) {
		Coord location = new Coord((int) this.getXPos(), (int) this.getYPos());
		Rectangle collisionBox = this.getCollisionBox();
		int maxX = (int) (location.getX() + collisionBox.getWidth());
		int maxY = (int) (location.getY() + collisionBox.getHeight());
		for (int i = location.getY() + 5; i < maxY - 5; i++) {
			if (!world.map[maxX][i]) {
				return true;
			} else if (location.getX() > 0 && !world.map[location.getX()][i]) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Move the robot in the y axis one pixel at a time. If it collides with any
	 * terrain then it stops the robot from moving
	 * 
	 * @param world
	 *            The game world
	 * @param time
	 *            The time used to calculate ho far to move the robot
	 */
	public void stepY(GameWorld world, double time) {
		double endPoint = (yPos - yVelocity * time);
		double startPoint = yPos;
		boolean coll = true;
		while ((int) yPos != (int) endPoint) {
			if (yVelocity > 0) {
				yPos -= 1;
			} else {
				yPos += 1;
			}

			// return if it's out of bounds
			if (yPos <= 0 || yPos + height >= world.worldHeight) {
				yPos = startPoint;
				return;
			}

			// If there's a collision, undo any movement in the y axis
			if (hasCollisionY(world)) {
				stopY();
				coll = false;
				break;
			}
		}
		// Set yPos to the exact double value it should be so that values with a
		// movement of less than 1 still have an effect on the robots position
		if (startPoint != endPoint && coll) {
			yPos = endPoint;
		}
		// Decrease velocity according to gravity
		if (!isBullet && isFalling) {
			yVelocity -= GameState.gravity * time;
			if (Math.abs(yVelocity) > maxVelocity) {
				if (Math.abs(yVelocity) > maxVelocity) {
					if (yVelocity > 0) {
						yVelocity = maxVelocity;
					} else {
						yVelocity = -1 * maxVelocity;
					}
				}
			}
		}
	}

	/**
	 * Check if the robot is currently colliding with the floor or the ceiling.
	 * If it hits the floor then fall damage is applied
	 * 
	 * @param world The game world
	 * @return True if the robot collides with something
	 */
	public boolean hasCollisionY(GameWorld world) {
		Coord location = new Coord((int) this.getXPos(), (int) this.getYPos());
		Rectangle collisionBox = this.getCollisionBox();
		int maxX = (int) (location.getX() + collisionBox.getWidth());
		int maxY = (int) (location.getY() + collisionBox.getHeight());
		for (int i = location.getX() + 3; i < maxX - 3; i++) {
			if (!world.map[i][maxY]) {
				takeFallDamage();
				return true;
			} else if (location.getY() > 0 && !world.map[i][location.getY()]) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Cause the robot to take fall damage based on it's current velocity when hitting the ground
	 */
	public void takeFallDamage() {
		setFalling(false);
		double damage = Math.abs(this.getYVelocity()) / 15;
		if (damage > 13) {
			takeDamage((int) damage);
		}
	}

	/**
	 * Get the ID of this robot
	 * @return This robots ID
	 */
	public int getRobotId() {
		return robotId;
	}

	/**
	 * Sets the image of the robot based on it's current direction and movement
	 */
	public void setImage() {
		switch (imageId) {
		case 0:
			setCurrentImage("legends");
			break;
		case 1:
			setCurrentImage("legendl");
			break;
		case 2:
			setCurrentImage("legendl3");
			break;
		case 3:
			setCurrentImage("legendl4");
			break;
		case 4:
			setCurrentImage("legendr");
			break;
		case 5:
			setCurrentImage("legendr3");
			break;
		case 6:
			setCurrentImage("legendr4");
			break;
		case 7:
			setCurrentImage("raven");
			break;
		case 8:
			setCurrentImage("ravenleft1");
			break;
		case 9:
			setCurrentImage("ravenleft2");
			break;
		case 10:
			setCurrentImage("ravenleft3");
			break;
		case 11:
			setCurrentImage("ravenright");
			break;
		case 12:
			setCurrentImage("ravenright1");
			break;
		case 13:
			setCurrentImage("ravenright2");
			break;
		case 14:
			setCurrentImage("ravenright3");
			break;
		default:
			break;
		}
	}

	/**
	 * Get the ID for the robots current image
	 * @return The image Id for the robots image
	 */
	public int getImageId() {
		return imageId;
	}

	/** 
	 * The slot of the weapon the robot currently has equipped
	 * @return The current slot of the robot
	 */
	public int getWeaponSlot() {
		return weaponSlot;
	}

	/**
	 * Set the current health of the robot
	 * @param health The value the robots health should be set to
	 */
	public void setHealth(int health) {
		this.health = health;
	}

	/**
	 * Set the current energy of the robot
	 * @param energy The value the robots energy should be set to
	 */
	public void setEnergy(int energy) {
		this.energy = energy;
	}

	/**
	 * Set the current angle the robots aiming at
	 * @param angle The angle to set the robots aim to
	 */
	public void setAngle(int angle) {
		this.angle = angle;
	}

	/**
	 * Set the robots current power
	 * @param power The value power should be set to
	 */
	public void setPower(int power) {
		this.power = power;
	}

	/**
	 * Set the robots imageID
	 * @param imageId The imageId that it should be set to
	 */
	public void setImageId(int imageId) {
		this.imageId = imageId;
	}

	/**
	 * Set the robots weapons
	 * @param weapons The weapons the robots weapons should be set to
	 */
	public void setWeapons(ArrayList<Weapon> weapons) {
		this.weapons = weapons;
	}

	/**
	 * Get the timer used in animaions
	 * @return The current timer value
	 */
	public int getTimer() {
		return timer;
	}

	/**
	 * Set the timer used in animations
	 * @param timer The value timer should be set to
	 */
	public void setTimer(int timer) {
		this.timer = timer;
	}

	/**
	 * Set if the robot is currently dead
	 * @param isDead The value that should be set 
	 */
	public void setIsDead(boolean isDead) {
		this.isDead = isDead;
	}

	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Id: " + robotId + "\n");
		builder.append("Is dead: " + isDead + "\n");
		builder.append("X pos: " + xPos + "; Y pos: " + yPos + "\n");
		builder.append("Health: " + health + "\n");
		builder.append("Energy: " + energy + "\n");
		builder.append("Ammo: ");
		for (Weapon w : weapons)
			builder.append(w.getAmmo() + " ");
		builder.append("\nCurrent weapon: " + weaponSlot + "\n");
		builder.append("Angle: " + angle + "\n");
		builder.append("Power: " + power + "\n");
		builder.append("Image id: " + imageId + "\n");
		builder.append("Timer: " + timer);

		return builder.toString();
	}

}
