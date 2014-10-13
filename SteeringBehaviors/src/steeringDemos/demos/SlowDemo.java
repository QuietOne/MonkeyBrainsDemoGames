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
import com.jme3.ai.agents.behaviors.npc.steering.CompoundSteeringBehavior;
import com.jme3.ai.agents.behaviors.npc.steering.MoveBehavior;
import com.jme3.ai.agents.behaviors.npc.steering.SlowBehavior;
import com.jme3.math.Vector3f;
import java.awt.event.ActionEvent;
import javax.swing.Timer;
import steeringDemos.BasicDemo;
import steeringDemos.control.CustomSteerControl;

/**
 * Slow demo
 *
 * @author Jesús Martín Berlanga
 * @version 2.0.1
 */
public class SlowDemo extends BasicDemo {

    private SlowBehavior slow;
    private SlowBehavior slow2;
    private java.awt.event.ActionListener resetSlows = new java.awt.event.ActionListener() {
        public void actionPerformed(ActionEvent event) {
            slow.reset();
            slow2.reset();
        }
    };
    private Timer iterationTimer;

    public static void main(String[] args) {
        SlowDemo app = new SlowDemo();
        app.start();
    }

    @Override
    public void simpleInitApp() {
        this.steerControl = new CustomSteerControl(15f, 5);
        this.steerControl.setCameraSettings(getCamera());
        this.steerControl.setFlyCameraSettings(getFlyByCamera());

        brainsAppState.setApp(this);
        brainsAppState.setGameControl(this.steerControl);

        Agent agent = this.createBoid("boid ", this.neighboursColor, 0.1f);
        brainsAppState.addAgent(agent); //Add the neighbours to the brainsAppState
        this.neighboursMoveSpeed *= 3;
        this.setStats(
                agent,
                this.neighboursMoveSpeed,
                this.neighboursRotationSpeed,
                this.neighboursMass,
                this.neighboursMaxForce);

        brainsAppState.getGameControl().spawn(agent, new Vector3f());

        this.iterationTimer = new Timer(6000, this.resetSlows);
        this.iterationTimer.start();

        SimpleMainBehavior main = new SimpleMainBehavior(agent);
        MoveBehavior move = new MoveBehavior(agent);
        slow = new SlowBehavior(agent, 250, 0.0625f);
        slow2 = new SlowBehavior(agent, 250, 0.0625f);

        move.setMoveDirection(new Vector3f(1, 0, 0));
        CompoundSteeringBehavior steer = new CompoundSteeringBehavior(agent);

        steer.addSteerBehavior(move);
        steer.addSteerBehavior(slow);
        steer.addSteerBehavior(slow2);

        //nested container test
        CompoundSteeringBehavior steer2 = new CompoundSteeringBehavior(agent);
        steer2.addSteerBehavior(steer);

        main.addBehavior(steer2);
        agent.setMainBehavior(main);

        slow.setAcive(true);
        slow2.setAcive(true);

        brainsAppState.start();
    }

    @Override
    public void simpleUpdate(float tpf) {
        brainsAppState.update(tpf);
    }
}