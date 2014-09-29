//Copyright (c) 2014, Jesús Martín Berlanga. All rights reserved. Distributed under the BSD licence. Read "com/jme3/ai/license.txt".

package steeringDemos.demos;

import steeringDemos.BasicDemo;
import steeringDemos.control.CustomSteerControl;

import com.jme3.ai.agents.Agent;
import com.jme3.ai.agents.behaviours.npc.steering.SeparationBehaviour;
import com.jme3.ai.agents.behaviours.npc.SimpleMainBehaviour;
import com.jme3.ai.agents.behaviours.npc.steering.AlignmentBehaviour;
import com.jme3.ai.agents.behaviours.npc.steering.CompoundSteeringBehaviour;
import com.jme3.ai.agents.behaviours.npc.steering.WanderBehaviour;

import com.jme3.math.Vector3f;
import com.jme3.math.FastMath;

import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;

/**
 * Alignment Demo
 *
 * @author Jesús Martín Berlanga
 * @version 2.0
 */
public class AlignmentDemo extends BasicDemo {

    public static void main(String[] args) {
        AlignmentDemo app = new AlignmentDemo();
        app.start();
    }

    @Override
    public void simpleInitApp() {
        
        this.steerControl = new CustomSteerControl(25, 30);
        this.steerControl.setCameraSettings(getCamera());
        this.steerControl.setFlyCameraSettings(getFlyByCamera());
        
        //defining rootNode for game processing
        game.setApp(this);
        this.numberNeighbours = 150;
        game.setGameControl(this.steerControl);

        Vector3f[] spawnArea = null;

        Agent[] boids = new Agent[this.numberNeighbours];

        for (int i = 0; i < this.numberNeighbours; i++) 
        {
            boids[i] = this.createBoid("boid " + i, this.neighboursColor, 0.1f);
            game.addAgent(boids[i]); //Add the neighbours to the game
            this.setStats
                    (
                        boids[i], 
                        this.neighboursMoveSpeed,
                        this.neighboursRotationSpeed, 
                        this.neighboursMass,
                        this.neighboursMaxForce
                    );
            game.getGameControl().spawn(boids[i], spawnArea);
        }

        List<Agent> obstacles = new ArrayList<Agent>();
        obstacles.addAll(Arrays.asList(boids));

        SimpleMainBehaviour[] neighboursMainBehaviour = new SimpleMainBehaviour[boids.length];

        SeparationBehaviour[] separation = new SeparationBehaviour[boids.length];
        AlignmentBehaviour[] alignment = new AlignmentBehaviour[boids.length];
        WanderBehaviour[] wander = new WanderBehaviour[boids.length];

        for (int i = 0; i < boids.length; i++) 
        {
            neighboursMainBehaviour[i] = new SimpleMainBehaviour(boids[i]);

            separation[i] = new SeparationBehaviour(boids[i], obstacles);
            alignment[i] = new AlignmentBehaviour(boids[i], obstacles, 5f,  FastMath.PI / 4);
            wander[i] = new WanderBehaviour(boids[i]);
            wander[i].setArea(Vector3f.ZERO, new Vector3f(75, 75, 75));

            separation[i].setupStrengthControl(0.85f);
            alignment[i].setupStrengthControl(2.15f);
            wander[i].setupStrengthControl(0.35f);
            

            CompoundSteeringBehaviour steer = new CompoundSteeringBehaviour(boids[i]);

           steer.addSteerBehaviour(alignment[i]);
           steer.addSteerBehaviour(separation[i]);
           steer.addSteerBehaviour(wander[i]);
           neighboursMainBehaviour[i].addBehaviour(steer);
            
           boids[i].setMainBehaviour(neighboursMainBehaviour[i]);   
        }
        
        game.start();
    }

    @Override
    public void simpleUpdate(float tpf) {
        game.update(tpf);
    }
}