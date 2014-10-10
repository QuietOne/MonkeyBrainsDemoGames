//Copyright (c) 2014, Jesús Martín Berlanga. All rights reserved. Distributed under the BSD licence. Read "com/jme3/ai/license.txt".

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
    
    private java.awt.event.ActionListener resetSlows = new java.awt.event.ActionListener()
    {
        public void actionPerformed(ActionEvent event)
        {           
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
    public void simpleInitApp() 
    {
        this.steerControl = new CustomSteerControl(15f, 5);
        this.steerControl.setCameraSettings(getCamera());
        this.steerControl.setFlyCameraSettings(getFlyByCamera());
        
        aiAppState.setApp(this);
        aiAppState.setGameControl(this.steerControl);
             
        Agent agent = this.createBoid("boid ", this.neighboursColor, 0.1f);
        aiAppState.addAgent(agent); //Add the neighbours to the aiAppState
        this.neighboursMoveSpeed *= 3;
        this.setStats
                (
                    agent, 
                    this.neighboursMoveSpeed,
                    this.neighboursRotationSpeed, 
                    this.neighboursMass,
                    this.neighboursMaxForce
                );
        
        aiAppState.getGameControl().spawn(agent, new Vector3f());
        
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
        agent.setMainBehaviour(main);
        
        slow.setAcive(true);
        slow2.setAcive(true);
        
        aiAppState.start();
    }
    
    @Override
    public void simpleUpdate(float tpf) {
        aiAppState.update(tpf);
    }
}