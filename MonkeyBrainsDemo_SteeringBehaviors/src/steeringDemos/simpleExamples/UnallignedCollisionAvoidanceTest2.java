//Copyright (c) 2014, Jesús Martín Berlanga. All rights reserved. Distributed under the BSD licence. Read "com/jme3/ai/license.txt".

package steeringDemos.simpleExamples;

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
import com.jme3.ai.agents.behaviours.npc.steering.MoveBehaviour;
import com.jme3.ai.agents.behaviours.npc.steering.UnalignedCollisionAvoidanceBehaviour;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Sphere;
import java.util.ArrayList;
import java.util.List;


import steeringDemos.control.CustomSteerControl;

/**
 * AI Steer Test - Testing the unalligned avoidance behaviour
 *
 * @author Jesús Martín Berlanga
 * @version 1.0
 */
public class UnallignedCollisionAvoidanceTest2 extends SimpleApplication {

    private Game game = Game.getInstance(); //creating game
    //TEST SETTINGS - START
    private final String BOID_MODEL_NAME = "Models/boid.j3o";
    private final String BOID_MATERIAL_NAME = "Common/MatDefs/Misc/Unshaded.j3md";
    private final ColorRGBA TARGET_COLOR = ColorRGBA.Red;
    private final float TARGET_MOVE_SPEED = 1f;
    private final float TARGET_ROTATION_SPEED = 30;
    private final float TARGET_MASS = 50;
    private final float TARGET_MAX_FORCE = 20;
    private final int NUMBER_NEIGHBOURS = 1000;
    private final ColorRGBA NEIGHBOURS_COLOR = ColorRGBA.Blue;
    private final float NEIGHBOURS_MOVE_SPEED = 0.99f;
    private final float NEIGHBOURS_ROTATION_SPEED = 30;
    private final float NEIGHBOURS_MASS = 50;
    private final float NEIGHBOURS_MAX_FORCE = 20;
    //TEST SETTINGS - END

    public static void main(String[] args) {
        UnallignedCollisionAvoidanceTest2 app = new UnallignedCollisionAvoidanceTest2();
        app.start();
    }

    @Override
    public void simpleInitApp() {
        //defining rootNode for game processing
        game.setApp(this);
        game.setGameControl(new CustomSteerControl(5f));

        this.setupCamera();

        Agent agent = this.createBoid("Target", ColorRGBA.Blue);
        agent.setRadius(0.1f);

        game.addAgent(agent); //Add the target to the game
        this.setStats(agent, this.TARGET_MOVE_SPEED, this.TARGET_ROTATION_SPEED,
                this.TARGET_MASS, this.TARGET_MAX_FORCE);
        game.getGameControl().spawn(agent, new Vector3f());
        
            Agent customNeigh1 = this.createSphere("customNeigh_1", ColorRGBA.Orange);
            customNeigh1.setRadius(1f);
            
            game.addAgent(customNeigh1); //Add the neighbours to the game
            this.setStats(customNeigh1, this.NEIGHBOURS_MOVE_SPEED,
                    this.NEIGHBOURS_ROTATION_SPEED, this.NEIGHBOURS_MASS,
                    this.NEIGHBOURS_MAX_FORCE);
            game.getGameControl().spawn(customNeigh1, new Vector3f(16,0,0));
            
            SimpleMainBehaviour mainB = new SimpleMainBehaviour(customNeigh1);
            customNeigh1.setMainBehaviour(mainB);
        
        List<Agent> obstacles = new ArrayList<Agent>();
        obstacles.add(customNeigh1); 

        //ADD OBSTACLE AVOIDANCE TO THE TARGET
        
        CompoundSteeringBehaviour steer = new BalancedCompoundSteeringBehaviour(agent);
        SimpleMainBehaviour targetMainB = new SimpleMainBehaviour(agent);
        MoveBehaviour move = new MoveBehaviour(agent);
        move.setMoveDirection(new Vector3f(1,0,0));
        
        steer.addSteerBehaviour(move);
            UnalignedCollisionAvoidanceBehaviour avoid = new UnalignedCollisionAvoidanceBehaviour(agent, obstacles, 5f, 10);
                    avoid.setupStrengthControl(1f);
        steer.addSteerBehaviour(avoid);
        targetMainB.addBehaviour(steer);
        agent.setMainBehaviour(targetMainB);

        game.start();
    }

    private void setupCamera() {
        getCamera().setLocation(new Vector3f(0, 20, 0));
        getCamera().lookAt(Vector3f.ZERO, Vector3f.UNIT_X);
        getFlyByCamera().setMoveSpeed(50);

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
    private Agent createSphere(String name, ColorRGBA color) {
        Sphere sphere = new Sphere(10, 10, 8f);
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
    }
}
