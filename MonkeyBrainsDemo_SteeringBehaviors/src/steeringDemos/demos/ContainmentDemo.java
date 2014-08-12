//Copyright (c) 2014, Jesús Martín Berlanga. All rights reserved. Distributed under the BSD licence. Read "com/jme3/ai/license.txt".

package steeringDemos.demos;

import com.jme3.ai.agents.Agent;
import com.jme3.ai.agents.util.control.Game;
import com.jme3.app.SimpleApplication;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.material.Material;
import com.jme3.scene.Spatial;

import com.jme3.ai.agents.behaviours.npc.SimpleMainBehaviour;
import com.jme3.ai.agents.behaviours.npc.steering.CompoundSteeringBehaviour;
import com.jme3.ai.agents.behaviours.npc.steering.ContainmentBehaviour;
import com.jme3.ai.agents.behaviours.npc.steering.WanderBehaviour;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.StripBox;

import steeringDemos.control.CustomSteerControl;

/**
 * AI Steer - Containment demo
 * 
 * @author Jesús Martín Berlanga
 * @version 1.0
 */
public class ContainmentDemo extends SimpleApplication {
    
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
        ContainmentDemo app = new ContainmentDemo();
        app.start();
    }
    
    @Override
    public void simpleInitApp() {
        //defining rootNode for game processing
        game.setApp(this);
        game.setGameControl(new CustomSteerControl(5f));
        
        this.setupCamera();

        Agent target = this.createBoid("Target", this.TARGET_COLOR);
   
        game.addAgent(target); //Add the target to the game
        game.getGameControl().spawn(target, new Vector3f());
        this.setStats(target, this.TARGET_MOVE_SPEED, this.TARGET_ROTATION_SPEED, 
                this.TARGET_MASS, this.TARGET_MAX_FORCE);
        
        ////////////////////////////////////////////////////////////////////////////
        ////////// Containment area ////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////////
            Node containmentArea = new Node();
            
            StripBox  mesh = new StripBox(1.5f, 1.5f, 1.5f); 
            Geometry geom = new Geometry("A shape", mesh); // wrap shape into geometry
            Geometry geomWire = new Geometry("A shape", mesh);
            
            Material matTranslucid = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
                matTranslucid.setColor("Color", new ColorRGBA(0,1,0,0.17f));
                matTranslucid.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);               
                geom.setQueueBucket(Bucket.Translucent);
                geom.setMaterial(matTranslucid);
                
            Material wireMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
                wireMat.setColor("Color", new ColorRGBA(0,1,0,0.25f));
                geomWire.setMaterial(wireMat);
                wireMat.getAdditionalRenderState().setWireframe(true);
                
            containmentArea.attachChild(geom);
            rootNode.attachChild(containmentArea); 
            rootNode.attachChild(geomWire);
        ////////////////////////////////////////////////////////////////////////////
        
        SimpleMainBehaviour targetMainBehaviour =  new SimpleMainBehaviour(target);
        CompoundSteeringBehaviour steering = new CompoundSteeringBehaviour(target);
        
        WanderBehaviour targetMoveBehavior = new WanderBehaviour(target);
        ContainmentBehaviour contain = new ContainmentBehaviour(target, containmentArea);
        contain.setupStrengthControl(75);
        
        steering.addSteerBehaviour(targetMoveBehavior);
        steering.addSteerBehaviour(contain);
        
        targetMainBehaviour.addBehaviour(steering);
        target.setMainBehaviour(targetMainBehaviour);
  
        game.start();
    }
    
    private void setupCamera(){
        getCamera().setLocation(new Vector3f(0,20,0));
        getCamera().lookAt(Vector3f.ZERO, Vector3f.UNIT_X);
        getFlyByCamera().setMoveSpeed(4);
        
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
