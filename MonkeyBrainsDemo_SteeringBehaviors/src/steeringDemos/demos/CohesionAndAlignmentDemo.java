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
import com.jme3.ai.agents.behaviours.npc.steering.AlignmentBehaviour;
import com.jme3.ai.agents.behaviours.npc.steering.CohesionBehaviour;
import com.jme3.ai.agents.behaviours.npc.steering.CompoundSteeringBehaviour;
import com.jme3.ai.agents.behaviours.npc.steering.WanderBehaviour;
import com.jme3.math.FastMath;
import java.util.ArrayList;

import steeringDemos.control.CustomSteerControl;

import java.util.List;
import java.util.Arrays;

/**
 * AI Steer Test - Testing the cohesion and the alignment behaviours
 *
 * @author Jesús Martín Berlanga
 * @version 1.0
 */
public class CohesionAndAlignmentDemo extends SimpleApplication {

    private Game game = Game.getInstance(); //creating game
    //TEST SETTINGS - START
    private final String BOID_MODEL_NAME = "Models/boid.j3o";
    private final String BOID_MATERIAL_NAME = "Common/MatDefs/Misc/Unshaded.j3md";

    private final int NUMBER_NEIGHBOURS = 250;
    private final ColorRGBA NEIGHBOURS_COLOR = ColorRGBA.Blue;
    private final float NEIGHBOURS_MOVE_SPEED = 0.96f;
    private final float NEIGHBOURS_ROTATION_SPEED = 30f;
    private final float NEIGHBOURS_MASS = 50;
    private final float NEIGHBOURS_MAX_FORCE = 20;
    //TEST SETTINGS - END

    public static void main(String[] args) {
        CohesionAndAlignmentDemo app = new CohesionAndAlignmentDemo();
        app.start();
    }

    @Override
    public void simpleInitApp() {
        //defining rootNode for game processing
        game.setRootNode(rootNode);

        game.setInputManager(inputManager);
        game.setGameControl(new CustomSteerControl(50f));
        game.getGameControl().loadInputManagerMapping();

        this.setupCamera();

        Vector3f[] spawnArea = null;

        Agent[] boids = new Agent[this.NUMBER_NEIGHBOURS];

        
        for (int i = 0; i < this.NUMBER_NEIGHBOURS; i++) {
            boids[i] = this.createBoid("boid " + i, this.NEIGHBOURS_COLOR);
            boids[i].setRadius(0.1f);
            game.addAgent(boids[i]); //Add the neighbours to the game
            this.setStats(boids[i], this.NEIGHBOURS_MOVE_SPEED,
                    this.NEIGHBOURS_ROTATION_SPEED, this.NEIGHBOURS_MASS,
                    this.NEIGHBOURS_MAX_FORCE);
            game.getGameControl().spawn(boids[i], spawnArea);
        }

        List<Agent> obstacles = new ArrayList<Agent>();
        obstacles.addAll(Arrays.asList(boids));

        SimpleMainBehaviour[] neighboursMainBehaviour = new SimpleMainBehaviour[boids.length];

        SeparationBehaviour[] separation = new SeparationBehaviour[boids.length];
        CohesionBehaviour[] cohesion = new CohesionBehaviour[boids.length];
        AlignmentBehaviour[] alignment = new AlignmentBehaviour[boids.length];
        WanderBehaviour[] wander = new WanderBehaviour[boids.length];

        for (int i = 0; i < boids.length; i++) {
            neighboursMainBehaviour[i] = new SimpleMainBehaviour(boids[i]);


            separation[i] = new SeparationBehaviour(boids[i], obstacles);
            cohesion[i] = new CohesionBehaviour(boids[i], obstacles, 5f,  FastMath.PI / 4);
            alignment[i] = new AlignmentBehaviour(boids[i], obstacles, 5f,  FastMath.PI / 4.2f);
            wander[i] = new WanderBehaviour(boids[i]);
            wander[i].setArea(Vector3f.ZERO, new Vector3f(75, 75, 75));

            separation[i].setupStrengthControl(0.85f);
            cohesion[i].setupStrengthControl(2.15f);
            alignment[i].setupStrengthControl(0.25f);
            wander[i].setupStrengthControl(0.35f);
            

            CompoundSteeringBehaviour steer = new CompoundSteeringBehaviour(boids[i]);

           steer.addSteerBehaviour(cohesion[i]);
           steer.addSteerBehaviour(alignment[i]);
           steer.addSteerBehaviour(separation[i]);
           steer.addSteerBehaviour(wander[i]);
           neighboursMainBehaviour[i].addBehaviour(steer);
            
           boids[i].setMainBehaviour(neighboursMainBehaviour[i]);
             
        }

        game.start();
    }

    private void setupCamera() {
        getCamera().setLocation(new Vector3f(0, 20, 0));
        getCamera().lookAt(Vector3f.ZERO, Vector3f.UNIT_X);
        getFlyByCamera().setMoveSpeed(50);

        flyCam.setDragToRotate(true);
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
