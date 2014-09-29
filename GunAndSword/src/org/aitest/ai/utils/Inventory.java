package org.aitest.ai.utils;

import com.jme3.ai.agents.Agent;
import com.jme3.ai.agents.util.systems.InventorySystem;
import com.jme3.ai.agents.util.weapons.AbstractWeapon;

/**
 *
 * @author Tihomir Radosavljević
 * @version 1.0.0
 */
public class Inventory implements InventorySystem{

    /**
     * Sword that this agent has.
     */
    private Sword sword;
    /**
     * Gun that this agent has.
     */
    private Gun gun;

    public Inventory(Agent agent) {
        sword = new Sword(agent);
        gun = new Gun(agent);
    }
    
    
    
    public void update(float tpf) {
        sword.update(tpf);
        gun.update(tpf);
    }

    public float getInventoryMass() {
        return sword.getMass() + gun.getMass();
    }

    public AbstractWeapon getActiveWeapon() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Sword getSword() {
        return sword;
    }

    public void setSword(Sword sword) {
        this.sword = sword;
    }

    public Gun getGun() {
        return gun;
    }

    public void setGun(Gun gun) {
        this.gun = gun;
    }
    
    

}
