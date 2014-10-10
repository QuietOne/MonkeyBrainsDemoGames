package org.aitest.ai.behaviours.npc;

import com.jme3.ai.agents.Agent;
import com.jme3.ai.agents.behaviors.npc.SimpleAttackBehavior;
import com.jme3.ai.agents.events.GameEntitySeenEvent;
import java.util.Random;
import org.aitest.ai.utils.GunAndSwordInventory;

/**
 * Behaviour for attacking opponent if opponnet is seen.
 *
 * @author Tihomir Radosavljevic
 * @version 1.0.1
 */
public class AIAttackBehaviour extends SimpleAttackBehavior {

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
            weapons.getGun().attack(targetedEntity, tpf);
            targetPosition = null;
            //is he supossed to miss next time
            missOrNot((Agent) targetedEntity);
        } else {
            //if target is seen
            if (targetedEntity != null && targetedEntity.isEnabled()) {
                //attack with all weapon at disposal

                //if target is in range of sword strike, strike him
                if (weapons.getSword().isInRange(targetedEntity)) {
                    weapons.getSword().attack(targetedEntity, tpf);
                }
                //if target is in range of gun, fire him
                if (weapons.getGun().isInRange(targetedEntity)) {
                    weapons.getGun().attack(targetedEntity, tpf);
                }
                //is he supossed to miss next time
                missOrNot((Agent) targetedEntity);
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
            targetedEntity = targetAgent;
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
