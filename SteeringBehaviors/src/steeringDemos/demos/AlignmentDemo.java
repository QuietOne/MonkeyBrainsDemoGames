/**
 * Copyright (c) 2014, jMonkeyEngine All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * Neither the name of 'jMonkeyEngine' nor the names of its contributors may be
 * used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
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
 * @version 2.0.0
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

        //defining rootNode for brainsAppState processing
        brainsAppState.setApp(this);
        this.numberNeighbours = 150;
        brainsAppState.setGameControl(this.steerControl);

        Vector3f[] spawnArea = null;

        Agent[] boids = new Agent[this.numberNeighbours];

        for (int i = 0; i < this.numberNeighbours; i++) {
            boids[i] = this.createBoid("boid " + i, this.neighboursColor, 0.1f);
            brainsAppState.addAgent(boids[i]); //Add the neighbours to the brainsAppState
            this.setStats(
                    boids[i],
                    this.neighboursMoveSpeed,
                    this.neighboursRotationSpeed,
                    this.neighboursMass,
                    this.neighboursMaxForce);
            brainsAppState.getGameControl().spawn(boids[i], spawnArea);
        }

        List<GameEntity> obstacles = new ArrayList<GameEntity>();
        obstacles.addAll(Arrays.asList(boids));

        SimpleMainBehavior[] neighboursMainBehavior = new SimpleMainBehavior[boids.length];

        SeparationBehavior[] separation = new SeparationBehavior[boids.length];
        AlignmentBehavior[] alignment = new AlignmentBehavior[boids.length];
        WanderBehavior[] wander = new WanderBehavior[boids.length];

        for (int i = 0; i < boids.length; i++) {
            neighboursMainBehavior[i] = new SimpleMainBehavior(boids[i]);

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
            neighboursMainBehavior[i].addBehavior(steer);

            boids[i].setMainBehavior(neighboursMainBehavior[i]);
        }

        brainsAppState.start();
    }

    @Override
    public void simpleUpdate(float tpf) {
        brainsAppState.update(tpf);
    }
}