//Copyright (c) 2014, Jesús Martín Berlanga. All rights reserved. Distributed under the BSD licence. Read "com/jme3/ai/license.txt".
package steeringDemos.demos;

import com.jme3.ai.agents.Agent;
import com.jme3.ai.agents.behaviors.npc.SimpleMainBehavior;
import com.jme3.ai.agents.behaviors.npc.steering.CompoundSteeringBehavior;
import com.jme3.ai.agents.behaviors.npc.steering.MoveBehavior;
import com.jme3.ai.agents.behaviors.npc.steering.SeekBehavior;
import com.jme3.ai.agents.behaviors.npc.steering.UnalignedCollisionAvoidanceBehavior;
import com.jme3.ai.agents.util.GameEntity;

import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.math.FastMath;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import javax.swing.Timer;

import steeringDemos.BasicDemo;
import steeringDemos.control.CustomSteerControl;

/**
 * Unaligned avoidance demo
 *
 * @author Jesús Martín Berlanga
 * @version 2.0
 */
public class UnalignedAvoidanceDemo extends BasicDemo {

    private Agent agent;
    private Agent focus;
    private boolean positiveXSide = true;
    //ObstaclesMove
    MoveBehavior obstsaclesMoves[] = new MoveBehavior[150];
    //Negate Direction Timer
    private Timer iterationTimer;
    private java.awt.event.ActionListener negateMoveDir = new java.awt.event.ActionListener() {
        public void actionPerformed(ActionEvent event) {
            for (MoveBehavior move : obstsaclesMoves) {
                move.setMoveDirection(move.getMoveDirection().negate());
            }
        }
    };

    public static void main(String[] args) {
        UnalignedAvoidanceDemo app = new UnalignedAvoidanceDemo();
        app.start();
    }

    @Override
    public void simpleInitApp() {
        this.steerControl = new CustomSteerControl(11, 4);
        this.steerControl.setCameraSettings(getCamera());
        this.steerControl.setFlyCameraSettings(getFlyByCamera());

        //defining rootNode for aiAppState processing
        aiAppState.setApp(this);
        aiAppState.setGameControl(this.steerControl);

        Vector3f[] spawnArea = null;
        this.numberNeighbours = 150;

        agent = this.createBoid("Target", ColorRGBA.Blue, 0.31f);

        aiAppState.addAgent(agent); //Add the target to the aiAppState
        this.setStats(
                agent,
                this.targetMoveSpeed,
                this.targetRotationSpeed,
                this.targetMass,
                this.targetMaxForce);
        aiAppState.getGameControl().spawn(agent, new Vector3f());

        Agent[] neighbours = new Agent[this.numberNeighbours];
        Random rand = FastMath.rand;
        for (int i = 0; i < this.numberNeighbours; i++) {
            neighbours[i] = this.createSphere("neighbour_" + i, ColorRGBA.Orange, 0.25f);
            aiAppState.addAgent(neighbours[i]); //Add the neighbours to the aiAppState
            this.setStats(
                    neighbours[i],
                    this.neighboursMoveSpeed,
                    this.neighboursRotationSpeed,
                    this.neighboursMass,
                    this.neighboursMaxForce);
            aiAppState.getGameControl().spawn(neighbours[i], spawnArea);

            SimpleMainBehavior mainB = new SimpleMainBehavior(neighbours[i]);

            obstsaclesMoves[i] = new MoveBehavior(neighbours[i]);
            obstsaclesMoves[i].setMoveDirection(new Vector3f(rand.nextFloat() - 1, rand.nextFloat() - 1, rand.nextFloat() - 1));

            mainB.addBehavior(obstsaclesMoves[i]);
            neighbours[i].setMainBehaviour(mainB);
        }

        this.iterationTimer = new Timer(4000, this.negateMoveDir); //4k ns = 4s
        this.iterationTimer.start();

        focus = this.createSphere("focus", ColorRGBA.Green, 0.35f);
        aiAppState.addAgent(focus);

        aiAppState.addAgent(focus); //Add the neighbours to the aiAppState
        this.setStats(
                focus,
                this.neighboursMoveSpeed,
                this.neighboursRotationSpeed,
                this.neighboursMass,
                this.neighboursMaxForce);
        aiAppState.getGameControl().spawn(focus, this.generateRandomPosition());

        SimpleMainBehavior mainB = new SimpleMainBehavior(focus);
        focus.setMainBehaviour(mainB);


        List<GameEntity> obstacles = new ArrayList<GameEntity>();
        obstacles.addAll(Arrays.asList(neighbours));

        //ADD OBSTACLE AVOIDANCE TO THE TARGET

        CompoundSteeringBehavior steer = new CompoundSteeringBehavior(agent);//new BalancedCompoundSteeringBehaviour(agent);//new CompoundSteeringBehavior(agent);
        SimpleMainBehavior targetMainB = new SimpleMainBehavior(agent);

        SeekBehavior seekSteer = new SeekBehavior(agent, focus);

        UnalignedCollisionAvoidanceBehavior obstacleAvoidance = new UnalignedCollisionAvoidanceBehavior(agent, obstacles, 2, 5, 0.75f);
        obstacleAvoidance.setupStrengthControl(5f);

        steer.addSteerBehavior(seekSteer);
        steer.addSteerBehavior(obstacleAvoidance);
        targetMainB.addBehavior(steer);
        agent.setMainBehaviour(targetMainB);

        aiAppState.start();
    }

    @Override
    public void simpleUpdate(float tpf) {
        aiAppState.update(tpf);

        if (this.agent.distanceRelativeToGameEntity(this.focus) < 0.5f) {
            this.focus.setLocalTranslation(this.generateRandomPosition());
        }
    }

    private Vector3f generateRandomPosition() {
        Random rand = FastMath.rand;
        Vector3f randomPos;

        if (this.positiveXSide) {
            randomPos = new Vector3f(7.5f, (rand.nextFloat() - 0.5f) * 7.5f, (rand.nextFloat() - 0.5f) * 7.5f);
            this.positiveXSide = false;
        } else {
            randomPos = new Vector3f(-7.5f, (rand.nextFloat() - 0.5f) * 7.5f, (rand.nextFloat() - 0.5f) * 7.5f);
            this.positiveXSide = true;
        }

        return randomPos;
    }
}
