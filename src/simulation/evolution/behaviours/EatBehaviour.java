package simulation.evolution.behaviours;

import com.jme3.ai.agents.Agent;
import com.jme3.ai.agents.behaviours.Behaviour;
import com.jme3.ai.agents.behaviours.npc.SimpleMoveBehaviour;
import com.jme3.ai.agents.events.PhysicalObjectSeenEvent;
import com.jme3.ai.agents.events.PhysicalObjectSeenListener;
import com.jme3.ai.agents.util.PhysicalObject;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import simulation.evolution.util.ALifeEntity;
import simulation.evolution.util.Food;

/**
 *
 * @author Tihomir Radosavljević
 */
public class EatBehaviour extends Behaviour implements PhysicalObjectSeenListener {

    private Food targetedFood;
    private SimpleMoveBehaviour moveBehaviour;
    private final float rangeOfEating = 5f;

    public EatBehaviour(Agent agent) {
        super(agent);
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
        if (targetedFood != null) {
            //if agent is close enough to eat it than eat it
            if (agent.getLocalTranslation().distance(targetedFood.getLocalTranslation()) < rangeOfEating) {
                ALifeEntity alf = (ALifeEntity) agent.getModel();
                targetedFood.beingEaten(alf.getEatPerTime() * tpf);
                alf.increaseFoodAmount(alf.getEatPerTime() * tpf);
                alf.increaseHappiness(10 * tpf);
                if (!targetedFood.moreEnergy()) {
                    targetedFood = null;
                }
                if (alf.fullStomach()) {
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

    public void handlePhysicalObjectSeenEvent(PhysicalObjectSeenEvent event) {
        PhysicalObject object = event.getPhysicalObjectSeen();
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
