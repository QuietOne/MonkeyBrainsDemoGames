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

import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.ai.agents.Agent;
import com.jme3.ai.agents.behaviors.npc.steering.SeparationBehavior;
import com.jme3.ai.agents.behaviors.npc.SimpleMainBehavior;
import com.jme3.ai.agents.behaviors.npc.steering.BalancedCompoundSteeringBehavior;
import com.jme3.ai.agents.behaviors.npc.steering.CompoundSteeringBehavior;
import com.jme3.ai.agents.behaviors.npc.steering.MoveBehavior;
import com.jme3.ai.agents.behaviors.npc.steering.PursuitBehavior;
import com.jme3.ai.agents.behaviors.npc.steering.WanderBehavior;
import com.jme3.ai.agents.util.GameEntity;
import com.jme3.font.BitmapText;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import steeringDemos.BasicDemo;
import steeringDemos.control.CustomSteerControl;
import java.util.ArrayList;
import java.awt.event.ActionEvent;
import javax.swing.Timer;
import java.util.List;
import java.util.Arrays;

/**
 * Pursuit demo
 *
 * @author Jesús Martín Berlanga
 * @version 2.0.0
 */
public class PursuitDemo extends BasicDemo {

    private SeparationBehavior separation[];
    private CompoundSteeringBehavior targetSteer;
    private Agent target;
    private boolean isStrengthScalar = true;
    private float scalarStrength = 0.1f;
    private BitmapText scalarStrengthHudText;
    private BitmapText scalarInfoHudtext;
    private final String SCALAR_INFO_HUD_MESSAGE = "Press H to increase the separation scalar strength, L to decrease.";
    private MoveBehavior targetMoveBehavior;
    private WanderBehavior targetWanderBehavior;
    private boolean turnDinamicMode = true;
    private java.awt.event.ActionListener changeDinamicMode = new java.awt.event.ActionListener() {
        public void actionPerformed(ActionEvent event) {
            if (turnDinamicMode) {
                targetMoveBehavior.setupStrengthControl(1);
                targetWanderBehavior.setupStrengthControl(0);
                turnDinamicMode = false;
            } else {
                targetMoveBehavior.setupStrengthControl(0.25f);
                targetWanderBehavior.setupStrengthControl(1);
                turnDinamicMode = true;
            }
        }
    };
    private Timer iterationTimer;

    public static void main(String[] args) {
        PursuitDemo app = new PursuitDemo();
        app.start();
    }

    @Override
    public void simpleInitApp() {

        //KEYS
        keys();

        //HUD TEXT
        BitmapText hudText = new BitmapText(guiFont, false);
        // font size
        hudText.setSize(guiFont.getCharSet().getRenderedSize() * 0.65f);
        // font color
        hudText.setColor(ColorRGBA.Red);
        // the text
        hudText.setText("Press N to switch betwen scalar 'separation strength' and 'plane strength'.");
        // position
        hudText.setLocalTranslation(0, 475, 0);
        guiNode.attachChild(hudText);

        this.scalarStrengthHudText = new BitmapText(guiFont, false);
        // font size
        this.scalarStrengthHudText.setSize(guiFont.getCharSet().getRenderedSize() * 0.65f);
        // font color
        this.scalarStrengthHudText.setColor(ColorRGBA.Orange);
        // the text
        this.scalarStrengthHudText.setText(String.valueOf(this.scalarStrength));
        // position
        this.scalarStrengthHudText.setLocalTranslation(0, 430, 0);
        guiNode.attachChild(scalarStrengthHudText);

        this.scalarInfoHudtext = new BitmapText(guiFont, false);
        // font size
        this.scalarInfoHudtext.setSize(guiFont.getCharSet().getRenderedSize() * 0.65f);
        // font color
        this.scalarInfoHudtext.setColor(ColorRGBA.Orange);
        // the text
        this.scalarInfoHudtext.setText(this.SCALAR_INFO_HUD_MESSAGE);
        // position
        this.scalarInfoHudtext.setLocalTranslation(0, 450, 0);
        guiNode.attachChild(scalarInfoHudtext);

        this.steerControl = new CustomSteerControl(11, 30);
        this.steerControl.setCameraSettings(getCamera());
        this.steerControl.setFlyCameraSettings(getFlyByCamera());

        //defining rootNode for brainsAppState processing
        brainsAppState.setApp(this);
        brainsAppState.setGameControl(this.steerControl);

        Vector3f[] spawnArea = null;
        this.numberNeighbours = 25;

        target = this.createBoid("Target", this.targetColor, 0.11f);

        //Add the target to the brainsAppState
        brainsAppState.addAgent(target);
        this.setStats(
                target,
                this.targetMoveSpeed,
                this.targetRotationSpeed,
                this.targetMass,
                this.targetMaxForce);
        brainsAppState.getGameControl().spawn(target, new Vector3f());

        Agent[] neighbours = new Agent[this.numberNeighbours];

        for (int i = 0; i < this.numberNeighbours; i++) {
            neighbours[i] = this.createBoid("Neighbour " + i, this.neighboursColor, 0.11f);
            //Add the neighbours to the brainsAppState
            brainsAppState.addAgent(neighbours[i]);
            this.setStats(
                    neighbours[i],
                    this.neighboursMoveSpeed,
                    this.neighboursRotationSpeed,
                    this.neighboursMass,
                    this.neighboursMaxForce);
            brainsAppState.getGameControl().spawn(neighbours[i], spawnArea);
        }

        List<GameEntity> obstacles = new ArrayList<GameEntity>();
        obstacles.addAll(Arrays.asList(neighbours));

        //adding behaviours to target
        SimpleMainBehavior targetMainBehavior = new SimpleMainBehavior(target);
        targetMoveBehavior = new MoveBehavior(target);
        targetMoveBehavior.setupStrengthControl(0.25f);
        targetWanderBehavior = new WanderBehavior(target);
        targetMoveBehavior.setMoveDirection(new Vector3f(1, 0, 1)); //moves in x-y direction

        this.iterationTimer = new Timer(10000, this.changeDinamicMode); //10000ns = 10s
        this.iterationTimer.start();

        targetSteer = new CompoundSteeringBehavior(target);
        targetSteer.addSteerBehavior(targetMoveBehavior);
        targetSteer.addSteerBehavior(targetWanderBehavior);

        float randomDistance = ((float) Math.random()) * 1000f;

        targetMoveBehavior.setMoveDirection(new Vector3f(randomDistance, randomDistance, randomDistance));

        targetMainBehavior.addBehavior(targetSteer);
        target.setMainBehavior(targetMainBehavior);

        SimpleMainBehavior[] neighboursMainBehavior = new SimpleMainBehavior[neighbours.length];

        separation = new SeparationBehavior[neighbours.length];

        //adding pursuit behaviours to agents
        for (int i = 0; i < neighbours.length; i++) {
            neighboursMainBehavior[i] = new SimpleMainBehavior(neighbours[i]);

            PursuitBehavior pursuit = new PursuitBehavior(
                    neighbours[i],
                    target);

            separation[i] = new SeparationBehavior(neighbours[i], obstacles);
            separation[i].setupStrengthControl(scalarStrength);

            BalancedCompoundSteeringBehavior neighSteer = new BalancedCompoundSteeringBehavior(neighbours[i]);
            neighSteer.addSteerBehavior(separation[i]);
            neighSteer.addSteerBehavior(pursuit);
            neighboursMainBehavior[i].addBehavior(neighSteer);
            neighbours[i].setMainBehavior(neighboursMainBehavior[i]);
        }

        brainsAppState.start();
    }

    @Override
    public void simpleUpdate(float tpf) {
        brainsAppState.update(tpf);

        if (this.isStrengthScalar) {
            scalarStrengthHudText.setText(String.valueOf(scalarStrength));
            this.scalarInfoHudtext.setText(this.SCALAR_INFO_HUD_MESSAGE);
        } else {
            scalarStrengthHudText.setText("");
            this.scalarInfoHudtext.setText("");
        }
    }

    /**
     * Custom Keybinding: Map named actions to inputs.
     */
    private void keys() {
        // You can map one or several inputs to one named action
        inputManager.addMapping("Switch mode", new KeyTrigger(KeyInput.KEY_N));
        inputManager.addMapping("Increase separation", new KeyTrigger(KeyInput.KEY_H));
        inputManager.addMapping("Decrease separation", new KeyTrigger(KeyInput.KEY_L));

        // Add the names to the action listener.
        inputManager.addListener(actionListener, "Switch mode");
        inputManager.addListener(analogListener, "Increase separation", "Decrease separation");

    }
    private ActionListener actionListener = new ActionListener() {
        public void onAction(String name, boolean keyPressed, float tpf) {
            if (name.equals("Switch mode") && !keyPressed) {
                changeMode();
            }

        }
    };

    private void changeMode() {
        if (this.isStrengthScalar) {
            for (SeparationBehavior behavior : this.separation) {
                behavior.setupStrengthControl(1, 0, 1);
            }
            targetSteer.setupStrengthControl(1, 0, 1);
            this.isStrengthScalar = false;
        } else {
            for (SeparationBehavior behavior : this.separation) {
                behavior.setupStrengthControl(scalarStrength);
            }
            targetSteer.turnOffStrengthControl();
            this.isStrengthScalar = true;
        }
    }
    private AnalogListener analogListener = new AnalogListener() {
        public void onAnalog(String name, float value, float tpf) {

            if (name.equals("Increase separation")) {
                increaseSeparation();
            } else if (name.equals("Decrease separation")) {
                decreaseSeparation();
            }

        }
    };

    private void increaseSeparation() {
        if (this.isStrengthScalar) {
            this.scalarStrength = this.scalarStrength + 0.05f;
            for (SeparationBehavior behaviour : this.separation) {
                behaviour.setupStrengthControl(this.scalarStrength);
            }
        }
    }

    private void decreaseSeparation() {
        if (this.isStrengthScalar) {
            this.scalarStrength = this.scalarStrength - 0.075f;
            if (this.scalarStrength < 0) {
                this.scalarStrength = 0;
            }
            for (SeparationBehavior behavior : this.separation) {
                behavior.setupStrengthControl(this.scalarStrength);
            }
        }
    }
}