package org.aitest.ai.behaviours.npc;

import com.jme3.ai.agents.Agent;
import com.jme3.ai.agents.behaviours.npc.SimpleAttackBehaviour;
import com.jme3.ai.agents.events.GameObjectSeenEvent;
import org.aitest.ai.model.AIModel;

/**
 * Behaviour for attacking opponent if opponnet is seen.
 *
 * @author Tihomir Radosavljevic
 * @version 1.0
 */
public class AIAttackBehaviour extends SimpleAttackBehaviour {

    private AIModel weapons;

    public AIAttackBehaviour(Agent agent) {
        super(agent);
        weapons = (AIModel) agent.getModel();
    }

    @Override
    protected void controlUpdate(float tpf) {
        //if target is seen
        if (targetedObject != null && targetedObject.isEnabled()) {
            //attack with all weapon at disposal

            //if target is in range of sword strike, strike him
            if (weapons.getSword().isInRange(targetedObject)) {
                weapons.getSword().attack(targetedObject, tpf);
            }
            //if target is in range of gun, fire him
            if (weapons.getGun().isInRange(targetedObject)) {
                weapons.getGun().attack(targetedObject, tpf);
            }
        }
        //update cooldown for weapons
        weapons.getGun().update(tpf);
        weapons.getSword().update(tpf);
    }

    @Override
    public void handleGameObjectSeenEvent(GameObjectSeenEvent event) {
        if (event.getGameObjectSeen() instanceof Agent) {
            Agent targetAgent = (Agent) event.getGameObjectSeen();
            if (agent.isSameTeam(targetAgent)) {
                return;
            }
            targetedObject = targetAgent;
            enabled = true;
        }
    }
}
