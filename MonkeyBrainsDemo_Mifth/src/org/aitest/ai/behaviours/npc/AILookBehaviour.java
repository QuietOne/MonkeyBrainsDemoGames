package org.aitest.ai.behaviours.npc;

import com.jme3.ai.agents.Agent;
import com.jme3.ai.agents.behaviours.npc.SimpleLookBehaviour;
import com.jme3.ai.agents.util.GameObject;
import com.jme3.ai.agents.util.control.Game;
import java.util.LinkedList;
import java.util.List;

/**
 * Behaviour for scanning environment.
 *
 * @author Tihomir Radosavljevic
 * @version 1.0
 */
public class AILookBehaviour extends SimpleLookBehaviour {

    Game game;

    public AILookBehaviour(Agent agent) {
        super(agent);
        game = Game.getInstance();
    }

    @Override
    protected List<GameObject> look(Agent agent, float viewAngle) {
        List<GameObject> temp = new LinkedList<GameObject>();
        //are there agents in seeing angle
        for (Agent agentInGame : game.getAgents()) {
            if (agentInGame.isEnabled()) {
                if (!agentInGame.equals(agent) && game.lookable(agent, agentInGame, viewAngle)) {
                    temp.add(agentInGame);
                }
            }
        }
        //is there obstacle between agent and observer
        //I will make as soon as bullet hitting the wall is fixed

        return temp;
    }
}
