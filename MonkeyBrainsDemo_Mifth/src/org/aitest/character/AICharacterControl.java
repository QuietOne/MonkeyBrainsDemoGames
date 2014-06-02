/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.aitest.character;

import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.animation.AnimEventListener;
import com.jme3.animation.LoopMode;
import com.jme3.animation.SkeletonControl;
import com.jme3.app.Application;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.PhysicsTickListener;
import com.jme3.bullet.collision.PhysicsCollisionObject;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.bullet.control.GhostControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.debug.Arrow;
import com.jme3.scene.shape.Box;
import java.util.LinkedList;
import java.util.List;
import org.aitest.AIGameManager;
import org.aitest.AIUpdateManager;
import org.aitest.weapon.AIBulletControl;
import org.aitest.weapon.AIWeaponController;

/**
 *
 * @author mifthbeat
 */
public class AICharacterControl extends BetterCharacterControl {

    private Application app;
//    private Node charNode;
//    private BetterCharacterControl charCrtl;
    private List<AnimControl> AnimLst;
    private String[] animNames = {"base_stand", "run_01", "shoot", "strike_sword"};
    private boolean doMove, doRotate, doShoot, doStrike;
    private boolean rotateLeft, moveForward;
    private float rotateSpeed, moveSpeed;
    private boolean updatePerFrame;
    private PhysicsSpace physics;
    private AICharacterState charState;
    private float shootTimer, shootMaxTime, strikeMaxTime;
    private boolean bulletCreated;
    private float health;
    private Node swordModel;
    private boolean swordKilled;
    private float swordDestruction, bulletDestruction;

    public AICharacterControl(Application app, Node charModel, boolean updatePerFrame) {

        super(0.85f, 2f, 50f); // create BetterCharacterControl

        this.app = app;

        this.updatePerFrame = updatePerFrame;

        doMove = false;
        doRotate = false;
        doShoot = false;
        doStrike = false;

        rotateLeft = true;
        moveForward = true;

        rotateSpeed = 1.0f;
        moveSpeed = 7.0f;
        setMoveSpeed(moveSpeed);

        health = 100f;
        swordKilled = false;
        swordDestruction = 30f;
        bulletDestruction = 20f;

        shootTimer = 0f;
        shootMaxTime = 0.4f;
        strikeMaxTime = 0.6f;
        bulletCreated = false;

        charState = AICharacterState.None;

        AIGameManager gameManager = app.getStateManager().getState(AIGameManager.class);

//        this.charNode = charModel;
        AnimLst = new LinkedList<AnimControl>();

        CollisionShape cShape = CollisionShapeFactory.createMeshShape(charModel);
//        charCrtl = new BetterCharacterControl();
        charModel.addControl(this); // FORCE TO ADD THE CONTROL TO THE SPATIAL

        physics = this.app.getStateManager().getState(BulletAppState.class).getPhysicsSpace();
        physics.add(this);

        prepareModel(charModel);

        // add arrow
        Mesh arrow = new Arrow(Vector3f.UNIT_Z);
        Geometry geoArrow = new Geometry("arrow", arrow);
        Material matArrow = new Material(app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        matArrow.setColor("Color", ColorRGBA.White);
        geoArrow.setMaterial(matArrow);
        geoArrow.setLocalTranslation(0f, 0.1f, 0f);
        charModel.attachChild(geoArrow);


    }

    private void prepareModel(Node nd) {
        for (Spatial sp : nd.getChildren()) {
            AnimControl aniControl = sp.getControl(AnimControl.class);

            if (aniControl == null && sp instanceof Node) {
                prepareModel((Node) sp);
            } else if (aniControl != null) {
                SkeletonControl skeletonControl = sp.getControl(SkeletonControl.class);
                skeletonControl.setHardwareSkinningPreferred(true); // PERFORMANCE IS MUCH MUCH BETTER WITH HW SKINNING

                if (swordModel == null) {
                    createSword(skeletonControl);
                }

                AnimChannel aniChannel = aniControl.createChannel();
                aniChannel.setAnim("base_stand");

                AnimLst.add(aniControl);
            }
        }
    }

    private void createSword(SkeletonControl skeletonControl) {
        swordModel = new Node("sword");

        GhostControl gh = new GhostControl(new BoxCollisionShape(new Vector3f(0.3f, 1f, 0.3f)));
        swordModel.addControl(gh);
        physics.add(gh);

        Node n = skeletonControl.getAttachmentsNode("sword");
        n.attachChild(swordModel);
    }

    private void killWithSword() {
        for (PhysicsCollisionObject physObj : swordModel.getControl(GhostControl.class).getOverlappingObjects()) {
            Spatial spObj = (Spatial) physObj.getUserObject();
            AICharacterControl charCtrl = spObj.getControl(AICharacterControl.class);
            if (charCtrl != null && !charCtrl.equals(this)) {
                charCtrl.substractHealth(swordDestruction);

                swordKilled = true;
                break;
            }
        }
    }

    private void createBullet() {
        Geometry newBullet = app.getStateManager().getState(AIWeaponController.class).getBullet().clone(false);
        newBullet.setLocalRotation(getSpatialRotation().clone());
        newBullet.setLocalTranslation(getSpatialTranslation().clone().addLocal(Vector3f.UNIT_Y).addLocal(newBullet.getLocalRotation().mult(Vector3f.UNIT_Z)));
        newBullet.addControl(new AIBulletControl(newBullet.getLocalTranslation(), newBullet, app, bulletDestruction));

        Node root = (Node) app.getViewPort().getScenes().get(0);
        root.attachChild(newBullet);


    }

    private void moveCharacter() {
        Vector3f walkDir = getViewDirection().mult(moveSpeed);
        if (!moveForward) {
            walkDir.negateLocal();
        }

        setWalkDirection(walkDir);
    }

    private void rotateCharacter() {
        Quaternion rotQua = new Quaternion().fromAngleAxis(FastMath.DEG_TO_RAD * rotateSpeed, Vector3f.UNIT_Y);
        if (!rotateLeft) {
            rotQua.inverseLocal();
        }

        rotQua = spatial.getLocalRotation().mult(rotQua);

        setViewDirection(rotQua.mult(Vector3f.UNIT_Z).normalizeLocal());
    }

    private void stopCharacter() {
        setWalkDirection(Vector3f.ZERO);
    }

//    // ANIMATION LISTENER
//    public void onAnimCycleDone(AnimControl control, AnimChannel channel, String animName) {
//    }
//
//    // ANIMATION LISTENER
//    public void onAnimChange(AnimControl control, AnimChannel channel, String animName) {
//    }
    public void destroyCtrl() {

        AnimLst.clear();

//        for (int i = 0; i < spatial.getNumControls(); i++) {
//        }

        if (spatial != null) {

            physics.remove(spatial);
            
            swordModel.removeFromParent();
            physics.remove(swordModel);
            swordModel = null;

            spatial.removeFromParent();
            spatial.removeControl(this);
            spatial = null;
        }


    }

    @Override
    public void update(float tpf) {

        // Update only for fixed rate
        if (app.getStateManager().getState(AIUpdateManager.class).IsUpdate()) {

            super.update(tpf);

            // Intputs settings
            if ((doShoot || doStrike)
                    && charState != AICharacterState.Shoot && charState != AICharacterState.Strike) {

                stopCharacter();
                charState = AICharacterState.None;

                if (doShoot) {
                    charState = AICharacterState.Shoot;
                } else if (!doShoot && doStrike) {
                    charState = AICharacterState.Strike;
                }
            } else if ((doMove || doRotate)
                    && charState != AICharacterState.Shoot && charState != AICharacterState.Strike) {

                if (!doMove) {
                    stopCharacter();
                }

                if (doMove && !doRotate) {
                    charState = AICharacterState.Run;
                } else if (!doMove && doRotate) {
                    charState = AICharacterState.Rotate;
                } else if (doMove && doRotate) {
                    charState = AICharacterState.RunAndRotate;
                }

            } else if (charState != AICharacterState.Shoot && charState != AICharacterState.Strike) {

//            System.out.println("sssssssss");
                if (charState != AICharacterState.None) {
                    stopCharacter();
                }
                charState = AICharacterState.None;
            }


            // SET LOGIC
            if (charState != AICharacterState.None) {
                if (charState == AICharacterState.Shoot) {

//                    rotateCharacter();

                    if (shootTimer >= shootMaxTime) {
                        shootTimer = 0f;
                        charState = AICharacterState.None;
                        bulletCreated = false;
                    } else {

                        if (shootTimer >= 0.1f && !bulletCreated) {
                            createBullet();
                            bulletCreated = true;
                        }

                        shootTimer += app.getStateManager().getState(AIUpdateManager.class).getCurrentTpf();
                    }

                } else if (charState == AICharacterState.Strike) {

                    if (shootTimer >= strikeMaxTime) {
                        shootTimer = 0f;
                        charState = AICharacterState.None;
                        swordKilled = false;
                    } else {

                        if (shootTimer >= 0.1f && !swordKilled) {
                            killWithSword();
                            bulletCreated = true;
                        }

                        shootTimer += app.getStateManager().getState(AIUpdateManager.class).getCurrentTpf();

                    }

                } else if (charState == AICharacterState.Run) {
                    moveCharacter();
                } else if (charState == AICharacterState.RunAndRotate) {
                    moveCharacter();
                    rotateCharacter();
                } else if (charState == AICharacterState.Rotate) {
                    rotateCharacter();
                }
            }


            // set Animations
            if (charState == AICharacterState.Run || charState == AICharacterState.Rotate || charState == AICharacterState.RunAndRotate) {
                for (AnimControl ani : AnimLst) {
                    if (!ani.getChannel(0).getAnimationName().equals("run_01")) {
                        ani.getChannel(0).setAnim("run_01", 0.3f);
                        ani.getChannel(0).setSpeed(1f);
                        ani.getChannel(0).setLoopMode(LoopMode.Loop);
                    }

                }
            } else if (charState == AICharacterState.Shoot) {

                for (AnimControl ani : AnimLst) {
                    if (!ani.getChannel(0).getAnimationName().equals("shoot")) {
                        ani.getChannel(0).setAnim("shoot", 0.1f);
                        ani.getChannel(0).setSpeed(1.5f);
                        ani.getChannel(0).setLoopMode(LoopMode.DontLoop);
                    }

                }

            } else if (charState == AICharacterState.Strike) {

                for (AnimControl ani : AnimLst) {
                    if (!ani.getChannel(0).getAnimationName().equals("strike_sword")) {
                        ani.getChannel(0).setAnim("strike_sword", 0.2f);
                        ani.getChannel(0).setSpeed(1.0f);
                        ani.getChannel(0).setLoopMode(LoopMode.DontLoop);
                    }

                }

            } else {
                for (AnimControl ani : AnimLst) {
                    if (!ani.getChannel(0).getAnimationName().equals("base_stand")) {
                        ani.getChannel(0).setAnim("base_stand", 0.3f);
                        ani.getChannel(0).setSpeed(1f);
                        ani.getChannel(0).setLoopMode(LoopMode.Loop);
                    }

                }
            }

            if (updatePerFrame) {
                doMove = false;
                doRotate = false;
                doShoot = false;
                doStrike = false;
            }

        }

    }

    public float getHealth() {
        return health;
    }

    public void substractHealth(float substractedHealth) {
        health -= substractedHealth;

        if (health <= 0) {
            destroyCtrl();
        }
    }

    public Node getCharNode() {
        return (Node) spatial;
    }

    public boolean isDoMove() {
        return doMove;
    }

    public void setDoMove(boolean doMove) {
        this.doMove = doMove;
    }

    public boolean isDoRotate() {
        return doRotate;
    }

    public void setDoRotate(boolean doRotate) {
        this.doRotate = doRotate;
    }

    public boolean isRotateLeft() {
        return rotateLeft;
    }

    public void setRotateLeft(boolean rotateLeft) {
        this.rotateLeft = rotateLeft;
    }

    public boolean isMoveForward() {
        return moveForward;
    }

    public void setMoveForward(boolean moveForward) {
        this.moveForward = moveForward;
    }

    public float getRotateSpeed() {
        return rotateSpeed;
    }

    public void setRotateSpeed(float rotateSpeed) {
        this.rotateSpeed = rotateSpeed;
    }

    public float getMoveSpeed() {
        return moveSpeed;
    }

    public void setMoveSpeed(float moveSpeed) {
        this.moveSpeed = moveSpeed;
    }

    public boolean isDoShoot() {
        return doShoot;
    }

    public void setDoShoot(boolean doShoot) {
        this.doShoot = doShoot;
    }

    public boolean isDoStrike() {
        return doStrike;
    }

    public void setDoStrike(boolean doStrike) {
        this.doStrike = doStrike;
    }

    public boolean isUpdatePerFrame() {
        return updatePerFrame;
    }

    public void setUpdatePerFrame(boolean updatePerFrame) {
        this.updatePerFrame = updatePerFrame;
    }

    public AICharacterState getCharState() {
        return charState;
    }

    public void setCharState(AICharacterState charState) {
        this.charState = charState;
    }
}
