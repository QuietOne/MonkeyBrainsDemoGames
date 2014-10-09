//Copyright (c) 2014, Jesús Martín Berlanga. All rights reserved. Distributed under the BSD licence. Read "com/jme3/ai/license.txt".
package steeringDemos.demos;

import com.jme3.ai.agents.Agent;
import com.jme3.ai.agents.behaviours.npc.SimpleMainBehaviour;
import com.jme3.ai.agents.behaviours.npc.steering.CompoundSteeringBehaviour;
import com.jme3.ai.agents.behaviours.npc.steering.PursuitBehaviour;
import com.jme3.ai.agents.behaviours.npc.steering.SeekBehaviour;
import com.jme3.ai.agents.behaviours.npc.steering.SeparationBehaviour;
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
 * @version 1.0
 */
public class PriorityDemo extends BasicDemo {

    private final String HUD_LAYER_TEXT = "Current priority layer: ";
    private BitmapText hudText;

    //The purpuse of this class is to save the active layer inside the activeLayer variable
    private class DebugCompoundSteeringBehaviour extends CompoundSteeringBehaviour {

        private DebugCompoundSteeringBehaviour(Agent agent) {
            super(agent);
        }
        private int activeLayer = Integer.MAX_VALUE; //We can see which is the active layer

        @Override
        protected Vector3f calculateRawSteering() {

            Vector3f totalForce = new Vector3f();
            float totalBraking = 1;

            this.behaviours.moveAtBeginning();

            if (!this.behaviours.nullPointer()) {
                int currentLayer = this.behaviours.getLayer();
                int inLayerCounter = 0;
                int validCounter = 0;

                while (!this.behaviours.nullPointer()) {
                    if (this.behaviours.getLayer() != currentLayer) //We have finished the last layer, check If it was a valid layer
                    {
                        if (inLayerCounter == validCounter) {
                            break; //If we have a valid layer, return the force
                        } else {
                            totalForce = new Vector3f(); //If not, reset the total force
                            totalBraking = 1;            //and braking
                        }

                        currentLayer = this.behaviours.getLayer();
                        inLayerCounter = 0;
                        validCounter = 0;
                    }

                    Vector3f force = this.calculatePartialForce(this.behaviours.getBehaviour());
                    if (force.length() > this.behaviours.getMinLengthToInvalidSteer()) {
                        validCounter++;
                    }
                    totalForce = totalForce.add(force);
                    totalBraking *= this.behaviours.getBehaviour().getBrakingFactor();

                    this.activeLayer = this.behaviours.getLayer();

                    inLayerCounter++;
                    this.behaviours.moveNext();
                }
            }

            this.setBrakingFactor(totalBraking);
            return totalForce;
        }
    }
    private Agent target;
    private Agent seeker;
    private SeekBehaviour targetMove;
    private DebugCompoundSteeringBehaviour targetSteer;
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

        //defining rootNode for aiAppState processing
        aiAppState.setApp(this);
        aiAppState.setGameControl(new CustomSteerControl(5f));

        target = this.createBoid("Target", this.targetColor, 0.11f);

        for (Vector3f loc : this.locations) {
            this.createSphereHelper("Sphere " + loc.toString(), ColorRGBA.Yellow, 0.05f, loc);
        }

        aiAppState.addAgent(target); //Add the target to the aiAppState
        aiAppState.getGameControl().spawn(target, Vector3f.ZERO);
        this.setStats(
                target,
                this.targetMoveSpeed,
                this.targetRotationSpeed,
                this.targetMass,
                this.targetMaxForce);

        seeker = this.createBoid("Target", this.neighboursColor, 0.11f);

        this.neighboursMoveSpeed = 3f;
        aiAppState.addAgent(seeker); //Add the target to the aiAppState
        aiAppState.getGameControl().spawn(seeker, new Vector3f(10, 10, 10));
        this.setStats(
                seeker,
                this.neighboursMoveSpeed,
                this.neighboursRotationSpeed,
                this.neighboursMass,
                this.neighboursMaxForce);

        SimpleMainBehaviour seekerMainBehaviour = new SimpleMainBehaviour(seeker);
        PursuitBehaviour pursuit = new PursuitBehaviour(seeker, target);
        pursuit.setupStrengthControl(5f);
        seekerMainBehaviour.addBehaviour(pursuit);
        seeker.setMainBehaviour(seekerMainBehaviour);

        SimpleMainBehaviour targetMainBehaviour = new SimpleMainBehaviour(target);

        this.targetMove = new SeekBehaviour(target, this.locations[0]);
        this.currentFocus = 0;
        ArrayList<GameEntity> separationObstacle = new ArrayList<GameEntity>();
        separationObstacle.add(seeker);
        SeparationBehaviour separation = new SeparationBehaviour(target, separationObstacle);

        targetSteer = new DebugCompoundSteeringBehaviour(target);
        targetSteer.addSteerBehaviour(targetMove);
        targetSteer.addSteerBehaviour(separation, 1, 0.1f);

        targetMainBehaviour.addBehaviour(this.targetSteer);
        target.setMainBehaviour(targetMainBehaviour);

        hudText = new BitmapText(guiFont, false);
        hudText.setSize(guiFont.getCharSet().getRenderedSize() * 1.25f);      // font size
        hudText.setColor(ColorRGBA.Red);                             // font color
        hudText.setText("");             // the text
        hudText.setLocalTranslation(0, 475, 0); // position

        guiNode.attachChild(hudText);
        aiAppState.start();
    }

    @Override
    public void simpleUpdate(float tpf) {
        aiAppState.update(tpf);

        if (this.target.distanceFromPosition(this.locations[this.currentFocus]) < 0.01f) {
            this.currentFocus++;

            if (this.currentFocus > this.locations.length - 1) {
                this.currentFocus = 0;
            }

            this.targetMove.setSeekingPosition(this.locations[this.currentFocus]);
        }

        this.hudText.setText(this.HUD_LAYER_TEXT + this.targetSteer.activeLayer);

        if (this.seeker.distanceRelativeToGameObject(this.target) < 0.25f) {
            this.seeker.setLocalTranslation(
                    (FastMath.nextRandomFloat() - 0.5f) * 10,
                    (FastMath.nextRandomFloat() - 0.5f) * 10,
                    (FastMath.nextRandomFloat() - 0.5f) * 10);
        }
    }
}
