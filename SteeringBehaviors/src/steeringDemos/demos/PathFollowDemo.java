//Copyright (c) 2014, Jesús Martín Berlanga. All rights reserved. Distributed under the BSD licence. Read "com/jme3/ai/license.txt".
package steeringDemos.demos;

import com.jme3.ai.agents.Agent;
import com.jme3.ai.agents.behaviours.npc.SimpleMainBehaviour;
import com.jme3.ai.agents.behaviours.npc.steering.CompoundSteeringBehaviour;
import com.jme3.ai.agents.behaviours.npc.steering.PathFollowBehaviour;
import com.jme3.ai.agents.behaviours.npc.steering.SeparationBehaviour;
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
 * @version 2.0
 */
public class PathFollowDemo extends BasicDemo {

    private PathFollowBehaviour targetPathFollow[];

    public static void main(String[] args) {
        PathFollowDemo app = new PathFollowDemo();
        app.start();
    }

    @Override
    public void simpleInitApp() {
        this.steerControl = new CustomSteerControl(8.5f, 5);
        this.steerControl.setCameraSettings(getCamera());
        this.steerControl.setFlyCameraSettings(getFlyByCamera());

        //defining rootNode for aiAppState processing
        aiAppState.setApp(this);
        aiAppState.setAIControl(this.steerControl);

        this.numberNeighbours = 10;
        Vector3f[] spawnArea = null;

        Agent[] neighbours = new Agent[this.numberNeighbours];

        for (int i = 0; i < this.numberNeighbours; i++) {
            neighbours[i] = this.createBoid("Neighbour " + i, this.neighboursColor, 0.11f);
            aiAppState.addAgent(neighbours[i]); //Add the neighbours to the aiAppState
            this.setStats(
                    neighbours[i],
                    this.neighboursMoveSpeed,
                    this.neighboursRotationSpeed,
                    this.neighboursMass,
                    this.neighboursMaxForce);
            aiAppState.getAIControl().spawn(neighbours[i], spawnArea);
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

        this.targetPathFollow = new PathFollowBehaviour[neighbours.length];
        for (int i = 0; i < neighbours.length; i++) {
            SimpleMainBehaviour targetMainBehaviour = new SimpleMainBehaviour(neighbours[i]);
            targetPathFollow[i] = new PathFollowBehaviour(neighbours[i], orderedPointsList, 1, 1);
            targetPathFollow[i].setupStrengthControl(0.225f);
            SeparationBehaviour neighSeparation = new SeparationBehaviour(neighbours[i], neighObstacles, 0.75f);
            neighSeparation.setupStrengthControl(0.45f);

            CompoundSteeringBehaviour steer = new CompoundSteeringBehaviour(neighbours[i]);
            steer.addSteerBehaviour(neighSeparation);
            steer.addSteerBehaviour(targetPathFollow[i]);

            targetMainBehaviour.addBehaviour(steer);
            neighbours[i].setMainBehaviour(targetMainBehaviour);
        }

        aiAppState.start();
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
        aiAppState.update(tpf);

        for (PathFollowBehaviour path : this.targetPathFollow) {
            if (!path.isActive()) {
                path.reset();
            }
        }
    }
}
