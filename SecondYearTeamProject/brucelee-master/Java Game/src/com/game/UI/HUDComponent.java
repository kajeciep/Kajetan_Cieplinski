package com.game.UI;

import com.game.physics.Coord;
import com.game.physics.Robot;

import javafx.geometry.Rectangle2D;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;

/**
 * Used for keeping all values relating to each HUDComponent together.
 * 
 * @author Kai Cieplinski
 *
 */
public class HUDComponent {

	private Robot robot;
	private String hudImage;
	private double percentage;
	private double barPercentage;
	private double otherRobotHealthBar;
	private double width;
	private double height;
	private double xPos;
	private double yPos;
	private boolean isBar;

	/**
	 * Default constructor for all currentcharacter HUDComponents.
	 * 
	 * @param img
	 *            the name of the .png file to be used for the image of the
	 *            HUDComponent.
	 * @param location
	 *            the position the HUDComponent will be on the screen.
	 * @param isBar
	 *            determines whether the HUDComponent is a percentage bar for the
	 *            purposes of resizing.
	 */
	public HUDComponent(String img, Coord location, boolean isBar) {
		robot = null;
		hudImage = img;
		xPos = location.getX();
		yPos = location.getY();
		percentage = 1.0;
		this.isBar = isBar;
		if (isBar) {
			barPercentage = 0.25;
		} else {
			barPercentage = 1.0;
		}
		otherRobotHealthBar = 1.0;
	}

	/**
	 * The constructor for all the other robot's HUDComponents (health bars).
	 * 
	 * @param robot
	 *            the robot that you're giving the health bar to.
	 */
	public HUDComponent(Robot robot) {
		this.robot = robot;
		hudImage = "HUDHighBar";
		xPos = robot.getXPos();
		yPos = (robot.getYPos() - 20);
		percentage = 1.0;
		barPercentage = 0.25;
		otherRobotHealthBar = 0.25;
		isBar = true;
	}

	/**
	 * Returns the name of the .png file to be used for the Image of the
	 * HUDComponent.
	 * 
	 * @return the name of the .png file.
	 */
	public String getHudImageName() {
		return hudImage;
	}

	/**
	 * Returns the restructured Image of the HUDComponent to be drawn onto the
	 * screen.
	 * 
	 * @param img
	 *            the Image to be modified by the method.
	 * @return the restructured Image.
	 */
	public Image getHudImage(Image img) {
		if (isBar) {
			width = img.getWidth();
			height = img.getHeight();
			ImageView hudImageView = new ImageView(img);
			SnapshotParameters params = new SnapshotParameters();
			params.setFill(Color.TRANSPARENT);
			params.setViewport(
					new Rectangle2D(0, 0, (percentage * (otherRobotHealthBar * width)), (barPercentage * height)));
			return hudImageView.snapshot(params, null);
		}
		return img;
	}

	/**
	 * Sets the name of the .png file to be used for the Image of the HUDComponent.
	 * 
	 * @param img
	 *            the new name of the .png file to be used.
	 */
	public void setHudImage(String img) {
		hudImage = img;
	}

	/**
	 * Sets the percentage of the HUDComponents' Image's width to be used.
	 * 
	 * @param percentage
	 *            the new percentage to be used.
	 */
	public void setPercentage(double percentage) {
		this.percentage = percentage;
	}

	/**
	 * Returns the x-postion part of the HUDComponent's Coord.
	 * 
	 * @return the x-position of the HUDComponent.
	 */
	public double getXPos() {
		return xPos;
	}

	/**
	 * Returns the y-postion part of the HUDComponent's Coord.
	 * 
	 * @return the y-position of the HUDComponent.
	 */
	public double getYPos() {
		return yPos;
	}

	/**
	 * Sets the x-postion part of the HUDComponent's Coord.
	 * 
	 * @param x
	 *            the new x-position.
	 */
	public void setXPos(double x) {
		xPos = x;
	}

	/**
	 * Sets the y-postion part of the HUDComponent's Coord.
	 * 
	 * @param y
	 *            the new y-position.
	 */
	public void setYPos(double y) {
		yPos = y;
	}

	/**
	 * Returns the robot of the HUDComponent.
	 * 
	 * @return the robot.
	 */
	public Robot getRobot() {
		return robot;
	}
}
