package org.aitest.ai.character;

import com.jme3.ai.agents.Agent;
import com.jme3.ai.agents.util.control.Game;
import com.jme3.animation.AnimControl;
import com.jme3.animation.LoopMode;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.debug.Arrow;
import java.util.LinkedList;
import java.util.List;
import org.aitest.ai.control.AIGameUpdateManager;
import org.aitest.ai.utils.Gun;
import org.aitest.ai.utils.Sword;

/*
 *
 * @author mifthbeat
 * @author Tihomir Radosavljevic
 * @version 1.0
 */
public class AIModel extends BetterCharacterControl {
    /**
     * Reference to agent to which this model is attached.
     */
    private Agent agent;
    /**
     * Sword that this agent has.
     */
    private Sword sword;
    /**
     * Gun that this agent has.
     */
    private Gun gun;
    private Node charNode;
    private List<AnimControl> animLst;
    private String[] animNames = {"base_stand", "run_01", "shoot", "strike_sword"};
    private boolean doMove, doRotate, doShoot, doStrike;
    private boolean rotateLeft, moveForward;
    private boolean updatePerFrame;
    private PhysicsSpace physics;
    private AICharacterState charState;
    private boolean bulletCreated;
    private Node swordModel;
    private boolean swordKilled;

    public AIModel(Agent agent) {
        super(0.85f, 2f, 50f);
        this.agent = agent;
        //needed for steering behaviours
        agent.setMass(mass);
        agent.setMoveSpeed(7.0f);
        agent.setRotationSpeed(1.0f);
        sword = new Sword(agent);
        gun = new Gun(agent);
        doMove = false;
        doRotate = false;
        doShoot = false;
        doStrike = false;
        rotateLeft = true;
        moveForward = true;
        swordKilled = false;
        bulletCreated = false;
        charState = AICharacterState.None;
        animLst = new LinkedList<AnimControl>();
        CollisionShape cShape = CollisionShapeFactory.createMeshShape(agent.getSpatial());
//        charCrtl = new BetterCharacterControl();
        agent.getSpatial().addControl(this); // FORCE TO ADD THE CONTROL TO THE SPATIAL

        physics = Game.getInstance().getApp().getStateManager().getState(BulletAppState.class).getPhysicsSpace();
        physics.add(this);

        prepareModel((Node) agent.getSpatial());

        // add arrow
        Mesh arrow = new Arrow(Vector3f.UNIT_Z);
        Geometry geoArrow = new Geometry("arrow", arrow);
        Material matArrow = new Material(Game.getInstance().getApp().getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        matArrow.setColor("Color", ColorRGBA.White);
        geoArrow.setMaterial(matArrow);
        geoArrow.setLocalTranslation(0f, 0.1f, 0f);
        ((Node) agent.getSpatial()).attachChild(geoArrow);
    }
    
    private void moveCharacter() {
        Vector3f walkDir = getViewDirection().mult(agent.getMoveSpeed());
        if (!moveForward) {
            walkDir.negateLocal();
        }
        setWalkDirection(walkDir);
    }

    private void rotateCharacter() {
        Quaternion rotQua = new Quaternion().fromAngleAxis(FastMath.DEG_TO_RAD * agent.getRotationSpeed(), Vector3f.UNIT_Y);
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

        animLst.clear();
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
        if (Game.getInstance().getApp().getStateManager().getState(AIGameUpdateManager.class).IsUpdate()) {
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
                        shootTimer += Game.getInstance().getApp().getStateManager().getState(AIGameUpdateManager.class).getCurrentTpf();
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
                        shootTimer += Game.getInstance().getApp().getStateManager().getState(AIGameUpdateManager.class).getCurrentTpf();
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
                for (AnimControl ani : animLst) {
                    if (!ani.getChannel(0).getAnimationName().equals("run_01")) {
                        ani.getChannel(0).setAnim("run_01", 0.3f);
                        ani.getChannel(0).setSpeed(1f);
                        ani.getChannel(0).setLoopMode(LoopMode.Loop);
                    }
                }
            } else if (charState == AICharacterState.Shoot) {
                for (AnimControl ani : animLst) {
                    if (!ani.getChannel(0).getAnimationName().equals("shoot")) {
                        ani.getChannel(0).setAnim("shoot", 0.1f);
                        ani.getChannel(0).setSpeed(1.5f);
                        ani.getChannel(0).setLoopMode(LoopMode.DontLoop);
                    }
                }
            } else if (charState == AICharacterState.Strike) {
                for (AnimControl ani : animLst) {
                    if (!ani.getChannel(0).getAnimationName().equals("strike_sword")) {
                        ani.getChannel(0).setAnim("strike_sword", 0.2f);
                        ani.getChannel(0).setSpeed(1.0f);
                        ani.getChannel(0).setLoopMode(LoopMode.DontLoop);
                    }
                }
            } else {
                for (AnimControl ani : animLst) {
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
