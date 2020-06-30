package com.game.UI.view;

import com.game.UI.UI;
import com.game.state.Loadout;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;

public class LoadoutMenu {
	private UI ui;
	private boolean multiplayer;
	private Loadout[] lds;
	private int currentLO = 0;

	@FXML
	private Label ammo1;
	@FXML
	private Label ammo2;
	@FXML
	private Label ammo3;
	@FXML
	private Label ammo4;
	@FXML
	private Label ammo5;
	@FXML
	private Label ammo6;
	@FXML
	private Label ammo7;
	@FXML
	private Label ammo8;
	@FXML
	private Label ammo9;
	@FXML
	private Label hp;
	@FXML
	private Label energy;
	@FXML
	private Label points;
	@FXML
	private Label cost1;
	@FXML
	private Label cost2;
	@FXML
	private Label cost3;
	@FXML
	private Label cost4;
	@FXML
	private Label cost5;
	@FXML
	private Label cost6;
	@FXML
	private Label cost7;
	@FXML
	private Label cost8;
	@FXML
	private Label cost9;
	@FXML
	private Label costhp;
	@FXML
	private Label costenergy;

	/**
	 * Default constructor for loadout menu controller.
	 */
	public LoadoutMenu() {

	}

	/**
	 * Sets the UI that the menu controller will be interacting with, as well as define whether or not the loadout will be for multiplayer or not.
	 * @param ui the UI that will be interacted with.
	 * @param multiplayer whether or not the controller is to be set up for multiplayer.
	 */
	public void setUI(UI ui, boolean multiplayer) {
		this.ui = ui;
		this.multiplayer = multiplayer;
	}

	/**
	 * Set the loadouts to be modified by the loadout controller.
	 * @param loadouts the loadouts to be modified.
	 */
	public void setLoadouts(Loadout[] loadouts) {
		lds = loadouts;
	}

	/**
	 * Switch to the main menu if in singleplayer, or the multiplayer menu if in multiplayer.
	 */
	@FXML
	private void goMainMenu() {
		if (multiplayer) {
			ui.showMultiplayerMenu();
		} else {
			ui.showMenu();
		}

	}

	/**
	 * Confirm choices in the loadout menu and move on.
	 */
	@FXML
	private void handlePlayButton() {
		ui.playGame();
	}

	/**
	 * Shows the costs of all weapons, health and energy points.
	 */
	@FXML
	private void init() {
		Loadout lo = new Loadout();
		cost1.setText("Cost: " + Integer.toString(lo.getCosts()[0]));
		cost2.setText("Cost: " + Integer.toString(lo.getCosts()[1]));
		cost3.setText("Cost: " + Integer.toString(lo.getCosts()[2]));
		cost4.setText("Cost: " + Integer.toString(lo.getCosts()[3]));
		cost5.setText("Cost: " + Integer.toString(lo.getCosts()[4]));
		cost6.setText("Cost: " + Integer.toString(lo.getCosts()[5]));
		cost7.setText("Cost: " + Integer.toString(lo.getCosts()[6]));
		cost8.setText("Cost: " + Integer.toString(lo.getCosts()[7]));
		cost9.setText("Cost: " + Integer.toString(lo.getCosts()[8]));
		costhp.setText("Cost: " + Integer.toString(lo.getHpCost()));
		costenergy.setText("Cost: " + Integer.toString(lo.getEnergyCost()));
		refresh();
	}

	/**
	 * Refreshes all the values of all fields in the loadout menu.
	 */
	@FXML
	private void refresh() {
		Loadout lo = lds[currentLO];
		ammo1.setText(String.valueOf(lo.getInv().get(0).getAmmo()));
		ammo2.setText(String.valueOf(lo.getInv().get(1).getAmmo()));
		ammo3.setText(String.valueOf(lo.getInv().get(2).getAmmo()));
		ammo4.setText(String.valueOf(lo.getInv().get(3).getAmmo()));
		ammo5.setText(String.valueOf(lo.getInv().get(4).getAmmo()));
		ammo6.setText(String.valueOf(lo.getInv().get(5).getAmmo()));
		ammo7.setText(String.valueOf(lo.getInv().get(6).getAmmo()));
		ammo8.setText(String.valueOf(lo.getInv().get(7).getAmmo()));
		ammo9.setText(String.valueOf(lo.getInv().get(8).getAmmo()));
		hp.setText(String.valueOf(lo.getStartHP()));
		energy.setText(String.valueOf(lo.getMaxEnergy()));
		points.setText(String.valueOf(lo.getPoints()));
		if (lo.getPoints() < 15) {
			points.setTextFill(Color.RED);
		} else {
			points.setTextFill(Color.WHITE);
		}
	}

	/**
	 * Selects loadout for Robot 1.
	 */
	@FXML
	private void r1() {
		currentLO = 0;
		refresh();
	}

	/**
	 * Selects loadout for Robot 2.
	 */
	@FXML
	private void r2() {
		currentLO = 1;
		refresh();
	}

	/**
	 * Selects loadout for Robot 3.
	 */
	@FXML
	private void r3() {
		currentLO = 2;
		refresh();
	}

	// ROCKET LAUNCHER
	/**
	 * Increase rocket launcher ammo.
	 */
	@FXML
	private void inc_rl() {
		lds[currentLO].incrementAmmo(0);
		refresh();
	}

	/**
	 * Decrease rocket launcher ammo.
	 */
	@FXML
	private void dec_rl() {
		lds[currentLO].decrementAmmo(0);
		refresh();
	}

	// SNIPER
	/**
	 * Increase sniper ammo.
	 */
	@FXML
	private void inc_sr() {
		lds[currentLO].incrementAmmo(1);
		refresh();
	}

	/**
	 * Decrease sniper ammo.
	 */
	@FXML
	private void dec_sr() {
		lds[currentLO].decrementAmmo(1);
		refresh();
	}

	// GRENADE LAUNCHER
	/**
	 * Increase grenade launcher ammo.
	 */
	@FXML
	private void inc_gl() {
		lds[currentLO].incrementAmmo(2);
		refresh();
	}

	/**
	 * Decrease grenade launcher ammo.
	 */
	@FXML
	private void dec_gl() {
		lds[currentLO].decrementAmmo(2);
		refresh();
	}

	// TELEPORT
	/**
	 * Increase teleporter ammo.
	 */
	@FXML
	private void inc_tg() {
		lds[currentLO].incrementAmmo(3);
		refresh();
	}

	/**
	 * Decrease teleporter ammo.
	 */
	@FXML
	private void dec_tg() {
		lds[currentLO].decrementAmmo(3);
		refresh();
	}

	// CLUSTER LAUNCHER
	/**
	 * Increase cluster launcher ammo.
	 */
	@FXML
	private void inc_cl() {
		lds[currentLO].incrementAmmo(4);
		refresh();
	}

	/**
	 * Decrease grenade launcher ammo.
	 */
	@FXML
	private void dec_cl() {
		lds[currentLO].decrementAmmo(4);
		refresh();
	}

	// GRAVITY GUN
	/**
	 * Increase gravity gun ammo.
	 */
	@FXML
	private void inc_gg() {
		lds[currentLO].incrementAmmo(5);
		refresh();
	}

	/**
	 * Decrease gravity gun ammo.
	 */
	@FXML
	private void dec_gg() {
		lds[currentLO].decrementAmmo(5);
		refresh();
	}

	// PHASE GUN
	/**
	 * Increase phase gun ammo.
	 */
	@FXML
	private void inc_pg() {
		lds[currentLO].incrementAmmo(6);
		refresh();
	}

	/**
	 * Decrease phase gun ammo.
	 */
	@FXML
	private void dec_pg() {
		lds[currentLO].decrementAmmo(6);
		refresh();
	}

	// DRILL LAUNCHER
	/**
	 * Increase drill launcher ammo.
	 */
	@FXML
	private void inc_dl() {
		lds[currentLO].incrementAmmo(7);
		refresh();
	}

	/**
	 * Decrease drill launcher ammo.
	 */
	@FXML
	private void dec_dl() {
		lds[currentLO].decrementAmmo(7);
		refresh();
	}

	// SPEED LAUNCHER
	/**
	 * Increase speed launcher ammo.
	 */
	@FXML
	private void inc_sl() {
		lds[currentLO].incrementAmmo(8);
		refresh();
	}

	/**
	 * Decrease speed launcher ammo.
	 */
	@FXML
	private void dec_sl() {
		lds[currentLO].decrementAmmo(8);
		refresh();
	}

	/**
	 * Increase health points.
	 */
	@FXML
	private void incHP() {
		lds[currentLO].incrementHP();
		refresh();
	}

	/**
	 * Decrease health points.
	 */
	@FXML
	private void decHP() {
		lds[currentLO].decrementHP();
		refresh();
	}

	/**
	 * Increase energy points.
	 */
	@FXML
	private void incEnergy() {
		lds[currentLO].incrementEnergy();
		refresh();
	}

	/**
	 * Decrease energy points.
	 */
	@FXML
	private void decEnergy() {
		lds[currentLO].decrementEnergy();
		refresh();
	}
}
