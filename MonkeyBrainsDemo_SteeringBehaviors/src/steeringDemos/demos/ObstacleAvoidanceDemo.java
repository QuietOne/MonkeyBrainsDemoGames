//Copyright (c) 2014, Jesús Martín Berlanga. All rights reserved. Distributed under the BSD licence. Read "com/jme3/ai/license.txt".

package steeringDemos.demos;

import com.jme3.ai.agents.Agent;
import com.jme3.ai.agents.behaviours.npc.steering.BalancedCompoundSteeringBehaviour;
import com.jme3.ai.agents.behaviours.npc.SimpleMainBehaviour;
import com.jme3.ai.agents.behaviours.npc.steering.CompoundSteeringBehaviour;
import com.jme3.ai.agents.behaviours.npc.steering.ObstacleAvoidanceBehaviour;
import com.jme3.ai.agents.behaviours.npc.steering.SeekBehaviour;

import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.math.FastMath;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import steeringDemos.BasicDemo;
import steeringDemos.control.CustomSteerControl;

/**
 * Obstacle avoidance demo
 *
 * @author Jesús Martín Berlanga
 * @version 2.0
 */
public class ObstacleAvoidanceDemo extends BasicDemo {

    private Agent agent;
    private Agent focus;
    private boolean positiveXSide = true;
    
    public static void main(String[] args) {
        ObstacleAvoidanceDemo app = new ObstacleAvoidanceDemo();
        app.start();
    }

    @Override
    public void simpleInitApp() 
    {  
        this.steerControl = new CustomSteerControl(7, 4.5f);
        this.steerControl.setCameraSettings(getCamera());
        this.steerControl.setFlyCameraSettings(getFlyByCamera());
        
        //defining rootNode for game processing
        game.setApp(this);
        game.setGameControl(this.steerControl);

        Vector3f[] spawnArea = null;
        this.numberNeighbours = 150;

        agent = this.createBoid("Target", ColorRGBA.Blue, 0.27f);

        game.addAgent(agent); //Add the target to the game
        this.setStats
                (
                    agent,
                    this.targetMoveSpeed,
                    this.targetRotationSpeed,
                    this.targetMass,
                    this.targetMaxForce
                );
        game.getGameControl().spawn(agent, new Vector3f());

        Agent[] neighbours = new Agent[this.numberNeighbours];

        for (int i = 0; i < this.numberNeighbours; i++) {
            neighbours[i] = this.createSphere("neighbour_" + i, ColorRGBA.Orange, 0.4f);

            game.addAgent(neighbours[i]); //Add the neighbours to the game
            this.setStats
                    (
                        neighbours[i], 
                        this.neighboursMoveSpeed,
                        this.neighboursRotationSpeed, 
                        this.neighboursMass,
                        this.neighboursMaxForce
                    );
            game.getGameControl().spawn(neighbours[i], spawnArea);

            SimpleMainBehaviour mainB = new SimpleMainBehaviour(neighbours[i]);
            neighbours[i].setMainBehaviour(mainB);
        }
        
        focus = this.createSphere("focus", ColorRGBA.Green, 0.4f);
        game.addAgent(focus);

            game.addAgent(focus); //Add the neighbours to the game
            this.setStats
                    (
                        focus, 
                        this.neighboursMoveSpeed,
                        this.neighboursRotationSpeed, 
                        this.neighboursMass,
                        this.neighboursMaxForce
                    );
            game.getGameControl().spawn(focus, this.generateRandomPosition());

            SimpleMainBehaviour mainB = new SimpleMainBehaviour(focus);
            focus.setMainBehaviour(mainB);
        

        List<Agent> obstacles = new ArrayList<Agent>();
        obstacles.addAll(Arrays.asList(neighbours));

        //ADD OBSTACLE AVOIDANCE TO THE TARGET

        CompoundSteeringBehaviour steer = new BalancedCompoundSteeringBehaviour(agent);
        SimpleMainBehaviour targetMainB = new SimpleMainBehaviour(agent);

        SeekBehaviour seekSteer = new SeekBehaviour(agent, focus);

        ObstacleAvoidanceBehaviour obstacleAvoidance = new ObstacleAvoidanceBehaviour(agent, obstacles, 15f, 15);
        obstacleAvoidance.setupStrengthControl(10f);

        steer.addSteerBehaviour(seekSteer);
        steer.addSteerBehaviour(obstacleAvoidance);
        targetMainB.addBehaviour(steer);
        agent.setMainBehaviour(targetMainB);

        game.start();
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
