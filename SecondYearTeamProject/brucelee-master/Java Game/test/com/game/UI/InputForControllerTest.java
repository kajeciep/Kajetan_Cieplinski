package com.game.UI;

import static org.junit.Assert.*;

import org.junit.Test;

import javafx.scene.input.KeyCode;

public class InputForControllerTest {

	@Test
	public void testFunctionality() {
		InputForController ifc = new InputForController();
		ifc.addInput(KeyCode.A);
		ifc.addInput(KeyCode.A);
		ifc.addInput(KeyCode.D);
		ifc.addInput(KeyCode.SPACE);
		assertEquals(ifc.getInput(), KeyCode.A);
		assertNotEquals(ifc.getInput(), KeyCode.A);
		ifc.clearInputList();
		assertEquals(ifc.getInput(), null);
	}
}
