package com.game.controller;

import com.game.UI.InputForController;
import com.game.UI.InputForNetwork;
import com.game.net.Client;
import com.game.state.GameState;
import javafx.scene.input.KeyCode;

import java.util.ArrayList;

/**
 * GameController houses the main game loop, through which we update the GameState, gets input from the UI,
 * and finally Render the new screen. In multiplayer, this also sends input and the GameState the server.
 */
public class GameController implements Runnable {

	public enum State {
		PLAY, PAUSE, LOSE, WIN, MENU, EXIT, DRAW, OPPONENT_DISCONNECTED
	}

	private State appState;
	private GameState state;
	private InputForController ifc;

	private int playerID = 0;
	private int frameCounter;

	private Client client;

	// store the buttons that are pressed
	public static ArrayList<KeyCode> buttonsPressed = new ArrayList<>();

	/**
	 * Default constructor for the GameController.
	 * @param ifc the input provider for the game.
	 * @param state the GameState which will be controlled by the GameController.
	 */
	public GameController(InputForController ifc, GameState state) {
		this.state = state;
		this.ifc = ifc;
	}

	/**
	 * A constructor used for multiplayer versions of the game.
	 * @param ifc the input provider for the game.
	 * @param gameState the GameState which will be controlled by the GameController.
	 * @param client the Client used to connect to the Server.
	 */
	public GameController(InputForController ifc, GameState gameState, Client client) {
		this.state = gameState;
		this.ifc = ifc;
		this.client = client;
	}

	/**
	 * GameController is a runnable, this method will launch the thread in which the GameController will run.
	 */
	public void run() {
		ifc.clearInputList();
		ifc.setPlayButtonPressed(true);
		appState = State.MENU;
		while (appState == State.MENU) {
			if (ifc.isPlayButtonPressed()) {
				appState = State.PLAY;
				ifc.setPlayButtonPressed(false);

			}
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
			}
		}

		Thread loop;
		if (InputForNetwork.gameType == 1) {
			loop = new Thread() {
				public void run() {
					gameLoop();
					return;
				}
			};
		} else {
			while (client.gameStatus != Client.GameStatus.START_GAME) {
				Thread.yield();
			}
			loop = new Thread() {
				public void run() {
					onlineGameLoop();
					return;
				}
			};
		}
		loop.start();
	}

	/**
	 * The loop which handles the main game features. Our game runs fixed time steps, making sure to catch up in
	 * updates if it spends too long on other parts of the game loop.
	 */
	public void gameLoop() {
		// I will now initialise some constants for timing the game loop
		// How frequently the game state should update
		final double UPDATE_RATE = 60.0;
		// Time between state updates (based on nano second timer)
		final double TIME_BETWEEN_UPDATES = 1000000000 / UPDATE_RATE;

		// Get the initial time of the loop
		double lastUpdateTime = System.nanoTime();

		// Set frameCounter for weapons to 0
		frameCounter = 0;
		while (appState == State.PLAY && state.gameResult == -1) {
			// Now we use the timing
			double now = System.nanoTime();

			while (now - lastUpdateTime > TIME_BETWEEN_UPDATES) {
				update();
				// //System.out.println("Step, with state: " + appState + " gameResult: " +
				// state.gameResult);

				state.step(TIME_BETWEEN_UPDATES / 1000000000);
				lastUpdateTime += TIME_BETWEEN_UPDATES;
				frameCounter += 1;
			}
			state.stop();
			while (now - lastUpdateTime < TIME_BETWEEN_UPDATES) {
				Thread.yield();
				try {
					Thread.sleep(1);
				} catch (Exception e) {
				}

				now = System.nanoTime();
			}
		}
		switch (state.gameResult) {
		case 0:
			appState = State.WIN;
			break;
		case 1:
			appState = State.LOSE;
			break;
		case 2:
			appState = State.DRAW;
			break;
		default:
			break;
		}
		// System.out.println("QAppState " + appState);
	}

	/**
	 * The update model which is called every loop of the game loop. The model for our game loop is Input -> Update
	 * -> Render.
	 */
	public void update() {
		KeyCode input = ifc.getInput();

		// Get ai plan
		if (state.hasCommands()) {
			KeyCode AIinput = state.getNextInput();
			if (AIinput == KeyCode.D) {
				state.moveRight(1);
			} else if (AIinput == KeyCode.A) {
				state.moveLeft(1);
			} else if (AIinput == KeyCode.E) {
				state.moveRight(1);
				state.jump(1);
			} else {
				state.moveLeft(1);
				state.jump(1);
			}
		}

		if (input != null) {
			// Input for ending turn
			if (input == KeyCode.ENTER) {
				state.endTurn();
			}
			// Input for movement
			if (input == KeyCode.W) {
				state.jump(playerID);
			}
			if (input == KeyCode.A) {
				state.moveLeft(playerID);
				buttonsPressed.add(KeyCode.A);
			}
			if (input == KeyCode.D) {
				state.moveRight(playerID);
				buttonsPressed.add(KeyCode.D);
			}
			if (input == KeyCode.Q) {
				state.moveLeft(playerID);
				state.jump(playerID);
			}
			if (input == KeyCode.E) {
				state.moveRight(playerID);
				state.jump(playerID);
			}

			// Input for weapon control
			if (input == KeyCode.UP) {
				state.increaseAngle(playerID);
			}
			if (input == KeyCode.DOWN) {
				state.decreaseAngle(playerID);
			}
			if (input == KeyCode.LEFT) {
				state.decreasePower(playerID);
			}
			if (input == KeyCode.RIGHT) {
				state.increasePower(playerID);
			}
			if (input == KeyCode.SPACE) {
				state.fireWeapon(playerID);
			}
			if (input == KeyCode.DIGIT1) {
				if (frameCounter >= 15) {
					state.setCurrentSlot(0, playerID);
					frameCounter = 0;
				}
			}
			if (input == KeyCode.DIGIT2) {
				if (frameCounter >= 15) {
					state.setCurrentSlot(1, playerID);
					frameCounter = 0;
				}
			}

			if (input == KeyCode.ESCAPE) {
				if (frameCounter > 40) {
					state.gameResult = 1;
					appState = State.EXIT;
					frameCounter = 0;
				}
			}
		}
	}

	/**
	 * A game loop which runs when the game is in multiplayer. This has adjusted method calls for multiplayer. It
	 * also has a lower target FPS to allow for receiving data from the Server.
	 */
	private void onlineGameLoop() {
		long lastLoopTime = System.nanoTime();
		final int TARGET_FPS = 30;
		final long OPTIMAL_TIME = 1000000000 / TARGET_FPS;
		int lastFpsTime = 0;

		frameCounter = 0;

		while (appState == State.PLAY && state.gameResult == -1) {
			long now = System.nanoTime();
			long updateLength = now - lastLoopTime;
			lastLoopTime = now;

			// update the frame counter
			lastFpsTime += updateLength;

			// update our FPS counter if a second has passed since
			// we last recorded
			if (lastFpsTime >= 1000000000) {
				lastFpsTime = 0;
			}

			// update the game logic
			if (client.hasTurn) {
				sendInput();
			}
			frameCounter++;
			state.updateGameHUD();
			state.step(0.03);
			state.onlineCheckDrops();

			// we want each frame to take 10 milliseconds, to do this
			// we've recorded when we started the frame. We add 10 milliseconds
			// to this and then factor in the current time to give
			// us our final value to wait for
			// remember this is in ms, whereas our lastLoopTime etc. vars are in ns.

			try {
				long sleepTime = (lastLoopTime - System.nanoTime() + OPTIMAL_TIME) / 1000000;
				if (sleepTime > 0)
					Thread.sleep(sleepTime);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		switch (state.gameResult) {
		case 0:
			appState = State.WIN;
			break;
		case 1:
			appState = State.LOSE;
			break;
		case 2:
			appState = State.DRAW;
			break;
		case 3:
			appState = State.OPPONENT_DISCONNECTED;
			break;
		default:
			break;
		}
		// System.out.println("AppState: " + appState);

	}

	/**
	 * Send the input from the local game instance to the Server.
	 */
	private synchronized void sendInput() {
		KeyCode input = ifc.getInput();
		if (input != null) {
			switch (input) {
			case SPACE:
				if (frameCounter >= 30 && state.getCurrentChar().getWeapon().getAmmo() > 0) {
					client.sendInput(input);
					frameCounter = 0;
				}
				break;
			case DIGIT1:
			case DIGIT2:
				if (frameCounter >= 12) {
					client.sendInput(input);
					frameCounter = 0;
				}
				break;
			case ENTER:
				if (frameCounter >= 30) {
					client.sendInput(input);
					frameCounter = 0;
				}
				break;
			case ESCAPE:
				if (frameCounter >= 30) {
					appState = State.EXIT;
					client.sendLeaveGamePacket();
				}
				break;
			default:
				client.sendInput(input);
				break;
			}
		}
	}

	/**
	 * Gets the appState - e.g. Menu, Play, Pause.
	 * @return the appState of the game.
	 */
	public State getAppState() {
		return appState;
	}
	public void setAppState(State s) { this.appState = s; }
}
