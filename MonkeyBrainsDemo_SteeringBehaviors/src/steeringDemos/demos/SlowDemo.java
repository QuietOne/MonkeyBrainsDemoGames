//Copyright (c) 2014, Jesús Martín Berlanga. All rights reserved. Distributed under the BSD licence. Read "com/jme3/ai/license.txt".

package steeringDemos.demos;

import com.jme3.ai.agents.Agent;
import com.jme3.ai.agents.behaviours.npc.SimpleMainBehaviour;
import com.jme3.ai.agents.behaviours.npc.steering.CompoundSteeringBehaviour;
import com.jme3.ai.agents.behaviours.npc.steering.MoveBehaviour;
import com.jme3.ai.agents.behaviours.npc.steering.supportBehaviors.SlowBehaviour;
import com.jme3.ai.agents.util.control.Game;
import com.jme3.app.SimpleApplication;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Sphere;
import steeringDemos.control.CustomSteerControl;

/**
 * Demo for the slow behaviour
 *
 * @author Jesús Martín Berlanga
 * @version 1.3
 */
public class SlowDemo extends SimpleApplication {
    
    private Game game = Game.getInstance(); //creating game
    //TEST SETTINGS - START
    private final String BOID_MODEL_NAME = "Models/boid.j3o";
    private final String MATERIAL_1 = "Common/MatDefs/Misc/Unshaded.j3md";
    private final ColorRGBA NEIGHBOURS_COLOR = ColorRGBA.Blue;
    private final float NEIGHBOURS_MOVE_SPEED = 1f;
    private final float NEIGHBOURS_ROTATION_SPEED = 30f;
    private final float NEIGHBOURS_MASS = 50;
    private final float NEIGHBOURS_MAX_FORCE = 20;
    //TEST SETTINGS - END
    
    public static void main(String[] args) {
        SlowDemo app = new SlowDemo();
        app.start();
    }
    
    @Override
    public void simpleInitApp() {
        game.setApp(this);
        game.setGameControl(new CustomSteerControl(5f));
        this.setupCamera();
        
        
        Agent agent = this.createBoid("boid ", this.NEIGHBOURS_COLOR);
        agent.setRadius(0.1f);
        game.addAgent(agent); //Add the neighbours to the game
        this.setStats(agent, this.NEIGHBOURS_MOVE_SPEED,
                this.NEIGHBOURS_ROTATION_SPEED, this.NEIGHBOURS_MASS,
                this.NEIGHBOURS_MAX_FORCE);
        
        
        game.getGameControl().spawn(agent, new Vector3f());
        
        
        SimpleMainBehaviour main = new SimpleMainBehaviour(agent);
        MoveBehaviour move = new MoveBehaviour(agent);
        move.setMoveDirection(new Vector3f(1, 0, 0));
        CompoundSteeringBehaviour steer = new CompoundSteeringBehaviour(agent);
        
        steer.addSteerBehaviour(move);
        
        SlowBehaviour slow = new SlowBehaviour(steer);
        slow.setAcive(true);
        
        main.addBehaviour(steer);
        agent.setMainBehaviour(main);
        
        game.start();
    }
    
    private void setupCamera() {
        getCamera().setLocation(new Vector3f(0, 20, 0));
        getCamera().lookAt(Vector3f.ZERO, Vector3f.UNIT_X);
        getFlyByCamera().setMoveSpeed(20);
        
        //flyCam.setDragToRotate(true);
        //flyCam.setEnabled(false);  
    }

    //Create an agent with a name and a color
    private Agent createBoid(String name, ColorRGBA color) {
        Spatial boidSpatial = assetManager.loadModel(this.BOID_MODEL_NAME);
        boidSpatial.setLocalScale(0.1f); //Resize

        Material mat = new Material(assetManager, this.MATERIAL_1);
        mat.setColor("Color", color);
        boidSpatial.setMaterial(mat);
        
        return new Agent(name, boidSpatial);
    }

    //Create a sphere
    private Agent createSphere(String name, ColorRGBA color) {
        Sphere sphere = new Sphere(10, 10, 0.25f);
        Geometry sphereG = new Geometry("Sphere Geometry", sphere);
        Spatial spatial = sphereG;
        
        spatial.setLocalScale(0.1f); //Resize0

        Material mat = new Material(assetManager, this.MATERIAL_1);
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
