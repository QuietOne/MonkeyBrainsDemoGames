//Copyright (c) 2014, Jesús Martín Berlanga. All rights reserved. Distributed under the BSD licence. Read "com/jme3/ai/license.txt".

package steeringDemos.demos;

import com.jme3.ai.agents.Agent;
import com.jme3.ai.agents.behaviours.npc.SimpleMainBehaviour;
import com.jme3.ai.agents.behaviours.npc.steering.ArriveBehaviour;
import com.jme3.ai.agents.behaviours.npc.steering.BalancedCompoundSteeringBehaviour;
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
 * Demo for ArriveBehaviour
 *
 * @author Jesús Martín Berlanga
 * @version 1.3
 */
public class ArriveDemo extends SimpleApplication {
    
    private Game game = Game.getInstance(); //creating game
    //TEST SETTINGS - START
    private final String BOID_MODEL_NAME = "Models/boid.j3o";
    private final String MATERIAL_1 = "Common/MatDefs/Misc/Unshaded.j3md";
    private final ColorRGBA NEIGHBOURS_COLOR = ColorRGBA.Blue;
    private final float NEIGHBOURS_MOVE_SPEED = 0.96f;
    private final float NEIGHBOURS_ROTATION_SPEED = 30f;
    private final float NEIGHBOURS_MASS = 50;
    private final float NEIGHBOURS_MAX_FORCE = 20;
    //TEST SETTINGS - END
    private Agent[] agents = new Agent[3];
    private Agent target;
    private ArriveBehaviour[] arrive;
    
    public static void main(String[] args) {       
        ArriveDemo app = new ArriveDemo();       
        app.start();
    }
    
    @Override
    public void simpleInitApp() {
        game.setApp(this);
        game.setGameControl(new CustomSteerControl(50f));
        this.setupCamera();
        
        this.target = this.createSphere("target", ColorRGBA.Red);
        target.setRadius(0.25f);
        game.addAgent(target);
        game.getGameControl().spawn(target, new Vector3f());
        
        for (int i = 0; i < this.agents.length; i++) {
            agents[i] = this.createBoid("boid " + i, this.NEIGHBOURS_COLOR);
            agents[i].setRadius(0.1f);
            game.addAgent(agents[i]); //Add the neighbours to the game
            this.setStats(agents[i], this.NEIGHBOURS_MOVE_SPEED,
                    this.NEIGHBOURS_ROTATION_SPEED, this.NEIGHBOURS_MASS,
                    this.NEIGHBOURS_MAX_FORCE);
        }
        
        game.getGameControl().spawn(agents[0], new Vector3f(-10, 0, 0));
        game.getGameControl().spawn(agents[1], new Vector3f(8, 0, 0));
        game.getGameControl().spawn(agents[2], new Vector3f(0, 50, 2));
        
        arrive = new ArriveBehaviour[3];
        
        SimpleMainBehaviour main0 = new SimpleMainBehaviour(agents[0]);
        SimpleMainBehaviour main1 = new SimpleMainBehaviour(agents[1]);
        SimpleMainBehaviour main2 = new SimpleMainBehaviour(agents[2]);
        
        arrive[0] = new ArriveBehaviour(agents[0], target);
        arrive[1] = new ArriveBehaviour(agents[1], target);
        arrive[2] = new ArriveBehaviour(agents[2], target);
        
        BalancedCompoundSteeringBehaviour steer1 = new BalancedCompoundSteeringBehaviour(agents[1]);
        steer1.addSteerBehaviour(arrive[1]);
        
        main0.addBehaviour(arrive[0]);
        main1.addBehaviour(steer1);
        main2.addBehaviour(arrive[2]);
        
        agents[0].setMainBehaviour(main0);
        agents[1].setMainBehaviour(main1);
        agents[2].setMainBehaviour(main2);
        
        SimpleMainBehaviour targetMain = new SimpleMainBehaviour(target);
        target.setMainBehaviour(targetMain);
        
        game.start();
    }
    
    private void setupCamera() {
        getCamera().setLocation(new Vector3f(0, 20, 0));
        getCamera().lookAt(Vector3f.ZERO, Vector3f.UNIT_X);
        getFlyByCamera().setMoveSpeed(17);
        
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
        Sphere sphere = new Sphere(10, 10, 3f);
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
        
        for(int i = 0; i < this.arrive.length; i++)
        {
            if(this.agents[i].distanceRelativeToAgent(target) <= 0.001f + target.getRadius())
            {
                    Vector3f resetPosition = null;
                    
                    switch(i)
                    {
                        case 0:
                            resetPosition = new Vector3f(-10, 0, 0); 
                            break;
                            
                        case 1:
                            resetPosition = new Vector3f(8, 0, 0);
                            break;
                            
                        case 2:
                            resetPosition = new Vector3f(0, 50, 2);
                            break;
                            
                    }
                    
                    this.agents[i].setLocalTranslation(resetPosition);
            }

         }
       
    }
}
