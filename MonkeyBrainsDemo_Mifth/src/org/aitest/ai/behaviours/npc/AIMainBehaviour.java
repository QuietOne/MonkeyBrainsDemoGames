package org.aitest.ai.behaviours.npc;

import com.jme3.ai.agents.Agent;
import com.jme3.ai.agents.behaviours.Behaviour;
import com.jme3.ai.agents.behaviours.npc.SimpleLookBehaviour;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;

/**
 *
 * @author Tihomir Radosavljevic
 * @version 1.0
 */
public class AIMainBehaviour extends Behaviour{

    private SimpleLookBehaviour lookBehaviour;
    private AIAttackBehaviour attackBehaviour;
    private AIWanderBehaviour wanderBehaviour;
    private AISeekBehaviour seekBehaviour;
    
    public AIMainBehaviour(Agent agent) {
        super(agent);
        attackBehaviour = new AIAttackBehaviour(agent);
        seekBehaviour = new AISeekBehaviour(agent);
        lookBehaviour = new SimpleLookBehaviour(agent);
        lookBehaviour.addListener(attackBehaviour);
        lookBehaviour.addListener(seekBehaviour);
        wanderBehaviour = new AIWanderBehaviour(agent);
    }

    @Override
    protected void controlUpdate(float tpf) {
        /*lookBehaviour.update(tpf);
        attackBehaviour.update(tpf);
        if (seekBehaviour.getTarget()!=null) {
            seekBehaviour.update(tpf);
        } else {
            wanderBehaviour.update(tpf);
        }*/
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }
}
