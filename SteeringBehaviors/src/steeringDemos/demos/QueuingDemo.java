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
import com.jme3.ai.agents.behaviors.npc.steering.SeparationBehavior;
import com.jme3.ai.agents.behaviors.npc.SimpleMainBehavior;
import com.jme3.ai.agents.behaviors.npc.steering.CompoundSteeringBehavior;
import com.jme3.ai.agents.behaviors.npc.steering.ObstacleAvoidanceBehavior;
import com.jme3.ai.agents.behaviors.npc.steering.QueuingBehavior;
import com.jme3.ai.agents.behaviors.npc.steering.SeekBehavior;
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
 * @version 2.0.0
 */
public class QueuingDemo extends BasicDemo {

    private SeekBehavior[] neighSeek;
    private Agent[] neighbours;

    public static void main(String[] args) {
        QueuingDemo app = new QueuingDemo();
        app.start();
    }

    @Override
    public void simpleInitApp() {
        this.steerControl = new CustomSteerControl(5, 5);
        this.steerControl.setCameraSettings(getCamera());
        this.steerControl.setFlyCameraSettings(getFlyByCamera());

        //defining rootNode for brainsAppState processing
        brainsAppState.setApp(this);
        brainsAppState.setGameControl(this.steerControl);

        this.numberNeighbours = 50;
        Vector3f[] spawnArea = null;

        ColorRGBA targetNewColor = new ColorRGBA(ColorRGBA.Cyan.r, ColorRGBA.Cyan.g, ColorRGBA.Cyan.b, 0.25f);

        Agent target = this.createDoor("Door", targetNewColor, 0.28f);
        brainsAppState.addAgent(target); //Add the target to the brainsAppState
        this.setStats(
                target,
                this.targetMoveSpeed,
                this.targetRotationSpeed,
                this.targetMass,
                this.targetMaxForce);
        brainsAppState.getGameControl().spawn(target, new Vector3f(0, 0, 15));
        SimpleMainBehavior targetMainB = new SimpleMainBehavior(target);
        target.setMainBehavior(targetMainB);

        this.neighbours = new Agent[this.numberNeighbours];
        this.neighboursMoveSpeed *= 1.5f;

        for (int i = 0; i < this.numberNeighbours; i++) {
            this.neighbours[i] = this.createBoid("Neighbour " + i, this.neighboursColor, 0.11f);
            brainsAppState.addAgent(this.neighbours[i]); //Add the neighbours to the brainsAppState

            this.setStats(
                    neighbours[i],
                    this.neighboursMoveSpeed,
                    this.neighboursRotationSpeed,
                    this.neighboursMass,
                    this.neighboursMaxForce);
            brainsAppState.getGameControl().spawn(this.neighbours[i], spawnArea);
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
            brainsAppState.getGameControl().spawn(wallObstacle.get(i), spheresSpawnPositions[i]);

            SimpleMainBehavior mainB = new SimpleMainBehavior((Agent) wallObstacle.get(i));
            ((Agent) wallObstacle.get(i)).setMainBehavior(mainB);
        }


        SimpleMainBehavior[] neighboursMainBehavior = new SimpleMainBehavior[this.neighbours.length];
        neighSeek = new SeekBehavior[this.neighbours.length];

        for (int i = 0; i < this.neighbours.length; i++) {
            neighboursMainBehavior[i] = new SimpleMainBehavior(this.neighbours[i]);

            SeekBehavior extraSeek = new SeekBehavior(this.neighbours[i], new Vector3f(0, 0, 14.9f));
            extraSeek.setupStrengthControl(0.35f);
            this.neighSeek[i] = new SeekBehavior(this.neighbours[i], new Vector3f(0, 0, 15.17f));
            SeparationBehavior separation = new SeparationBehavior(this.neighbours[i], obstacles, 0.13f);
            separation.setupStrengthControl(0.25f);

            ObstacleAvoidanceBehavior obstacleAvoid = new ObstacleAvoidanceBehavior(this.neighbours[i], wallObstacle, 1, 5);

            // BalancedCompoundSteeringBehaviour steer = new BalancedCompoundSteeringBehaviour(this.neighbours[i]);
            CompoundSteeringBehavior steer = new CompoundSteeringBehavior(this.neighbours[i]);
            QueuingBehavior queue = new QueuingBehavior(this.neighbours[i], convertToAgents(obstacles), 1.5f);
            SeparationBehavior queueSeparation = new SeparationBehavior(this.neighbours[i], obstacles, 0.25f);
            queueSeparation.setupStrengthControl(0.01f); //1%

            steer.addSteerBehavior(this.neighSeek[i]);
            steer.addSteerBehavior(extraSeek);
            steer.addSteerBehavior(obstacleAvoid);
            steer.addSteerBehavior(queue);
            steer.addSteerBehavior(queueSeparation);
            steer.addSteerBehavior(separation, 1, 0.01f); //Highest layer => Highest priority

            //Remove behaviour for testing purposes
            steer.removeSteerBehavior(separation);
            steer.removeSteerBehavior(this.neighSeek[i]);
            steer.removeSteerBehavior(obstacleAvoid);
            steer.removeSteerBehavior(extraSeek);
            steer.removeSteerBehavior(queue);
            steer.removeSteerBehavior(queueSeparation);

            //Add then again
            steer.addSteerBehavior(this.neighSeek[i]);
            steer.addSteerBehavior(extraSeek);
            steer.addSteerBehavior(obstacleAvoid);
            steer.addSteerBehavior(queue);
            steer.addSteerBehavior(queueSeparation);
            steer.addSteerBehavior(separation, 1, 0.01f); //Highest layer => Highest priority

            neighboursMainBehavior[i].addBehavior(steer);

            this.neighbours[i].setMainBehavior(neighboursMainBehavior[i]);
        }

        brainsAppState.start();
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
        brainsAppState.update(tpf);

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
