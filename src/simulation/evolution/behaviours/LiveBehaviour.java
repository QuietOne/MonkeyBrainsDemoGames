package simulation.evolution.behaviours;

import behaviours.FleeInsideTerrain;
import behaviours.WanderInsideTerrain;
import com.jme3.ai.agents.Agent;
import com.jme3.ai.agents.behaviours.Behaviour;
import com.jme3.ai.agents.behaviours.npc.SimpleAttackBehaviour;
import com.jme3.ai.agents.behaviours.npc.SimpleLookBehaviour;
import com.jme3.ai.agents.behaviours.npc.steering.FleeBehaviour;
import com.jme3.ai.agents.util.control.Game;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import simulation.evolution.events.BeingAttackedEvent;
import simulation.evolution.events.BeingAttackedEventListener;
import simulation.evolution.util.ALifeEntity;

/**
 *
 * @author Tihomir Radosavljević
 */
public class LiveBehaviour extends Behaviour implements BeingAttackedEventListener {

    private ALifeEntity agentLife;
    private SimpleLookBehaviour lookBehaviour;
    private EatBehaviour eatBehaviour;
    private FuckBehaviour fuckBehaviour;
    private Agent attackingAgent;
    private FleeBehaviour fleeBehaviour;
    private SimpleAttackBehaviour attackBehaviour;
    private WanderInsideTerrain wanderBehaviour;
    private float time;

    public LiveBehaviour(float terrainSize, Agent agent) {
        super(agent);
        agentLife = (ALifeEntity) agent.getModel();
        eatBehaviour = new EatBehaviour(agent);
        fuckBehaviour = new FuckBehaviour(terrainSize, agent);
        lookBehaviour = new SimpleLookBehaviour(agent);
        lookBehaviour.addListener(eatBehaviour);
        lookBehaviour.addListener(fuckBehaviour);
        fleeBehaviour = new FleeInsideTerrain(agent, null);
        attackBehaviour = new SimpleAttackBehaviour(agent);
        wanderBehaviour = new WanderInsideTerrain(agent, terrainSize);
    }

    @Override
    protected void controlUpdate(float tpf) {
        lookBehaviour.update(tpf);
        //if no one is attacking
        if (attackingAgent == null) {
            if (fuckBehaviour.isDoingIt()) {
                fuckBehaviour.update(tpf);
            } else {
                //if agent is hungry
                if (agentLife.isHungry()) {
                    agentLife.decreaseHappiness(5 * tpf);
                    eatBehaviour.update(tpf);
                    if (agentLife.isReallyHungry()) {
                        agentLife.decreaseHappiness(15 * tpf);
                    }
                }
                //if agent is horny
                if (agentLife.isHorny()) {
                    agentLife.decreaseHappiness(10 * tpf);
                    fuckBehaviour.update(tpf);
                    if (agentLife.isReallyHorny()) {
                        agentLife.decreaseHappiness(30 * tpf);
                    }
                }
                if (!agentLife.isHungry() && !agentLife.isHorny()) {
                    wanderBehaviour.update(tpf);
                    agentLife.increaseHappiness(100 * tpf);
                }
            }
        } else {
            //if someone is attacking
            if (agent.getHitPoint() > attackingAgent.getHitPoint()) {
                // I am stronger
                attackBehaviour.setTarget(attackingAgent);
                attackBehaviour.update(tpf);
                //agent is dead now
                if (!attackingAgent.isEnabled()) {
                    attackingAgent = null;
                }
            } else {
                //he is stronger
                fleeBehaviour.setTarget(attackingAgent);
                fleeBehaviour.update(tpf);
                if (time <= 0) {
                    attackBehaviour = null;
                } else {
                    time -= tpf;
                }
            }
        }
        wanderBehaviour.update(tpf);
        //if agent is unhappy suicide will happen
        if (agentLife.isUnhappy()) {
            //System.out.println(agent.getName() + " is unhappy.");
            Game.getInstance().decreaseHitPoints(agent, agent.getHitPoint());
            System.out.println(agent.getName()+" have commited suicide.");
        }
        //in life everything goes away
        agentLife.increaseSexDeprivation(tpf);
        agentLife.decreaseFoodAmount(tpf * 2);
        agentLife.age(tpf);
        if (agentLife.timeToDie()) {
            Game.getInstance().decreaseHitPoints(agent, agent.getHitPoint());
            System.out.println(agent.getName() + " have died of old age at " + agentLife.getLifeSpan() + ".");
        }
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void handleBeingAttackedEvent(BeingAttackedEvent event) {
        attackingAgent = event.getAttackingAgent();
        time = 5f;
    }
}
