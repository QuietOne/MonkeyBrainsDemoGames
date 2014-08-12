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
import com.jme3.ai.agents.behaviours.npc.steering.PathFollowBehaviour;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Cylinder;
import java.util.ArrayList;

import steeringDemos.control.CustomSteerControl;

/**
 * AI Steer Test - Path follow demo
 * 
 * @author Jesús Martín Berlanga
 * @version 1.0
 */
public class PathFollowDemo extends SimpleApplication {
    
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
        PathFollowDemo app = new PathFollowDemo();
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
        
        ///////////////////////////////////////////////////////////////////////////
        ////////// Path ///////////////////////////////////////////////////////////
        ///////////////////////////////////////////////////////////////////////////
        ArrayList<Vector3f> orderedPointsList = new ArrayList<Vector3f>();
            orderedPointsList.add(new Vector3f(0, 0, 0));
                orderedPointsList.add(new Vector3f(0, 0, 0.001f)); //Start area offset
            orderedPointsList.add(new Vector3f(0, 0, 5));
            orderedPointsList.add(new Vector3f(5, 0, 5));
            orderedPointsList.add(new Vector3f(5, 5, 5));
            orderedPointsList.add(new Vector3f(5, 5, 0));
            orderedPointsList.add(new Vector3f(0, 5, 0));
            orderedPointsList.add(new Vector3f(0, 0, 0));
        
        this.drawPathSegment(new Vector3f(0,0,-0.01f), new Vector3f(0,0,0.01f), 1, ColorRGBA.Blue, 3); //Start area
        
        //Transition Areas
        this.drawPathSegment(new Vector3f(0,0,4.99f), new Vector3f(0,0,5.01f), 1, ColorRGBA.Cyan, 3); 
        this.drawPathSegment(new Vector3f(4.99f,0,5), new Vector3f(5.01f,0,5), 1, ColorRGBA.Cyan, 3);
        this.drawPathSegment(new Vector3f(5,4.99f,5), new Vector3f(5,5.01f,5), 1, ColorRGBA.Cyan, 3); 
        this.drawPathSegment(new Vector3f(5,5,-0.01f), new Vector3f(5,5,0.01f), 1, ColorRGBA.Cyan, 3);
        this.drawPathSegment(new Vector3f(-0.01f,5,0), new Vector3f(0.01f,5,0), 1, ColorRGBA.Cyan, 3);
        
        this.drawPathSegment(orderedPointsList.get(0), orderedPointsList.get(2), 1, ColorRGBA.Green, 1);
        this.drawPathSegment(orderedPointsList.get(2), orderedPointsList.get(3), 1, ColorRGBA.Green, 1);
        this.drawPathSegment(orderedPointsList.get(3), orderedPointsList.get(4), 1, ColorRGBA.Green, 1);
        this.drawPathSegment(orderedPointsList.get(4), orderedPointsList.get(5), 1, ColorRGBA.Green, 1);
        this.drawPathSegment(orderedPointsList.get(5), orderedPointsList.get(6), 1, ColorRGBA.Green, 1);
        this.drawPathSegment(orderedPointsList.get(6), orderedPointsList.get(7), 1, ColorRGBA.Green, 1);
        ////////////////////////////////////////////////////////////////////////////
        
        SimpleMainBehaviour targetMainBehaviour =  new SimpleMainBehaviour(target);
        //CompoundSteeringBehaviour steering = new CompoundSteeringBehaviour(target);
        
        //WanderBehaviour targetMoveBehavior = new WanderBehaviour(target);
        //ContainmentBehaviour contain = new ContainmentBehaviour(target, containmentArea);
        //contain.setupStrengthControl(75);
        
             PathFollowBehaviour pathFollow = new PathFollowBehaviour(target, orderedPointsList, 1, 1);
        
            //steering.addSteerBehaviour(targetMoveBehavior);
            //steering.addSteerBehaviour(contain);
        
            targetMainBehaviour.addBehaviour(pathFollow);
            target.setMainBehaviour(targetMainBehaviour);

  
        game.start();
    }
    
    private void setupCamera(){
        getCamera().setLocation(new Vector3f(0,20,0));
        getCamera().lookAt(Vector3f.ZERO, Vector3f.UNIT_X);
        getFlyByCamera().setMoveSpeed(11);
        
         // Disable flying camera - DEBUG ONLY
         //flyCam.setDragToRotate(true);
         //flyCam.setEnabled(false); 
    }
    
    //Create the path visuals
    private void drawPathSegment(Vector3f a, Vector3f b, float radius, ColorRGBA color, float AlphaMult)
    {
         Vector3f direction = b.subtract(a);
         float height = direction.length();
         
         Node origin = new Node();
         origin.lookAt(direction, Vector3f.UNIT_Y);
         origin.setLocalTranslation(a.add(direction.divide(2)));

         Cylinder mesh = new Cylinder(4, 20, radius, height, true);
         //ylinder meshWire = new Cylinder(2, 5, radius, height, false);
         
         Geometry geom = new Geometry("A shape", mesh); // wrap shape into geometry
         //Geometry geomWire = new Geometry("A shape", meshWire);
         
         origin.attachChild(geom);
         //origin.attachChild(geomWire);
         
         Material matTranslucid = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
                matTranslucid.setColor("Color", new ColorRGBA(color.r,color.g,color.b,0.17f * AlphaMult));
                matTranslucid.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);               
                geom.setQueueBucket(Bucket.Translucent);
                geom.setMaterial(matTranslucid);
        
         /*
         Material wireMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
                wireMat.setColor("Color", color);
                geomWire.setMaterial(wireMat);
                wireMat.getAdditionalRenderState().setWireframe(true);
         */
                
         rootNode.attachChild(origin);
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
