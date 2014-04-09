/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package simulation.evolution.behaviours;

import behaviours.SeekInsideTerrain;
import com.jme3.ai.agents.Agent;
import com.jme3.ai.agents.behaviours.Behaviour;
import com.jme3.ai.agents.behaviours.npc.SimpleAttackBehaviour;
import com.jme3.ai.agents.behaviours.npc.steering.SeekBehaviour;
import com.jme3.ai.agents.events.PhysicalObjectSeenEvent;
import com.jme3.ai.agents.events.PhysicalObjectSeenListener;
import com.jme3.ai.agents.util.PhysicalObject;
import com.jme3.ai.agents.util.control.Game;
import com.jme3.math.ColorRGBA;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import java.util.Random;
import simulation.evolution.control.Simulation;
import simulation.evolution.util.ALifeEntity;
import simulation.evolution.util.EvolutionSpatials;

/**
 *
 * @author Tihomir Radosavljević
 */
public class FuckBehaviour extends Behaviour implements PhysicalObjectSeenListener {

    private Agent withWhom;
    float rangeOfFucking;
    SeekBehaviour seekBehaviour;
    float timeUntilOrgasm;
    float fuckTime;
    SimpleAttackBehaviour attackBehaviour;
    private boolean doingIt;

    public FuckBehaviour(float terrainSize, Agent agent) {
        super(agent);
        seekBehaviour = new SeekInsideTerrain(terrainSize, agent, null);
        rangeOfFucking = 10f;
        fuckTime = 1f;
        timeUntilOrgasm = fuckTime;
        attackBehaviour = new SimpleAttackBehaviour(agent);
        doingIt = false;
    }

    @Override
    protected void controlUpdate(float tpf) {
        if (withWhom != null && withWhom.isEnabled()) {
            //if agent is close enough to eat it than eat it
            if (agent.getLocalTranslation().distance(withWhom.getLocalTranslation()) < rangeOfFucking) {
                //does it already have sexual partner
                if (((ALifeEntity) withWhom.getModel()).hasSexualPartner()) {
                    if (((ALifeEntity) withWhom.getModel()).getSexualPartner() != agent) {
                        //if agent is really horny or just stronger
                        if (((ALifeEntity) agent.getModel()).isReallyHorny()
                                || agent.getHitPoint() > ((ALifeEntity) withWhom.getModel()).getSexualPartner().getHitPoint()) {
                            attackBehaviour.setTarget(((ALifeEntity) withWhom.getModel()).getSexualPartner());
                            attackBehaviour.update(tpf);
                            if (attackBehaviour.getTargetObject() == null) {
                                System.out.println(agent.getName()+ " has killed " + ((ALifeEntity) withWhom.getModel()).getSexualPartner());
                                ((ALifeEntity) withWhom.getModel()).setSexualPartner(agent);
                                timeUntilOrgasm = fuckTime;
                            }
                        } else {
                            //if agent will not fight for other agent
                            withWhom = null;
                        }
                    } else {
                        doingIt = true;
                        ((ALifeEntity) agent.getModel()).decreaseSexDeprivation(tpf);
                        ((ALifeEntity) agent.getModel()).increaseHappiness(100 * tpf);
                        ((ALifeEntity) agent.getModel()).setSexualPartner(withWhom);
                        EvolutionSpatials.changeBodyColor(agent, ColorRGBA.Red);
                        EvolutionSpatials.changeBodyColor(withWhom, ColorRGBA.Red);
                        if (timeUntilOrgasm <= 0) {
                            EvolutionSpatials.changeBodyColor(agent, ColorRGBA.Gray);
                            EvolutionSpatials.changeBodyColor(withWhom, ColorRGBA.Gray);
                            withWhom = null;
                            //Make 3 babies per sex well done
                            ((Simulation)Game.getInstance().getGameControl()).spawnAgentChildren();
                            ((Simulation)Game.getInstance().getGameControl()).spawnAgentChildren();
                            ((Simulation)Game.getInstance().getGameControl()).spawnAgentChildren();
                            doingIt = false;
                        } else {
                            timeUntilOrgasm -= tpf;
                        }
                    }
                } else {
                    ((ALifeEntity) withWhom.getModel()).setSexualPartner(agent);
                    timeUntilOrgasm = fuckTime;
                }

            } else {
                //if not close enough, move to partner, you dog
                seekBehaviour.setTarget(withWhom);
                seekBehaviour.update(tpf);
            }
        } else {
            EvolutionSpatials.changeBodyColor(agent, ColorRGBA.Gray);
        }
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void handlePhysicalObjectSeenEvent(PhysicalObjectSeenEvent event) {
        PhysicalObject object = event.getPhysicalObjectSeen();
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
                    timeUntilOrgasm = fuckTime;
                }
            } else {
                withWhom = (Agent) object;
                timeUntilOrgasm = fuckTime;
            }
        }
    }

    public boolean isDoingIt() {
        return doingIt;
    }
    

}
