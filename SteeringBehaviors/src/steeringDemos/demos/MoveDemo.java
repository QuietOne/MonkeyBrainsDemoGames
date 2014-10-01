//Copyright (c) 2014, Jesús Martín Berlanga. All rights reserved. Distributed under the BSD licence. Read "com/jme3/ai/license.txt".
package steeringDemos.demos;

import com.jme3.ai.agents.Agent;
import com.jme3.ai.agents.behaviours.npc.SimpleMainBehaviour;
import com.jme3.ai.agents.behaviours.npc.steering.MoveBehaviour;
import com.jme3.math.FastMath;

import com.jme3.math.Vector3f;

import steeringDemos.BasicDemo;
import steeringDemos.control.CustomSteerControl;

/**
 * Move Demo
 *
 * @author Jesús Martín Berlanga
 * @version 1.0
 */
public class MoveDemo extends BasicDemo {

    public static void main(String[] args) {
        MoveDemo app = new MoveDemo();
        app.start();
    }

    @Override
    public void simpleInitApp() {
        this.steerControl = new CustomSteerControl(9, 1);
        this.steerControl.setCameraSettings(getCamera());
        this.steerControl.setFlyCameraSettings(getFlyByCamera());

        //defining rootNode for aiAppState processing
        aiAppState.setApp(this);
        aiAppState.setAIControl(new CustomSteerControl(5f));

        Agent target = this.createBoid("Target", this.targetColor, 0.11f);

        aiAppState.addAgent(target); //Add the target to the aiAppState
        aiAppState.getAIControl().spawn(target, Vector3f.ZERO);
        this.setStats(
                target,
                this.targetMoveSpeed,
                this.targetRotationSpeed,
                this.targetMass,
                this.targetMaxForce);

        SimpleMainBehaviour targetMainBehaviour = new SimpleMainBehaviour(target);

        MoveBehaviour targetMoveBehavior = new MoveBehaviour(target);
        targetMoveBehavior.setMoveDirection(new Vector3f(
                FastMath.nextRandomFloat() - 0.5f,
                FastMath.nextRandomFloat() - 0.5f,
                FastMath.nextRandomFloat() - 0.5f));

        targetMainBehaviour.addBehaviour(targetMoveBehavior);
        target.setMainBehaviour(targetMainBehaviour);

        aiAppState.start();
    }

    @Override
    public void simpleUpdate(float tpf) {
        aiAppState.update(tpf);
    }
}
