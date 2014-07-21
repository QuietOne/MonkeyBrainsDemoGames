package simulation.evolution.behaviours;

import com.jme3.ai.agents.Agent;
import com.jme3.ai.agents.behaviours.Behaviour;
import com.jme3.ai.agents.behaviours.npc.SimpleMoveBehaviour;
import com.jme3.ai.agents.events.GameObjectSeenEvent;
import com.jme3.ai.agents.events.GameObjectSeenListener;
import com.jme3.ai.agents.util.GameObject;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import simulation.evolution.util.ALifeEntity;
import simulation.evolution.util.Food;

/**
 * Eating behaviour. Agent sees food and if it is hungry, it will go to food and
 * eat it.
 *
 * @author Tihomir Radosavljević
 * @version 1.0
 */
public class EatBehaviour extends Behaviour implements GameObjectSeenListener {

    private Food targetedFood;
    private SimpleMoveBehaviour moveBehaviour;
    private final float rangeOfEating = 5f;

    public EatBehaviour(Agent agent) {
        super(agent);
        //moving behaviour, but without moving on Y-axis
        moveBehaviour = new SimpleMoveBehaviour(agent) {
            @Override
            protected void controlUpdate(float tpf) {
                //if there is target position where agent should move
                if (targetPosition != null) {
                    if (agent.getLocalTranslation().distance(targetPosition) <= distanceError) {
                        targetPosition = null;
                        moveDirection = null;
                        enabled = false;
                        return;
                    }
                    moveDirection = targetPosition.subtract(agent.getLocalTranslation()).normalize();
                    //no movement allong y-axis
                    moveDirection.y = 0;
                }
                //if there is movement direction in which agent should move
                if (moveDirection != null) {
                    agent.getSpatial().move(moveDirection.mult(agent.getMoveSpeed() * tpf));
                    rotateAgent(tpf);
                }
            }
        };
        moveBehaviour.setDistanceError(rangeOfEating);
    }

    @Override
    protected void controlUpdate(float tpf) {
        //if there is no food being targeted, than nothing will be done
        if (targetedFood != null) {
            //if agent is close enough to eat it than eat it
            if (agent.getLocalTranslation().distance(targetedFood.getLocalTranslation()) < rangeOfEating) {
                ALifeEntity alf = (ALifeEntity) agent.getModel();
                //decreasing amount of food left
                targetedFood.beingEaten(alf.getEatPerTime() * tpf);
                //increase food being eaten by agent
                alf.increaseFoodAmount(alf.getEatPerTime() * tpf);
                alf.increaseHappiness(10 * tpf);
                //there isn't more food left or agent is full
                if (!targetedFood.moreEnergy() || alf.fullStomach()) {
                    targetedFood = null;
                }
            } else {
                //if not close enough, move to food
                moveBehaviour.setTargetPosition(targetedFood.getLocalTranslation());
                moveBehaviour.update(tpf);
            }
        }
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void handleGameObjectSeenEvent(GameObjectSeenEvent event) {
        GameObject object = event.getGameObjectSeen();
        //if it is food
        if (object instanceof Food) {
            if (targetedFood != null) {
                //is that better food than previous one
                if (targetedFood.getEnergy() < ((Food) object).getEnergy()) {
                    targetedFood = (Food) object;
                }
            } else {
                targetedFood = (Food) object;
            }
        }
    }
}
