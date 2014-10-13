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
import com.jme3.ai.agents.behaviors.npc.SimpleMainBehavior;
import com.jme3.ai.agents.behaviors.npc.steering.RelativeWanderBehavior;
import com.jme3.math.Vector3f;
import steeringDemos.BasicDemo;
import steeringDemos.control.CustomSteerControl;

/**
 * Relative Wander Demo
 *
 * @author Jesús Martín Berlanga
 * @version 1.0.0
 */
public class RelativeWanderDemo extends BasicDemo {

    public static void main(String[] args) {
        RelativeWanderDemo app = new RelativeWanderDemo();
        app.start();
    }

    @Override
    public void simpleInitApp() {
        this.steerControl = new CustomSteerControl(9, 1);
        this.steerControl.setCameraSettings(getCamera());
        this.steerControl.setFlyCameraSettings(getFlyByCamera());

        //defining rootNode for brainsAppState processing
        brainsAppState.setApp(this);
        brainsAppState.setGameControl(new CustomSteerControl(5f));

        Agent target = this.createBoid("Target", this.targetColor, 0.11f);

        brainsAppState.addAgent(target); //Add the target to the brainsAppState
        brainsAppState.getGameControl().spawn(target, Vector3f.ZERO);
        this.setStats(
                target,
                this.targetMoveSpeed,
                this.targetRotationSpeed,
                this.targetMass,
                this.targetMaxForce);

        SimpleMainBehavior targetMainBehavior = new SimpleMainBehavior(target);
        RelativeWanderBehavior targetMoveBehavior = new RelativeWanderBehavior(target, new Vector3f(-5, -5, -5), new Vector3f(5, 5, 5), 0.2f);
        targetMainBehavior.addBehavior(targetMoveBehavior);
        target.setMainBehavior(targetMainBehavior);

        brainsAppState.start();
    }

    @Override
    public void simpleUpdate(float tpf) {
        brainsAppState.update(tpf);
    }
}
