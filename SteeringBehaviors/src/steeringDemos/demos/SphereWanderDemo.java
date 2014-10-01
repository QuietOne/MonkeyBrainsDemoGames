//Copyright (c) 2014, Jesús Martín Berlanga. All rights reserved. Distributed under the BSD licence. Read "com/jme3/ai/license.txt".

package steeringDemos.demos;

import com.jme3.ai.agents.Agent;
import com.jme3.ai.agents.behaviours.npc.SimpleMainBehaviour;
import com.jme3.ai.agents.behaviours.npc.steering.SphereWanderBehaviour;
import com.jme3.math.Vector3f;
import steeringDemos.BasicDemo;
import steeringDemos.control.CustomSteerControl;

/**
 * Sphere Wander Demo
 * 
 * @author Jesús Martín Berlanga
 * @version 1.0
 */
public class SphereWanderDemo extends BasicDemo {
    
    public static void main(String[] args) {
        SphereWanderDemo app = new SphereWanderDemo();
        app.start();
    }
    
    @Override
    public void simpleInitApp() 
    {     
        this.steerControl = new CustomSteerControl(9, 1);
        this.steerControl.setCameraSettings(getCamera());
        this.steerControl.setFlyCameraSettings(getFlyByCamera());
        
        //defining rootNode for aiAppState processing
        aiAppState.setApp(this);
        aiAppState.setAIControl(new CustomSteerControl(5f));
                       
        Agent target = this.createBoid("Target", this.targetColor, 0.11f);
   
        aiAppState.addAgent(target); //Add the target to the aiAppState
        aiAppState.getAIControl().spawn(target, Vector3f.ZERO);
        this.setStats
                (
                    target,
                    this.targetMoveSpeed,
                    this.targetRotationSpeed,
                    this.targetMass,
                    this.targetMaxForce
                );
                
        SimpleMainBehaviour targetMainBehaviour =  new SimpleMainBehaviour(target);
        SphereWanderBehaviour targetMoveBehavior = new SphereWanderBehaviour(target, 0.1f, 0.8f, 0.15f);
        targetMainBehaviour.addBehaviour(targetMoveBehavior);
        target.setMainBehaviour(targetMainBehaviour);
 
        aiAppState.start();
    }
    
    @Override
    public void simpleUpdate(float tpf) {
        aiAppState.update(tpf);
    }
}
