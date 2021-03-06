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
package org.aitest.gui;

import com.jme3.ai.agents.util.control.MonkeyBrainsAppState;
import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.niftygui.NiftyJmeDisplay;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import org.aitest.ai.control.AIGameUpdateManager;
import org.aitest.ai.control.AIGameControl;

/**
 * Class for managing GUI.
 *
 * @author mifthbeat
 * @version 1.0.0
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

    public void setDebugButton() {
        AIGameControl control = (AIGameControl) MonkeyBrainsAppState.getInstance().getGameControl();
        if (control.isGameDebug()) {
            control.setGameDebug(false);
        } else {
            control.setGameDebug(true);
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
        MonkeyBrainsAppState.getInstance().getGameControl().restart();
        gameScreen.getFocusHandler().resetFocusElements();
    }

    // AppState Method
    @Override
    public void update(float tpf) {
        // Update only for fixed rate
        if (app.getStateManager().getState(AIGameUpdateManager.class).IsUpdate()) {
        }
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
