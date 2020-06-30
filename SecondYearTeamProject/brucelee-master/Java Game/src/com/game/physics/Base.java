package com.game.physics;

import javafx.scene.shape.Rectangle;

/**
 * Base physics class that stores all of the essential values for an object in
 * the world including it's position and hit box
 * 
 * @author Isaac
 *
 */
public abstract class Base {

	protected int height;
	protected int width;
	protected double xPos;
	protected double yPos;
	private Rectangle collisionBox;

	/**
	 * Creates a base that hold simple values. A rectangle is used for the hitbox of the characters
	 * 
	 * @param height
	 *            The height of the rectangle
	 * @param width
	 *            The width of the rectangle
	 * @param location
	 *            the current location of the object
	 */
	public Base(int height, int width, Coord location) {
		this.height = height;
		this.width = width;
		this.xPos = location.getX();
		this.yPos = location.getY();
		this.collisionBox = new Rectangle(xPos, yPos, width, height);
	}

	/**
	 * Get the x position of the object. This is the top left corner of it's hit box
	 * @return The x position of the object
	 */
	public double getXPos() {
		return xPos;
	}
	
	/**
	 * Get the y position of the object. This is the top left cornor
	 * @return The y position of the object
	 */
	public double getYPos() {
		return yPos;
	}

	/**
	 * Set the x position of the object
	 * @param xPos The position to set the objects location to
	 */
	public void setXPos(double xPos) {
		this.xPos = xPos;
	}
	
	/**
	 * Set the y position of the object
	 * @param yPos The y position to set the objects locaion to
	 */
	public void setYPos(double yPos) {
		this.yPos = yPos;
	}

	/**
	 * Get the width of the objects collision box
	 * @return The width of the collision box
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * Gets the height of the objects collision box
	 * @return The height of the collision box
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * returns a collision box with at the base's current location * @return
	 * Returns the collision box
	 */
	public Rectangle getCollisionBox() {
		collisionBox.setX(xPos);
		collisionBox.setY(yPos);
		return collisionBox;
	}

}
