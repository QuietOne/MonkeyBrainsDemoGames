//Copyright (c) 2014, Jesús Martín Berlanga. All rights reserved. Distributed under the BSD licence. Read "com/jme3/ai/license.txt".

package steeringDemos.demos;

import com.jme3.ai.agents.Agent;
import com.jme3.ai.agents.behaviours.npc.SimpleMainBehaviour;
import com.jme3.ai.agents.behaviours.npc.steering.CompoundSteeringBehaviour;
import com.jme3.ai.agents.behaviours.npc.steering.WallApproachBehaviour;
import com.jme3.ai.agents.behaviours.npc.steering.WanderBehaviour;

import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Sphere;

import steeringDemos.BasicDemo;
import steeringDemos.control.CustomSteerControl;

/**
 * Wall approach demo
 * 
 * @author Jesús Martín Berlanga
 * @version 2.0
 */
public class WallApproachDemo extends BasicDemo {
    
    public static void main(String[] args) {
        WallApproachDemo app = new WallApproachDemo();
        app.start();
    }
    
    @Override
    public void simpleInitApp() 
    {
        this.steerControl = new CustomSteerControl(6, 5f);
        this.steerControl.setCameraSettings(getCamera());
        this.steerControl.setFlyCameraSettings(getFlyByCamera());
        
        //defining rootNode for aiAppState processing
        aiAppState.setApp(this);
        aiAppState.setAIControl(this.steerControl);
        
        Agent target = this.createBoid("Target", this.targetColor, 0.11f);
   
        aiAppState.addAgent(target); //Add the target to the aiAppState
        aiAppState.getAIControl().spawn(target, new Vector3f(4f,0,0));
        this.setStats
                (
                    target,
                    this.targetMoveSpeed,
                    this.targetRotationSpeed,
                    this.targetMass,
                    this.targetMaxForce
                );
        
        ////////////////////////////////////////////////////////////////////////////
        ////////// Wall ////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////////
            Node wall = new Node();
            
            this.addSphere(wall, 3f, new Vector3f(0,0,0));
            
            rootNode.attachChild(wall);
        ////////////////////////////////////////////////////////////////////////////
        
        SimpleMainBehaviour targetMainBehaviour =  new SimpleMainBehaviour(target);
        CompoundSteeringBehaviour wallSteer = new CompoundSteeringBehaviour(target);
        
        WanderBehaviour targetMoveBehavior = new WanderBehaviour(target);       
        WallApproachBehaviour wallApproach = new WallApproachBehaviour(target, wall, 0.25f);
        
        wallSteer.addSteerBehaviour(targetMoveBehavior);
        wallSteer.addSteerBehaviour(wallApproach);
        
        targetMainBehaviour.addBehaviour(wallSteer);
        target.setMainBehaviour(targetMainBehaviour);
  
        aiAppState.start();
    }

    //Custom sphere for this demo
    private void addSphere(Node parentNode, float size, Vector3f location)
    {
        Node finalSphere = new Node();
        Sphere sphere = new Sphere(18, 4, size);

        Geometry geom = new Geometry("A shape", sphere); // wrap shape into geometry
        Geometry geomWire = new Geometry("A shape", sphere);
         
            Material matTranslucid = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
                matTranslucid.setColor("Color", new ColorRGBA(0,1,0,0.17f));
                matTranslucid.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);               
                geom.setQueueBucket(Bucket.Translucent);
                geom.setMaterial(matTranslucid);
                
            Material wireMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
                wireMat.setColor("Color", new ColorRGBA(0,1,0,0.25f));
                geomWire.setMaterial(wireMat);
                wireMat.getAdditionalRenderState().setWireframe(true);
        
        finalSphere.attachChild(geom);
        finalSphere.attachChild(geomWire);
        finalSphere.setLocalTranslation(location);
        
        parentNode.attachChild(finalSphere);
    }
   
    @Override
    public void simpleUpdate(float tpf) {
        aiAppState.update(tpf);
    }
}
