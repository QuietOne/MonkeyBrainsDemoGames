//Copyright (c) 2014, Jesús Martín Berlanga. All rights reserved.
//Distributed under the BSD licence. Read "com/jme3/ai/license.txt".
package steeringDemos.demos;

import steeringDemos.BasicDemo;
import steeringDemos.control.CustomSteerControl;

import com.jme3.ai.agents.Agent;
import com.jme3.ai.agents.behaviors.npc.steering.SeparationBehavior;
import com.jme3.ai.agents.behaviors.npc.SimpleMainBehavior;
import com.jme3.ai.agents.behaviors.npc.steering.AlignmentBehavior;
import com.jme3.ai.agents.behaviors.npc.steering.CompoundSteeringBehavior;
import com.jme3.ai.agents.behaviors.npc.steering.WanderBehavior;
import com.jme3.ai.agents.util.GameEntity;

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

        //defining rootNode for aiAppState processing
        aiAppState.setApp(this);
        this.numberNeighbours = 150;
        aiAppState.setGameControl(this.steerControl);

        Vector3f[] spawnArea = null;

        Agent[] boids = new Agent[this.numberNeighbours];

        for (int i = 0; i < this.numberNeighbours; i++) {
            boids[i] = this.createBoid("boid " + i, this.neighboursColor, 0.1f);
            aiAppState.addAgent(boids[i]); //Add the neighbours to the aiAppState
            this.setStats(
                    boids[i],
                    this.neighboursMoveSpeed,
                    this.neighboursRotationSpeed,
                    this.neighboursMass,
                    this.neighboursMaxForce);
            aiAppState.getGameControl().spawn(boids[i], spawnArea);
        }

        List<GameEntity> obstacles = new ArrayList<GameEntity>();
        obstacles.addAll(Arrays.asList(boids));

        SimpleMainBehavior[] neighboursMainBehaviour = new SimpleMainBehavior[boids.length];

        SeparationBehavior[] separation = new SeparationBehavior[boids.length];
        AlignmentBehavior[] alignment = new AlignmentBehavior[boids.length];
        WanderBehavior[] wander = new WanderBehavior[boids.length];

        for (int i = 0; i < boids.length; i++) {
            neighboursMainBehaviour[i] = new SimpleMainBehavior(boids[i]);

            separation[i] = new SeparationBehavior(boids[i], obstacles);
            alignment[i] = new AlignmentBehavior(boids[i], obstacles, 5f, FastMath.PI / 4);
            wander[i] = new WanderBehavior(boids[i]);
            wander[i].setArea(Vector3f.ZERO, new Vector3f(75, 75, 75));

            separation[i].setupStrengthControl(0.85f);
            alignment[i].setupStrengthControl(2.15f);
            wander[i].setupStrengthControl(0.35f);


            CompoundSteeringBehavior steer = new CompoundSteeringBehavior(boids[i]);

            steer.addSteerBehavior(alignment[i]);
            steer.addSteerBehavior(separation[i]);
            steer.addSteerBehavior(wander[i]);
            neighboursMainBehaviour[i].addBehavior(steer);

            boids[i].setMainBehaviour(neighboursMainBehaviour[i]);
        }

        aiAppState.start();
    }

    @Override
    public void simpleUpdate(float tpf) {
        aiAppState.update(tpf);
    }
}