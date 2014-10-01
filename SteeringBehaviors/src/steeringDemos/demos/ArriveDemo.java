//Copyright (c) 2014, Jesús Martín Berlanga. All rights reserved.
//Distributed under the BSD licence. Read "com/jme3/ai/license.txt".
package steeringDemos.demos;

import steeringDemos.control.CustomSteerControl;
import steeringDemos.BasicDemo;

import com.jme3.ai.agents.Agent;
import com.jme3.ai.agents.behaviours.npc.SimpleMainBehaviour;
import com.jme3.ai.agents.behaviours.npc.steering.ArriveBehaviour;
import com.jme3.ai.agents.behaviours.npc.steering.BalancedCompoundSteeringBehaviour;

import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;

/**
 * Demo for ArriveBehaviour
 *
 * @author Jesús Martín Berlanga
 * @version 2.0
 */
public class ArriveDemo extends BasicDemo {

    private Agent[] agents = new Agent[3];
    private Agent target;
    private ArriveBehaviour[] arrive;

    public static void main(String[] args) {
        ArriveDemo app = new ArriveDemo();
        app.start();
    }

    @Override
    public void simpleInitApp() {

        this.steerControl = new CustomSteerControl(7, 50);
        this.steerControl.setCameraSettings(getCamera());
        this.steerControl.setFlyCameraSettings(getFlyByCamera());

        aiAppState.setApp(this);
        aiAppState.setAIControl(this.steerControl);


        this.target = this.createSphere("target", ColorRGBA.Red, 0.25f);
        aiAppState.addAgent(target);
        aiAppState.getAIControl().spawn(target, new Vector3f());

        for (int i = 0; i < this.agents.length; i++) {
            agents[i] = this.createBoid("boid " + i, this.neighboursColor, 0.1f);
            aiAppState.addAgent(agents[i]); //Add the neighbours to the aiAppState
            this.setStats(agents[i], this.neighboursMoveSpeed,
                    this.neighboursRotationSpeed, this.neighboursMass,
                    this.neighboursMaxForce);
        }

        aiAppState.getAIControl().spawn(agents[0], new Vector3f(-10, 0, 0));
        aiAppState.getAIControl().spawn(agents[1], new Vector3f(8, 0, 0));
        aiAppState.getAIControl().spawn(agents[2], new Vector3f(0, 50, 2));

        arrive = new ArriveBehaviour[3];

        SimpleMainBehaviour main0 = new SimpleMainBehaviour(agents[0]);
        SimpleMainBehaviour main1 = new SimpleMainBehaviour(agents[1]);
        SimpleMainBehaviour main2 = new SimpleMainBehaviour(agents[2]);

        arrive[0] = new ArriveBehaviour(agents[0], target);
        arrive[1] = new ArriveBehaviour(agents[1], target);
        arrive[2] = new ArriveBehaviour(agents[2], target);

        BalancedCompoundSteeringBehaviour steer1 = new BalancedCompoundSteeringBehaviour(agents[1]);
        steer1.addSteerBehaviour(arrive[1]);

        main0.addBehaviour(arrive[0]);
        main1.addBehaviour(steer1);
        main2.addBehaviour(arrive[2]);

        agents[0].setMainBehaviour(main0);
        agents[1].setMainBehaviour(main1);
        agents[2].setMainBehaviour(main2);

        SimpleMainBehaviour targetMain = new SimpleMainBehaviour(target);
        target.setMainBehaviour(targetMain);

        aiAppState.start();
    }

    @Override
    public void simpleUpdate(float tpf) {
        aiAppState.update(tpf);

        for (int i = 0; i < this.arrive.length; i++) {
            if (this.agents[i].distanceRelativeToGameObject(target) <= 0.001f + target.getRadius()) {
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
