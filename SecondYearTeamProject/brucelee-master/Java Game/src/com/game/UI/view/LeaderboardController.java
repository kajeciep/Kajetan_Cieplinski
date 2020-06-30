package com.game.UI.view;

import java.io.IOException;

import com.game.UI.UI;
import com.game.net.Player;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class LeaderboardController {
	@FXML
	private TableView<Player> leaderBoard;
	@FXML
	private TableColumn<Player, String> positionColumn;
	@FXML
	private TableColumn<Player, String> usernameColumn;
	@FXML
	private TableColumn<Player, String> gamesPlayedColumn;
	@FXML
	private TableColumn<Player, String> gamesWonColumn;
	@FXML
	private TableColumn<Player, String> gamesLostColumn;
	@FXML
	private TableColumn<Player, String> gamesDrawnColumn;

	@FXML
	private Button returnToMultiplayerMenu;
	@FXML
	private Button refreshList;

	private UI UI;

	/**
	 * The default constructor for the leaderboard controller.
	 */
	public LeaderboardController() {

	}

	/**
	 * Initialises all the fields in the leaderboard menu.
	 */
	@FXML
	private void initialize() {
		positionColumn.setCellValueFactory(cellData -> cellData.getValue().getPositionProperty());
		usernameColumn.setCellValueFactory(cellData -> cellData.getValue().getUsernameProperty());
		gamesPlayedColumn.setCellValueFactory(cellData -> cellData.getValue().getGamesPlayedProperty());
		gamesWonColumn.setCellValueFactory(cellData -> cellData.getValue().getWinsProperty());
		gamesLostColumn.setCellValueFactory(cellData -> cellData.getValue().getLosesProperty());
		gamesDrawnColumn.setCellValueFactory(cellData -> cellData.getValue().getDrawsProperty());
	}

	/**
	 * Sets the UI that the controller will interact with, as well perform an
	 * instance of refreshList() to show the up to date leaderBoard.
	 * 
	 * @param UI
	 *            the UI to be set.
	 */
	public void setUI(UI UI) {
		this.UI = UI;
		refreshList();
	}

	/**
	 * Switch from the leaderboard menu to the multiplayer menu.
	 * 
	 * @throws IOException
	 */
	@FXML
	private void goMultiplayerMenu() throws IOException {
		UI.showMultiplayerMenu();
	}

	/**
	 * Refreshes the leaderboard, updating all values to whatever is on the server
	 * side.
	 */
	@FXML
	private void refreshList() {
		leaderBoard.setItems(UI.getLeaderBoard());
	}

}
