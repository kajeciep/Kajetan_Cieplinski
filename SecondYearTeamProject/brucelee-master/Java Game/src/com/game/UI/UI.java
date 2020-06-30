package com.game.UI;

import com.game.UI.view.LeaderboardController;
import com.game.UI.view.LoadoutMenu;
import com.game.UI.view.MenuController;
import com.game.UI.view.MultiplayerMenuController;
import com.game.UI.view.OptionsController;
import com.game.controller.GameController;
import com.game.controller.GameController.State;
import com.game.graphics.Renderer;
import com.game.net.Client;
import com.game.net.GameServer;
import com.game.net.Player;
import com.game.sound.SoundEffect;
import com.game.state.GameState;
import com.game.state.Loadout;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Optional;

/**
 * The class that the game is run from, as well as controls all classes that
 * have anything to do with user interface.
 * 
 * @author Kai Cieplinski
 *
 */
public class UI extends Application {

	private Stage primaryStage;
	private BorderPane rootLayout;
	private InputForController ifc;
	private GameController gameController;
	private Canvas inGameCanvas;
	private Renderer renderer;
	private GameState state;
	private Client client;
	private Loadout[] loadouts;
	private boolean loadoutMultiplayer;
	private boolean join;
	private SoundEffect menuMusic;
	// Options
	private boolean randomGravity;
	private int difficulty;
	private double masterVolume;
	private SoundEffect inGameMusic;

	HashMap<KeyCode, Boolean> button = new HashMap<>();

	@Override
	public void start(Stage primaryStage) {
		randomGravity = false;
		difficulty = 0;
		masterVolume = 0.5;
		showStartupDialogue();
		loadouts = new Loadout[3];
		loadouts[0] = new Loadout();
		loadouts[1] = new Loadout();
		loadouts[2] = new Loadout();
		this.renderer = new Renderer(masterVolume);
		File resDirectory = new File("Java Game/src/res");
		renderer.importFiles(resDirectory);
		this.primaryStage = primaryStage;
		this.primaryStage.setTitle("SolarLegends");
		this.ifc = new InputForController();

		initRootLayout();

		System.out.println("We starting boys");
		menuMusic = new SoundEffect("menu", "HIGH", true);
		inGameMusic = new SoundEffect("ingame", "HIGH", true);
		renderer.playMusic(menuMusic);
		showMenu();

	}

	/**
	 * Returns a Stage object, which is the post game menu corresponding with the
	 * provided State.
	 * 
	 * @param gameResult
	 *            the State that determines the nature of the post game screen.
	 * @return a Stage object.
	 */
	public Stage popupPostGameMenu(State gameResult) {
		String titleText;
		String image;
		String labelText;
		if (gameResult == State.WIN) {
			titleText = "You win!";
			image = "legends/upforever.gif";
			labelText = "Victory! \nYou have rid the planet of inferior machines \nand brought prestige to your faction!";
		} else if (gameResult == State.LOSE) {
			titleText = "You lose!";
			image = "head/head7.png";
			labelText = "Defeat! \nYour machines have been wiped out \nand have disgraced your faction!";
		} else if (gameResult == State.DRAW) {
			titleText = "You drew!";
			image = "supply.png";
			labelText = "Truce! \nBoth factions machines have been wiped out! \nUnder better leadership, victory may have \nbeen attained... ";
		} else if (gameResult == State.EXIT) {
			titleText = "You have abandoned your machines!";
			image = "whiteflag.png";
			labelText = "Surrender! \nYou have left your machines to their demise! \nThey have no chance without your leadership, \nyour faction will not forgive this act of \ncowardice...";
		} else {
			titleText = "The opposing commander has fled!";
			image = "legends/upforever.gif";
			labelText = "You win! \nThe opposing commander has fled! \nYour superior skills have caused a hasty \nretreat from your opponent! ";
		}
		final Stage dialog = new Stage();
		dialog.setTitle(titleText);
		Button returnToMenu = new Button("Return to Main Menu");
		Image robot = new Image("res/images/" + image);
		ImageView robotView = new ImageView(robot);
		robotView.setFitHeight(200);
		robotView.setFitWidth(200);

		Label displayLabel = new Label(labelText);
		displayLabel.setFont(Font.font(null, FontWeight.BOLD, 20));
		displayLabel.setTextFill(Color.WHITE);

		dialog.initModality(Modality.NONE);
		dialog.initOwner((Stage) primaryStage.getScene().getWindow());

		
		HBox dialogHbox = new HBox(20);
		dialogHbox.setAlignment(Pos.CENTER);

		VBox dialogVbox1 = new VBox(20);
		dialogVbox1.setAlignment(Pos.CENTER_LEFT);

		VBox dialogVbox2 = new VBox(20);
		dialogVbox2.setAlignment(Pos.CENTER_RIGHT);

		dialogHbox.getChildren().add(robotView);
		dialogVbox1.getChildren().add(displayLabel);
		dialogVbox1.getChildren().add(returnToMenu);

		returnToMenu.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent e) {
				// inside here you can use the minimize or close the previous stage//
				dialog.close();
				initRootLayout();
				renderer.stopMusic();
				loadouts = new Loadout[3];
				loadouts[0] = new Loadout();
				loadouts[1] = new Loadout();
				loadouts[2] = new Loadout();
				showMenu();
				renderer.playMusic(menuMusic);
			}
		});

		dialogHbox.getChildren().addAll(dialogVbox1, dialogVbox2);
		dialogHbox.setStyle("-fx-background-color: #000033;");
		Scene dialogScene = new Scene(dialogHbox, 850, 400);
		dialog.setScene(dialogScene);
		return dialog;
	}
	
	/**
	 * Constructor used for testing the UI.
	 */
	public UI() {
		randomGravity = false;
		difficulty = 0;
		masterVolume = 0.5;
		loadouts = new Loadout[3];
		loadouts[0] = new Loadout();
		loadouts[1] = new Loadout();
		loadouts[2] = new Loadout();
		this.renderer = new Renderer(masterVolume);
		this.primaryStage = new Stage();
		BorderPane bp = new BorderPane();
		Scene scn = new Scene(bp, 500, 500);
		primaryStage.setScene(scn);
	}
	
	/**
	 * Constructor used for testing the UI.
	 */
	public UI(InputForController ifc) {
		randomGravity = false;
		difficulty = 0;
		masterVolume = 0.5;
		loadouts = new Loadout[3];
		loadouts[0] = new Loadout();
		loadouts[1] = new Loadout();
		loadouts[2] = new Loadout();
		this.renderer = new Renderer(masterVolume);
		this.primaryStage = new Stage();
		BorderPane bp = new BorderPane();
		Scene scn = new Scene(bp, 500, 500);
		primaryStage.setScene(scn);
		this.ifc = ifc;
	}

	/**
	 * Sets the root layout, which the rest of the menus are put onto.
	 */
	public void initRootLayout() {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(UI.class.getResource("view/RootLayout.fxml"));
			rootLayout = (BorderPane) loader.load();

			Scene scene = new Scene(rootLayout);
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Shows the main menu.
	 */
	public void showMenu() {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(UI.class.getResource("view/Menu.fxml"));
			AnchorPane menu = (AnchorPane) loader.load();

			rootLayout.setCenter(menu);

			MenuController controller = loader.getController();
			controller.setUI(this);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Shows the multiplayer menu and connects the client to the server. If server
	 * isn't found, the multiplayer menu will not show.
	 */
	public void showMultiplayerMenu() {
		client = new Client();
		try {
			if (client.connectionStatus == Client.ConnectionStatus.NOT_CONNECTED) {
				client.connect();
			}
		} catch (NullPointerException e) {
			Alert failedDialog = new Alert(AlertType.ERROR);
			failedDialog.setTitle("Error");
			failedDialog.setHeaderText(null);
			failedDialog.setContentText("Servers are not running at the moment, sorry for any inconvenience");
			failedDialog.showAndWait();
			return;
		}

		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(UI.class.getResource("view/MultiplayerMenu.fxml"));
			AnchorPane multiplayerMenu = (AnchorPane) loader.load();
			rootLayout.setCenter(multiplayerMenu);

			MenuController controller = loader.getController();
			controller.setUI(this);

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Shows the multiplayer hosting menu.
	 */
	public void showMultiplayerMenuHost() {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(UI.class.getResource("view/MultiplayerMenuHost.fxml"));
			AnchorPane multiplayerMenuHost = (AnchorPane) loader.load();
			rootLayout.setCenter(multiplayerMenuHost);

			MenuController controller = loader.getController();
			controller.setUI(this);

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Shows the multiplayer join menu.
	 */
	public void showMultiplayerMenuJoin() {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(UI.class.getResource("view/MultiplayerMenuJoin.fxml"));
			AnchorPane multiplayerMenuJoin = (AnchorPane) loader.load();
			rootLayout.setCenter(multiplayerMenuJoin);

			MultiplayerMenuController controller = loader.getController();
			controller.setUI(this);

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Shows the help menu.
	 */
	public void showHelpMenu() {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(UI.class.getResource("view/HowToPlayMenu.fxml"));
			AnchorPane multiplayerMenuJoin = (AnchorPane) loader.load();
			rootLayout.setCenter(multiplayerMenuJoin);

			MenuController controller = loader.getController();
			controller.setUI(this);

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Shows the options menu.
	 */
	public void showOptionsMenu() {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(UI.class.getResource("view/OptionsMenu.fxml"));
			AnchorPane multiplayerMenuHost = (AnchorPane) loader.load();
			rootLayout.setCenter(multiplayerMenuHost);

			OptionsController controller = loader.getController();
			controller.setUI(this);
			controller.setOptions(randomGravity, difficulty, masterVolume);

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Shows the leaderboard.
	 */
	public void showLeaderBoard() {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(UI.class.getResource("view/LeaderboardMenu.fxml"));
			AnchorPane leaderboard = (AnchorPane) loader.load();
			rootLayout.setCenter(leaderboard);

			LeaderboardController controller = loader.getController();
			controller.setUI(this);

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Shows the loadout menu.
	 * 
	 * @param multiplayer
	 *            determines whether the loadout menu to be shown will affect
	 *            singleplayer or multiplayer.
	 * @param join
	 *            determines whether the multiplayer loadout menu to be shown is for
	 *            joining or hosting.
	 */
	public void showLoadoutMenu(boolean multiplayer, boolean join) {
		try {
			loadoutMultiplayer = multiplayer;
			this.join = join;
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(UI.class.getResource("view/LoadoutMenu.fxml"));
			AnchorPane loadoutMenu = (AnchorPane) loader.load();
			rootLayout.setCenter(loadoutMenu);

			LoadoutMenu controller = loader.getController();
			controller.setLoadouts(loadouts);
			controller.setUI(this, multiplayer);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Shows the connecting dialogue (for multiplayer).
	 * 
	 * @param serverName
	 *            the name of the game which is trying to be connected to.
	 * @param gameSelected
	 *            whether a game has been selected to connect to.
	 */
	public void showConnectingDialogue(String serverName, boolean gameSelected) {
		if (!gameSelected) {
			Alert failedDialog = new Alert(AlertType.ERROR);
			failedDialog.setTitle("Error");
			failedDialog.setHeaderText(null);
			failedDialog.setContentText("Please select a gameserver first");
			failedDialog.showAndWait();
			return;
		}
		TextInputDialog userInput = new TextInputDialog();
		userInput.setTitle(null);
		userInput.setHeaderText("Password required");
		userInput.setContentText("Please enter the password:");

		Optional<String> input = userInput.showAndWait();

		if (input.isPresent()) {
			InputForNetwork.serverPassword = input.get();
		} else {
			return;
		}
		InputForNetwork.serverName = serverName;
		InputForNetwork.playerId = 1;
		InputForNetwork.gameType = 2;
		client.setLoadout(loadouts);
		client.joinGame(InputForNetwork.serverName, InputForNetwork.serverPassword);
		launchUIGameLoop();

	}

	/**
	 * Shows the hosting dialogue (for multiplayer).
	 * 
	 * @param serverName
	 *            the name of the game which is being hosted.
	 * @param serverPassword
	 *            the password for the game which is being hosted.
	 */
	public void showHostingDialogue(String serverName, String serverPassword) {
		InputForNetwork.serverName = serverName;
		InputForNetwork.serverPassword = serverPassword;
		InputForNetwork.playerId = 0;
		InputForNetwork.gameType = 2;
		client.setLoadout(loadouts);
		client.createGame(InputForNetwork.serverName, InputForNetwork.serverPassword, randomGravity);
		Alert hostDialog = new Alert(AlertType.INFORMATION);
		hostDialog.setTitle("Attempting to host...");
		hostDialog.setHeaderText(null);
		hostDialog.setContentText("Attempting to host...");

		hostDialog.showAndWait();
		ifc.setPlayButtonPressed(true);
		launchUIGameLoop();

		/*
		 * hostDialog.setTitle("Hosting! Waiting for 2nd player...");
		 * hostDialog.setHeaderText(null);
		 * hostDialog.setContentText("Hosting! Waiting for 2nd player...");
		 *
		 * hostDialog.showAndWait();
		 */
	}

	/**
	 * Show the startup dialogue for the game.
	 */
	public void showStartupDialogue() {
		TextInputDialog userInput = new TextInputDialog();
		userInput.setTitle("Welcome!");
		userInput.setHeaderText("Welcome to Solar Legends!");
		userInput.setContentText("Please enter a username:");

		Optional<String> input = userInput.showAndWait();

		if (input.isPresent()) {
			InputForNetwork.username = input.get();
		} else {
			System.exit(0);
		}
	}

	/**
	 * Attempts to launch the game.
	 */
	public void playGame() {
		// menuMusic.stop();
		renderer.reset();
		if (loadoutMultiplayer) {
			if (join) {
				this.state = new GameState(false, true, loadouts);
				state.setDifficulty(difficulty);
				client.setGameState(state);
				showMultiplayerMenuJoin();
			} else {
				this.state = new GameState(false, true, loadouts);
				state.setDifficulty(difficulty);
				client.setGameState(state);
				showMultiplayerMenuHost();
			}

		} else {
			this.state = new GameState(true, true, loadouts);
			state.setDifficulty(difficulty);
			state.setRandomGravity(randomGravity);
			if (!(ifc.isPlayButtonPressed())) {
				launchUIGameLoop();
			}
		}
	}

	/**
	 * Returns the list of GameServers that are currently on the server.
	 * 
	 * @return the ObservableList of GameServers.
	 */
	public ObservableList<GameServer> getGameServers() {
		ObservableList<GameServer> obgs = FXCollections.observableArrayList();
		client.sendServersListPacket();
		while (client.menuStatus != Client.MenuStatus.SERVERS_LIST) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		for (GameServer gs : client.getGameServers()) {
			obgs.add(gs);
		}
		return obgs;
	}

	/**
	 * Returns the list of Players that are on the server's database.
	 * 
	 * @return the ObservableList of Players.
	 */
	public ObservableList<Player> getLeaderBoard() {
		ArrayList<Player> alp;
		ObservableList<Player> obp = FXCollections.observableArrayList();
		int position = 1;
		client.sendLeaderboardPacket();
		while (client.menuStatus != Client.MenuStatus.LEADERBOARD) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		alp = client.getLeaderboard();
		Collections.sort(alp);
		for (int i = 0; i < alp.size(); i++) {
			alp.get(i).setPosition(position);
			obp.add(alp.get(i));
			if (!(i == (alp.size() - 1))) {
				if (alp.get(i + 1).getWinRatio() < alp.get(i).getWinRatio()) {
					position += 1;
				}
			}
		}

		return obp;
	}

	/**
	 * Returns the Stage that all the menus and renderer use.
	 * 
	 * @return the primaryStage.
	 */
	public Stage getPrimaryStage() {
		return primaryStage;
	}

	/**
	 * Returns the Client that the UI uses to communicate with the servers.
	 * 
	 * @return the client.
	 */
	public Client getClient() {
		return client;
	}

	/**
	 * Sets the difficulty that will be passed to the A.I. in game.
	 * 
	 * @param difficulty
	 *            the new difficulty the A.I. is to be set to.
	 */
	public void setDifficulty(int difficulty) {
		this.difficulty = difficulty;
	}

	/**
	 * Determines whether or not random gravity is enabled in game.
	 * 
	 * @param randomGravity
	 *            if random gravity is to be set to true.
	 */
	public void setRandomGravity(boolean randomGravity) {
		this.randomGravity = randomGravity;
	}

	/**
	 * Determines the volume that sounds and music in the game will be played at.
	 * 
	 * @param masterVolume
	 *            1.0 is full volume, 0 is muted.
	 */
	public void setMasterVolume(double masterVolume) {
		this.masterVolume = masterVolume;
		renderer.setMusicVolume(masterVolume);
		renderer.playMusic(menuMusic);
	}

	/**
	 * Returns whether or not a key on the keyboard is being pressed or not.
	 * 
	 * @param key
	 *            the key we are checking.
	 * @return whether or not the key is being pressed.
	 */
	private boolean isPressed(KeyCode key) {
		boolean helterSkelter = button.getOrDefault(key, false);
		return helterSkelter;
	}

	/**
	 * Launches the UI's game loop, which in turn launches the entire game. This
	 * includes launching 2 threads, continuously rendering the game and other
	 * relevant methods.
	 */
	public void launchUIGameLoop() {
		ifc.clearInputList();
		if (InputForNetwork.gameType == 2) {
			Alert connectDialog = new Alert(AlertType.INFORMATION);
			connectDialog.setTitle("Attempting to connect...");
			connectDialog.setHeaderText(null);
			connectDialog.setContentText("Attempting to connect...");
			connectDialog.show();
			while (client.gameStatus != Client.GameStatus.START_GAME) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				if (client.menuStatus == Client.MenuStatus.SERVER_NOT_FOUND) {
					client.menuStatus = Client.MenuStatus.DEFAULT;
					connectDialog.close();
					Alert failedDialog = new Alert(AlertType.ERROR);
					failedDialog.setTitle("Error");
					failedDialog.setHeaderText(null);
					failedDialog.setContentText("Server not found/no longer exists");
					failedDialog.showAndWait();
					return;
				}
				if (client.menuStatus == Client.MenuStatus.INVALID_PASSWORD) {
					client.menuStatus = Client.MenuStatus.DEFAULT;
					connectDialog.close();
					Alert failedDialog = new Alert(AlertType.ERROR);
					failedDialog.setTitle("Error");
					failedDialog.setHeaderText(null);
					failedDialog.setContentText("Password entered is incorrect");
					failedDialog.showAndWait();
					return;
				}
				if (client.menuStatus == Client.MenuStatus.FULL_SERVER) {
					client.menuStatus = Client.MenuStatus.DEFAULT;
					connectDialog.close();
					Alert failedDialog = new Alert(AlertType.ERROR);
					failedDialog.setTitle("Error");
					failedDialog.setHeaderText(null);
					failedDialog.setContentText("Server is full, cannot connect");
					failedDialog.showAndWait();
					return;
				}
			}
			connectDialog.close();
			client.setIFC(ifc);
		}
		if (InputForNetwork.gameType == 2) {
		} else {
			state.init();
		}
		Group root = new Group();
		Scene theScene = new Scene(root, 1920, 1080, Color.YELLOW);
		primaryStage.setScene(theScene);

		inGameCanvas = new Canvas(1920, 1080);
		root.getChildren().add(inGameCanvas);
		GraphicsContext gc = inGameCanvas.getGraphicsContext2D();

		if (InputForNetwork.gameType == 2) {
			gameController = new GameController(ifc, state, client);
		} else {
			gameController = new GameController(ifc, state);
		}
		Thread actualGame = new Thread(gameController);

		(primaryStage.getScene()).setOnKeyPressed(event -> button.put(event.getCode(), true));
		(primaryStage.getScene()).setOnKeyReleased(event -> button.put(event.getCode(), false));

		AnimationTimer timer = new AnimationTimer() {

			@Override
			public void handle(long now) {
				inputLoop();
				renderer.draw(gc, state);
				state.updateRobotsHealthBars();
				if (!(gameController.getAppState() == State.MENU || gameController.getAppState() == State.PLAY)) {
					endGame(this);
				}
			}
		};
		actualGame.start();
		timer.start();
		renderer.playMusic(inGameMusic);
	}

	/**
	 * This method checks if any keys are being pressed, and if they are, adds them
	 * to the ifc.
	 */
	public void inputLoop() {
		if (isPressed(KeyCode.W)) {
			if (isPressed(KeyCode.A)) {
				ifc.addInput(KeyCode.Q);
			} else if (isPressed(KeyCode.D)) {
				ifc.addInput(KeyCode.E);
			} else {
				ifc.addInput(KeyCode.W);
			}

		}
		if (isPressed(KeyCode.A)) {
			if (isPressed(KeyCode.W)) {
				ifc.addInput(KeyCode.Q);
			} else {
				ifc.addInput(KeyCode.A);
			}
		}
		if (isPressed(KeyCode.D)) {
			if (isPressed(KeyCode.W)) {
				ifc.addInput(KeyCode.E);
			} else {
				ifc.addInput(KeyCode.D);
			}
		}
		if (isPressed(KeyCode.SPACE)) {
			ifc.addInput(KeyCode.SPACE);
		}
		if (isPressed(KeyCode.ENTER)) {
			ifc.addInput(KeyCode.ENTER);
		}
		if (isPressed(KeyCode.UP)) {
			ifc.addInput(KeyCode.UP);
		}
		if (isPressed(KeyCode.DOWN)) {
			ifc.addInput(KeyCode.DOWN);
		}
		if (isPressed(KeyCode.LEFT)) {
			ifc.addInput(KeyCode.LEFT);
		}
		if (isPressed(KeyCode.RIGHT)) {
			ifc.addInput(KeyCode.RIGHT);
		}
		if (isPressed(KeyCode.DIGIT1)) {
			ifc.addInput(KeyCode.DIGIT1);
		}
		if (isPressed(KeyCode.DIGIT2)) {
			ifc.addInput(KeyCode.DIGIT2);
		}
		if (isPressed(KeyCode.ESCAPE)) {
			ifc.addInput(KeyCode.ESCAPE);
		}
	}

	/**
	 * The method which attempts to end the game. Is passed the AnimationTimer which
	 * it stops, and then shows the post game menu.
	 * 
	 * @param at
	 *            the AnimationTimer that is to be stopped.
	 */
	public void endGame(AnimationTimer at) {
		at.stop();
		for (KeyCode kc : button.keySet()) {
			button.put(kc, false);
		}
		Stage dialog = popupPostGameMenu(gameController.getAppState());
		dialog.show();

	}

	/**
	 * The main method that starts it all.
	 * 
	 * @param args
	 *            (not used for anything).
	 */
	public static void main(String[] args) {
		launch(args);
	}

}