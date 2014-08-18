//Copyright (c) 2014, Jesús Martín Berlanga. All rights reserved. Distributed under the BSD licence. Read "com/jme3/ai/license.txt".

package steeringDemos.demos;

import com.jme3.ai.agents.Agent;
import com.jme3.ai.agents.behaviours.npc.SimpleMainBehaviour;
import com.jme3.ai.agents.behaviours.npc.steering.CompoundSteeringBehaviour;
import com.jme3.ai.agents.behaviours.npc.steering.ContainmentBehaviour;
import com.jme3.ai.agents.behaviours.npc.steering.WanderBehaviour;

import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.StripBox;

import steeringDemos.BasicDemo;
import steeringDemos.control.CustomSteerControl;

/**
 * Containment demo
 * 
 * @author Jesús Martín Berlanga
 * @version 2.0
 */
public class ContainmentDemo extends BasicDemo {
    
    public static void main(String[] args) {
        ContainmentDemo app = new ContainmentDemo();
        app.start();
    }
    
    @Override
    public void simpleInitApp() 
    {
        this.steerControl = new CustomSteerControl(7, 5);
        this.steerControl.setCameraSettings(getCamera());
        this.steerControl.setFlyCameraSettings(getFlyByCamera());
        
        //defining rootNode for game processing
        game.setApp(this);
        game.setGameControl(this.steerControl);
        
        Agent target = this.createBoid("Target", this.targetColor, 0.11f);
        game.addAgent(target); //Add the target to the game
        game.getGameControl().spawn(target, new Vector3f());
        
        this.setStats
                (
                    target, 
                    this.targetMoveSpeed, 
                    this.targetRotationSpeed, 
                    this.targetMass, 
                    this.targetMaxForce
                );
        
        ////////////////////////////////////////////////////////////////////////////
        ////////// Containment area ////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////////
            Node containmentArea = new Node();
            
            StripBox  mesh = new StripBox(1.5f, 1.5f, 1.5f); 
            Geometry geom = new Geometry("A shape", mesh); // wrap shape into geometry
            Geometry geomWire = new Geometry("A shape", mesh);
            
            Material matTranslucid = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
                matTranslucid.setColor("Color", new ColorRGBA(0,1,0,0.17f));
                matTranslucid.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);               
                geom.setQueueBucket(Bucket.Translucent);
                geom.setMaterial(matTranslucid);
                
            Material wireMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
                wireMat.setColor("Color", new ColorRGBA(0,1,0,0.25f));
                geomWire.setMaterial(wireMat);
                wireMat.getAdditionalRenderState().setWireframe(true);
                
            containmentArea.attachChild(geom);
            rootNode.attachChild(containmentArea); 
            rootNode.attachChild(geomWire);
        ////////////////////////////////////////////////////////////////////////////
        
        SimpleMainBehaviour targetMainBehaviour =  new SimpleMainBehaviour(target);
        CompoundSteeringBehaviour steering = new CompoundSteeringBehaviour(target);
        
        WanderBehaviour targetMoveBehavior = new WanderBehaviour(target);
        ContainmentBehaviour contain = new ContainmentBehaviour(target, containmentArea);
        contain.setupStrengthControl(75);
        
        steering.addSteerBehaviour(targetMoveBehavior);
        steering.addSteerBehaviour(contain);
        
        targetMainBehaviour.addBehaviour(steering);
        target.setMainBehaviour(targetMainBehaviour);
  
        game.start();
    }
    
    @Override
    public void simpleUpdate(float tpf) {
        game.update(tpf);
    }
}
