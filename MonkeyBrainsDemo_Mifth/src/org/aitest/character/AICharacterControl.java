/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.aitest.character;

import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.animation.SkeletonControl;
import com.jme3.app.Application;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.PhysicsTickListener;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import java.util.LinkedList;
import java.util.List;
import org.aitest.AIGameManager;

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

    public AICharacterControl(Application app, Node charModel, boolean updatePerFrame) {

        super(0.7f, 2f, 50f); // create BetterCharacterControl

        this.app = app;

        this.updatePerFrame = updatePerFrame;

        doMove = false;
        doRotate = false;
        doShoot = false;
        doStrike = false;

        rotateLeft = true;
        moveForward = true;

        rotateSpeed = 4.0f;
        moveSpeed = 10.0f;
        setMoveSpeed(moveSpeed);

        AIGameManager gameManager = app.getStateManager().getState(AIGameManager.class);

//        this.charNode = charModel;
        AnimLst = new LinkedList<AnimControl>();

        findAnimation(charModel);

        CollisionShape cShape = CollisionShapeFactory.createMeshShape(charModel);
//        charCrtl = new BetterCharacterControl();
        charModel.addControl(this); // FORCE TO ADD THE CONTROL TO THE SPATIAL

        physics = this.app.getStateManager().getState(BulletAppState.class).getPhysicsSpace();
        physics.add(this);
//        physics.addTickListener(this);

    }

    private void findAnimation(Node nd) {
        for (Spatial sp : nd.getChildren()) {
            AnimControl aniControl = sp.getControl(AnimControl.class);

            if (aniControl == null && sp instanceof Node) {
                findAnimation((Node) sp);
            } else {
                SkeletonControl skeletonControl = sp.getControl(SkeletonControl.class);
                skeletonControl.setHardwareSkinningPreferred(true); // PERFORMANCE IS MUCH MUCH BETTER WITH HW SKINNING

                AnimChannel aniChannel = aniControl.createChannel();
                aniChannel.setAnim("base_stand");

                AnimLst.add(aniControl);
            }
        }
    }

    @Override
    public void update(float tpf) {
        super.update(tpf);

        // set Rotation
        if (doRotate) {
            Quaternion rotQua = new Quaternion().fromAngleAxis(FastMath.DEG_TO_RAD * rotateSpeed, Vector3f.UNIT_Y);
            if (!rotateLeft) {
                rotQua.inverseLocal();
            }

            rotQua = spatial.getLocalRotation().mult(rotQua);

            setViewDirection(rotQua.mult(Vector3f.UNIT_Z).normalizeLocal());


        }

        // set movement
        if (doMove) {
            Vector3f walkDir = getViewDirection().mult(moveSpeed);
            if (!moveForward) {
                walkDir.negateLocal();
            }

            setWalkDirection(walkDir);


        } else {
            setWalkDirection(Vector3f.ZERO);
        }


        // set Animations
        if (doMove) {
            for (AnimControl ani : AnimLst) {
                if (!ani.getChannel(0).getAnimationName().equals("run_01")) {
                    ani.getChannel(0).setAnim("run_01");
                }

            }
        } else {
            for (AnimControl ani : AnimLst) {
                if (!ani.getChannel(0).getAnimationName().equals("base_stand")) {
                    ani.getChannel(0).setAnim("base_stand");
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
    
}
