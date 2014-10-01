//Copyright (c) 2014, Jesús Martín Berlanga. All rights reserved. Distributed under the BSD licence. Read "com/jme3/ai/license.txt".
package steeringDemos.control;

import com.jme3.ai.agents.Agent;
import com.jme3.ai.agents.util.GameEntity;
import com.jme3.ai.agents.util.control.AIAppState;
import com.jme3.ai.agents.util.control.AIControl;

import com.jme3.input.FlyByCamera;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;

/**
 * Custom steer control.
 *
 * @author Jesús Martín Berlanga
 * @version 1.3
 */
public class CustomSteerControl implements AIControl {

    private float cameraMoveSpeed;

    public void setInputManagerMapping() {
    }

    public void setCameraSettings(Camera cam) {
        cam.setLocation(new Vector3f(0, 20, 0));
        cam.lookAt(Vector3f.ZERO, Vector3f.UNIT_X);
    }

    public void setFlyCameraSettings(FlyByCamera flyCam) {
        flyCam.setMoveSpeed(this.cameraMoveSpeed);
    }
    private float aleatoryFactorX, aleatoryFactorY, aleatoryFactorZ;
    private AIAppState game;
    //private InputManager inputManager;

    public CustomSteerControl(float cameraMoveSpeed) {
        game = AIAppState.getInstance();
        this.cameraMoveSpeed = cameraMoveSpeed;
        this.aleatoryFactorX = 10f;
        this.aleatoryFactorY = 10f;
        this.aleatoryFactorZ = 10f;
        //inputManager = game.getInputManager();
    }

    public CustomSteerControl(float cameraMoveSpeed, float aleatoryFactor) {
        game = AIAppState.getInstance();
        this.cameraMoveSpeed = cameraMoveSpeed;
        this.aleatoryFactorX = aleatoryFactor;
        this.aleatoryFactorY = aleatoryFactor;
        this.aleatoryFactorZ = aleatoryFactor;
    }

    public CustomSteerControl(float cameraMoveSpeed, float aleatoryFactorX, float aleatoryFactorY, float aleatoryFactorZ) {
        game = AIAppState.getInstance();
        this.cameraMoveSpeed = cameraMoveSpeed;
        this.aleatoryFactorX = aleatoryFactorX;
        this.aleatoryFactorY = aleatoryFactorY;
        this.aleatoryFactorZ = aleatoryFactorZ;
    }

    /**
     * There is no imput mapping by default
     *
     * @see AIControl#loadInputManagerMapping()
     */
    public void loadInputManagerMapping() {
        //Void
    }

    /**
     * @see AIControl#finish()
     *
     * @return Always return false.
     */
    public boolean finish() {
        return false;
    }

    /**
     * @see AIControl#win(com.jme3.ai.agents.Agent)
     *
     * @param agent
     * @return Always return false.
     */
    public boolean win(Agent agent) {
        return false;
    }

    /**
     * There is no restart
     *
     * @see AIControl#restart()
     */
    public void restart() {
        //Void
    }

    /**
     * Method for creating objects in given area.
     *
     * <b> PRE: </b> The area must be void or a point. <br>
     * <br>
     *
     * @param gameObject object that should be created
     * @param area Null for random location and a point for a stablished
     * location.
     *
     * @see AIControl#spawn(com.jme3.ai.agents.util.GameEntity,
     * com.jme3.math.Vector3f[])
     */
    public void spawn(GameEntity gameObject, Vector3f... area) {

        if (area == null) {
            //Random location
            gameObject.setLocalTranslation(
                    ((float) ((FastMath.nextRandomFloat() * 2) - 1)) * this.aleatoryFactorX,
                    ((float) ((FastMath.nextRandomFloat() * 2) - 1)) * this.aleatoryFactorY,
                    ((float) ((FastMath.nextRandomFloat() * 2) - 1)) * this.aleatoryFactorZ);
        } else if (area.length == 1) {
            //Spawn in a point
            gameObject.setLocalTranslation(area[0]);
        }

        if (gameObject instanceof Agent) {
            game.addAgent((Agent) gameObject);
        } else {
            game.addGameEntity(gameObject);
        }
    }
}