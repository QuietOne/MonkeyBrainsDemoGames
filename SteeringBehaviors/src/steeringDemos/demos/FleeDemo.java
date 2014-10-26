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

import com.jme3.ai.agents.Agent;
import com.jme3.ai.agents.behaviors.npc.steering.BalancedCompoundSteeringBehavior;
import com.jme3.ai.agents.behaviors.npc.steering.SeekBehavior;
import com.jme3.ai.agents.behaviors.npc.SimpleMainBehavior;
import com.jme3.ai.agents.behaviors.npc.steering.CompoundSteeringBehavior;
import com.jme3.ai.agents.behaviors.npc.steering.FleeBehavior;
import com.jme3.ai.agents.behaviors.npc.steering.RelativeWanderBehavior;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import steeringDemos.BasicDemo;
import steeringDemos.control.CustomSteerControl;

/**
 * Flee demo
 *
 * @author Jesús Martín Berlanga
 * @version 2.0.1
 */
public class FleeDemo extends BasicDemo {

    public static void main(String[] args) {
        FleeDemo app = new FleeDemo();
        app.start();
    }

    @Override
    public void simpleInitApp() {
        this.steerControl = new CustomSteerControl(11, 15);
        this.steerControl.setCameraSettings(getCamera());
        this.steerControl.setFlyCameraSettings(getFlyByCamera());

        //defining rootNode for brainsAppState processing
        brainsAppState.setApp(this);
        brainsAppState.setGameControl(this.steerControl);

        Vector3f[] spawnArea = null;
        this.numberNeighbours = 20;

        Agent target = this.createBoid("Target", this.targetColor, 0.1f);

        brainsAppState.addAgent(target); //Add the target to the brainsAppState
        brainsAppState.getGameControl().spawn(target, new Vector3f(
                (FastMath.nextRandomFloat() - 0.5f) * 5,
                (FastMath.nextRandomFloat() - 0.5f) * 5,
                (FastMath.nextRandomFloat() - 0.5f) * 5));
        this.setStats(
                target,
                this.targetMoveSpeed,
                this.targetRotationSpeed,
                this.targetMass,
                this.targetMaxForce);


        Agent[] neighbours = new Agent[this.numberNeighbours];

        for (int i = 0; i < this.numberNeighbours; i++) {
            neighbours[i] = this.createBoid("Neighbour " + i, this.neighboursColor, 0.1f);
            brainsAppState.addAgent(neighbours[i]); //Add the neighbours to the brainsAppState
            this.setStats(
                    neighbours[i],
                    this.neighboursMoveSpeed,
                    this.neighboursRotationSpeed,
                    this.neighboursMass,
                    this.neighboursMaxForce);
            brainsAppState.getGameControl().spawn(neighbours[i], spawnArea);
        }

        for (int i = 0; i < neighbours.length; i++) {
            SimpleMainBehavior neighboursMainBehavior = new SimpleMainBehavior(neighbours[i]);
            CompoundSteeringBehavior compound = new CompoundSteeringBehavior(neighbours[i]);
            RelativeWanderBehavior wander = new RelativeWanderBehavior(
                    neighbours[i],
                    10,
                    10,
                    10,
                    0.25f);
            wander.setupStrengthControl(0.37f);
            compound.addSteerBehavior(wander);
            compound.addSteerBehavior(new SeekBehavior(neighbours[i], target));
            neighboursMainBehavior.addBehavior(compound);
            neighbours[i].setMainBehavior(neighboursMainBehavior);
        }

        SimpleMainBehavior evaderMainBehavior = new SimpleMainBehavior(target);
        BalancedCompoundSteeringBehavior balancedCompoundSteeringBehavior = new BalancedCompoundSteeringBehavior(target);

        for (int i = 0; i < neighbours.length; i++) {
            FleeBehavior flee = new FleeBehavior(target, neighbours[i]);
            balancedCompoundSteeringBehavior.addSteerBehavior(flee);
        }
        evaderMainBehavior.addBehavior(balancedCompoundSteeringBehavior);
        target.setMainBehavior(evaderMainBehavior);

        brainsAppState.start();
    }

    @Override
    public void simpleUpdate(float tpf) {
        brainsAppState.update(tpf);
    }
}
