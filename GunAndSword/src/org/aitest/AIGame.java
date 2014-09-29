package org.aitest;

import org.aitest.ai.control.AIGameUpdateManager;
import com.jme3.ai.agents.util.control.AIAppState;
import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.math.ColorRGBA;
import org.aitest.gui.AIGuiManager;
import org.aitest.ai.control.AIGameControl;
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
    private AIAppState aiAppState = AIAppState.getInstance();

    public static void main(String[] args) {
        AIGame app = new AIGame();
        app.start();
    }

    @Override
    public void simpleInitApp() {
        //defining app
        aiAppState.setApp(this);

        //setting game control
        aiAppState.setAIControl(new AIGameControl());

        //registering input
        aiAppState.getAIControl().setInputManagerMapping();

        //setting camera
        aiAppState.getAIControl().setCameraSettings(cam);

        //setting flying camera
        aiAppState.getAIControl().setFlyCameraSettings(flyCam);

        //setting background game color
        viewPort.setBackgroundColor(ColorRGBA.DarkGray);

        //setting lightings
        AIGameSpatials.getInstance().setGameLighting();

        //approves updates for controls
        ////////this maybe add to AIAppState update
        AIGameUpdateManager updateManager = new AIGameUpdateManager();
        stateManager.attach(updateManager);

        //starting game physics
        BulletAppState bulletState = new BulletAppState();
        bulletState.setThreadingType(BulletAppState.ThreadingType.PARALLEL);
        stateManager.attach(bulletState);

        //loading scene used in game
        //It loads all needed graphics and it creates agents for game
        ((AIGameControl) aiAppState.getAIControl()).loadScene();

        //initializing GUI
        AIGuiManager guiManager = new AIGuiManager(this);
        stateManager.attach(guiManager);

        //setting game options
        aiAppState.setFriendlyFire(false);

        //starting game (enabling the agents)
        //without this agents wouldn't do anything
        aiAppState.start();

        stateManager.attach(aiAppState);
    }
}
