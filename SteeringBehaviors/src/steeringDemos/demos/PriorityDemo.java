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
import com.jme3.ai.agents.behaviors.npc.steering.PursuitBehavior;
import com.jme3.ai.agents.behaviors.npc.steering.SeekBehavior;
import com.jme3.ai.agents.behaviors.npc.steering.SeparationBehavior;
import com.jme3.ai.agents.util.GameEntity;
import com.jme3.font.BitmapText;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import java.util.ArrayList;
import steeringDemos.BasicDemo;
import steeringDemos.control.CustomSteerControl;

/**
 * Priority Demo
 *
 * @author Jesús Martín Berlanga
 * @version 1.0.0
 */
public class PriorityDemo extends BasicDemo {

    private final String HUD_LAYER_TEXT = "Current priority layer: ";
    private BitmapText hudText;

    //The purpose of this class is to save the active layer inside the activeLayer variable
    private class DebugCompoundSteeringBehavior extends CompoundSteeringBehavior {

        private DebugCompoundSteeringBehavior(Agent agent) {
            super(agent);
        }
        private int activeLayer = Integer.MAX_VALUE; //We can see which is the active layer

        @Override
        protected Vector3f calculateRawSteering() {

            Vector3f totalForce = new Vector3f();
            float totalBraking = 1;

            this.behaviors.moveAtBeginning();

            if (!this.behaviors.nullPointer()) {
                int currentLayer = this.behaviors.getLayer();
                int inLayerCounter = 0;
                int validCounter = 0;

                while (!this.behaviors.nullPointer()) {
                    if (this.behaviors.getLayer() != currentLayer) //We have finished the last layer, check If it was a valid layer
                    {
                        if (inLayerCounter == validCounter) {
                            break; //If we have a valid layer, return the force
                        } else {
                            totalForce = new Vector3f(); //If not, reset the total force
                            totalBraking = 1;            //and braking
                        }

                        currentLayer = this.behaviors.getLayer();
                        inLayerCounter = 0;
                        validCounter = 0;
                    }

                    Vector3f force = this.calculatePartialForce(this.behaviors.getBehavior());
                    if (force.length() > this.behaviors.getMinLengthToInvalidSteer()) {
                        validCounter++;
                    }
                    totalForce = totalForce.add(force);
                    totalBraking *= this.behaviors.getBehavior().getBrakingFactor();

                    this.activeLayer = this.behaviors.getLayer();

                    inLayerCounter++;
                    this.behaviors.moveNext();
                }
            }

            this.setBrakingFactor(totalBraking);
            return totalForce;
        }
    }
    private Agent target;
    private Agent seeker;
    private SeekBehavior targetMove;
    private DebugCompoundSteeringBehavior targetSteer;
    Vector3f[] locations = new Vector3f[]{
        new Vector3f(7, 0, 0),
        new Vector3f(0, 7, 0),
        new Vector3f(0, 0, 7)
    };
    private int currentFocus = -1;

    public static void main(String[] args) {
        PriorityDemo app = new PriorityDemo();
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

        target = this.createBoid("Target", this.targetColor, 0.11f);

        for (Vector3f loc : this.locations) {
            this.createSphereHelper("Sphere " + loc.toString(), ColorRGBA.Yellow, 0.05f, loc);
        }

        brainsAppState.addAgent(target); //Add the target to the brainsAppState
        brainsAppState.getGameControl().spawn(target, Vector3f.ZERO);
        this.setStats(
                target,
                this.targetMoveSpeed,
                this.targetRotationSpeed,
                this.targetMass,
                this.targetMaxForce);

        seeker = this.createBoid("Target", this.neighboursColor, 0.11f);

        this.neighboursMoveSpeed = 3f;
        brainsAppState.addAgent(seeker); //Add the target to the brainsAppState
        brainsAppState.getGameControl().spawn(seeker, new Vector3f(10, 10, 10));
        this.setStats(
                seeker,
                this.neighboursMoveSpeed,
                this.neighboursRotationSpeed,
                this.neighboursMass,
                this.neighboursMaxForce);

        SimpleMainBehavior seekerMainBehavior = new SimpleMainBehavior(seeker);
        PursuitBehavior pursuit = new PursuitBehavior(seeker, target);
        pursuit.setupStrengthControl(5f);
        seekerMainBehavior.addBehavior(pursuit);
        seeker.setMainBehavior(seekerMainBehavior);

        SimpleMainBehavior targetMainBehavior = new SimpleMainBehavior(target);

        this.targetMove = new SeekBehavior(target, this.locations[0]);
        this.currentFocus = 0;
        ArrayList<GameEntity> separationObstacle = new ArrayList<GameEntity>();
        separationObstacle.add(seeker);
        SeparationBehavior separation = new SeparationBehavior(target, separationObstacle);

        targetSteer = new DebugCompoundSteeringBehavior(target);
        targetSteer.addSteerBehavior(targetMove);
        targetSteer.addSteerBehavior(separation, 1, 0.1f);

        targetMainBehavior.addBehavior(this.targetSteer);
        target.setMainBehavior(targetMainBehavior);

        hudText = new BitmapText(guiFont, false);
        hudText.setSize(guiFont.getCharSet().getRenderedSize() * 1.25f);      // font size
        hudText.setColor(ColorRGBA.Red);                             // font color
        hudText.setText("");             // the text
        hudText.setLocalTranslation(0, 475, 0); // position

        guiNode.attachChild(hudText);
        brainsAppState.start();
    }

    @Override
    public void simpleUpdate(float tpf) {
        brainsAppState.update(tpf);

        if (this.target.distanceFromPosition(this.locations[this.currentFocus]) < 0.01f) {
            this.currentFocus++;

            if (this.currentFocus > this.locations.length - 1) {
                this.currentFocus = 0;
            }
            this.targetMove.setSeekingPosition(this.locations[this.currentFocus]);
        }

        this.hudText.setText(this.HUD_LAYER_TEXT + this.targetSteer.activeLayer);

        if (this.seeker.distanceRelativeToGameEntity(this.target) < 0.25f) {
            this.seeker.setLocalTranslation(
                    (FastMath.nextRandomFloat() - 0.5f) * 10,
                    (FastMath.nextRandomFloat() - 0.5f) * 10,
                    (FastMath.nextRandomFloat() - 0.5f) * 10);
        }
    }
}