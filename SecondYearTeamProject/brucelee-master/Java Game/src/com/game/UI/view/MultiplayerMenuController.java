package com.game.UI.view;

import com.game.net.GameServer;

import java.io.IOException;

import com.game.UI.UI;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class MultiplayerMenuController {
	@FXML
	private TableView<GameServer> serverTable;
	@FXML
	private TableColumn<GameServer, String> serverTableColumn;

	@FXML
	private Label gameName;
	@FXML
	private Label hostingPlayerName;
	@FXML
	private Label noOfPlayers;

	@FXML
	private Button returnToMultiplayerMenu;
	@FXML
	private Button refreshList;
	@FXML
	private Button joinCurrentGame;

	private UI UI;
	private boolean gameSelected;

	/**
	 * Default constructor for the multiplayer join menu.
	 */
	public MultiplayerMenuController() {

	}

	/**
	 * Shows the server details of the GameServer.
	 * 
	 * @param gs
	 *            the GameServer to have details shown.
	 */
	private void showServerDetails(GameServer gs) {
		if (gs != null) {
			gameName.setText(gs.getName());
			hostingPlayerName.setText(gs.getHostName());
			noOfPlayers.setText(gs.getCapacity() + "/2");
			gameSelected = true;
		} else {
			gameName.setText("Gameserver not selected");
			hostingPlayerName.setText("Gameserver not selected");
			noOfPlayers.setText("Gameserver not selected");
			gameSelected = false;
		}

	}

	/**
	 * Initialises the serverTable, to allow for values to be input.
	 */
	@FXML
	private void initialize() {
		serverTableColumn.setCellValueFactory(cellData -> cellData.getValue().getNameProperty());
		showServerDetails(null);
		serverTable.getSelectionModel().selectedItemProperty()
				.addListener((observable, oldValue, newValue) -> showServerDetails(newValue));
	}

	/**
	 * Sets the UI that the controller will interact with, as well as make it so
	 * that no GameServer is selected.
	 * 
	 * @param UI
	 *            the UI to be set.
	 */
	public void setUI(UI UI) {
		this.UI = UI;
		gameSelected = false;
		serverTable.setItems(UI.getGameServers());
	}

	/**
	 * Switch from the multiplayer join menu to the multiplayer menu.
	 * 
	 * @throws IOException
	 */
	@FXML
	private void goMultiplayerMenu() throws IOException {
		UI.showMultiplayerMenu();
	}

	/**
	 * Show the connecting dialogue box.
	 */
	@FXML
	private void goConnectingDialogue() {
		UI.showConnectingDialogue(gameName.getText(), gameSelected);
	}

	/**
	 * Refreshes the list of GameServers, keeping up to date with the server.
	 */
	@FXML
	private void refreshServerList() {
		serverTable.setItems(UI.getGameServers());
	}

}
