/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.aitest.gui;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.niftygui.NiftyJmeDisplay;
import com.jme3.scene.Node;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import org.aitest.AIGameManager;
import org.aitest.AIUpdateManager;

/**
 *
 * @author mifthbeat
 */
public class AIGuiManager extends AbstractAppState implements ScreenController {

    private Nifty nifty;
    private NiftyJmeDisplay niftyDisplay;
    private Application app;
    private Screen gameScreen;
    private Element popupKeys;

    public AIGuiManager(Application app) {

        this.app = app;

        niftyDisplay = new NiftyJmeDisplay(this.app.getAssetManager(),
                this.app.getInputManager(),
                this.app.getAudioRenderer(),
                this.app.getGuiViewPort());

        // attach the nifty display to the gui view port as a processor
        this.app.getGuiViewPort().addProcessor(niftyDisplay);

        nifty = niftyDisplay.getNifty();

        nifty.registerScreenController(this);
        nifty.addXml("Interface/Main/basicPopups.xml");
        nifty.addXml("Interface/Main/basicGui.xml");
        
        
//        nifty.setIgnoreKeyboardEvents(true);
        nifty.gotoScreen("aiDemo"); // start the screen 
        gameScreen = nifty.getScreen("aiDemo");
        
        popupKeys = nifty.createPopup("popupKeys");
        popupKeys.disable();
        
        gameScreen.getFocusHandler().resetFocusElements();
        
    }

    // AppState Method
    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);

    }

    public void setDebugButton() {
        
        AIGameManager gameManager =  app.getStateManager().getState(AIGameManager.class);

        if (gameManager.isGameDebug()) {
            gameManager.setGameDebug(false);
        } else {
            gameManager.setGameDebug(true);
        }
        gameScreen.getFocusHandler().resetFocusElements();
    }
    
    public void helpButton(String str) {
        if (str.equals("true")) {
            popupKeys.enable();
            nifty.showPopup(nifty.getCurrentScreen(), popupKeys.getId(), null);
        } else {
            nifty.closePopup(popupKeys.getId());
            popupKeys.disable();
            popupKeys.getFocusHandler().resetFocusElements();
            gameScreen.getFocusHandler().resetFocusElements();
        }
    }
    
    public void resetSceneButton() {
        app.getStateManager().getState(AIGameManager.class).reloadScene();
        gameScreen.getFocusHandler().resetFocusElements();
    }
    
    
    // AppState Method
    @Override
    public void update(float tpf) {
        
        // Update only for fixed rate
        if (app.getStateManager().getState(AIUpdateManager.class).IsUpdate()) {
        }
        
    }

    // AppState Method
    @Override
    public void cleanup() {
        super.cleanup();

    }

    // Nifty Method
    public void bind(Nifty nifty, Screen screen) {
    }

    // Nifty Method
    public void onStartScreen() {
    }

    // Nifty Method
    public void onEndScreen() {
    }
}
