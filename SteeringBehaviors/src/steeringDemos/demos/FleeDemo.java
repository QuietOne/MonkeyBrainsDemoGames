//Copyright (c) 2014, Jesús Martín Berlanga. All rights reserved. Distributed under the BSD licence. Read "com/jme3/ai/license.txt".

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
 * @version 2.0
 */
public class FleeDemo extends BasicDemo {

    public static void main(String[] args) {
        FleeDemo app = new FleeDemo();
        app.start();
    }

    @Override
    public void simpleInitApp() 
    {
        this.steerControl = new CustomSteerControl(11, 15);
        this.steerControl.setCameraSettings(getCamera());
        this.steerControl.setFlyCameraSettings(getFlyByCamera());
        
        //defining rootNode for aiAppState processing
        aiAppState.setApp(this);
        aiAppState.setGameControl(this.steerControl);

        Vector3f[] spawnArea = null;
        this.numberNeighbours = 20;

        Agent target = this.createBoid("Target", this.targetColor, 0.1f);

        aiAppState.addAgent(target); //Add the target to the aiAppState
        aiAppState.getGameControl().spawn(target, new Vector3f
                (
                    (FastMath.nextRandomFloat() - 0.5f) * 5, 
                    (FastMath.nextRandomFloat() - 0.5f) * 5, 
                    (FastMath.nextRandomFloat() - 0.5f) * 5
                ));
        this.setStats
                (
                    target,
                    this.targetMoveSpeed,
                    this.targetRotationSpeed,
                    this.targetMass,
                    this.targetMaxForce
                );


        Agent[] neighbours = new Agent[this.numberNeighbours];

        for (int i = 0; i < this.numberNeighbours; i++) 
        {
            neighbours[i] = this.createBoid("Neighbour " + i, this.neighboursColor, 0.1f);
            aiAppState.addAgent(neighbours[i]); //Add the neighbours to the aiAppState
            this.setStats
                    (
                        neighbours[i], 
                        this.neighboursMoveSpeed,
                        this.neighboursRotationSpeed, 
                        this.neighboursMass,
                        this.neighboursMaxForce
                    );
            aiAppState.getGameControl().spawn(neighbours[i], spawnArea);
        }

        for (int i = 0; i < neighbours.length; i++) 
        {
            SimpleMainBehavior neighboursMainBehaviour = new SimpleMainBehavior(neighbours[i]);
            CompoundSteeringBehavior compound = new CompoundSteeringBehavior(neighbours[i]);
            RelativeWanderBehavior wander = new RelativeWanderBehavior
                    (
                        neighbours[i],
                        new Vector3f(-10,-10,-10), 
                        new Vector3f(10,10,10), 
                        0.25f
                    );
            wander.setupStrengthControl(0.37f);
            compound.addSteerBehavior(wander);
            compound.addSteerBehavior(new SeekBehavior(neighbours[i], target));
            neighboursMainBehaviour.addBehavior(compound);
            neighbours[i].setMainBehaviour(neighboursMainBehaviour);
        }

        SimpleMainBehavior evaderMainBehaviour = new SimpleMainBehavior(target);
        BalancedCompoundSteeringBehavior balancedCompoundSteeringBehaviour = new BalancedCompoundSteeringBehavior(target);
        
        for (int i = 0; i < neighbours.length; i++) {
            FleeBehavior flee = new FleeBehavior(target, neighbours[i]);
            balancedCompoundSteeringBehaviour.addSteerBehavior(flee);
        }
        evaderMainBehaviour.addBehavior(balancedCompoundSteeringBehaviour);
        target.setMainBehaviour(evaderMainBehaviour);

        aiAppState.start();
    }

    @Override
    public void simpleUpdate(float tpf) {
        aiAppState.update(tpf);
    }
}
