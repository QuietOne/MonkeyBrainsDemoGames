//Copyright (c) 2014, Jesús Martín Berlanga. All rights reserved. Distributed under the BSD licence. Read "com/jme3/ai/license.txt".
package steeringDemos.demos;

import com.jme3.ai.agents.Agent;
import com.jme3.ai.agents.behaviors.npc.steering.SeparationBehavior;
import com.jme3.ai.agents.behaviors.npc.SimpleMainBehavior;
import com.jme3.ai.agents.behaviors.npc.steering.AlignmentBehavior;
import com.jme3.ai.agents.behaviors.npc.steering.CohesionBehavior;
import com.jme3.ai.agents.behaviors.npc.steering.CompoundSteeringBehavior;
import com.jme3.ai.agents.behaviors.npc.steering.WanderBehavior;
import com.jme3.ai.agents.util.GameEntity;
import com.jme3.math.Vector3f;
import com.jme3.math.FastMath;
import com.jme3.math.Plane;
import steeringDemos.control.CustomSteerControl;
import steeringDemos.BasicDemo;
import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;

/**
 * Cohesion and alignment behaviour demo - 2D Version
 *
 * @author Jesús Martín Berlanga
 * @version 2.0
 */
public class CohesionAndAlignmentDemo2D extends BasicDemo {

    public static void main(String[] args) {
        CohesionAndAlignmentDemo2D app = new CohesionAndAlignmentDemo2D();
        app.start();
    }

    @Override
    public void simpleInitApp() {

        this.steerControl = new CustomSteerControl(25, 30, 0, 30);
        this.steerControl.setCameraSettings(getCamera());
        this.steerControl.setFlyCameraSettings(getFlyByCamera());

        //defining rootNode for aiAppState processing
        aiAppState.setApp(this);
        aiAppState.setGameControl(this.steerControl);

        this.numberNeighbours = 150;

        Vector3f[] spawnArea = null;
        Agent[] boids = new Agent[this.numberNeighbours];


        for (int i = 0; i < this.numberNeighbours; i++) {
            boids[i] = this.createBoid("boid " + i, this.neighboursColor, 0.1f);
            aiAppState.addAgent(boids[i]); //Add the neighbours to the aiAppState
            this.setStats(boids[i], this.neighboursMoveSpeed,
                    this.neighboursRotationSpeed, this.neighboursMass,
                    this.neighboursMaxForce);
            aiAppState.getGameControl().spawn(boids[i], spawnArea);
        }

        List<GameEntity> obstacles = new ArrayList<GameEntity>();
        obstacles.addAll(Arrays.asList(boids));

        SimpleMainBehavior[] neighboursMainBehaviour = new SimpleMainBehavior[boids.length];

        SeparationBehavior[] separation = new SeparationBehavior[boids.length];
        CohesionBehavior[] cohesion = new CohesionBehavior[boids.length];
        AlignmentBehavior[] alignment = new AlignmentBehavior[boids.length];
        WanderBehavior[] wander = new WanderBehavior[boids.length];

        for (int i = 0; i < boids.length; i++) {
            neighboursMainBehaviour[i] = new SimpleMainBehavior(boids[i]);

            separation[i] = new SeparationBehavior(boids[i], obstacles);
            cohesion[i] = new CohesionBehavior(boids[i], obstacles, 5f, FastMath.PI / 4);
            alignment[i] = new AlignmentBehavior(boids[i], obstacles, 5f, FastMath.PI / 4.2f);
            wander[i] = new WanderBehavior(boids[i]);
            wander[i].setArea(Vector3f.ZERO, new Vector3f(75, 75, 75));

            separation[i].setupStrengthControl(0.85f);
            cohesion[i].setupStrengthControl(2.15f);
            alignment[i].setupStrengthControl(0.25f);
            wander[i].setupStrengthControl(0.35f);


            CompoundSteeringBehavior steer = new CompoundSteeringBehavior(boids[i]);

            steer.addSteerBehavior(cohesion[i]);
            steer.addSteerBehavior(alignment[i]);
            steer.addSteerBehavior(separation[i]);
            steer.addSteerBehavior(wander[i]);
            steer.setupStrengthControl(new Plane(new Vector3f(0, 1, 0), 0));
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
