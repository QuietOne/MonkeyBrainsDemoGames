//Copyright (c) 2014, Jesús Martín Berlanga. All rights reserved. Distributed under the BSD licence. Read "com/jme3/ai/license.txt".
package steeringDemos.demos;

import com.jme3.ai.agents.Agent;
import com.jme3.ai.agents.behaviours.npc.steering.SeparationBehaviour;
import com.jme3.ai.agents.behaviours.npc.SimpleMainBehaviour;
import com.jme3.ai.agents.behaviours.npc.steering.CompoundSteeringBehaviour;
import com.jme3.ai.agents.behaviours.npc.steering.ObstacleAvoidanceBehaviour;
import com.jme3.ai.agents.behaviours.npc.steering.QueuingBehaviour;
import com.jme3.ai.agents.behaviours.npc.steering.SeekBehaviour;
import com.jme3.ai.agents.util.GameEntity;

import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.material.Material;
import com.jme3.scene.Spatial;
import com.jme3.material.RenderState;
import com.jme3.math.FastMath;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Cylinder;

import steeringDemos.BasicDemo;
import steeringDemos.control.CustomSteerControl;

import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;

/**
 * Queuing demo
 *
 * @author Jesús Martín Berlanga
 * @version 2.0
 */
public class QueuingDemo extends BasicDemo {

    SeekBehaviour[] neighSeek;
    Agent[] neighbours;

    public static void main(String[] args) {
        QueuingDemo app = new QueuingDemo();
        app.start();
    }

    @Override
    public void simpleInitApp() {
        this.steerControl = new CustomSteerControl(5, 5);
        this.steerControl.setCameraSettings(getCamera());
        this.steerControl.setFlyCameraSettings(getFlyByCamera());

        //defining rootNode for aiAppState processing
        aiAppState.setApp(this);
        aiAppState.setAIControl(this.steerControl);

        this.numberNeighbours = 50;
        Vector3f[] spawnArea = null;

        ColorRGBA targetNewColor = new ColorRGBA(ColorRGBA.Cyan.r, ColorRGBA.Cyan.g, ColorRGBA.Cyan.b, 0.25f);

        Agent target = this.createDoor("Door", targetNewColor, 0.28f);
        aiAppState.addAgent(target); //Add the target to the aiAppState
        this.setStats(
                target,
                this.targetMoveSpeed,
                this.targetRotationSpeed,
                this.targetMass,
                this.targetMaxForce);
        aiAppState.getAIControl().spawn(target, new Vector3f(0, 0, 15));
        SimpleMainBehaviour targetMainB = new SimpleMainBehaviour(target);
        target.setMainBehaviour(targetMainB);

        this.neighbours = new Agent[this.numberNeighbours];
        this.neighboursMoveSpeed *= 1.5f;

        for (int i = 0; i < this.numberNeighbours; i++) {
            this.neighbours[i] = this.createBoid("Neighbour " + i, this.neighboursColor, 0.11f);
            aiAppState.addAgent(this.neighbours[i]); //Add the neighbours to the aiAppState

            this.setStats(
                    neighbours[i],
                    this.neighboursMoveSpeed,
                    this.neighboursRotationSpeed,
                    this.neighboursMass,
                    this.neighboursMaxForce);
            aiAppState.getAIControl().spawn(this.neighbours[i], spawnArea);
        }

        List<GameEntity> obstacles = new ArrayList<GameEntity>();
        obstacles.addAll(Arrays.asList(this.neighbours));

        List<GameEntity> wallObstacle = new ArrayList<GameEntity>();

        Vector3f[] spheresSpawnPositions = new Vector3f[]{
            new Vector3f(1, 0, 0).mult(0.28f).add(new Vector3f(0, 0, 15)),
            new Vector3f(0, 1, 0).mult(0.28f).add(new Vector3f(0, 0, 15)),
            new Vector3f(-1, 0, 0).mult(0.28f).add(new Vector3f(0, 0, 15)),
            new Vector3f(0, -1, 0).mult(0.28f).add(new Vector3f(0, 0, 15)),
            new Vector3f(0.70710678118654752440084436210485f, 0.70710678118654752440084436210485f, 0).mult(0.28f).add(new Vector3f(0, 0, 15)),
            new Vector3f(-0.70710678118654752440084436210485f, 0.70710678118654752440084436210485f, 0).mult(0.28f).add(new Vector3f(0, 0, 15)),
            new Vector3f(0.70710678118654752440084436210485f, -0.70710678118654752440084436210485f, 0).mult(0.28f).add(new Vector3f(0, 0, 15)),
            new Vector3f(-0.70710678118654752440084436210485f, -0.70710678118654752440084436210485f, 0).mult(0.28f).add(new Vector3f(0, 0, 15)),
            new Vector3f(0.92387953251128675612818318939679f, 0.3826834323650897717284599840304f, 0).mult(0.28f).add(new Vector3f(0, 0, 15)),
            new Vector3f(-0.92387953251128675612818318939679f, 0.3826834323650897717284599840304f, 0).mult(0.28f).add(new Vector3f(0, 0, 15)),
            new Vector3f(0.92387953251128675612818318939679f, -0.3826834323650897717284599840304f, 0).mult(0.28f).add(new Vector3f(0, 0, 15)),
            new Vector3f(-0.92387953251128675612818318939679f, -0.3826834323650897717284599840304f, 0).mult(0.28f).add(new Vector3f(0, 0, 15)),
            new Vector3f(0.3826834323650897717284599840304f, 0.92387953251128675612818318939679f, 0).mult(0.28f).add(new Vector3f(0, 0, 15)),
            new Vector3f(-0.3826834323650897717284599840304f, 0.92387953251128675612818318939679f, 0).mult(0.28f).add(new Vector3f(0, 0, 15)),
            new Vector3f(0.3826834323650897717284599840304f, -0.92387953251128675612818318939679f, 0).mult(0.28f).add(new Vector3f(0, 0, 15)),
            new Vector3f(-0.3826834323650897717284599840304f, -0.92387953251128675612818318939679f, 0).mult(0.28f).add(new Vector3f(0, 0, 15)),};

        for (int i = 0; i < 16; i++) {
            wallObstacle.add(this.createSphere("Door obstacle " + i, ColorRGBA.Orange, 0.075f));

            this.setStats(
                    (Agent) wallObstacle.get(i),
                    this.neighboursMoveSpeed,
                    this.neighboursRotationSpeed,
                    this.neighboursMass,
                    this.neighboursMaxForce);
            aiAppState.getAIControl().spawn(wallObstacle.get(i), spheresSpawnPositions[i]);

            SimpleMainBehaviour mainB = new SimpleMainBehaviour((Agent) wallObstacle.get(i));
            ((Agent) wallObstacle.get(i)).setMainBehaviour(mainB);
        }


        SimpleMainBehaviour[] neighboursMainBehaviour = new SimpleMainBehaviour[this.neighbours.length];
        neighSeek = new SeekBehaviour[this.neighbours.length];

        for (int i = 0; i < this.neighbours.length; i++) {
            neighboursMainBehaviour[i] = new SimpleMainBehaviour(this.neighbours[i]);

            SeekBehaviour extraSeek = new SeekBehaviour(this.neighbours[i], new Vector3f(0, 0, 14.9f));
            extraSeek.setupStrengthControl(0.35f);
            this.neighSeek[i] = new SeekBehaviour(this.neighbours[i], new Vector3f(0, 0, 15.17f));
            SeparationBehaviour separation = new SeparationBehaviour(this.neighbours[i], obstacles, 0.13f);
            separation.setupStrengthControl(0.25f);

            ObstacleAvoidanceBehaviour obstacleAvoid = new ObstacleAvoidanceBehaviour(this.neighbours[i], wallObstacle, 1, 5);

            // BalancedCompoundSteeringBehaviour steer = new BalancedCompoundSteeringBehaviour(this.neighbours[i]);
            CompoundSteeringBehaviour steer = new CompoundSteeringBehaviour(this.neighbours[i]);
            QueuingBehaviour queue = new QueuingBehaviour(this.neighbours[i], convertToAgents(obstacles), 1.5f);
            SeparationBehaviour queueSeparation = new SeparationBehaviour(this.neighbours[i], obstacles, 0.25f);
            queueSeparation.setupStrengthControl(0.01f); //1%

            steer.addSteerBehaviour(this.neighSeek[i]);
            steer.addSteerBehaviour(extraSeek);
            steer.addSteerBehaviour(obstacleAvoid);
            steer.addSteerBehaviour(queue);
            steer.addSteerBehaviour(queueSeparation);
            steer.addSteerBehaviour(separation, 1, 0.01f); //Highest layer => Highest priority

            //Remove behaviour for testing purposes
            steer.removeSteerBehaviour(separation);
            steer.removeSteerBehaviour(this.neighSeek[i]);
            steer.removeSteerBehaviour(obstacleAvoid);
            steer.removeSteerBehaviour(extraSeek);
            steer.removeSteerBehaviour(queue);
            steer.removeSteerBehaviour(queueSeparation);

            //Add then again
            steer.addSteerBehaviour(this.neighSeek[i]);
            steer.addSteerBehaviour(extraSeek);
            steer.addSteerBehaviour(obstacleAvoid);
            steer.addSteerBehaviour(queue);
            steer.addSteerBehaviour(queueSeparation);
            steer.addSteerBehaviour(separation, 1, 0.01f); //Highest layer => Highest priority

            neighboursMainBehaviour[i].addBehaviour(steer);

            this.neighbours[i].setMainBehaviour(neighboursMainBehaviour[i]);
        }

        aiAppState.start();
    }

    private Agent createDoor(String name, ColorRGBA color, float radius) {
        Node origin = new Node();
        Cylinder mesh = new Cylinder(4, 20, radius, 0.01f, true);
        Geometry geom = new Geometry("A shape", mesh);
        origin.attachChild(geom);

        Spatial doorSpatial = origin;

        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        mat.setColor("Color", color);
        doorSpatial.setMaterial(mat);

        return new Agent(name, doorSpatial);
    }

    @Override
    public void simpleUpdate(float tpf) {
        aiAppState.update(tpf);

        for (int i = 0; i < this.neighbours.length; i++) {
            if (new Vector3f(0, 0, 15.17f).subtract(this.neighbours[i].getLocalTranslation()).length() < 0.15f) {
                this.neighbours[i].setLocalTranslation(
                        ((float) ((FastMath.nextRandomFloat() * 2) - 1)) * 5,
                        ((float) ((FastMath.nextRandomFloat() * 2) - 1)) * 5,
                        ((float) ((FastMath.nextRandomFloat() * 2) - 1)) * 5);
            }
        }
    }

    private List<Agent> convertToAgents(List<GameEntity> gameEntities) {
        List<Agent> agents = new ArrayList<Agent>();
        for (GameEntity gameEntity : gameEntities) {
            if (gameEntity instanceof Agent) {
                agents.add((Agent) gameEntity);
            }
        }
        return agents;
    }
}
