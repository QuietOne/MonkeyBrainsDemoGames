//Copyright (c) 2014, Jesús Martín Berlanga. All rights reserved. Distributed under the BSD licence. Read "com/jme3/ai/license.txt".

package steeringDemos.simpleExamples;

import com.jme3.ai.agents.Agent;
import com.jme3.ai.agents.util.control.Game;
import com.jme3.app.SimpleApplication;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.material.Material;
import com.jme3.scene.Spatial;

import com.jme3.ai.agents.behaviours.npc.SimpleMainBehaviour;
import com.jme3.ai.agents.behaviours.npc.steering.WanderBehaviour;


import steeringDemos.control.CustomSteerControl;

/**
 * AI Steer Test - Testing the wander behaviour
 * 
 * @author Jesús Martín Berlanga
 * @version 1.1
 */
public class WanderTest extends SimpleApplication {
    
    private Game game = Game.getInstance(); //creating game
    
    //TEST SETTINGS - START
    private final String BOID_MODEL_NAME = "Models/boid.j3o";
    private final String BOID_MATERIAL_NAME = "Common/MatDefs/Misc/Unshaded.j3md";
    private final ColorRGBA TARGET_COLOR = ColorRGBA.Red;
    private final float TARGET_MOVE_SPEED = 1;
    private final float TARGET_ROTATION_SPEED = 30;
    private final float TARGET_MASS = 50;
    private final float TARGET_MAX_FORCE = 20;
    //TEST SETTINGS - END

    public static void main(String[] args) {
        WanderTest app = new WanderTest();
        app.start();
    }
    
    @Override
    public void simpleInitApp() {
        //defining rootNode for game processing
        game.setApp(this);
        game.setGameControl(new CustomSteerControl(5f));
        
        this.setupCamera();
        
        Vector3f[] spawnArea = null;
        
        Agent target = this.createBoid("Target", this.TARGET_COLOR);
   
        game.addAgent(target); //Add the target to the game
        game.getGameControl().spawn(target, spawnArea);
        this.setStats(target, this.TARGET_MOVE_SPEED, this.TARGET_ROTATION_SPEED, 
                this.TARGET_MASS, this.TARGET_MAX_FORCE);
                

        
        SimpleMainBehaviour targetMainBehaviour =  new SimpleMainBehaviour(target);
        WanderBehaviour targetMoveBehavior = new WanderBehaviour(target);
        //targetMoveBehavior.setArea(Vector3f.ZERO, Vector3f.POSITIVE_INFINITY);
        targetMainBehaviour.addBehaviour(targetMoveBehavior);
        target.setMainBehaviour(targetMainBehaviour);
      
       
        
        game.start();
    }
    
    private void setupCamera(){
        getCamera().setLocation(new Vector3f(0,20,0));
        getCamera().lookAt(Vector3f.ZERO, Vector3f.UNIT_X);
        getFlyByCamera().setMoveSpeed(50);
        
         // Disable flying camera - DEBUG ONLY
         //flyCam.setDragToRotate(true);
         //flyCam.setEnabled(false); 
    }
    
    //Create an agent with a name and a color
    private Agent createBoid(String name, ColorRGBA color){
            Spatial boidSpatial = assetManager.loadModel(this.BOID_MODEL_NAME);
            boidSpatial.setLocalScale(0.1f); //Resize
 
            Material mat = new Material(assetManager, this.BOID_MATERIAL_NAME);                         
            mat.setColor("Color", color);
            boidSpatial.setMaterial(mat);
            
            return new Agent(name, boidSpatial);
    }
    
    //Setup the stats for an agent
    private void setStats(Agent myAgent, float moveSpeed, float rotationSpeed,
            float mass, float maxForce){
        
            myAgent.setMoveSpeed(moveSpeed);
            myAgent.setRotationSpeed(rotationSpeed);
            myAgent.setMass(mass);
            myAgent.setMaxForce(maxForce);
    }
    
    @Override
    public void simpleUpdate(float tpf) {
        game.update(tpf);
    }
}
