//Copyright (c) 2014, Jesús Martín Berlanga. All rights reserved.
//Distributed under the BSD licence. Read "com/jme3/ai/license.txt".
package steeringDemos.demos;

import com.jme3.ai.agents.Agent;
import com.jme3.ai.agents.behaviours.npc.steering.SeparationBehaviour;
import com.jme3.ai.agents.behaviours.npc.SimpleMainBehaviour;
import com.jme3.ai.agents.behaviours.npc.steering.CohesionBehaviour;
import com.jme3.ai.agents.behaviours.npc.steering.CompoundSteeringBehaviour;
import com.jme3.ai.agents.behaviours.npc.steering.WanderBehaviour;
import com.jme3.ai.agents.util.GameEntity;
import com.jme3.math.Vector3f;
import com.jme3.math.FastMath;
import steeringDemos.control.CustomSteerControl;
import steeringDemos.BasicDemo;
import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;

/**
 * Cohesion behaviour demo
 *
 * @author Jesús Martín Berlanga
 * @version 2.0
 */
public class CohesionDemo extends BasicDemo {

    public static void main(String[] args) {
        CohesionDemo app = new CohesionDemo();
        app.start();
    }

    @Override
    public void simpleInitApp() {
        this.steerControl = new CustomSteerControl(25, 40);
        this.steerControl.setCameraSettings(getCamera());
        this.steerControl.setFlyCameraSettings(getFlyByCamera());

        //defining rootNode for aiAppState processing
        aiAppState.setApp(this);
        aiAppState.setAIControl(this.steerControl);

        this.numberNeighbours = 150;
        Vector3f[] spawnArea = null;
        Agent[] boids = new Agent[this.numberNeighbours];

        for (int i = 0; i < this.numberNeighbours; i++) {
            boids[i] = this.createBoid("boid " + i, this.neighboursColor, 0.1f);
            aiAppState.addAgent(boids[i]); //Add the neighbours to the aiAppState
            this.setStats(boids[i], this.neighboursMoveSpeed,
                    this.neighboursRotationSpeed, this.neighboursMass,
                    this.neighboursMaxForce);
            aiAppState.getAIControl().spawn(boids[i], spawnArea);
        }

        List<GameEntity> obstacles = new ArrayList<GameEntity>();
        obstacles.addAll(Arrays.asList(boids));

        SimpleMainBehaviour[] neighboursMainBehaviour = new SimpleMainBehaviour[boids.length];

        SeparationBehaviour[] separation = new SeparationBehaviour[boids.length];
        CohesionBehaviour[] cohesion = new CohesionBehaviour[boids.length];
        WanderBehaviour[] wander = new WanderBehaviour[boids.length];

        for (int i = 0; i < boids.length; i++) {
            neighboursMainBehaviour[i] = new SimpleMainBehaviour(boids[i]);

            separation[i] = new SeparationBehaviour(boids[i], obstacles);
            cohesion[i] = new CohesionBehaviour(boids[i], obstacles, 5f, FastMath.PI / 4);
            wander[i] = new WanderBehaviour(boids[i]);
            wander[i].setArea(Vector3f.ZERO, new Vector3f(75, 75, 75));

            separation[i].setupStrengthControl(0.85f);
            cohesion[i].setupStrengthControl(2.15f);
            wander[i].setupStrengthControl(0.35f);


            CompoundSteeringBehaviour steer = new CompoundSteeringBehaviour(boids[i]);

            steer.addSteerBehaviour(cohesion[i]);
            steer.addSteerBehaviour(separation[i]);
            steer.addSteerBehaviour(wander[i]);
            neighboursMainBehaviour[i].addBehaviour(steer);

            boids[i].setMainBehaviour(neighboursMainBehaviour[i]);

        }

        aiAppState.start();
    }

    @Override
    public void simpleUpdate(float tpf) {
        aiAppState.update(tpf);
    }
}
