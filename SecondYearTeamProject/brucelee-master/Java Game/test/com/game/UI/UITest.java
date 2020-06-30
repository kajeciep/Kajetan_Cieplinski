package com.game.UI;

import static org.junit.Assert.*;
import org.junit.Rule;
import org.junit.Test;

import com.game.controller.GameController.State;

import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

public class UITest {

	@Rule
	public JavaFXTestingRule javafxRule = new JavaFXTestingRule();
	
	@Test
	public void testPopupPostGameMenu() {
		UI ui = new UI();
		Stage yoyo = ui.popupPostGameMenu(State.WIN);
		assertEquals("You win!", yoyo.getTitle());
		yoyo = ui.popupPostGameMenu(State.LOSE);
		assertEquals("You lose!", yoyo.getTitle());
		yoyo = ui.popupPostGameMenu(State.DRAW);
		assertEquals("You drew!", yoyo.getTitle());
		yoyo = ui.popupPostGameMenu(State.EXIT);
		assertEquals("You have abandoned your machines!", yoyo.getTitle());
		yoyo = ui.popupPostGameMenu(State.OPPONENT_DISCONNECTED);
		assertEquals("The opposing commander has fled!", yoyo.getTitle());
	}
	
	@Test
	public void testInputLoop() {
		InputForController ifc = new InputForController();
		UI ui = new UI(ifc);
		ui.button.put(KeyCode.A, true);
		ui.button.put(KeyCode.UP, true);
		ui.button.put(KeyCode.RIGHT, true);
		ui.inputLoop();
		ui.button.put(KeyCode.A, false);
		ui.button.put(KeyCode.UP, false);
		ui.button.put(KeyCode.RIGHT, false);
		assertEquals(ifc.getInput(), KeyCode.A);
		assertEquals(ifc.getInput(), KeyCode.UP);
		assertEquals(ifc.getInput(), KeyCode.RIGHT);
	}
}
