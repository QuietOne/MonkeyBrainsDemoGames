package org.aitest.ai.behaviours.npc;

import com.jme3.ai.agents.Agent;
import com.jme3.ai.agents.behaviours.npc.SimpleAttackBehaviour;
import com.jme3.ai.agents.events.GameEntitySeenEvent;
import java.util.Random;
import org.aitest.ai.utils.GunAndSwordInventory;

/**
 * Behaviour for attacking opponent if opponnet is seen.
 *
 * @author Tihomir Radosavljevic
 * @version 1.0.1
 */
public class AIAttackBehaviour extends SimpleAttackBehaviour {

    private GunAndSwordInventory weapons;
    /**
     * Bigger value means easier game, if it is 1, then agent will never miss.
     * Must be greater or equal to 1.
     */
    private final int simplicity = 60;
    /**
     * To add some randomness to game.
     */
    private Random random;

    public AIAttackBehaviour(Agent agent) {
        super(agent);
        weapons = (GunAndSwordInventory) agent.getInventory();
        random = new Random();
    }

    @Override
    protected void controlUpdate(float tpf) {
        if (targetPosition != null) {
            weapons.getGun().attack(targetedObject, tpf);
            targetPosition = null;
            //is he supossed to miss next time
            missOrNot((Agent) targetedObject);
        } else {
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
                //is he supossed to miss next time
                missOrNot((Agent) targetedObject);
            }
        }
    }

    @Override
    public void handleGameEntitySeenEvent(GameEntitySeenEvent event) {
        if (event.getGameEntitySeen() instanceof Agent) {
            Agent targetAgent = (Agent) event.getGameEntitySeen();
            if (agent.isSameTeam(targetAgent)) {
                return;
            }
            targetedObject = targetAgent;
            missOrNot(targetAgent);
            enabled = true;
        }
    }

    private void missOrNot(Agent agent) {
        if (simplicity > 1) {
            int number = random.nextInt(simplicity);
            if (number > 1) {
                targetPosition = agent.getLocalTranslation().clone().mult(1.1f);
            }
        }
    }
}
