package com.game.UI;

import javafx.scene.input.KeyCode;

import java.util.ArrayDeque;
import java.util.Queue;

/**
 * This class was made so that we had a list structure containing KeyCodes that
 * acted as a queue, but didn't allow KeyCodes to be added that were already in
 * the list.
 * 
 * @author Kai Cieplinski
 *
 */
public class SetQueue {
	Queue<KeyCode> iq = new ArrayDeque<KeyCode>();

	/**
	 * Used for inserting a KeyCode into the list, but doesn't allow a duplicate
	 * KeyCode to be added to it.
	 * 
	 * @param i
	 *            the KeyCode to be inserted into the list.
	 */
	public void insert(KeyCode i) {
		if (!iq.contains(i)) {
			iq.add(i);
		}
	}

	/**
	 * Returns the head of the list, removing it in the process.
	 * 
	 * @return the KeyCode which is the head of the list.
	 */
	public KeyCode poll() {
		return iq.poll();
	}

	/**
	 * Clears the list of all KeyCodes.
	 */
	public void clear() {
		iq.clear();
	}
}
