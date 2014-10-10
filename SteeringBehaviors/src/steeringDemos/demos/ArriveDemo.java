//Copyright (c) 2014, Jesús Martín Berlanga. All rights reserved.
//Distributed under the BSD licence. Read "com/jme3/ai/license.txt".
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
 * @version 2.0
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

        aiAppState.setApp(this);
        aiAppState.setGameControl(this.steerControl);


        this.target = this.createSphere("target", ColorRGBA.Red, 0.25f);
        aiAppState.addAgent(target);
        aiAppState.getGameControl().spawn(target, new Vector3f());

        for (int i = 0; i < this.agents.length; i++) {
            agents[i] = this.createBoid("boid " + i, this.neighboursColor, 0.1f);
            aiAppState.addAgent(agents[i]); //Add the neighbours to the aiAppState
            this.setStats(agents[i], this.neighboursMoveSpeed,
                    this.neighboursRotationSpeed, this.neighboursMass,
                    this.neighboursMaxForce);
        }

        aiAppState.getGameControl().spawn(agents[0], new Vector3f(-10, 0, 0));
        aiAppState.getGameControl().spawn(agents[1], new Vector3f(8, 0, 0));
        aiAppState.getGameControl().spawn(agents[2], new Vector3f(0, 50, 2));

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

        agents[0].setMainBehaviour(main0);
        agents[1].setMainBehaviour(main1);
        agents[2].setMainBehaviour(main2);

        SimpleMainBehavior targetMain = new SimpleMainBehavior(target);
        target.setMainBehaviour(targetMain);

        aiAppState.start();
    }

    @Override
    public void simpleUpdate(float tpf) {
        aiAppState.update(tpf);

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
