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
package org.aitest;

import org.aitest.ai.control.AIGameUpdateManager;
import com.jme3.ai.agents.util.control.MonkeyBrainsAppState;
import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.math.ColorRGBA;
import org.aitest.gui.AIGuiManager;
import org.aitest.ai.control.AIGameControl;
import org.aitest.ai.control.HPControl;
import org.aitest.ai.utils.AIGameSpatials;

/**
 * Main game class.
 *
 * @author normenhansen
 * @author Tihomir Radosavljević
 * @version 1.1.0
 */
public class AIGame extends SimpleApplication {

    //Defining game
    private MonkeyBrainsAppState brainsAppState = MonkeyBrainsAppState.getInstance();

    public static void main(String[] args) {
        AIGame app = new AIGame();
        app.start();
    }

    @Override
    public void simpleInitApp() {
        //defining app
        brainsAppState.setApp(this);
        
        //setting hp control
        brainsAppState.setHitPointsControl(new HPControl());

        //setting game control
        brainsAppState.setGameControl(new AIGameControl());

        //registering input
        brainsAppState.getGameControl().setInputManagerMapping();

        //setting camera
        brainsAppState.getGameControl().setCameraSettings(cam);

        //setting flying camera
        brainsAppState.getGameControl().setFlyCameraSettings(flyCam);

        //setting background game color
        viewPort.setBackgroundColor(ColorRGBA.DarkGray);

        //setting lightings
        AIGameSpatials.getInstance().setGameLighting();

        //approves updates for controls
        ////////this maybe add to MonkeyBrainsAppState update
        AIGameUpdateManager updateManager = new AIGameUpdateManager();
        stateManager.attach(updateManager);

        //starting game physics
        BulletAppState bulletState = new BulletAppState();
        bulletState.setThreadingType(BulletAppState.ThreadingType.PARALLEL);
        stateManager.attach(bulletState);

        //loading scene used in game
        //It loads all needed graphics and it creates agents for game
        ((AIGameControl) brainsAppState.getGameControl()).loadScene();

        //initializing GUI
        AIGuiManager guiManager = new AIGuiManager(this);
        stateManager.attach(guiManager);

        //setting game options
        brainsAppState.setFriendlyFire(true);

        //starting game (enabling the agents)
        //without this agents wouldn't do anything
        brainsAppState.start();

        stateManager.attach(brainsAppState);
    }
}
