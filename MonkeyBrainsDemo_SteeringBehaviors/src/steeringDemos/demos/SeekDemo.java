//Copyright (c) 2014, Jesús Martín Berlanga. All rights reserved. Distributed under the BSD licence. Read "com/jme3/ai/license.txt".

package steeringDemos.demos;

import com.jme3.ai.agents.Agent;
import com.jme3.ai.agents.behaviours.npc.SimpleMainBehaviour;
import com.jme3.ai.agents.behaviours.npc.steering.SeekBehaviour;
import com.jme3.math.ColorRGBA;

import com.jme3.math.Vector3f;

import steeringDemos.BasicDemo;
import steeringDemos.control.CustomSteerControl;

/**
 * Seek Demo
 * 
 * @author Jesús Martín Berlanga
 * @version 1.0
 */
public class SeekDemo extends BasicDemo {
    
    private Agent target;
    private SeekBehaviour targetMove;
    
    Vector3f[] locations = new Vector3f[]
        {
            new Vector3f(7,0,0),
            new Vector3f(0,7,0),
            new Vector3f(0,0,7)
        };
    private int currentFocus = -1;
    
    public static void main(String[] args) {
        SeekDemo app = new SeekDemo();
        app.start();
    }
    
    @Override
    public void simpleInitApp() 
    {     
        this.steerControl = new CustomSteerControl(9, 1);
        this.steerControl.setCameraSettings(getCamera());
        this.steerControl.setFlyCameraSettings(getFlyByCamera());
        
        //defining rootNode for game processing
        game.setApp(this);
        game.setGameControl(new CustomSteerControl(5f));
                       
        target = this.createBoid("Target", this.targetColor, 0.11f);
        
        for(Vector3f loc : this.locations)
            this.createSphereHelper("Sphere " + loc.toString(), ColorRGBA.Yellow, 0.05f, loc);
        
        game.addAgent(target); //Add the target to the game
        game.getGameControl().spawn(target, Vector3f.ZERO);
        this.setStats
                (
                    target,
                    this.targetMoveSpeed,
                    this.targetRotationSpeed,
                    this.targetMass,
                    this.targetMaxForce
                );
                
        SimpleMainBehaviour targetMainBehaviour =  new SimpleMainBehaviour(target);
        
        this.targetMove = new SeekBehaviour(target, this.locations[0]);
        this.currentFocus = 0;
        targetMainBehaviour.addBehaviour(this.targetMove);
        
        target.setMainBehaviour(targetMainBehaviour);
 
        game.start();
    }
    
    @Override
    public void simpleUpdate(float tpf) {
        game.update(tpf);
        
        if(this.target.distanceFromPos(this.locations[this.currentFocus]) < 0.01f)
        {
                this.currentFocus++;
                
                if(this.currentFocus > this.locations.length - 1)
                    this.currentFocus = 0;
                
                this.targetMove.setSeekingPos(this.locations[this.currentFocus]);  
        }
    }
}
