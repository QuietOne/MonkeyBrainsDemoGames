//Copyright (c) 2014, Jesús Martín Berlanga. All rights reserved. Distributed under the BSD licence. Read "com/jme3/ai/license.txt".

package steeringDemos.demos;

import com.jme3.ai.agents.Agent;
import com.jme3.ai.agents.behaviours.npc.steering.BalancedCompoundSteeringBehaviour;
import com.jme3.ai.agents.util.control.Game;
import com.jme3.app.SimpleApplication;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.material.Material;
import com.jme3.scene.Spatial;

import com.jme3.ai.agents.behaviours.npc.SimpleMainBehaviour;
import com.jme3.ai.agents.behaviours.npc.steering.CompoundSteeringBehaviour;
import com.jme3.ai.agents.behaviours.npc.steering.ObstacleAvoidanceBehaviour;
import com.jme3.ai.agents.behaviours.npc.steering.SeekBehaviour;
import com.jme3.math.FastMath;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Sphere;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;


import steeringDemos.control.CustomSteerControl;

/**
 * AI Steer Test - Testing the obstacle avoidance behaviour
 *
 * @author Jesús Martín Berlanga
 * @version 1.0
 */
public class ObstacleAvoidanceDemo extends SimpleApplication {

    private Game game = Game.getInstance(); //creating game
    //TEST SETTINGS - START
    private final String BOID_MODEL_NAME = "Models/boid.j3o";
    private final String BOID_MATERIAL_NAME = "Common/MatDefs/Misc/Unshaded.j3md";
    private final ColorRGBA TARGET_COLOR = ColorRGBA.Red;
    private final float TARGET_MOVE_SPEED = 1f;
    private final float TARGET_ROTATION_SPEED = 30;
    private final float TARGET_MASS = 50;
    private final float TARGET_MAX_FORCE = 20;
    private final int NUMBER_NEIGHBOURS = 75;
    private final ColorRGBA NEIGHBOURS_COLOR = ColorRGBA.Blue;
    private final float NEIGHBOURS_MOVE_SPEED = 0.99f;
    private final float NEIGHBOURS_ROTATION_SPEED = 30;
    private final float NEIGHBOURS_MASS = 50;
    private final float NEIGHBOURS_MAX_FORCE = 20;
    //TEST SETTINGS - END

    private Agent agent;
    private Agent focus;
    private boolean positiveXSide = true;
    
    public static void main(String[] args) {
        ObstacleAvoidanceDemo app = new ObstacleAvoidanceDemo();
        app.start();
    }

    @Override
    public void simpleInitApp() {
        //defining rootNode for game processing
        game.setRootNode(rootNode);

        game.setInputManager(inputManager);
        game.setGameControl(new CustomSteerControl(5.5f));
        game.getGameControl().loadInputManagerMapping();

        this.setupCamera();

        Vector3f[] spawnArea = null;

        agent = this.createBoid("Target", ColorRGBA.Blue);
        agent.setRadius(1.5f);

        game.addAgent(agent); //Add the target to the game
        this.setStats(agent, this.TARGET_MOVE_SPEED, this.TARGET_ROTATION_SPEED,
                this.TARGET_MASS, this.TARGET_MAX_FORCE);
        game.getGameControl().spawn(agent, new Vector3f());

        Agent[] neighbours = new Agent[this.NUMBER_NEIGHBOURS];

        for (int i = 0; i < this.NUMBER_NEIGHBOURS; i++) {
            neighbours[i] = this.createSphere("neighbour_" + i, ColorRGBA.Orange, 3);
            neighbours[i].setRadius(3f);

            game.addAgent(neighbours[i]); //Add the neighbours to the game
            this.setStats(neighbours[i], this.NEIGHBOURS_MOVE_SPEED,
                    this.NEIGHBOURS_ROTATION_SPEED, this.NEIGHBOURS_MASS,
                    this.NEIGHBOURS_MAX_FORCE);
            game.getGameControl().spawn(neighbours[i], spawnArea);

            SimpleMainBehaviour mainB = new SimpleMainBehaviour(neighbours[i]);
            neighbours[i].setMainBehaviour(mainB);
        }
        
        focus = this.createSphere("focus", ColorRGBA.Green, 1.5f);
        game.addAgent(focus);

            game.addAgent(focus); //Add the neighbours to the game
            this.setStats(focus, this.NEIGHBOURS_MOVE_SPEED,
                    this.NEIGHBOURS_ROTATION_SPEED, this.NEIGHBOURS_MASS,
                    this.NEIGHBOURS_MAX_FORCE);
            game.getGameControl().spawn(focus, this.generateRandomPosition());

            SimpleMainBehaviour mainB = new SimpleMainBehaviour(focus);
            focus.setMainBehaviour(mainB);
        

        List<Agent> obstacles = new ArrayList<Agent>();
        obstacles.addAll(Arrays.asList(neighbours));

        //ADD OBSTACLE AVOIDANCE TO THE TARGET

        CompoundSteeringBehaviour steer = new BalancedCompoundSteeringBehaviour(agent);
        SimpleMainBehaviour targetMainB = new SimpleMainBehaviour(agent);

        SeekBehaviour seekSteer = new SeekBehaviour(agent, focus);

        ObstacleAvoidanceBehaviour obstacleAvoidance = new ObstacleAvoidanceBehaviour(agent, obstacles, 1);
        obstacleAvoidance.setupStrengthControl(0.65f);

        steer.addSteerBehaviour(seekSteer);
        steer.addSteerBehaviour(obstacleAvoidance);
        targetMainB.addBehaviour(steer);
        agent.setMainBehaviour(targetMainB);

        game.start();
    }

    private void setupCamera() {
        getCamera().setLocation(new Vector3f(0, 20, 0));
        getCamera().lookAt(Vector3f.ZERO, Vector3f.UNIT_X);
        getFlyByCamera().setMoveSpeed(15);

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

    //Create a sphere
    private Agent createSphere(String name, ColorRGBA color, float size) {
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
        
        if(this.agent.distanceRelativeToAgent(this.focus) < 0.5f)
        {
            this.focus.setLocalTranslation(this.generateRandomPosition());
        }
    }
    
    private Vector3f generateRandomPosition()
    {
        Random rand = FastMath.rand;
        Vector3f randomPos;
        
        if(this.positiveXSide)
        {
            randomPos = new Vector3f(7.5f, (rand.nextFloat() - 0.5f) * 7.5f, (rand.nextFloat() - 0.5f) * 7.5f);
            this.positiveXSide = false;
        }
        else
        {  
            randomPos = new Vector3f(-7.5f, (rand.nextFloat() - 0.5f) * 7.5f, (rand.nextFloat() - 0.5f) * 7.5f); 
            this.positiveXSide = true;
        }
        
        return randomPos;
    }
}
