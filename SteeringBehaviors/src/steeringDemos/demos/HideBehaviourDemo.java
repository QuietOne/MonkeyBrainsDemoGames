//Copyright (c) 2014, Jesús Martín Berlanga. All rights reserved. Distributed under the BSD licence. Read "com/jme3/ai/license.txt".
package steeringDemos.demos;

import com.jme3.ai.agents.Agent;
import com.jme3.ai.agents.behaviours.npc.SimpleMainBehaviour;
import com.jme3.ai.agents.behaviours.npc.steering.HideBehaviour;
import com.jme3.ai.agents.behaviours.npc.steering.PathFollowBehaviour;
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
 * @version 1.0
 */
public class HideBehaviourDemo extends BasicDemo {

    PathFollowBehaviour targetPathFollow;

    public static void main(String[] args) {
        HideBehaviourDemo app = new HideBehaviourDemo();
        app.start();
    }

    @Override
    public void simpleInitApp() {
        this.steerControl = new CustomSteerControl(8.5f, 5);
        this.steerControl.setCameraSettings(getCamera());
        this.steerControl.setFlyCameraSettings(getFlyByCamera());

        //defining rootNode for aiAppState processing
        aiAppState.setApp(this);
        aiAppState.setGameControl(this.steerControl);

        Vector3f[] spawnArea = null;

        Agent target = this.createBoid("Target", this.targetColor, 0.11f);
        aiAppState.addAgent(target); //Add the target to the aiAppState
        aiAppState.getGameControl().spawn(target, new Vector3f(0, 0, -1));
        this.setStats(
                target,
                this.targetMoveSpeed,
                this.targetRotationSpeed,
                this.targetMass,
                this.targetMaxForce);

        Agent hider = this.createBoid("Seeker", this.neighboursColor, 0.11f);
        aiAppState.addAgent(hider);
        this.setStats(
                hider,
                this.neighboursMoveSpeed,
                this.neighboursMoveSpeed,
                this.neighboursMass,
                this.neighboursMaxForce);
        aiAppState.getGameControl().spawn(hider, spawnArea);

        Agent obstacle = this.createSphere("Obstacle", ColorRGBA.Yellow, 0.35f);
        aiAppState.addAgent(obstacle);
        this.setStats(
                obstacle,
                this.neighboursMoveSpeed,
                this.neighboursRotationSpeed,
                this.neighboursMass,
                this.neighboursMaxForce);
        aiAppState.getGameControl().spawn(obstacle, new Vector3f(2.5f, 2.5f, 2.5f));

        Agent farAwayObstacle = this.createSphere("Obstacle", ColorRGBA.Yellow, 0.35f);
        aiAppState.addAgent(farAwayObstacle);
        this.setStats(
                farAwayObstacle,
                this.neighboursMoveSpeed,
                this.neighboursRotationSpeed,
                this.neighboursMass,
                this.neighboursMaxForce);
        aiAppState.getGameControl().spawn(farAwayObstacle, new Vector3f(7.5f, 7.5f, 5f));

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

        SimpleMainBehaviour targetMainBehaviour = new SimpleMainBehaviour(target);
        this.targetPathFollow = new PathFollowBehaviour(target, orderedPointsList, 1, 1);
        targetMainBehaviour.addBehaviour(this.targetPathFollow);
        target.setMainBehaviour(targetMainBehaviour);

        obstacle.setMainBehaviour(new SimpleMainBehaviour(obstacle));
        farAwayObstacle.setMainBehaviour(new SimpleMainBehaviour(obstacle));

        SimpleMainBehaviour hiderMainBehaviour = new SimpleMainBehaviour(hider);
        HideBehaviour hide = new HideBehaviour(hider, target, obstacles, 1f);
        hiderMainBehaviour.addBehaviour(hide);
        hider.setMainBehaviour(hiderMainBehaviour);

        aiAppState.start();
    }

    @Override
    public void simpleUpdate(float tpf) {
        aiAppState.update(tpf);

        if (!this.targetPathFollow.isActive()) {
            this.targetPathFollow.reset();
        }
    }
}
