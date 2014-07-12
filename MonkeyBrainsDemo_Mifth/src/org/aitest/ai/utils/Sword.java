package org.aitest.ai.utils;

import com.jme3.ai.agents.Agent;
import com.jme3.ai.agents.util.AbstractBullet;
import com.jme3.ai.agents.util.AbstractWeapon;
import com.jme3.ai.agents.util.control.Game;
import com.jme3.bullet.collision.PhysicsCollisionObject;
import com.jme3.bullet.control.GhostControl;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import org.aitest.ai.character.AIModel;

/**
 *
 * @author Tihomir Radosavljevic
 * @author mifth
 * @version 1.0
 */
public class Sword extends AbstractWeapon {

    public Sword(Agent agent) {
        this.agent = agent;
        name = "sword";
        cooldown = 0.6f;
        attackDamage = 30f;
        numberOfBullets = -1;
        minAttackRange = 0;
        //CHECK: if number is correct.
        maxAttackRange = 1f;
        //spatial = swordModel;
        ((Node) agent.getSpatial()).attachChild(spatial);
    }
    
    public Sword(Agent agent, Spatial spatial) {
        this.agent = agent;
        name = "sword";
        cooldown = 0.6f;
        attackDamage = 30f;
        numberOfBullets = -1;
        minAttackRange = 0;
        //CHECK: if number is correct.
        maxAttackRange = 1f;
        ((Node) agent.getSpatial()).attachChild(spatial);
    }

    @Override
    protected AbstractBullet controlAttack(Vector3f direction, float tpf) {
        for (PhysicsCollisionObject physObj : spatial.getControl(GhostControl.class).getOverlappingObjects()) {
            Spatial spObj = (Spatial) physObj.getUserObject();
            AIModel charCtrl = spObj.getControl(AIModel.class);
            //if somebody is being hit and that one is not me
            if (charCtrl != null && !charCtrl.equals((AIModel) agent.getModel())) {
                Game.getInstance().agentAttack(agent, charCtrl.getAgent(), this);
                break;
            }
        }
        return null;
    }
}
