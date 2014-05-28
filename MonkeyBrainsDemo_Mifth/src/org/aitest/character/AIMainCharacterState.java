/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.aitest.character;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.input.ChaseCamera;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;

/**
 *
 * @author mifthbeat
 */
public class AIMainCharacterState extends AbstractAppState implements AnalogListener, ActionListener {

    private Application app;
    private String[] mappings;
    private ChaseCamera chaseCam;
    private AICharacterControl charCtrl;

    public AIMainCharacterState(AICharacterControl charCtrl) {
        this.charCtrl = charCtrl;
    }
    
    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);

        this.app = app;
        
        chaseCam = new ChaseCamera(this.app.getCamera(), charCtrl.getCharNode(), this.app.getInputManager());
        chaseCam.setToggleRotationTrigger(new MouseButtonTrigger(MouseInput.BUTTON_MIDDLE));
        chaseCam.setUpVector(Vector3f.UNIT_Y);
        
        SimpleApplication sapp = (SimpleApplication) app;
        sapp.getFlyByCamera().setEnabled(false);
        
        chaseCam.setEnabled(true);

        mappings = new String[]{
            "mouseLeftClick",
            "mouseRightClick",
            "WKeyChar",
            "AKeyChar",
            "SKeyChar",
            "DKeyChar"
        };

        setupKeys();

    }

    private void setupKeys() {

        InputManager inputManager = app.getInputManager();

        inputManager.addMapping("mouseLeftClick", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        inputManager.addMapping("mouseRightClick", new MouseButtonTrigger(MouseInput.BUTTON_RIGHT));
        app.getInputManager().addMapping("WKeyChar", new KeyTrigger(KeyInput.KEY_W));
        app.getInputManager().addMapping("AKeyChar", new KeyTrigger(KeyInput.KEY_A));
        app.getInputManager().addMapping("SKeyChar", new KeyTrigger(KeyInput.KEY_S));
        app.getInputManager().addMapping("DKeyChar", new KeyTrigger(KeyInput.KEY_D));

        addListener();

        inputManager = null;

    }

    public void addListener() {
        app.getInputManager().addListener(this, mappings);
    }

    public void removeListener() {
        app.getInputManager().removeListener(this);
    }

    public void onAnalog(String name, float value, float tpf) {
        if (name.equals("AKeyChar")){
            charCtrl.setDoRotate(true);
            charCtrl.setRotateLeft(true);
        } else if (name.equals("DKeyChar")){
            charCtrl.setDoRotate(true);
            charCtrl.setRotateLeft(false);
        } else if (name.equals("WKeyChar")){
            charCtrl.setDoMove(true);
            charCtrl.setMoveForward(true);
        } else if (name.equals("SKeyChar")){
            charCtrl.setDoMove(true);
            charCtrl.setMoveForward(false);
        }
    }

    public void onAction(String name, boolean isPressed, float tpf) {
    }

    @Override
    public void update(float tpf) {
    }

    @Override
    public void cleanup() {
        super.cleanup();

    }
}
