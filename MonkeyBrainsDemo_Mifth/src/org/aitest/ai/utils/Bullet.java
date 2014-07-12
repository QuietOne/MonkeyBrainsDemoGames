package org.aitest.ai.utils;

import com.jme3.ai.agents.util.AbstractBullet;
import com.jme3.ai.agents.util.AbstractWeapon;
import com.jme3.ai.agents.util.control.Game;
import com.jme3.app.Application;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.PhysicsCollisionObject;
import com.jme3.bullet.collision.PhysicsRayTestResult;
import com.jme3.bullet.control.GhostControl;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Line;
import java.util.List;
import org.aitest.ai.character.AIModel;
import org.aitest.ai.control.AIGameUpdateManager;
import org.aitest.ai.control.AIGameControl;

/**
 *
 * @author Tihomir Radosavljevic
 * @version 1.0
 */
public class Bullet extends AbstractBullet {

    private Vector3f vecMove, bornPlace, contactPoint;
    private boolean work;
    private float bulletLength;
    private float hit;
    private Spatial spToKill;
    private Application app;
    private Geometry geoRay;
    private float healthDestruction;
    
    public Bullet(AbstractWeapon weapon, Vector3f bornPlace, Geometry geometry, Application app, float healthDestruction) {
        super(weapon, geometry);
        
        spatial.setUserData("Type", "Bullet");

        this.bornPlace = bornPlace.clone();
        this.app = app;

        this.healthDestruction = healthDestruction;

        hit = 1000f;

        vecMove = spatial.getLocalRotation().mult(Vector3f.UNIT_Z).normalizeLocal().mult(4f);
        bulletLength = 70f;
        work = true;

        Vector3f vecStart = this.bornPlace;
        Vector3f vecEnd = this.bornPlace.add(spatial.getLocalRotation().mult(Vector3f.UNIT_Z).normalizeLocal().mult(bulletLength));

        BulletAppState bulState = this.app.getStateManager().getState(BulletAppState.class);
        List<PhysicsRayTestResult> rayTest = bulState.getPhysicsSpace().rayTest(vecStart, vecEnd);
        if (rayTest.size() > 0) {
            for (Object obj : rayTest) {
                PhysicsRayTestResult getObject = (PhysicsRayTestResult) obj;
                float fl = getObject.getHitFraction();
                PhysicsCollisionObject collisionObject = getObject.getCollisionObject();
                Spatial spThis = (Spatial) collisionObject.getUserObject();

                // Get the Enemy to skill
                if (fl < hit && spThis.getControl(GhostControl.class) == null) {
                    hit = fl;
                    spToKill = spThis;
                }
            }

//            System.out.println(rayTest.size());
            if (spToKill != null) {
                contactPoint = vecStart.clone().interpolate(vecEnd, hit);
                // set destruction
                if (spToKill.getControl(AIModel.class) != null) {
                    AIModel aiCharCtrl = spToKill.getControl(AIModel.class);
                    //aiCharCtrl.substractHealth(this.healthDestruction);
                    Game.getInstance().agentAttack(null, null);
                }
            }
        }

        // testRay forDebug
        if (((AIGameControl) Game.getInstance().getGameControl()).isGameDebug()) {
            Vector3f endVecToDebug = vecEnd;
            if (contactPoint != null) {
                endVecToDebug = contactPoint;
            }
            geoRay = new Geometry("line", new Line(vecStart, endVecToDebug));
            Material mat_bullet = new Material(app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
            mat_bullet.setColor("Color", ColorRGBA.Cyan);
            geoRay.setMaterial(mat_bullet);
            Node root = (Node) this.app.getViewPort().getScenes().get(0);
            root.attachChild(geoRay);
        }


    }

    protected void destroy() {
        if (geoRay != null) {
            geoRay.removeFromParent();
            geoRay = null;
        }
        work = false;
        spatial.removeFromParent();
        spatial.removeControl(this);
        spatial = null;
    }
    
    
    
    @Override
    protected void controlUpdate(float tpf) {
        // Update only for fixed rate
        if (app.getStateManager().getState(AIGameUpdateManager.class).IsUpdate()) {

            if (work) {
                float distance = bornPlace.distance(spatial.getLocalTranslation());

                if (contactPoint != null) {
//                    System.out.println("eeyyyyy");
                    float contactPointDistance = bornPlace.distance(contactPoint);

                    if (distance >= contactPointDistance
                            || distance + vecMove.length() > contactPointDistance) {
                        Node nd = new Node("expl");
                        nd.addControl(new ExplosionControl(contactPoint, nd, app));
                        Node root = (Node) app.getViewPort().getScenes().get(0);
                        root.attachChild(nd);
                        destroy();
                        return;
                    }
                }

                if (distance >= bulletLength || distance + vecMove.length() > bulletLength) {
                    destroy();
                    return;
                }
                spatial.move(vecMove);
            }
        }
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }
}
