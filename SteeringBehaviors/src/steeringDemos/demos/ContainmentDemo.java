//Copyright (c) 2014, Jesús Martín Berlanga. All rights reserved.
//Distributed under the BSD licence. Read "com/jme3/ai/license.txt".
package steeringDemos.demos;

import com.jme3.ai.agents.Agent;
import com.jme3.ai.agents.behaviors.npc.SimpleMainBehavior;
import com.jme3.ai.agents.behaviors.npc.steering.CompoundSteeringBehavior;
import com.jme3.ai.agents.behaviors.npc.steering.ContainmentBehavior;
import com.jme3.ai.agents.behaviors.npc.steering.WanderBehavior;
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
    public void simpleInitApp() {
        this.steerControl = new CustomSteerControl(7, 5);
        this.steerControl.setCameraSettings(getCamera());
        this.steerControl.setFlyCameraSettings(getFlyByCamera());

        //defining rootNode for aiAppState processing
        aiAppState.setApp(this);
        aiAppState.setGameControl(this.steerControl);

        Agent target = this.createBoid("Target", this.targetColor, 0.11f);
        aiAppState.addAgent(target); //Add the target to the aiAppState
        aiAppState.getGameControl().spawn(target, new Vector3f());

        this.setStats(
                target,
                this.targetMoveSpeed,
                this.targetRotationSpeed,
                this.targetMass,
                this.targetMaxForce);

        ////////////////////////////////////////////////////////////////////////////
        ////////// Containment area ////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////////
        Node containmentArea = new Node();

        StripBox mesh = new StripBox(1.5f, 1.5f, 1.5f);
        Geometry geom = new Geometry("A shape", mesh); // wrap shape into geometry
        Geometry geomWire = new Geometry("A shape", mesh);

        Material matTranslucid = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        matTranslucid.setColor("Color", new ColorRGBA(0, 1, 0, 0.17f));
        matTranslucid.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
        geom.setQueueBucket(Bucket.Translucent);
        geom.setMaterial(matTranslucid);

        Material wireMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        wireMat.setColor("Color", new ColorRGBA(0, 1, 0, 0.25f));
        geomWire.setMaterial(wireMat);
        wireMat.getAdditionalRenderState().setWireframe(true);

        containmentArea.attachChild(geom);
        rootNode.attachChild(containmentArea);
        rootNode.attachChild(geomWire);
        ////////////////////////////////////////////////////////////////////////////

        SimpleMainBehavior targetMainBehaviour = new SimpleMainBehavior(target);
        CompoundSteeringBehavior steering = new CompoundSteeringBehavior(target);

        WanderBehavior targetMoveBehavior = new WanderBehavior(target);
        ContainmentBehavior contain = new ContainmentBehavior(target, containmentArea);
        contain.setupStrengthControl(75);

        steering.addSteerBehavior(targetMoveBehavior);
        steering.addSteerBehavior(contain);

        targetMainBehaviour.addBehavior(steering);
        target.setMainBehaviour(targetMainBehaviour);

        aiAppState.start();
    }

    @Override
    public void simpleUpdate(float tpf) {
        aiAppState.update(tpf);
    }
}
