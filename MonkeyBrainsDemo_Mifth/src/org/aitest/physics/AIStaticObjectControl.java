package org.aitest.physics;

import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;

/**
 *
 * @author mifthbeat
 */
public class AIStaticObjectControl extends RigidBodyControl {
    AIStaticObjectType type;

    public AIStaticObjectControl(AIStaticObjectType type, CollisionShape shape, float mass) {
        super(shape, mass);
        this.type = type;
    }

    public AIStaticObjectType getType() {
        return type;
    }
    
}
