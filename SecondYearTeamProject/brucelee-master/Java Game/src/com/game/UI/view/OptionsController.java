package com.game.UI.view;

import java.io.IOException;

import com.game.UI.UI;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Slider;

public class OptionsController implements ChangeListener<Object> {

	ObservableList<String> difficulties = FXCollections.observableArrayList("Easy", "Medium", "Hard");

	@FXML
	private Slider volumeSlider;
	@FXML
	private CheckBox gravityBox;
	@FXML
	private ChoiceBox<String> difficultyMenu;
	@FXML
	private Button returnToMenu;

	private UI UI;

	/**
	 * The default constructor for the options menu controller
	 */
	public OptionsController() {

	}

	/**
	 * Sets the UI that the controller will interact with.
	 * 
	 * @param UI
	 *            the UI to be set.
	 */
	public void setUI(UI UI) {
		this.UI = UI;
	}

	/**
	 * Sets the information in the options menu to be representative of what the
	 * options are set to.
	 * 
	 * @param randomGravity
	 *            whether or not random gravity is enabled.
	 * @param difficulty
	 *            what difficulty the A.I. is currently set to.
	 * @param masterVolume
	 *            what the master volume is currently set to.
	 */
	public void setOptions(boolean randomGravity, int difficulty, double masterVolume) {
		gravityBox.setSelected(randomGravity);
		volumeSlider.valueProperty().removeListener(this);
		volumeSlider.setValue(100 * masterVolume);
		volumeSlider.valueProperty().addListener(this);
		if (difficulty == 0) {
			difficultyMenu.setValue("Easy");
		} else if (difficulty == 1) {
			difficultyMenu.setValue("Medium");
		} else {
			difficultyMenu.setValue("Hard");
		}
		difficultyMenu.setItems(difficulties);
	}

	/**
	 * Update the UI's options to correspond with the currently selected options in
	 * the options menu (excluding volume).
	 */
	private void setUIOptions() {
		String diff = (String) difficultyMenu.getValue();
		if (diff.equals("Easy")) {
			UI.setDifficulty(0);
		} else if (diff.equals("Medium")) {
			UI.setDifficulty(1);
		} else {
			UI.setDifficulty(2);
		}
		UI.setRandomGravity(gravityBox.selectedProperty().getValue().booleanValue());
	}

	/**
	 * Sets the master volume in the UI's options to correspond with the volume
	 * currently specified by the options menu.
	 */
	@Override
	public void changed(ObservableValue<? extends Object> observable, Object oldValue, Object newValue) {
		UI.setMasterVolume(volumeSlider.getValue() / 100);
	}

	/**
	 * Initialises the listener for the volume slider.
	 */
	@FXML
	private void initialize() {
		volumeSlider.valueProperty().addListener(this);
	}

	/**
	 * Switch from the options menu to the main menu.
	 * 
	 * @throws IOException
	 */
	@FXML
	private void goMainMenu() throws IOException {
		setUIOptions();
		UI.showMenu();
	}

}
