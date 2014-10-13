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
package org.aitest.ai.model;

import com.jme3.ai.agents.Agent;
import com.jme3.ai.agents.util.control.MonkeyBrainsAppState;
import com.jme3.ai.agents.util.systems.SimpleAgentHitPoints;
import com.jme3.animation.AnimControl;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.debug.Arrow;
import java.util.LinkedList;
import java.util.List;
import org.aitest.ai.control.AIGameUpdateManager;
import org.aitest.ai.utils.AIGameSpatials;
import org.aitest.ai.utils.GunAndSwordInventory;

/*
 *
 * @author mifthbeat
 * @author Tihomir Radosavljević
 * @version 1.0.0
 */
public class AIModel extends BetterCharacterControl {

    /**
     * Reference to agent to which this model is attached.
     */
    private Agent agent;
    /**
     * List of animation that this agent has.
     */
    private List<AnimControl> animationList;
    /**
     * Names of animations.
     */
    private String[] animationNames = {"base_stand", "run_01", "shoot", "strike_sword"};

    public AIModel(Agent agent) {
        super(0.85f, 2f, 50f);
        this.agent = agent;
        //needed for steering behaviours
        agent.setMass(mass);
        agent.setMoveSpeed(7.0f);
        agent.setRotationSpeed(1.0f);
        agent.setMaxForce(3);
        agent.setHitPoints(new SimpleAgentHitPoints(agent));
        agent.setInventory(new GunAndSwordInventory(agent));

        animationList = new LinkedList<AnimControl>();
        agent.getSpatial().addControl(this); // FORCE TO ADD THE CONTROL TO THE SPATIAL

        //add this character under the influence of physics
        MonkeyBrainsAppState.getInstance().getApp().getStateManager().getState(BulletAppState.class).getPhysicsSpace().add(this);

        
        //same spatial is used in agent.spatial and physics space
        spatial = agent.getSpatial();

        // add arrow
        Mesh arrow = new Arrow(Vector3f.UNIT_Z);
        Geometry geoArrow = new Geometry("arrow", arrow);
        Material matArrow = new Material(MonkeyBrainsAppState.getInstance().getApp().getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        matArrow.setColor("Color", ColorRGBA.White);
        geoArrow.setMaterial(matArrow);
        geoArrow.setLocalTranslation(0f, 0.1f, 0f);
        ((Node) agent.getSpatial()).attachChild(geoArrow);
    }
    
    public void setGraphicModel(){
        // Setting spatial for agent with this kind of model
        AIGameSpatials.getInstance().prepareModel(agent, (Node) agent.getSpatial());
    }

    @Override
    public void update(float tpf) {
        // Update only for fixed rate
        if (MonkeyBrainsAppState.getInstance().getApp().getStateManager().getState(AIGameUpdateManager.class).IsUpdate()) {
            //update character
            super.update(tpf);
        }
    }

    public Vector3f getSpatialTranslation(Geometry geometry){
        return getSpatialTranslation().clone().addLocal(Vector3f.UNIT_Y).addLocal(geometry.getLocalRotation().mult(Vector3f.UNIT_Z));
    }
    
    public Agent getAgent() {
        return agent;
    }

    public void setAgent(Agent agent) {
        this.agent = agent;
    }

    public List<AnimControl> getAnimationList() {
        return animationList;
    }

    public void setAnimationList(List<AnimControl> animationList) {
        this.animationList = animationList;
    }

    public String[] getAnimationNames() {
        return animationNames;
    }

    public void setAnimationNames(String[] animationNames) {
        this.animationNames = animationNames;
    }
}
