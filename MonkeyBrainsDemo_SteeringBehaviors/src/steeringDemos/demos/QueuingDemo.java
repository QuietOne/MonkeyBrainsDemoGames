//Copyright (c) 2014, Jesús Martín Berlanga. All rights reserved. Distributed under the BSD licence. Read "com/jme3/ai/license.txt".

package steeringDemos.demos;

import com.jme3.ai.agents.Agent;
import com.jme3.ai.agents.util.control.Game;
import com.jme3.app.SimpleApplication;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.material.Material;
import com.jme3.scene.Spatial;

import com.jme3.ai.agents.behaviours.npc.steering.SeparationBehaviour;
import com.jme3.ai.agents.behaviours.npc.SimpleMainBehaviour;
import com.jme3.ai.agents.behaviours.npc.steering.CompoundSteeringBehaviour;
import com.jme3.ai.agents.behaviours.npc.steering.ObstacleAvoidanceBehaviour;
import com.jme3.ai.agents.behaviours.npc.steering.QueuingBehaviour;
import com.jme3.ai.agents.behaviours.npc.steering.SeekBehaviour;
import com.jme3.material.RenderState;
import com.jme3.math.FastMath;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Cylinder;
import com.jme3.scene.shape.Sphere;
import java.util.ArrayList;

import steeringDemos.control.CustomSteerControl;

import java.util.List;
import java.util.Arrays;

/**
 * AI Steer Demo - Queuing demo
 *
 * @author Jesús Martín Berlanga
 * @version 1.0
 */
public class QueuingDemo extends SimpleApplication {
       
    private Game game = Game.getInstance(); //creating game
    //TEST SETTINGS - START
    private final String BOID_MODEL_NAME = "Models/boid.j3o";
    private final String BOID_MATERIAL_NAME = "Common/MatDefs/Misc/Unshaded.j3md";
    private final ColorRGBA TARGET_COLOR = ColorRGBA.Red;
    private final float TARGET_MOVE_SPEED = 1f;
    private final float TARGET_ROTATION_SPEED = 30;
    private final float TARGET_MASS = 50;
    private final float TARGET_MAX_FORCE = 20;
    private final int NUMBER_NEIGHBOURS = 50;
    private final ColorRGBA NEIGHBOURS_COLOR = ColorRGBA.Green;
    private final float NEIGHBOURS_MOVE_SPEED = 0.96f;
    private final float NEIGHBOURS_ROTATION_SPEED = 30;
    private final float NEIGHBOURS_MASS = 50;
    private final float NEIGHBOURS_MAX_FORCE = 20;
    //TEST SETTINGS - END
    
    SeekBehaviour[] neighSeek;
    Agent[] neighbours;
    
    public static void main(String[] args) {
        QueuingDemo app = new QueuingDemo();
        app.start();
    }
    
    @Override
    public void simpleInitApp() {
            
        //defining rootNode for game processing
        game.setApp(this);
        game.setGameControl(new CustomSteerControl(5f));
        
        this.setupCamera();
        
        Vector3f[] spawnArea = null;
        
        ColorRGBA targetColor = new ColorRGBA(ColorRGBA.Cyan.r, ColorRGBA.Cyan.g, ColorRGBA.Cyan.b, 0.25f);
        
        Agent target = this.createDoor("Door", targetColor, 0.28f);
        game.addAgent(target); //Add the target to the game
        this.setStats(target, this.TARGET_MOVE_SPEED,
                this.TARGET_ROTATION_SPEED, this.TARGET_MASS,
                this.TARGET_MAX_FORCE);
        game.getGameControl().spawn(target, new Vector3f(0,0,15));
        SimpleMainBehaviour targetMainB = new SimpleMainBehaviour(target);
        target.setMainBehaviour(targetMainB);
        //this.setStats(target, this.TARGET_MOVE_SPEED, this.TARGET_ROTATION_SPEED, 
        //        this.TARGET_MASS, this.TARGET_MAX_FORCE);
        
        this.neighbours = new Agent[this.NUMBER_NEIGHBOURS];
        
        for (int i = 0; i < this.NUMBER_NEIGHBOURS; i++) {
            this.neighbours[i] = this.createBoid("Neighbour " + i, this.NEIGHBOURS_COLOR);
            game.addAgent(this.neighbours[i]); //Add the neighbours to the game
            this.setStats(this.neighbours[i], this.NEIGHBOURS_MOVE_SPEED * 1.5f, //* (FastMath.nextRandomFloat() * 2) + 0.25f,
                    this.NEIGHBOURS_ROTATION_SPEED, this.NEIGHBOURS_MASS,
                    this.NEIGHBOURS_MAX_FORCE);
            game.getGameControl().spawn(this.neighbours[i], spawnArea);
            this.neighbours[i].setRadius(0.11f);
        }
        
        List<Agent> obstacles = new ArrayList<Agent>();
        obstacles.addAll(Arrays.asList(this.neighbours));
        
        List<Agent> wallObstacle = new ArrayList<Agent>();
        
        Vector3f[] spheresSpawnPositions = new Vector3f[]
        {
            new Vector3f(1,0,0).mult(0.28f).add(new Vector3f(0,0,15)),
            new Vector3f(0,1,0).mult(0.28f).add(new Vector3f(0,0,15)),
            new Vector3f(-1,0,0).mult(0.28f).add(new Vector3f(0,0,15)),
            new Vector3f(0,-1,0).mult(0.28f).add(new Vector3f(0,0,15)),
            
            new Vector3f(0.70710678118654752440084436210485f,0.70710678118654752440084436210485f,0).mult(0.28f).add(new Vector3f(0,0,15)),
            new Vector3f(-0.70710678118654752440084436210485f,0.70710678118654752440084436210485f,0).mult(0.28f).add(new Vector3f(0,0,15)),
            new Vector3f(0.70710678118654752440084436210485f,-0.70710678118654752440084436210485f,0).mult(0.28f).add(new Vector3f(0,0,15)),
            new Vector3f(-0.70710678118654752440084436210485f,-0.70710678118654752440084436210485f,0).mult(0.28f).add(new Vector3f(0,0,15)),
            
            new Vector3f(0.92387953251128675612818318939679f,0.3826834323650897717284599840304f,0).mult(0.28f).add(new Vector3f(0,0,15)),
            new Vector3f(-0.92387953251128675612818318939679f,0.3826834323650897717284599840304f,0).mult(0.28f).add(new Vector3f(0,0,15)),
            new Vector3f(0.92387953251128675612818318939679f,-0.3826834323650897717284599840304f,0).mult(0.28f).add(new Vector3f(0,0,15)),
            new Vector3f(-0.92387953251128675612818318939679f,-0.3826834323650897717284599840304f,0).mult(0.28f).add(new Vector3f(0,0,15)),
            
            new Vector3f(0.3826834323650897717284599840304f,0.92387953251128675612818318939679f,0).mult(0.28f).add(new Vector3f(0,0,15)),
            new Vector3f(-0.3826834323650897717284599840304f,0.92387953251128675612818318939679f,0).mult(0.28f).add(new Vector3f(0,0,15)),
            new Vector3f(0.3826834323650897717284599840304f,-0.92387953251128675612818318939679f,0).mult(0.28f).add(new Vector3f(0,0,15)),
            new Vector3f(-0.3826834323650897717284599840304f,-0.92387953251128675612818318939679f,0).mult(0.28f).add(new Vector3f(0,0,15)),
            
        };
        
        for(int i = 0; i < 16; i++)
        {
            wallObstacle.add(this.createSphere("Door obstacle " + i, ColorRGBA.Blue, 0.65f));
            
            this.setStats(wallObstacle.get(i), this.NEIGHBOURS_MOVE_SPEED,
                    this.NEIGHBOURS_ROTATION_SPEED, this.NEIGHBOURS_MASS,
                    this.NEIGHBOURS_MAX_FORCE);
            game.getGameControl().spawn(wallObstacle.get(i), spheresSpawnPositions[i]);
            wallObstacle.get(i).setRadius(0.325f);
            
            SimpleMainBehaviour mainB = new SimpleMainBehaviour(wallObstacle.get(i));
            wallObstacle.get(i).setMainBehaviour(mainB);
        }
        
        
        SimpleMainBehaviour[] neighboursMainBehaviour = new SimpleMainBehaviour[this.neighbours.length];
        neighSeek = new SeekBehaviour[this.neighbours.length];
        
        for (int i = 0; i < this.neighbours.length; i++) {
            neighboursMainBehaviour[i] = new SimpleMainBehaviour(this.neighbours[i]);
            
            SeekBehaviour extraSeek = new SeekBehaviour(this.neighbours[i], new Vector3f(0,0,14.9f));
            extraSeek.setupStrengthControl(0.35f);
            this.neighSeek[i] = new SeekBehaviour(this.neighbours[i], new Vector3f(0,0,15.17f));
            SeparationBehaviour separation = new SeparationBehaviour(this.neighbours[i], obstacles, 0.13f);
            separation.setupStrengthControl(0.25f);

            ObstacleAvoidanceBehaviour obstacleAvoid = new ObstacleAvoidanceBehaviour(this.neighbours[i], wallObstacle, 1, 5);
            
           // BalancedCompoundSteeringBehaviour steer = new BalancedCompoundSteeringBehaviour(this.neighbours[i]);
            CompoundSteeringBehaviour steer = new CompoundSteeringBehaviour(this.neighbours[i]);
            QueuingBehaviour queue = new QueuingBehaviour(this.neighbours[i], obstacles, 1.5f);
            SeparationBehaviour queueSeparation = new SeparationBehaviour(this.neighbours[i], obstacles, 0.25f);
            queueSeparation.setupStrengthControl(0.01f); //1%
            
            steer.addSteerBehaviour(this.neighSeek[i]);
            steer.addSteerBehaviour(extraSeek);
            steer.addSteerBehaviour(obstacleAvoid);
            steer.addSteerBehaviour(queue);
            steer.addSteerBehaviour(queueSeparation);
            steer.addSteerBehaviour(separation, 1, 0.01f); //Highest layer => Highest priority
            
            neighboursMainBehaviour[i].addBehaviour(steer);

            this.neighbours[i].setMainBehaviour(neighboursMainBehaviour[i]);
        }
        
        game.start();
    }
    
    private void setupCamera() {
        getCamera().setLocation(new Vector3f(0, 20, 0));
        getCamera().lookAt(Vector3f.ZERO, Vector3f.UNIT_X);
        getFlyByCamera().setMoveSpeed(6);

        //flyCam.setDragToRotate(true);
        //flyCam.setEnabled(false);  
    }

    //Create an agent with a name and a color
    private Agent createBoid(String name, ColorRGBA color) {
        Spatial boidSpatial = assetManager.loadModel(this.BOID_MODEL_NAME);
        boidSpatial.setLocalScale(0.1f); //Resize
        
        Material mat = new Material(assetManager, this.BOID_MATERIAL_NAME);        
        mat.setColor("Color", color);
        boidSpatial.setMaterial(mat);
        
        return new Agent(name, boidSpatial);
    }
    
    private Agent createDoor(String name, ColorRGBA color, float radius)
    { 
        Node origin = new Node();
        Cylinder mesh = new Cylinder(4, 20, radius, 0.01f, true);
        Geometry geom = new Geometry("A shape", mesh);
        origin.attachChild(geom);
         
        Spatial doorSpatial = origin;
        
        Material mat = new Material(assetManager, this.BOID_MATERIAL_NAME); 
        mat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);         
        mat.setColor("Color", color);
        doorSpatial.setMaterial(mat);
        
        return new Agent(name, doorSpatial);   
    }
    
    private Agent createSphere(String name, ColorRGBA color, float size) 
    {
        Sphere sphere = new Sphere(13, 12, size);
        Geometry sphereG = new Geometry("Sphere Geometry", sphere);
        Spatial spatial = sphereG;

        spatial.setLocalScale(0.1f); //Resize0

        Material mat = new Material(assetManager, this.BOID_MATERIAL_NAME);
        mat.setColor("Color", color);
        spatial.setMaterial(mat);

        return new Agent(name, spatial);
    }

    //Setup the stats for an agent
    private void setStats(Agent myAgent, float moveSpeed, float rotationSpeed,
            float mass, float maxForce) {
        
        myAgent.setMoveSpeed(moveSpeed);
        myAgent.setRotationSpeed(rotationSpeed);
        myAgent.setMass(mass);
        myAgent.setMaxForce(maxForce);
    }
    
    @Override
    public void simpleUpdate(float tpf) {
        game.update(tpf);
        
        for(int i = 0; i < this.neighbours.length; i++)
        {
            if(new Vector3f(0,0,15.17f).subtract(this.neighbours[i].getLocalTranslation()).length() < 0.15f)
                this.neighbours[i].setLocalTranslation
                        (
                            ( (float)((FastMath.nextRandomFloat()*2) - 1) )* 5, 
                            ( (float)((FastMath.nextRandomFloat()*2) - 1) )* 5, 
                            ( (float)((FastMath.nextRandomFloat()*2) - 1) )* 5 
                        );
        }
    }

}
