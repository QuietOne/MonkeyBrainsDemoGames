//Copyright (c) 2014, Jesús Martín Berlanga. All rights reserved. 
//Distributed under the BSD licence. Read "com/jme3/ai/license.txt".
package steeringDemos;

import steeringDemos.control.CustomSteerControl;
import com.jme3.ai.agents.Agent;
import com.jme3.ai.agents.util.control.AIAppState;
import com.jme3.app.SimpleApplication;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Sphere;

/**
 * Provides the basic structure for all the demos.
 *
 * @author Jesús Martín Berlanga
 */
public abstract class BasicDemo extends SimpleApplication {

    protected CustomSteerControl steerControl;
    //creating aiAppState
    protected AIAppState aiAppState = AIAppState.getInstance(); 
    private static final String BOID_MODEL_NAME = "Models/boid.j3o";
    private static final float BOID_MODEL_SIZE = 0.1f;
    private static final String BOID_MATERIAL_NAME = "Common/MatDefs/Misc/Unshaded.j3md";
    protected ColorRGBA targetColor = ColorRGBA.Red;
    protected float targetMoveSpeed = 1f;
    protected float targetRotationSpeed = 30;
    protected float targetMass = 50;
    protected float targetMaxForce = 20;
    protected ColorRGBA neighboursColor = ColorRGBA.Blue;
    protected float neighboursMoveSpeed = 0.96f;
    protected float neighboursRotationSpeed = 30;
    protected float neighboursMass = 50;
    protected float neighboursMaxForce = 20;
    protected int numberNeighbours;

    //Create an agent with a name and a color
    protected Agent createBoid(String name, ColorRGBA color, float size) {
        Spatial boidSpatial = assetManager.loadModel(BasicDemo.BOID_MODEL_NAME);
        boidSpatial.setLocalScale(BasicDemo.BOID_MODEL_SIZE); //Resize

        Material mat = new Material(assetManager, BasicDemo.BOID_MATERIAL_NAME);
        mat.setColor("Color", color);
        boidSpatial.setMaterial(mat);
        Agent agent = new Agent(name, boidSpatial);
        agent.setRadius(size);
        return agent;
    }

    //Create a sphere
    protected Agent createSphere(String name, ColorRGBA color, float size) {
        Sphere sphere = new Sphere(13, 12, size);
        Geometry sphereG = new Geometry("Sphere Geometry", sphere);
        Spatial spatial = sphereG;

        Material mat = new Material(assetManager, BasicDemo.BOID_MATERIAL_NAME);
        mat.setColor("Color", color);
        spatial.setMaterial(mat);
        Agent agent = new Agent(name, spatial);
        agent.setRadius(size);
        return agent;
    }

    protected void createSphereHelper(String name, ColorRGBA color, float size, Vector3f loc) {
        Sphere sphere = new Sphere(13, 12, size);
        Geometry sphereG = new Geometry("Sphere Geometry", sphere);
        Spatial spatial = sphereG;

        Material mat = new Material(assetManager, BasicDemo.BOID_MATERIAL_NAME);
        mat.setColor("Color", color);
        spatial.setMaterial(mat);

        sphereG.setLocalTranslation(loc);
        rootNode.attachChild(sphereG);
    }

    protected void addBoxHelper(Vector3f center, float x, float y, float z) {
        Box box = new Box(center, x, y, z);

        Geometry geom = new Geometry("A shape", box); // wrap shape into geometry
        Geometry geomWire = new Geometry("A shape", box);

        Material matTranslucid = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        matTranslucid.setColor("Color", new ColorRGBA(0, 1, 0, 0.17f));
        matTranslucid.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        geom.setQueueBucket(RenderQueue.Bucket.Translucent);
        geom.setMaterial(matTranslucid);

        Material wireMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        wireMat.setColor("Color", new ColorRGBA(0, 1, 0, 0.25f));
        geomWire.setMaterial(wireMat);
        wireMat.getAdditionalRenderState().setWireframe(true);

        rootNode.attachChild(geom);
        rootNode.attachChild(geomWire);
    }

    //Setup the stats for an agent
    protected void setStats(Agent myAgent, float moveSpeed, float rotationSpeed, float mass, float maxForce) {
        myAgent.setMoveSpeed(moveSpeed);
        myAgent.setRotationSpeed(rotationSpeed);
        myAgent.setMass(mass);
        myAgent.setMaxForce(maxForce);
    }
}