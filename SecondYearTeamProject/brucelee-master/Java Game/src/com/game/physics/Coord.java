package com.game.physics;

/**
 * This class stores an x y position in the world, as well as various common
 * methods to do on co-ordinates
 * 
 * @author Isaac
 *
 */
public class Coord {
	private int x;
	private int y;

	/**
	 * Creates a cord object with the specified co-ordinates
	 * 
	 * @param x
	 *            The x co-ordinate
	 * @param y
	 *            The y co-ordinate
	 */
	public Coord(int x, int y) {
		this.x = x;
		this.y = y;
	}

	/** 
	 * Get the x value of this position
	 * @return The x value
	 */
	public int getX() {
		return x;
	}

	/**
	 * Get the y value of this position
	 * @return The y value
	 */
	public int getY() {
		return y;
	}

	/**
	 * Set the x value of this position
	 * @param x The x value to set this x value to
	 */
	public void setX(int x) {
		this.x = x;
	}

	/**
	 * Set the y value of this position
	 * @param y The y value to set this y position to
	 */
	public void setY(int y) {
		this.y = y;
	}

	/**
	 * Calculate the straight line distance between this co-ordinate and another co-ordinate
	 * @param point2 The co-ordinate to find the distance to 
	 * @return The distance between this point and the given point
	 */
	public double getDistance(Coord point2) {
		double a = x - point2.getX();
		double b = y - point2.getY();
		return Math.sqrt(Math.pow(a, 2) + Math.pow(b, 2));
	}


	@Override
	public String toString() {
		return "X : " + x + " Y :" + y;
	}
}
