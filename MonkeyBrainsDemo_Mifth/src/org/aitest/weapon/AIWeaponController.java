/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.aitest.weapon;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import org.aitest.AIGameManager;
import org.aitest.AIUpdateManager;

/**
 *
 * @author mifthbeat
 */
public class AIWeaponController extends AbstractAppState {
    
    private Geometry bullet, swordMesh;
    private  Application app;

    public AIWeaponController(Application app) {
        
        this.app = app;
        
        // Setup Bullet
        Box b = new Box(Vector3f.ZERO, 1f, 1f, 1f);
        bullet = new Geometry("Box", b);
        bullet.setLocalScale(0.1f, 0.1f, 0.5f);
        Material mat_bullet = new Material(this.app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        mat_bullet.setColor("Color", ColorRGBA.Red);
        bullet.setMaterial(mat_bullet);
        
    }
    
    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
    }
    
    @Override
    public void update(float tpf) {
        
        // Update only for fixed rate
        if (app.getStateManager().getState(AIUpdateManager.class).IsUpdate()) {
        }

    }
    
    @Override
    public void cleanup() {
        super.cleanup();
    }

    public Geometry getBullet() {
        return bullet;
    }

    
}
