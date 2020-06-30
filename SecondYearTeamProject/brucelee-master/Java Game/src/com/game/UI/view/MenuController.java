package com.game.UI.view;

import com.game.UI.UI;
import java.io.IOException;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;

public class MenuController {

	// Menu.fxml ID's (m)

	@FXML
	private ImageView m_GameStill;

	@FXML
	private Button m_SinglePlayer;

	@FXML
	private Button m_MultiPlayer;

	@FXML
	private Button m_HowToPlay;

	@FXML
	private Button m_Options;

	@FXML
	private Button m_Exit;

	// Multiplayer.fxml ID's (mu)

	@FXML
	private Label mu_TitleLabel;

	@FXML
	private Button mu_Join;

	@FXML
	private Button mu_Host;

	@FXML
	private Button mu_Leaderboard;

	@FXML
	private Button mu_Return;

	// MultiplayerHost.fxml ID's (mh)

	@FXML
	private Label mh_TitleLabel;

	@FXML
	private Label mh_NameOfGame;

	@FXML
	private TextField mh_NameOfGameText;

	@FXML
	private Label mh_PassForGame;

	@FXML
	private TextField mh_PassForGameText;

	@FXML
	private Button mh_Host;

	@FXML
	private Button mh_Return;

	// HowToPlayMenu.fxml ID's (htp)

	@FXML
	private Button htp_Return;

	private UI UI;

	/**
	 * The default constructor for the general menu controller.
	 */
	public MenuController() {

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
	 * Switch to the multiplayer menu.
	 * 
	 * @throws IOException
	 */
	@FXML
	private void goMultiplayerMenu() throws IOException {
		UI.showMultiplayerMenu();
	}

	/**
	 * Switch to the multiplayer host menu.
	 * 
	 * @throws IOException
	 */
	@FXML
	private void goMultiplayerMenuHost() throws IOException {
		UI.showLoadoutMenu(true, false);
	}

	/**
	 * Switch to the multiplayer join menu.
	 * 
	 * @throws IOException
	 */
	@FXML
	private void goMultiplayerMenuJoin() throws IOException {
		UI.showLoadoutMenu(true, true);
	}

	/**
	 * Switch to the leaderboard menu.
	 * 
	 * @throws IOException
	 */
	@FXML
	private void goLeaderboardMenu() throws IOException {
		UI.showLeaderBoard();
	}

	/**
	 * Switch to the main menu.
	 * 
	 * @throws IOException
	 */
	@FXML
	private void goMainMenu() throws IOException {
		UI.showMenu();
	}

	/**
	 * Switch the options menu.
	 * 
	 * @throws IOException
	 */
	@FXML
	private void goOptionsMenu() throws IOException {
		UI.showOptionsMenu();
	}

	/**
	 * Switch to the help menu.
	 * 
	 * @throws IOException
	 */
	@FXML
	private void goHelpMenu() throws IOException {
		UI.showHelpMenu();
	}

	/**
	 * Show the hosting dialogue box.
	 */
	@FXML
	private void goHostingDialogue() {
		System.out.println("We want to host a server with name '" + mh_NameOfGameText.getText()
				+ "' that has the password '" + mh_PassForGameText.getText() + "'.");
		UI.showHostingDialogue(mh_NameOfGameText.getText(), mh_PassForGameText.getText());
	}

	/**
	 * Show the loadout (for singleplayer) menu.
	 */
	@FXML
	private void handlePlayButton() {
		UI.showLoadoutMenu(false, false);
	}

	/**
	 * Exit the entire game.
	 */
	@FXML
	private void handleExitButton() {
		if (UI.getClient() != null) {
			UI.getClient().disconnect();
		}
		Platform.exit();
	}

}
