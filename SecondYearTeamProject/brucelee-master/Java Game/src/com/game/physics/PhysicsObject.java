package com.game.physics;

import com.game.state.GameState;

import javafx.scene.image.Image;
import javafx.scene.shape.Rectangle;

/**
 * This class simulates a physical object moving in the world. It can be
 * affected by gravity and stores it's values as it accelerates and moves
 * through the world
 * 
 * @author Isaac
 *
 */
public class PhysicsObject extends Base {
	protected double xVelocity;
	protected double yVelocity;
	protected boolean isBullet;
	protected boolean isFalling;
	protected double maxVelocity = 800;
	protected Image img;
	protected String name;

	/**
	 * 
	 * @param height
	 *            The height of the object
	 * @param width
	 *            The width of the object
	 * @param location
	 *            the initial location of the object
	 * @param b
	 *            Is the projectile a bullet
	 */
	public PhysicsObject(int height, int width, Coord location, boolean b, String name) {
		super(height, width, location);
		isBullet = b;
		isFalling = true;
		this.name = name;
	}

	/**
	 * Get the current location of the object as a co-ordinate
	 * 
	 * @return The co-ordinate of the object
	 */
	public Coord getPosition() {
		return new Coord((int) this.getXPos(), (int) this.getYPos());
	}

	/**
	 * Sets x velocity to 0
	 */
	public void stop() {
		if (!isFalling) {
			xVelocity = 0;
		}
	}

	/**
	 * Sets the Y velocity to 0
	 */
	public void stopY() {
		yVelocity = 0;
	}

	/**
	 * Sets the current x velocity of the object
	 * 
	 * @param velocity
	 *            The value to set the xVelocity to
	 */
	public void setVelocity(double velocity) {
		this.xVelocity = velocity;
	}

	/**
	 * Sets the current y velocity of the object
	 * 
	 * @param velocity
	 *            The value to the yVelocity to
	 */
	public void setYVelocity(double velocity) {
		this.yVelocity = velocity;
	}

	/**
	 * Get the Y velocity
	 * 
	 * @return The current y velocity
	 */
	public double getYVelocity() {
		return yVelocity;
	}

	/**
	 * Get the current x velocity
	 * 
	 * @return The current x velocity
	 */
	public double getXVelocity() {
		return xVelocity;
	}

	/**
	 * Moves the object forward in time
	 * 
	 * @param time
	 *            The time the simulation takes to move forward and get the
	 *            right values
	 */
	public void moveObject(double time) {
		double startPos = xPos;
		xPos += xVelocity * time;
		yPos -= yVelocity * time; // This is because the y co-ordinate increases
									// as you go down the screen
		// Decrease velocity according to gravity
		if (!isBullet && isFalling) {
			yVelocity -= GameState.gravity * time;
			if (Math.abs(yVelocity) > maxVelocity) {
				if (yVelocity > 0) {
					yVelocity = maxVelocity;
				} else {
					yVelocity = -1 * maxVelocity;
				}
			}
		}

	}

	/**
	 * Sets the value and angle then launches the projectile
	 * 
	 * @param angle
	 *            The angle to the 0 that the ball should be launched at from
	 *            the o
	 * @param velocity
	 *            The velocity that the ball should be launched at
	 */
	public void launch(double angle, double velocity) {
		if (Math.abs(velocity) > maxVelocity) {
			velocity = maxVelocity;
		}
		double a = angle;
		angle = Math.toRadians(angle);
		xVelocity = velocity * Math.cos(angle) + xVelocity;
		yVelocity = velocity * Math.sin(angle);
		setFalling(true);
	}

	/**
	 * Get the centre of the object
	 * 
	 * @return The co-ordinates of the objects centre
	 */
	public Coord getCentre() {
		return new Coord((int) xPos + width / 2, (int) yPos + height / 2);
	}

	/**
	 * Simulates the current path of the projectile until it connects with an
	 * obstacle
	 * 
	 * @param tx
	 * @param ty
	 * @return
	 */
	public boolean simulate(double tx, double ty, GameState p, Robot[] team) {
		double simX = xPos;
		double simY = yPos;
		double yvSim = yVelocity;
		double xvSim = xVelocity;
		double simConstant;
		// //System.out.println(xVelocity + " " + yVelocity);
		// //System.out.println(xPos + ", " + yPos + ", " + ty);
		while (true) {
			// This constant can be changed based on the current distance to the
			// target
			simConstant = 0.05;
			simY -= yvSim * simConstant;
			simX += xvSim * simConstant;
			if (!isBullet) {
				yvSim -= GameState.gravity * simConstant;
			}
			// Checks to see if colliding with teammates
			for (Robot r : team) {
				Rectangle hitBox = r.getCollisionBox();
				if (hitBox.intersects(simX, simY, width, height)) {
					// System.out.println("Collided with teammate");
					return false;
				}
			}
			double error = Math.abs((ty - simY)) + Math.abs((tx - simX));
			if (error < 5) {
				return true;
			} else if (p.isColliding(this, simX, simY)) {
				return false;
			}
		}
	}

	/**
	 * Get if the object is currently falling and so should experience gravity
	 * 
	 * @return true if the object is currently falling
	 */
	public boolean getFalling() {
		return isFalling;
	}

	/**
	 * Set if the object is currently falling
	 * 
	 * @param b
	 *            True if the object should be falling
	 */
	public void setFalling(boolean b) {
		isFalling = b;
	}

	/**
	 * Get the max velocity objects can move at
	 * 
	 * @return The max velocity objects can move at
	 */
	public double getMax() {
		return maxVelocity;
	}

	/**
	 * Call this method when the object is moving on the ground to cause it to
	 * experience friction and decrease it's x velocity
	 */
	public void friction() {
		if (!isFalling) {
			this.xVelocity *= 0.8;
			if (Math.abs(xVelocity) < 1) {
				xVelocity = 0;
			}
		}
	}

	/**
	 * Get the current image of this physics object
	 * @return The objects current image
	 */
	public Image getImage() {
		return this.img;
	}

	/**
	 * Get the name of the object
	 * @return The objects name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Reset the velocities of the objects to 0 so he object is no longer moving
	 */
	public void resetValues() {
		xVelocity = 0;
		yVelocity = 0;

	}

	/**
	 * Returns if the projectile is a bullet and doesn't feel gravity
	 * 
	 * @return If the projectile is a bullet
	 */
	public boolean isBullet() {
		return isBullet;
	}

}
