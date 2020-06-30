package com.game.UI;

import javafx.scene.input.KeyCode;

/**
 * Used for exchanging messages between the UI and the GameController.
 * 
 * @author Kai Cieplinski
 */
public class InputForController {
	private boolean singlePlayerButtonPressed;
	public SetQueue inputList;

	/**
	 * Default constructor for InputForController.
	 */
	public InputForController() {
		singlePlayerButtonPressed = false;
		inputList = new SetQueue();
	}

	/**
	 * Returns a boolean, true if the single player button has been pressed, false
	 * if it hasn't.
	 * 
	 * @return a boolean.
	 */
	public boolean isPlayButtonPressed() {
		return singlePlayerButtonPressed;
	}

	/**
	 * Sets whether the single player button has been pressed or not.
	 * 
	 * @param singlePlayerButtonPressed
	 *            the boolean that determines if single player button has been
	 *            pressed.
	 */
	public void setPlayButtonPressed(boolean singlePlayerButtonPressed) {
		this.singlePlayerButtonPressed = singlePlayerButtonPressed;
	}

	/**
	 * Returns the next KeyCode in the input list. This also removes the KeyCode, so
	 * that the next call to this method returns the next KeyCode.
	 * 
	 * @return the KeyCode to be executed next.
	 */
	public KeyCode getInput() {
		return inputList.poll();
	}

	/**
	 * Adds a KeyCode to the input list.
	 * 
	 * @param input
	 *            the KeyCode to be added to the input list.
	 */
	public void addInput(KeyCode input) {
		inputList.insert(input);
	}

	/**
	 * Clears the whole input list, removing all KeyCode objects from it.
	 */
	public void clearInputList() {
		inputList.clear();
	}

}
