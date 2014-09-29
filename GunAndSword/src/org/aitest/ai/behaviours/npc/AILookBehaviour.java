package org.aitest.ai.behaviours.npc;

import com.jme3.ai.agents.Agent;
import com.jme3.ai.agents.behaviours.npc.SimpleLookBehaviour;
import com.jme3.ai.agents.util.GameEntity;
import com.jme3.ai.agents.util.control.AIAppState;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.PhysicsCollisionObject;
import com.jme3.bullet.collision.PhysicsRayTestResult;
import com.jme3.bullet.control.GhostControl;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import java.util.LinkedList;
import java.util.List;

/**
 * Behaviour for scanning environment.
 *
 * @author Tihomir Radosavljevic
 * @version 1.0
 */
public class AILookBehaviour extends SimpleLookBehaviour {

    AIAppState game;

    public AILookBehaviour(Agent agent) {
        super(agent);
        game = AIAppState.getInstance();
    }

    @Override
    protected List<GameEntity> look(Agent agent, float viewAngle) {
        List<GameEntity> temp = new LinkedList<GameEntity>();
        //are there agents in seeing angle
        for (Agent agentInGame : game.getAgents()) {
            if (agentInGame.isEnabled()) {
                if (!agentInGame.equals(agent) && !agent.isSameTeam(agentInGame) && game.lookable(agent, agentInGame, viewAngle)) {
                    temp.add(agentInGame);
                }
            }
        }
        //is there obstacle between agent and observer
        Vector3f vecStart = agent.getLocalTranslation().clone().setY(1);
        BulletAppState bulletState = game.getApp().getStateManager().getState(BulletAppState.class);
        for (int i = 0; i < temp.size(); i++) {
            GameEntity agentInRange = temp.get(i);
            Vector3f vecEnd = agentInRange.getLocalTranslation().clone().setY(1);
            //what has bullet hit
            List<PhysicsRayTestResult> rayTest = bulletState.getPhysicsSpace().rayTest(vecStart, vecEnd);

            float distance = vecEnd.length();
            PhysicsCollisionObject o = null;
            if (rayTest.size() > 0) {
                for (PhysicsRayTestResult getObject : rayTest) {
                    //distance to next collision
                    float fl = getObject.getHitFraction();
                    PhysicsCollisionObject collisionObject = getObject.getCollisionObject();
                    //bullet does not is not supposed to be seen
                    if (collisionObject instanceof GhostControl) {
                        continue;
                    }
                    Spatial thisSpatial = (Spatial) collisionObject.getUserObject();
                    // Get the Enemy to kill
                    if (fl < distance && !thisSpatial.equals(agentInRange.getSpatial())) {
                        temp.remove(agentInRange);
                        o = collisionObject;
                    }
                    
                }
            }
        }
        return temp;
    }
}