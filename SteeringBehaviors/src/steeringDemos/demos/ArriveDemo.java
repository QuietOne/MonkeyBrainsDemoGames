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

import steeringDemos.control.CustomSteerControl;
import steeringDemos.BasicDemo;
import com.jme3.ai.agents.Agent;
import com.jme3.ai.agents.behaviors.npc.SimpleMainBehavior;
import com.jme3.ai.agents.behaviors.npc.steering.ArriveBehavior;
import com.jme3.ai.agents.behaviors.npc.steering.BalancedCompoundSteeringBehavior;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;

/**
 * Demo for ArriveBehavior
 *
 * @author Jesús Martín Berlanga
 * @version 2.0.0
 */
public class ArriveDemo extends BasicDemo {

    private Agent[] agents = new Agent[3];
    private Agent target;
    private ArriveBehavior[] arrive;

    public static void main(String[] args) {
        ArriveDemo app = new ArriveDemo();
        app.start();
    }

    @Override
    public void simpleInitApp() {

        this.steerControl = new CustomSteerControl(7, 50);
        this.steerControl.setCameraSettings(getCamera());
        this.steerControl.setFlyCameraSettings(getFlyByCamera());

        brainsAppState.setApp(this);
        brainsAppState.setGameControl(this.steerControl);

        this.target = this.createSphere("target", ColorRGBA.Red, 0.25f);
        brainsAppState.addAgent(target);
        brainsAppState.getGameControl().spawn(target, new Vector3f());

        for (int i = 0; i < this.agents.length; i++) {
            agents[i] = this.createBoid("boid " + i, this.neighboursColor, 0.1f);
            brainsAppState.addAgent(agents[i]); //Add the neighbours to the brainsAppState
            this.setStats(agents[i], this.neighboursMoveSpeed,
                    this.neighboursRotationSpeed, this.neighboursMass,
                    this.neighboursMaxForce);
        }

        brainsAppState.getGameControl().spawn(agents[0], new Vector3f(-10, 0, 0));
        brainsAppState.getGameControl().spawn(agents[1], new Vector3f(8, 0, 0));
        brainsAppState.getGameControl().spawn(agents[2], new Vector3f(0, 50, 2));

        arrive = new ArriveBehavior[3];

        SimpleMainBehavior main0 = new SimpleMainBehavior(agents[0]);
        SimpleMainBehavior main1 = new SimpleMainBehavior(agents[1]);
        SimpleMainBehavior main2 = new SimpleMainBehavior(agents[2]);

        arrive[0] = new ArriveBehavior(agents[0], target);
        arrive[1] = new ArriveBehavior(agents[1], target);
        arrive[2] = new ArriveBehavior(agents[2], target);

        BalancedCompoundSteeringBehavior steer1 = new BalancedCompoundSteeringBehavior(agents[1]);
        steer1.addSteerBehavior(arrive[1]);

        main0.addBehavior(arrive[0]);
        main1.addBehavior(steer1);
        main2.addBehavior(arrive[2]);

        agents[0].setMainBehavior(main0);
        agents[1].setMainBehavior(main1);
        agents[2].setMainBehavior(main2);

        SimpleMainBehavior targetMain = new SimpleMainBehavior(target);
        target.setMainBehavior(targetMain);

        brainsAppState.start();
    }

    @Override
    public void simpleUpdate(float tpf) {
        brainsAppState.update(tpf);

        for (int i = 0; i < this.arrive.length; i++) {
            if (this.agents[i].distanceRelativeToGameEntity(target) <= 0.001f + target.getRadius()) {
                Vector3f resetPosition = null;

                switch (i) {
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