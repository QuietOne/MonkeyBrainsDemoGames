package org.aitest.ai.behaviours.player;

import com.jme3.ai.agents.Agent;
import com.jme3.ai.agents.behaviors.Behavior;
import com.jme3.animation.AnimControl;
import com.jme3.input.controls.AnalogListener;
import com.jme3.math.Vector3f;
import java.util.List;
import org.aitest.ai.utils.GunAndSwordInventory;

/**
 *
 * @author Tihomir Radosavljevic
 * @version 1.1.0
 */
public class PlayerAttackBehaviour extends Behavior implements AnalogListener {

    GunAndSwordInventory inventory;
    List<AnimControl> animationList;

    public PlayerAttackBehaviour(Agent agent) {
        super(agent);
        inventory = (GunAndSwordInventory) agent.getInventory();
    }

    @Override
    protected void controlUpdate(float tpf) {
    }

    public void onAnalog(String name, float value, float tpf) {
        if (name.equals("gunFired")) {
            inventory.getGun().attack(Vector3f.ZERO, tpf);
        }
        if (name.equals("swordStrike")) {
            inventory.getSword().attack(Vector3f.ZERO, tpf);
        }
    }
}
