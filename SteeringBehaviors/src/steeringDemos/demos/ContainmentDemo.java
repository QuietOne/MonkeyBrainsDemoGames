/**
 * Copyright (c) 2014, jMonkeyEngine All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * Neither the name of 'jMonkeyEngine' nor the names of its contributors may be
 * used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package steeringDemos.demos;

import com.jme3.ai.agents.Agent;
import com.jme3.ai.agents.behaviors.npc.SimpleMainBehavior;
import com.jme3.ai.agents.behaviors.npc.steering.CompoundSteeringBehavior;
import com.jme3.ai.agents.behaviors.npc.steering.ContainmentBehavior;
import com.jme3.ai.agents.behaviors.npc.steering.SimpleWanderBehavior;
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
 * @version 2.0.1
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

        //defining rootNode for brainsAppState processing
        brainsAppState.setApp(this);
        brainsAppState.setGameControl(this.steerControl);

        Agent target = this.createBoid("Target", this.targetColor, 0.11f);
        brainsAppState.addAgent(target); //Add the target to the brainsAppState
        brainsAppState.getGameControl().spawn(target, new Vector3f());

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

        SimpleMainBehavior targetMainBehavior = new SimpleMainBehavior(target);
        CompoundSteeringBehavior steering = new CompoundSteeringBehavior(target);

        SimpleWanderBehavior targetMoveBehavior = new SimpleWanderBehavior(target, 5, 5, 5);
        ContainmentBehavior contain = new ContainmentBehavior(target, containmentArea);
        contain.setupStrengthControl(75);

        steering.addSteerBehavior(targetMoveBehavior);
        steering.addSteerBehavior(contain);

        targetMainBehavior.addBehavior(steering);
        target.setMainBehavior(targetMainBehavior);

        brainsAppState.start();
    }

    @Override
    public void simpleUpdate(float tpf) {
        brainsAppState.update(tpf);
    }
}