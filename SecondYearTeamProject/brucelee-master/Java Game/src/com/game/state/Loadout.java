package com.game.state;

import com.game.weapon.*;

import java.util.ArrayList;

/**
 * A Loadout is used to initialise the ammo and stats of each Robot.
 * <p>
 *     A Loadout is used to prepare the ammo, HP and Energy of a Robot. It is passed to a Robot to create the
 *     Robot object.
 * </p>
 */
public class Loadout {
    private int startHP;
    private int maxEnergy;
    private ArrayList<Weapon> inv;
    private int points = 50;
    private final int[] costs = {20, 20, 40, 15, 50, 40, 45, 55, 55};

    private final int hpCost = 30;
    private final int energyCost = 30;


    /**
     * The default constructor.
     * <p>
     *     The default ammo counts for each weapon. Also sets the default HP and Energy of a Robot. This Loadout will
     *     likely be edited from the LoadoutMenu.
     * </p>
     */
    public Loadout() {
        inv = new ArrayList<>();
        inv.add(new RocketLauncher(3));
        inv.add(new Sniper(3));
        inv.add(new GrenadeLauncher(1));
        inv.add(new Teleport(3));
        inv.add(new ClusterLauncher(1));
        inv.add(new GravityGun(1));
        inv.add(new PhaseGun(1));
        inv.add(new DrillLauncher(1));
        inv.add(new SpeedLauncher(1));
        this.startHP = 100;
        this.maxEnergy = 150;
    }

    /**
     * A constructor for the AI Loadout.
     * <p>
     *     A constructor used to create a Loadout for each AI controlled Robot. These take a difficulty and are given
     *     more ammo/HP/energy accordingly.
     * </p>
     * @param difficulty of the AI, determines the ammo/HP/energy of each AI controlled Robot.
     */
    public Loadout(int difficulty) {
        inv = new ArrayList<>();
        switch(difficulty) {
            case(0):
                this.startHP = 100;
                this.maxEnergy = 150;
                inv.add(new RocketLauncher(3));
                inv.add(new Sniper(3));
                inv.add(new GrenadeLauncher(1));
                inv.add(new Teleport(3));
                inv.add(new ClusterLauncher(1));
                inv.add(new GravityGun(1));
                inv.add(new PhaseGun(1));
                inv.add(new DrillLauncher(1));
                inv.add(new SpeedLauncher(1));
                break;
            case(1):
                this.startHP = 120;
                this.maxEnergy = 200;
                inv.add(new RocketLauncher(2));
                inv.add(new Sniper(2));
                inv.add(new GrenadeLauncher(2));
                inv.add(new Teleport(2));
                inv.add(new ClusterLauncher(1));
                inv.add(new GravityGun(1));
                inv.add(new PhaseGun(3));
                inv.add(new DrillLauncher(1));
                inv.add(new SpeedLauncher(2));
                break;
            case(2):
                this.startHP = 150;
                this.maxEnergy = 225;
                inv.add(new RocketLauncher(2));
                inv.add(new Sniper(2));
                inv.add(new GrenadeLauncher(3));
                inv.add(new Teleport(2));
                inv.add(new ClusterLauncher(2));
                inv.add(new GravityGun(2));
                inv.add(new PhaseGun(3));
                inv.add(new DrillLauncher(2));
                inv.add(new SpeedLauncher(2));
                break;
        }
    }

    /**
     * A constructor for Loadout with preset values.
     * <p>
     *     A constructor which takes all values. Used for online play when a Loadout is received from the GameServer.
     * </p>
     * @param startHP of the Robot.
     * @param maxEnergy of the Robot.
     * @param ammo list for each Weapon.
     */
    public Loadout(int startHP, int maxEnergy, int[] ammo) {
        inv = new ArrayList<>();
        inv.add(new RocketLauncher(ammo[0]));
        inv.add(new Sniper(ammo[1]));
        inv.add(new GrenadeLauncher(ammo[2]));
        inv.add(new Teleport(ammo[3]));
        inv.add(new ClusterLauncher(ammo[4]));
        inv.add(new GravityGun(ammo[5]));
        inv.add(new PhaseGun(ammo[6]));
        inv.add(new DrillLauncher(ammo[7]));
        inv.add(new SpeedLauncher(ammo[8]));
        this.startHP = startHP;
        this.maxEnergy = maxEnergy;
    }

    /**
     * Get the start HP value of the current Loadout.
     * @return the start HP of the Loadout.
     */
    public int getStartHP() {
        return startHP;
    }

    /**
     * Get the energy per turn of the current Loadout.
     * @return the energy per turn of the Loadout.
     */
    public int getMaxEnergy() {
        return maxEnergy;
    }

    /**
     * Get the points remaining for the current Loadout.
     * @return the current points remaining of the Loadout.
     */
    public int getPoints() { return points; }

    /**
     * Get the list of Weapon for the current Loadout.
     * @return an ArrayList of Weapon for the current Loadout.
     */
    public ArrayList<Weapon> getInv() {
        return inv;
    }

    /**
     * Increment the ammo for the given Weapon.
     * <p>
     *     Increments the ammo of the given Weapon if the Loadout has enough points to purchase it,
     *     then it decreases the points by the cost of the ammo.
     * </p>
     * @param id of the Weapon to have ammo increased (fixed list order).
     */
    public void incrementAmmo(int id) {
        if (costs[id] <= points) {
            inv.get(id).refillAmmo(1);
            points -= costs[id];
        }
    }

    /**
     * Decrement the ammo for the given Weapon.
     * <p>
     *     Decrement the ammo of the given Weapon if the Loadout has ammo, then it increases the points of the Loadout
     *     by the cost of the ammo.
     * </p>
     * @param id of the Weapon to have ammo decreased (fixed list order).
     */
    public void decrementAmmo(int id) {
        if (inv.get(id).getAmmo() > 0) {
            inv.get(id).refillAmmo(-1);
            points += costs[id];
        }
    }

    /**
     * Get the array of costs for each Weapon.
     * @return the array of costs.
     */
    public int[] getCosts() {
        return costs;
    }

    /**
     * Get the cost of HP.
     * @return the cost of HP.
     */
    public int getHpCost() {
        return hpCost;
    }

    /**
     * Get the cost of energy.
     * @return the cost of energy.
     */
    public int getEnergyCost() {
        return energyCost;
    }

    /**
     * Increment the HP of the Loadout by the increment amount.
     * <p>
     *     Increment HP by the specified increment if the Loadout has enough points to purchase it,
     *     then it decreases the points by the cost of HP.
     * </p>
     */
    public void incrementHP() {
        if (hpCost <= points) {
            this.startHP += 10;
            points -= hpCost;
        }
    }

    /**
     * Decrement the HP of the Loadout by the increment amount.
     * <p>
     *     Decrement HP by the specified increment amount if the Loadout has more than the minimum HP,
     *     then is increases the points by the cost of HP.
     * </p>
     */
    public void decrementHP() {
        if (startHP > 100) {
            this.startHP -= 10;
            points += hpCost;
        }
    }

    /**
     * Increment the energy of the Loadout by the increment amount.
     * <p>
     *     Increment energy by the specified increment if the Loadout has enough points to purchase it,
     *     then it decreases the points by the cost of energy.
     * </p>
     */
    public void incrementEnergy() {
        if (energyCost <= points) {
            this.maxEnergy += 25;
            points -= energyCost;
        }
    }

    /**
     * Decrement the energy of the Loadout by the increment amount.
     * <p>
     *     Decrement energy by the specified increment amount if the Loadout has more than the minimum energy,
     *     then is increases the points by the cost of energy.
     * </p>
     */
    public void decrementEnergy() {
        if (maxEnergy > 150) {
            this.maxEnergy -= 25;
            points += energyCost;
        }
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("Health: ");
        buffer.append(startHP);
        buffer.append("\n");
        buffer.append("Max energy: ");
        buffer.append(maxEnergy);
        buffer.append("\n");
        buffer.append("Weapon ammo: ");
        for (Weapon w : inv) {
            buffer.append(w.getAmmo());
            buffer.append(", ");
        }
        buffer.append("\n");
        return buffer.toString();
    }
}
