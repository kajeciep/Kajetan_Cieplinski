package com.game.state;



import com.game.weapon.Weapon;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

public class LoadoutTest {

    Loadout loadout;

    @Before
    public void setUp() throws Exception {
        loadout = new Loadout();
    }

    @Test
    public void getStartHP() throws Exception {
        assert(loadout.getStartHP() == 100);
    }

    @Test
    public void getMaxEnergy() throws Exception {
        assert(loadout.getMaxEnergy() == 150);
    }

    @Test
    public void getPoints() throws Exception {
        assert(loadout.getPoints() == 50);
    }

    @Test
    public void getInv() throws Exception {
        ArrayList<Weapon> inv = loadout.getInv();
        assert(inv.get(0).getAmmo() == 3);
        assert(inv.get(1).getAmmo() == 3);
        assert(inv.get(2).getAmmo() == 1);
        assert(inv.get(3).getAmmo() == 3);
        assert(inv.get(4).getAmmo() == 1);
        assert(inv.get(5).getAmmo() == 1);
        assert(inv.get(6).getAmmo() == 1);
        assert(inv.get(7).getAmmo() == 1);
        assert(inv.get(8).getAmmo() == 1);
    }

    @Test
    public void incrementAmmo() throws Exception {
        loadout.incrementAmmo(8);
        loadout.incrementAmmo(0);
        ArrayList<Weapon> inv = loadout.getInv();
        assert(inv.get(0).getAmmo() == 4);
        assert(inv.get(8).getAmmo() == 1); //Not enough points
        assert(loadout.getPoints() == 30);
    }

    @Test
    public void decrementAmmo() throws Exception {
        loadout.decrementAmmo(8);
        loadout.decrementAmmo(8); //No ammo remaining
        ArrayList<Weapon> inv = loadout.getInv();
        assert(inv.get(8).getAmmo() == 0);
        assert(loadout.getPoints() == 105);
    }

    @Test
    public void getCosts() throws Exception {
        int[] costs = {20, 20, 40, 15, 50, 40, 45, 55, 55};
        assert(Arrays.equals(costs, loadout.getCosts()));
    }

    @Test
    public void getHpCost() throws Exception {
        assert(loadout.getHpCost() == 30);
    }

    @Test
    public void getEnergyCost() throws Exception {
        assert(loadout.getEnergyCost() == 30);
    }

    @Test
    public void incrementHP() throws Exception {
        loadout.incrementHP();
        loadout.incrementHP(); //Cannot afford
        assert(loadout.getStartHP() == 110);
        assert(loadout.getPoints() == 20);
    }

    @Test
    public void decrementHP() throws Exception {
        loadout.decrementHP(); //Will not go down further
        assert(loadout.getStartHP() == 100);
        assert(loadout.getPoints() == 50);
    }

    @Test
    public void incrementEnergy() throws Exception {
        loadout.incrementEnergy();
        loadout.incrementEnergy(); //Cannot afford this
        assert(loadout.getMaxEnergy() == 175);
        assert(loadout.getPoints() == 20);
    }

    @Test
    public void decrementEnergy() throws Exception {
        loadout.decrementEnergy(); //Will not go down further
        assert(loadout.getMaxEnergy() == 150);
        assert(loadout.getPoints() == 50);
    }

}