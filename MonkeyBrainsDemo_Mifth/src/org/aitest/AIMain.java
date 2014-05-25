package org.aitest;

import org.aitest.gui.AIGuiManager;
import com.jme3.app.SimpleApplication;
import com.jme3.asset.DesktopAssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;

/**
 * test
 *
 * @author normenhansen
 */
public class AIMain extends SimpleApplication {

    public static void main(String[] args) {
        AIMain app = new AIMain();
        app.start();
    }

    @Override
    public void simpleInitApp() {

        // set camera
        cam.setLocation(new Vector3f(6.7807055f, 22.863451f, 19.72432f));
        cam.setRotation(new Quaternion(-0.08801406f, 0.9110843f, -0.26273727f, -0.30520147f));
        flyCam.setZoomSpeed(0f);
        flyCam.setMoveSpeed(50f);
        flyCam.setDragToRotate(true);
        
        viewPort.setBackgroundColor(ColorRGBA.DarkGray);
        
        // set lights
        AmbientLight amb = new AmbientLight();
        amb.setColor(new ColorRGBA(0.7f, 0.8f, 1.0f, 1f));
        rootNode.addLight(amb);
        
        DirectionalLight dl = new DirectionalLight();
        dl.setDirection(new Vector3f(-0.5501984f, -0.6679371f, 0.5011405f));
        dl.setColor(new ColorRGBA(1.0f, 1.0f, 0.7f, 1f));
        rootNode.addLight(dl);
        
        BulletAppState bulletState = new BulletAppState();
        stateManager.attach(bulletState);

        AIGameManager aiManager = new AIGameManager((DesktopAssetManager) assetManager, this);
        stateManager.attach(aiManager);
        aiManager.loadScene();
//        rootNode.attachChild(scene);
        
        AIGuiManager guiManager = new AIGuiManager(this);
        stateManager.attach(aiManager);

    }

    @Override
    public void simpleUpdate(float tpf) {
        //TODO: add update code
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }
}
