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
import com.jme3.ai.agents.behaviors.npc.steering.SimpleWanderBehavior;
import com.jme3.ai.agents.behaviors.npc.steering.WallApproachBehavior;
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
 * @version 2.0.1
 */
public class WallApproachDemo extends BasicDemo {

    public static void main(String[] args) {
        WallApproachDemo app = new WallApproachDemo();
        app.start();
    }
    Agent target;

    @Override
    public void simpleInitApp() {
        this.steerControl = new CustomSteerControl(6, 5f);
        this.steerControl.setCameraSettings(getCamera());
        this.steerControl.setFlyCameraSettings(getFlyByCamera());

        //defining rootNode for brainsAppState processing
        brainsAppState.setApp(this);
        brainsAppState.setGameControl(this.steerControl);

        target = this.createBoid("Target", this.targetColor, 0.11f);
        
        brainsAppState.addAgent(target); //Add the target to the brainsAppState
        brainsAppState.getGameControl().spawn(target, new Vector3f(3.35f, 0, 0));
        this.setStats(
                target,
                this.targetMoveSpeed,
                this.targetRotationSpeed,
                this.targetMass,
                this.targetMaxForce);

        ////////////////////////////////////////////////////////////////////////////
        ////////// Wall ////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////////
        Node wall = new Node();

        this.addSphere(wall, 3f, new Vector3f(0, 0, 0));

        rootNode.attachChild(wall);
        ////////////////////////////////////////////////////////////////////////////

        SimpleMainBehavior targetMainBehavior = new SimpleMainBehavior(target);
        CompoundSteeringBehavior wallSteer = new CompoundSteeringBehavior(target);

        SimpleWanderBehavior targetMoveBehavior = new SimpleWanderBehavior(target,10,10,10);
        targetMoveBehavior.setConstantMod(1f);
        WallApproachBehavior wallApproach = new WallApproachBehavior(target, wall, 0.25f);

        wallSteer.addSteerBehavior(targetMoveBehavior);
        wallSteer.addSteerBehavior(wallApproach);

        targetMainBehavior.addBehavior(wallSteer);
        target.setMainBehavior(targetMainBehavior);

        brainsAppState.start();
    }

    //Custom sphere for this demo
    private void addSphere(Node parentNode, float size, Vector3f location) {
        Node finalSphere = new Node();
        Sphere sphere = new Sphere(18, 4, size);

        Geometry geom = new Geometry("A shape", sphere); // wrap shape into geometry
        Geometry geomWire = new Geometry("A shape", sphere);

        Material matTranslucid = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        matTranslucid.setColor("Color", new ColorRGBA(0, 1, 0, 0.17f));
        matTranslucid.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
        geom.setQueueBucket(Bucket.Translucent);
        geom.setMaterial(matTranslucid);

        Material wireMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        wireMat.setColor("Color", new ColorRGBA(0, 1, 0, 0.25f));
        geomWire.setMaterial(wireMat);
        wireMat.getAdditionalRenderState().setWireframe(true);

        finalSphere.attachChild(geom);
        finalSphere.attachChild(geomWire);
        finalSphere.setLocalTranslation(location);

        parentNode.attachChild(finalSphere);
    }

    @Override
    public void simpleUpdate(float tpf) {
        brainsAppState.update(tpf);
    }
}