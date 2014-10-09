//Copyright (c) 2014, Jesús Martín Berlanga. All rights reserved. Distributed under the BSD licence. Read "com/jme3/ai/license.txt".
package steeringDemos.demos;

import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;

import com.jme3.ai.agents.Agent;
import com.jme3.ai.agents.behaviours.npc.steering.SeparationBehaviour;
import com.jme3.ai.agents.behaviours.npc.SimpleMainBehaviour;
import com.jme3.ai.agents.behaviours.npc.steering.BalancedCompoundSteeringBehaviour;
import com.jme3.ai.agents.behaviours.npc.steering.CompoundSteeringBehaviour;
import com.jme3.ai.agents.behaviours.npc.steering.MoveBehaviour;
import com.jme3.ai.agents.behaviours.npc.steering.PursuitBehaviour;
import com.jme3.ai.agents.behaviours.npc.steering.WanderBehaviour;
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
 * @version 2.0
 */
public class PursuitDemo extends BasicDemo {

    private SeparationBehaviour separation[];
    private CompoundSteeringBehaviour targetSteer;
    Agent target;
    private boolean isStrengthScalar = true;
    private float scalarStrength = 0.1f;
    private BitmapText scalarStrengthHudText;
    private BitmapText scalarInfoHudtext;
    private final String ESCALAR_INFO_HUD_MESSAGE = "Press H to increase the separation escalar strength, L to decrease.";
    MoveBehaviour targetMoveBehavior;
    WanderBehaviour targetWanderBehavior;
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
        hudText.setText("Press N to switch betwen escalar 'separation strength' and 'plane strength'.");
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
        this.scalarInfoHudtext.setText(this.ESCALAR_INFO_HUD_MESSAGE);
        // position
        this.scalarInfoHudtext.setLocalTranslation(0, 450, 0);
        guiNode.attachChild(scalarInfoHudtext);

        this.steerControl = new CustomSteerControl(11, 30);
        this.steerControl.setCameraSettings(getCamera());
        this.steerControl.setFlyCameraSettings(getFlyByCamera());

        //defining rootNode for aiAppState processing
        aiAppState.setApp(this);
        aiAppState.setGameControl(this.steerControl);

        Vector3f[] spawnArea = null;
        this.numberNeighbours = 25;

        target = this.createBoid("Target", this.targetColor, 0.11f);

        //Add the target to the aiAppState
        aiAppState.addAgent(target);
        this.setStats(
                target,
                this.targetMoveSpeed,
                this.targetRotationSpeed,
                this.targetMass,
                this.targetMaxForce);
        aiAppState.getGameControl().spawn(target, new Vector3f());

        Agent[] neighbours = new Agent[this.numberNeighbours];

        for (int i = 0; i < this.numberNeighbours; i++) {
            neighbours[i] = this.createBoid("Neighbour " + i, this.neighboursColor, 0.11f);
            //Add the neighbours to the aiAppState
            aiAppState.addAgent(neighbours[i]);
            this.setStats(
                    neighbours[i],
                    this.neighboursMoveSpeed,
                    this.neighboursRotationSpeed,
                    this.neighboursMass,
                    this.neighboursMaxForce);
            aiAppState.getGameControl().spawn(neighbours[i], spawnArea);
        }

        List<GameEntity> obstacles = new ArrayList<GameEntity>();
        obstacles.addAll(Arrays.asList(neighbours));

        //adding behaviours to target
        SimpleMainBehaviour targetMainBehaviour = new SimpleMainBehaviour(target);
        targetMoveBehavior = new MoveBehaviour(target);
        targetMoveBehavior.setupStrengthControl(0.25f);
        targetWanderBehavior = new WanderBehaviour(target);
        targetMoveBehavior.setMoveDirection(new Vector3f(1, 0, 1)); //moves in x-y direction

        this.iterationTimer = new Timer(10000, this.changeDinamicMode); //10000ns = 10s
        this.iterationTimer.start();

        targetSteer = new CompoundSteeringBehaviour(target);
        targetSteer.addSteerBehaviour(targetMoveBehavior);
        targetSteer.addSteerBehaviour(targetWanderBehavior);

        float randomDistance = ((float) Math.random()) * 1000f;

        targetMoveBehavior.setMoveDirection(new Vector3f(randomDistance, randomDistance, randomDistance));

        targetMainBehaviour.addBehaviour(targetSteer);
        target.setMainBehaviour(targetMainBehaviour);

        SimpleMainBehaviour[] neighboursMainBehaviour = new SimpleMainBehaviour[neighbours.length];

        separation = new SeparationBehaviour[neighbours.length];

        //adding pursuit behaviours to agents
        for (int i = 0; i < neighbours.length; i++) {
            neighboursMainBehaviour[i] = new SimpleMainBehaviour(neighbours[i]);

            PursuitBehaviour pursuit = new PursuitBehaviour(
                    neighbours[i],
                    target);

            separation[i] = new SeparationBehaviour(neighbours[i], obstacles);
            separation[i].setupStrengthControl(scalarStrength);

            BalancedCompoundSteeringBehaviour neighSteer = new BalancedCompoundSteeringBehaviour(neighbours[i]);
            neighSteer.addSteerBehaviour(separation[i]);
            neighSteer.addSteerBehaviour(pursuit);
            neighboursMainBehaviour[i].addBehaviour(neighSteer);
            neighbours[i].setMainBehaviour(neighboursMainBehaviour[i]);
        }

        aiAppState.start();
    }

    @Override
    public void simpleUpdate(float tpf) {
        aiAppState.update(tpf);

        if (this.isStrengthScalar) {
            scalarStrengthHudText.setText(String.valueOf(scalarStrength));
            this.scalarInfoHudtext.setText(this.ESCALAR_INFO_HUD_MESSAGE);
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
            for (SeparationBehaviour behaviour : this.separation) {
                behaviour.setupStrengthControl(1, 0, 1);
            }

            targetSteer.setupStrengthControl(1, 0, 1);

            this.isStrengthScalar = false;
        } else {
            for (SeparationBehaviour behaviour : this.separation) {
                behaviour.setupStrengthControl(scalarStrength);
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

            for (SeparationBehaviour behaviour : this.separation) {
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

            for (SeparationBehaviour behaviour : this.separation) {
                behaviour.setupStrengthControl(this.scalarStrength);
            }
        }

    }
}