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
package steeringDemos.control;

import com.jme3.ai.agents.Agent;
import com.jme3.ai.agents.util.GameEntity;
import com.jme3.ai.agents.util.control.MonkeyBrainsAppState;
import com.jme3.ai.agents.util.control.GameControl;
import com.jme3.input.FlyByCamera;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;

/**
 * Custom steer control.
 *
 * @author Jesús Martín Berlanga
 * @version 1.3.1
 */
public class CustomSteerControl implements GameControl {

    private float cameraMoveSpeed;
    private float aleatoryFactorX, aleatoryFactorY, aleatoryFactorZ;
    private MonkeyBrainsAppState brainsAppState = MonkeyBrainsAppState.getInstance();

    /**
     * There is no input mapping by default.
     */
    public void setInputManagerMapping() {
        //Void
    }

    public void setCameraSettings(Camera cam) {
        cam.setLocation(new Vector3f(0, 20, 0));
        cam.lookAt(Vector3f.ZERO, Vector3f.UNIT_X);
    }

    public void setFlyCameraSettings(FlyByCamera flyCam) {
        flyCam.setMoveSpeed(this.cameraMoveSpeed);
    }

    public CustomSteerControl(float cameraMoveSpeed) {
        this.cameraMoveSpeed = cameraMoveSpeed;
        this.aleatoryFactorX = 10f;
        this.aleatoryFactorY = 10f;
        this.aleatoryFactorZ = 10f;
    }

    public CustomSteerControl(float cameraMoveSpeed, float aleatoryFactor) {
        this.cameraMoveSpeed = cameraMoveSpeed;
        this.aleatoryFactorX = aleatoryFactor;
        this.aleatoryFactorY = aleatoryFactor;
        this.aleatoryFactorZ = aleatoryFactor;
    }

    public CustomSteerControl(float cameraMoveSpeed, float aleatoryFactorX, float aleatoryFactorY, float aleatoryFactorZ) {
        this.cameraMoveSpeed = cameraMoveSpeed;
        this.aleatoryFactorX = aleatoryFactorX;
        this.aleatoryFactorY = aleatoryFactorY;
        this.aleatoryFactorZ = aleatoryFactorZ;
    }

    /**
     * @see GameControl#finish()
     *
     * @return Always return false.
     */
    public boolean finish() {
        return false;
    }

    /**
     * @see GameControl#win(com.jme3.ai.agents.Agent)
     *
     * @param agent
     * @return Always return false.
     */
    public boolean win(Agent agent) {
        return false;
    }

    /**
     * There is no restart.
     *
     * @see GameControl#restart()
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
     * @param gameEntity entity that should be created
     * @param area Null for random location and a point for a stablished
     * location.
     *
     * @see GameControl#spawn(com.jme3.ai.agents.util.GameEntity,
     * com.jme3.math.Vector3f[])
     */
    public void spawn(GameEntity gameEntity, Vector3f... area) {

        if (area == null) {
            //Random location
            gameEntity.setLocalTranslation(
                    ((float) ((FastMath.nextRandomFloat() * 2) - 1)) * this.aleatoryFactorX,
                    ((float) ((FastMath.nextRandomFloat() * 2) - 1)) * this.aleatoryFactorY,
                    ((float) ((FastMath.nextRandomFloat() * 2) - 1)) * this.aleatoryFactorZ);
        } else if (area.length == 1) {
            //Spawn in a point
            gameEntity.setLocalTranslation(area[0]);
        }

        if (gameEntity instanceof Agent) {
            brainsAppState.addAgent((Agent) gameEntity);
        } else {
            brainsAppState.addGameEntity(gameEntity);
        }
    }
}