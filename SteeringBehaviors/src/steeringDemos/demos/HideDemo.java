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
import com.jme3.ai.agents.behaviors.npc.steering.HideBehavior;
import com.jme3.ai.agents.behaviors.npc.steering.PathFollowBehavior;
import com.jme3.ai.agents.util.GameEntity;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import java.util.ArrayList;
import java.util.List;
import steeringDemos.BasicDemo;
import steeringDemos.control.CustomSteerControl;

/**
 * Hide demo
 *
 * @author Jesús Martín Berlanga
 * @version 1.0.0
 */
public class HideDemo extends BasicDemo {

    private PathFollowBehavior targetPathFollow;

    public static void main(String[] args) {
        HideDemo app = new HideDemo();
        app.start();
    }

    @Override
    public void simpleInitApp() {
        this.steerControl = new CustomSteerControl(8.5f, 5);
        this.steerControl.setCameraSettings(getCamera());
        this.steerControl.setFlyCameraSettings(getFlyByCamera());

        //defining rootNode for brainsAppState processing
        brainsAppState.setApp(this);
        brainsAppState.setGameControl(this.steerControl);

        Vector3f[] spawnArea = null;

        Agent target = this.createBoid("Target", this.targetColor, 0.11f);
        brainsAppState.addAgent(target); //Add the target to the brainsAppState
        brainsAppState.getGameControl().spawn(target, new Vector3f(0, 0, -1));
        this.setStats(
                target,
                this.targetMoveSpeed,
                this.targetRotationSpeed,
                this.targetMass,
                this.targetMaxForce);

        Agent hider = this.createBoid("Seeker", this.neighboursColor, 0.11f);
        brainsAppState.addAgent(hider);
        this.setStats(
                hider,
                this.neighboursMoveSpeed,
                this.neighboursMoveSpeed,
                this.neighboursMass,
                this.neighboursMaxForce);
        brainsAppState.getGameControl().spawn(hider, spawnArea);

        Agent obstacle = this.createSphere("Obstacle", ColorRGBA.Yellow, 0.35f);
        brainsAppState.addAgent(obstacle);
        this.setStats(
                obstacle,
                this.neighboursMoveSpeed,
                this.neighboursRotationSpeed,
                this.neighboursMass,
                this.neighboursMaxForce);
        brainsAppState.getGameControl().spawn(obstacle, new Vector3f(2.5f, 2.5f, 2.5f));

        Agent farAwayObstacle = this.createSphere("Obstacle", ColorRGBA.Yellow, 0.35f);
        brainsAppState.addAgent(farAwayObstacle);
        this.setStats(
                farAwayObstacle,
                this.neighboursMoveSpeed,
                this.neighboursRotationSpeed,
                this.neighboursMass,
                this.neighboursMaxForce);
        brainsAppState.getGameControl().spawn(farAwayObstacle, new Vector3f(7.5f, 7.5f, 5f));

        List<GameEntity> obstacles = new ArrayList<GameEntity>();
        obstacles.add(obstacle);
        obstacles.add(farAwayObstacle);

        ArrayList<Vector3f> orderedPointsList = new ArrayList<Vector3f>();
        orderedPointsList.add(new Vector3f(0, 0, 0));
        orderedPointsList.add(new Vector3f(0, 0, 5));
        orderedPointsList.add(new Vector3f(5, 0, 5));
        orderedPointsList.add(new Vector3f(5, 5, 5));
        orderedPointsList.add(new Vector3f(5, 5, 0));
        orderedPointsList.add(new Vector3f(0, 5, 0));
        orderedPointsList.add(new Vector3f(0, 0, 0));

        SimpleMainBehavior targetMainBehavior = new SimpleMainBehavior(target);
        this.targetPathFollow = new PathFollowBehavior(target, orderedPointsList, 1, 1);
        targetMainBehavior.addBehavior(this.targetPathFollow);
        target.setMainBehavior(targetMainBehavior);

        obstacle.setMainBehavior(new SimpleMainBehavior(obstacle));
        farAwayObstacle.setMainBehavior(new SimpleMainBehavior(obstacle));

        SimpleMainBehavior hiderMainBehavior = new SimpleMainBehavior(hider);
        HideBehavior hide = new HideBehavior(hider, target, obstacles, 1f);
        hiderMainBehavior.addBehavior(hide);
        hider.setMainBehavior(hiderMainBehavior);

        brainsAppState.start();
    }

    @Override
    public void simpleUpdate(float tpf) {
        brainsAppState.update(tpf);

        if (!this.targetPathFollow.isActive()) {
            this.targetPathFollow.reset();
        }
    }
}