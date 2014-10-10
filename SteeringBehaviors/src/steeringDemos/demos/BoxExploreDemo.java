//Copyright (c) 2014, Jesús Martín Berlanga. All rights reserved.
//Distributed under the BSD licence. Read "com/jme3/ai/license.txt".

package steeringDemos.demos;

import com.jme3.ai.agents.Agent;
import com.jme3.ai.agents.behaviors.npc.SimpleMainBehavior;
import com.jme3.ai.agents.behaviors.npc.steering.BoxExploreBehavior;

import com.jme3.math.Vector3f;

import steeringDemos.BasicDemo;
import steeringDemos.control.CustomSteerControl;

/**
 * AI Steer Demo - Box Explore
 * 
 * @author Jesús Martín Berlanga
 * @version 2.0
 */
public class BoxExploreDemo extends BasicDemo {
    
    public static void main(String[] args) {
        BoxExploreDemo app = new BoxExploreDemo();
        app.start();
    }
    
    @Override
    public void simpleInitApp() {
        this.steerControl = new CustomSteerControl(7, 5);
        this.steerControl.setCameraSettings(getCamera());
        this.steerControl.setFlyCameraSettings(getFlyByCamera());
        
        //defining rootNode for aiAppState processing
        aiAppState.setApp(this);
        aiAppState.setGameControl(this.steerControl);
        
        Vector3f[] spawnArea = null;
        
        Agent target = this.createBoid("Target", this.targetColor, 0.11f);
   
        aiAppState.addAgent(target); //Add the target to the aiAppState
        aiAppState.getGameControl().spawn(target, spawnArea);
        this.setStats(target, this.targetMoveSpeed, this.targetRotationSpeed, 
                this.targetMass, this.targetMaxForce);
                
        this.addBoxHelper(Vector3f.ZERO, 2.5f, 2, 1.5f);
        
        SimpleMainBehavior targetMainBehaviour =  new SimpleMainBehavior(target);
        BoxExploreBehavior boxExplore = new BoxExploreBehavior(target, Vector3f.ZERO, 5, 3, 4, 0.75f);
        targetMainBehaviour.addBehavior(boxExplore);
        target.setMainBehaviour(targetMainBehaviour);
         
        aiAppState.start();
    }
    
    @Override
    public void simpleUpdate(float tpf) {
        aiAppState.update(tpf);
    }
}
