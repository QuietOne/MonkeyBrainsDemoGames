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
import com.jme3.ai.agents.behaviors.npc.steering.WanderAreaBehavior;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.StripBox;
import steeringDemos.BasicDemo;
import steeringDemos.control.CustomSteerControl;

/**
 * Wander Area Demo
 *
 * @author Jesús Martín Berlanga
 * @version 2.0
 */
public class WanderAreaDemo extends BasicDemo {

    public static final Vector3f WANDER_AREA = new Vector3f(2.5f, 2.5f, 2.5f);
    
    public static void main(String[] args) {
        WanderAreaDemo app = new WanderAreaDemo();
        app.start();
    }

    @Override
    public void simpleInitApp() {
        this.steerControl = new CustomSteerControl(9, 1);
        this.steerControl.setCameraSettings(getCamera());
        this.steerControl.setFlyCameraSettings(getFlyByCamera());

        //defining rootNode for brainsAppState processing
        brainsAppState.setApp(this);
        brainsAppState.setGameControl(new CustomSteerControl(5f));

        Agent target = this.createBoid("Target", this.targetColor, 0.11f);

        brainsAppState.addAgent(target); //Add the target to the brainsAppState
        brainsAppState.getGameControl().spawn(target, Vector3f.ZERO);
        this.setStats(
                target,
                this.targetMoveSpeed,
                this.targetRotationSpeed,
                this.targetMass,
                this.targetMaxForce);
        
        ////////////////////////////////////////////////////////////////////////////
        /////////////// Wander Area ////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////////
        Node wanderArea = new Node();

        StripBox mesh = new StripBox(Vector3f.ZERO, WANDER_AREA.x, WANDER_AREA.y, WANDER_AREA.z);
        Geometry geom = new Geometry("A shape", mesh); // wrap shape into geometry
        Geometry geomWire = new Geometry("A shape", mesh);

        Material matTranslucid = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        matTranslucid.setColor("Color", new ColorRGBA(0, 1, 0, 0.17f));
        matTranslucid.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        geom.setQueueBucket(RenderQueue.Bucket.Translucent);
        geom.setMaterial(matTranslucid);

        Material wireMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        wireMat.setColor("Color", new ColorRGBA(0, 1, 0, 0.25f));
        geomWire.setMaterial(wireMat);
        wireMat.getAdditionalRenderState().setWireframe(true);

        wanderArea.attachChild(geom);
        rootNode.attachChild(wanderArea);
        rootNode.attachChild(geomWire);
        ////////////////////////////////////////////////////////////////////////////

        WanderAreaBehavior targetMoveBehavior = new WanderAreaBehavior(target);
        targetMoveBehavior.setArea(Vector3f.ZERO, WANDER_AREA.x, WANDER_AREA.y, WANDER_AREA.z);
        targetMoveBehavior.setTimeInterval(4);
        target.setMainBehavior(targetMoveBehavior);
        
        brainsAppState.start();
    }

    @Override
    public void simpleUpdate(float tpf) {
        brainsAppState.update(tpf);
    }
}