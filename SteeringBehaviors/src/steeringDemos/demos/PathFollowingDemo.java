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
import com.jme3.ai.agents.behaviors.npc.steering.PathFollowBehavior;
import com.jme3.ai.agents.behaviors.npc.steering.SeparationBehavior;
import com.jme3.ai.agents.util.GameEntity;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Cylinder;
import steeringDemos.BasicDemo;
import steeringDemos.control.CustomSteerControl;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Path follow demo
 *
 * @author Jesús Martín Berlanga
 * @version 2.0.0
 */
public class PathFollowingDemo extends BasicDemo {

    private PathFollowBehavior targetPathFollow[];

    public static void main(String[] args) {
        PathFollowingDemo app = new PathFollowingDemo();
        app.start();
    }

    @Override
    public void simpleInitApp() {
        this.steerControl = new CustomSteerControl(8.5f, 5);
        this.steerControl.setCameraSettings(getCamera());
        this.steerControl.setFlyCameraSettings(getFlyByCamera());

        //defining rootNode for brainsAppState processing
        brainsAppState.setApp(this);
        brainsAppState.setGameControl(this.steerControl);

        this.numberNeighbours = 10;
        Vector3f[] spawnArea = null;

        Agent[] neighbours = new Agent[this.numberNeighbours];

        for (int i = 0; i < this.numberNeighbours; i++) {
            neighbours[i] = this.createBoid("Neighbour " + i, this.neighboursColor, 0.11f);
            brainsAppState.addAgent(neighbours[i]); //Add the neighbours to the brainsAppState
            this.setStats(
                    neighbours[i],
                    this.neighboursMoveSpeed,
                    this.neighboursRotationSpeed,
                    this.neighboursMass,
                    this.neighboursMaxForce);
            brainsAppState.getGameControl().spawn(neighbours[i], spawnArea);
        }
        ArrayList<GameEntity> neighObstacles = new ArrayList<GameEntity>();
        neighObstacles.addAll(Arrays.asList(neighbours));

        ///////////////////////////////////////////////////////////////////////////
        ////////// Path ///////////////////////////////////////////////////////////
        ///////////////////////////////////////////////////////////////////////////
        ArrayList<Vector3f> orderedPointsList = new ArrayList<Vector3f>();
        orderedPointsList.add(new Vector3f(0, 0, 0));
        orderedPointsList.add(new Vector3f(0, 0, 5));
        orderedPointsList.add(new Vector3f(5, 0, 5));
        orderedPointsList.add(new Vector3f(5, 5, 5));
        orderedPointsList.add(new Vector3f(5, 5, 0));
        orderedPointsList.add(new Vector3f(0, 5, 0));
        orderedPointsList.add(new Vector3f(0, 0, 0));

        this.drawPathSegment(new Vector3f(0, 0, -0.01f), new Vector3f(0, 0, 0.01f), 1, ColorRGBA.Blue, 3); //Start area

        //Transition Areas
        this.drawPathSegment(new Vector3f(0, 0, 4.99f), new Vector3f(0, 0, 5.01f), 1, ColorRGBA.Cyan, 3);
        this.drawPathSegment(new Vector3f(4.99f, 0, 5), new Vector3f(5.01f, 0, 5), 1, ColorRGBA.Cyan, 3);
        this.drawPathSegment(new Vector3f(5, 4.99f, 5), new Vector3f(5, 5.01f, 5), 1, ColorRGBA.Cyan, 3);
        this.drawPathSegment(new Vector3f(5, 5, -0.01f), new Vector3f(5, 5, 0.01f), 1, ColorRGBA.Cyan, 3);
        this.drawPathSegment(new Vector3f(-0.01f, 5, 0), new Vector3f(0.01f, 5, 0), 1, ColorRGBA.Cyan, 3);

        this.drawPathSegment(orderedPointsList.get(0), orderedPointsList.get(1), 1, ColorRGBA.Green, 1);
        this.drawPathSegment(orderedPointsList.get(1), orderedPointsList.get(2), 1, ColorRGBA.Green, 1);
        this.drawPathSegment(orderedPointsList.get(2), orderedPointsList.get(3), 1, ColorRGBA.Green, 1);
        this.drawPathSegment(orderedPointsList.get(3), orderedPointsList.get(4), 1, ColorRGBA.Green, 1);
        this.drawPathSegment(orderedPointsList.get(4), orderedPointsList.get(5), 1, ColorRGBA.Green, 1);
        this.drawPathSegment(orderedPointsList.get(5), orderedPointsList.get(6), 1, ColorRGBA.Green, 1);
        ////////////////////////////////////////////////////////////////////////////

        this.targetPathFollow = new PathFollowBehavior[neighbours.length];
        for (int i = 0; i < neighbours.length; i++) {
            SimpleMainBehavior targetMainBehavior = new SimpleMainBehavior(neighbours[i]);
            targetPathFollow[i] = new PathFollowBehavior(neighbours[i], orderedPointsList, 1, 1);
            targetPathFollow[i].setupStrengthControl(0.225f);
            SeparationBehavior neighSeparation = new SeparationBehavior(neighbours[i], neighObstacles, 0.75f);
            neighSeparation.setupStrengthControl(0.45f);

            CompoundSteeringBehavior steer = new CompoundSteeringBehavior(neighbours[i]);
            steer.addSteerBehavior(neighSeparation);
            steer.addSteerBehavior(targetPathFollow[i]);

            targetMainBehavior.addBehavior(steer);
            neighbours[i].setMainBehavior(targetMainBehavior);
        }

        brainsAppState.start();
    }

    //Create the path visuals
    private void drawPathSegment(Vector3f a, Vector3f b, float radius, ColorRGBA color, float AlphaMult) {
        Vector3f direction = b.subtract(a);
        float height = direction.length();

        Node origin = new Node();
        origin.lookAt(direction, Vector3f.UNIT_Y);
        origin.setLocalTranslation(a.add(direction.divide(2)));

        Cylinder mesh = new Cylinder(4, 20, radius, height, true);
        //ylinder meshWire = new Cylinder(2, 5, radius, height, false);

        Geometry geom = new Geometry("A shape", mesh); // wrap shape into geometry
        //Geometry geomWire = new Geometry("A shape", meshWire);

        origin.attachChild(geom);
        //origin.attachChild(geomWire);

        Material matTranslucid = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        matTranslucid.setColor("Color", new ColorRGBA(color.r, color.g, color.b, 0.17f * AlphaMult));
        matTranslucid.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
        geom.setQueueBucket(Bucket.Translucent);
        geom.setMaterial(matTranslucid);

        rootNode.attachChild(origin);
    }

    @Override
    public void simpleUpdate(float tpf) {
        brainsAppState.update(tpf);

        for (PathFollowBehavior path : this.targetPathFollow) {
            if (!path.isActive()) {
                path.reset();
            }
        }
    }
}