package behaviours;

import com.jme3.ai.agents.Agent;
import com.jme3.ai.agents.behaviours.Behaviour;
import com.jme3.input.controls.ActionListener;
import fps.robotfight.util.RobotFightInventory;

/**
 *
 * @author Tihomir Radosavljevic
 * @version 1.0.0
 */
public class SwitchWeaponsBehaviour extends Behaviour implements ActionListener {

    public SwitchWeaponsBehaviour(Agent agent) {
        super(agent);
    }

    @Override
    protected void controlUpdate(float tpf) {
    }

    public void onAction(String name, boolean isPressed, float tpf) {
        if (name.equals("Switch") && isPressed) {
            ((RobotFightInventory) agent.getInventory()).switchWeapons();
        }
    }
}
