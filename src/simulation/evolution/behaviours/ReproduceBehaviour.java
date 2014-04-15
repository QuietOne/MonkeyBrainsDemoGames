package simulation.evolution.behaviours;

import behaviours.SeekInsideTerrain;
import com.jme3.ai.agents.Agent;
import com.jme3.ai.agents.behaviours.Behaviour;
import com.jme3.ai.agents.behaviours.npc.SimpleAttackBehaviour;
import com.jme3.ai.agents.behaviours.npc.steering.SeekBehaviour;
import com.jme3.ai.agents.events.GameObjectSeenEvent;
import com.jme3.ai.agents.events.GameObjectSeenListener;
import com.jme3.ai.agents.util.GameObject;
import com.jme3.ai.agents.util.control.Game;
import com.jme3.math.ColorRGBA;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import simulation.evolution.control.Simulation;
import simulation.evolution.util.ALifeEntity;
import simulation.evolution.util.EvolutionSpatials;

/**
 * Reproduction behaviour. Two agent are making new generation of agents.
 *
 * @author Tihomir Radosavljević
 * @version 1.0
 */
public class ReproduceBehaviour extends Behaviour implements GameObjectSeenListener {

    /**
     * Agent with whom is this agent reproducing.
     */
    private Agent withWhom;
    /**
     * Maximal distance of reproduction.
     */
    private float rangeOfReproducing;
    private SeekBehaviour seekBehaviour;
    /**
     * Time left until reproduction being made.
     */
    private float timeUntilReproduction;
    /**
     * Time needed for reproduction.
     */
    private final float reproductionTime = 1f;
    private SimpleAttackBehaviour attackBehaviour;
    /**
     * Is proces of reproduction being active.
     */
    private boolean active;

    public ReproduceBehaviour(float terrainSize, Agent agent) {
        super(agent);
        seekBehaviour = new SeekInsideTerrain(terrainSize, agent, null);
        rangeOfReproducing = 5f;
        timeUntilReproduction = reproductionTime;
        attackBehaviour = new SimpleAttackBehaviour(agent);
        active = false;
    }

    @Override
    protected void controlUpdate(float tpf) {
        if (withWhom != null && withWhom.isEnabled()) {
            //if agent is close enough to eat it than eat it
            if (agent.getLocalTranslation().distance(withWhom.getLocalTranslation()) < rangeOfReproducing) {
                //does it already have sexual partner
                if (((ALifeEntity) withWhom.getModel()).hasSexualPartner()) {
                    //if this agent is not sexual partner of that one
                    if (((ALifeEntity) withWhom.getModel()).getSexualPartner() != agent) {
                        //if agent is really horny or just stronger
                        if (((ALifeEntity) agent.getModel()).isReallyHorny()
                                || agent.getHitPoint() > ((ALifeEntity) withWhom.getModel()).getSexualPartner().getHitPoint()) {
                            attackBehaviour.setTarget(((ALifeEntity) withWhom.getModel()).getSexualPartner());
                            attackBehaviour.update(tpf);
                            if (attackBehaviour.isTargetSet()) {
                                System.out.println(agent.getName() + " has killed " + ((ALifeEntity) withWhom.getModel()).getSexualPartner());
                                ((ALifeEntity) withWhom.getModel()).setSexualPartner(agent);
                                timeUntilReproduction = reproductionTime;
                            }
                        } else {
                            //if agent will not fight for other agent
                            withWhom = null;
                        }
                    } else {
                        active = true;
                        //reproduction process is active
                        ((ALifeEntity) agent.getModel()).decreaseSexDeprivation(tpf);
                        ((ALifeEntity) agent.getModel()).increaseHappiness(100 * tpf);
                        ((ALifeEntity) agent.getModel()).setSexualPartner(withWhom);
                        EvolutionSpatials.changeBodyColor(agent, ColorRGBA.Red);
                        EvolutionSpatials.changeBodyColor(withWhom, ColorRGBA.Red);
                        if (timeUntilReproduction <= 0) {
                            EvolutionSpatials.changeBodyColor(agent, ColorRGBA.Gray);
                            EvolutionSpatials.changeBodyColor(withWhom, ColorRGBA.Gray);
                            withWhom = null;
                            //three newborns are made
                            ((Simulation) Game.getInstance().getGameControl()).spawnAgentChildren();
                            ((Simulation) Game.getInstance().getGameControl()).spawnAgentChildren();
                            ((Simulation) Game.getInstance().getGameControl()).spawnAgentChildren();
                            active = false;
                        } else {
                            timeUntilReproduction -= tpf;
                        }
                    }
                } else {
                    //if agent doesn't have sexual partner
                    ((ALifeEntity) withWhom.getModel()).setSexualPartner(agent);
                    timeUntilReproduction = reproductionTime;
                }

            } else {
                //if not close enough, move to partner, you dog
                seekBehaviour.setTarget(withWhom);
                seekBehaviour.update(tpf);
                EvolutionSpatials.changeBodyColor(agent, ColorRGBA.Gray);
            }
        } else {
            //if there isn't any potential sexual partner
            EvolutionSpatials.changeBodyColor(agent, ColorRGBA.Gray);
            withWhom = null;
        }
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void handleGameObjectSeenEvent(GameObjectSeenEvent event) {
        GameObject object = event.getGameObjectSeen();
        //if it is food
        if (object instanceof Agent) {
            if (withWhom != null) {
                //if it is the same gender, I don't care about it
                if (((ALifeEntity) ((Agent) object).getModel()).isSameGender(agent)) {
                    return;
                }
                //is that agent better looking
                if (((ALifeEntity) withWhom.getModel()).getHotness()
                        < (((ALifeEntity) withWhom.getModel()).getHotness())) {
                    withWhom = (Agent) object;
                    timeUntilReproduction = reproductionTime;
                }
            } else {
                withWhom = (Agent) object;
                timeUntilReproduction = reproductionTime;
            }
        }
    }

    public boolean isActive() {
        return active;
    }
}
