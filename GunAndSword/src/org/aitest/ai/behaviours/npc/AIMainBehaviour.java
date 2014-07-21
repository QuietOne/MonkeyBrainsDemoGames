package org.aitest.ai.behaviours.npc;

import com.jme3.ai.agents.Agent;
import com.jme3.ai.agents.behaviours.Behaviour;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;

/**
 *
 * @author Tihomir Radosavljevic
 * @version 1.0
 */
public class AIMainBehaviour extends Behaviour {

    private AILookBehaviour lookBehaviour;
    private AIAttackBehaviour attackBehaviour;
    private AIWanderBehaviour wanderBehaviour;
    private AISeekBehaviour seekBehaviour;
    private final Vector3f area = new Vector3f(2, 0, 2);

    public AIMainBehaviour(Agent agent) {
        super(agent);
        attackBehaviour = new AIAttackBehaviour(agent);
        seekBehaviour = new AISeekBehaviour(agent);
        lookBehaviour = new AILookBehaviour(agent);
        lookBehaviour.addListener(attackBehaviour);
        lookBehaviour.addListener(seekBehaviour);
        wanderBehaviour = new AIWanderBehaviour(agent);
        wanderBehaviour.setArea(agent.getLocalTranslation().subtract(area), agent.getLocalTranslation().add(area));

    }

    @Override
    protected void controlUpdate(float tpf) {
        lookBehaviour.update(tpf);
        attackBehaviour.update(tpf);
        if (seekBehaviour.getTarget() != null && seekBehaviour.getTarget().isEnabled()) {
            seekBehaviour.update(tpf);
        } else {
            //wanderBehaviour.update(tpf);
            //need to include obstacles avoidance
        }
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }
}
