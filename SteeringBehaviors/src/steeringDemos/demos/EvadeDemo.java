//Copyright (c) 2014, Jesús Martín Berlanga. All rights reserved. Distributed under the BSD licence. Read "com/jme3/ai/license.txt".

package steeringDemos.demos;

import com.jme3.ai.agents.Agent;
import com.jme3.ai.agents.behaviours.npc.steering.BalancedCompoundSteeringBehaviour;
import com.jme3.ai.agents.behaviours.npc.steering.SeekBehaviour;
import com.jme3.ai.agents.behaviours.npc.SimpleMainBehaviour;
import com.jme3.ai.agents.behaviours.npc.steering.CompoundSteeringBehaviour;
import com.jme3.ai.agents.behaviours.npc.steering.EvadeBehaviour;
import com.jme3.ai.agents.behaviours.npc.steering.RelativeWanderBehaviour;

import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;

import steeringDemos.BasicDemo;
import steeringDemos.control.CustomSteerControl;

/**
 * Evade demo - Similar to the Flee Demo but using evade
 *
 * @author Jesús Martín Berlanga
 * @version 2.0
 */
public class EvadeDemo extends BasicDemo {

    public static void main(String[] args) {
        EvadeDemo app = new EvadeDemo();
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
            SimpleMainBehaviour neighboursMainBehaviour = new SimpleMainBehaviour(neighbours[i]);
            CompoundSteeringBehaviour compound = new CompoundSteeringBehaviour(neighbours[i]);
            RelativeWanderBehaviour wander = new RelativeWanderBehaviour
                    (
                        neighbours[i],
                        new Vector3f(-10,-10,-10), 
                        new Vector3f(10,10,10), 
                        0.25f
                    );
            wander.setupStrengthControl(0.37f);
            compound.addSteerBehaviour(wander);
            compound.addSteerBehaviour(new SeekBehaviour(neighbours[i], target));
            neighboursMainBehaviour.addBehaviour(compound);
            neighbours[i].setMainBehaviour(neighboursMainBehaviour);
        }

        SimpleMainBehaviour evaderMainBehaviour = new SimpleMainBehaviour(target);
        BalancedCompoundSteeringBehaviour balancedCompoundSteeringBehaviour = new BalancedCompoundSteeringBehaviour(target);
        
        for (int i = 0; i < neighbours.length; i++) {
            EvadeBehaviour evadeBehavior = new EvadeBehaviour(target, neighbours[i]);
            balancedCompoundSteeringBehaviour.addSteerBehaviour(evadeBehavior);
        }
        evaderMainBehaviour.addBehaviour(balancedCompoundSteeringBehaviour);
        target.setMainBehaviour(evaderMainBehaviour);

        aiAppState.start();
    }

    @Override
    public void simpleUpdate(float tpf) {
        aiAppState.update(tpf);
    }
}
