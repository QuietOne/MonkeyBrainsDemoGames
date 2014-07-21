package org.aitest;

import org.aitest.ai.control.AIGameUpdateManager;
import com.jme3.ai.agents.util.control.Game;
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
 * @version 1.0
 */
public class AIGame extends SimpleApplication {

    //Defining game
    private Game game = Game.getInstance();

    public static void main(String[] args) {
        AIGame app = new AIGame();
        app.start();
    }

    @Override
    public void simpleInitApp() {
        //defining app
        game.setApp(this);
        
        //setting game control
        game.setGameControl(new AIGameControl());
        
        //registering input
        game.getGameControl().setInputManagerMapping();
        
        //setting camera
        game.getGameControl().setCameraSettings(cam);
        
        //setting flying camera
        game.getGameControl().setFlyCameraSettings(flyCam);
        
        //setting background game color
        viewPort.setBackgroundColor(ColorRGBA.DarkGray);
        
        //setting lightings
        AIGameSpatials.getInstance().setGameLighting();

        //approves updates for controls
        ////////this maybe add to Game update
        AIGameUpdateManager updateManager = new AIGameUpdateManager();
        stateManager.attach(updateManager);
        
        //starting game physics
        BulletAppState bulletState = new BulletAppState();
        bulletState.setThreadingType(BulletAppState.ThreadingType.PARALLEL);
        stateManager.attach(bulletState);

        //loading scene used in game
        //It loads all needed graphics and it creates agents for game
        ((AIGameControl) game.getGameControl()).loadScene();
        
        //initializing GUI
        AIGuiManager guiManager = new AIGuiManager(this);
        stateManager.attach(guiManager);

        //setting game options
        game.setFriendlyFire(false);

        //starting game (enabling the agents)
        //without this agents wouldn't do anything
        game.start();
        
        stateManager.attach(game);
    }
}
