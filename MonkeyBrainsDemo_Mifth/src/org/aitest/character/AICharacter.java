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
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import java.util.LinkedList;
import java.util.List;
import org.aitest.AIGameManager;

/**
 *
 * @author mifthbeat
 */
public class AICharacter {

    private Application app;
    private Node charNode;
    private BetterCharacterControl charCrtl;
    private List<SkeletonControl> skeletonsLst;
    private AnimChannel channel;
    private AnimControl control;
    private String[] animNames = {"base_stand", "run_01", "jump", "shoot", "strike_sword"};

    public AICharacter(Application app, Node charModel) {
        this.app = app;

        AIGameManager gameManager = app.getStateManager().getState(AIGameManager.class);

        this.charNode = charModel;
        skeletonsLst = new LinkedList<SkeletonControl>();

        CollisionShape cShape = CollisionShapeFactory.createMeshShape(charNode);
//        RigidBodyControl rg = new RigidBodyControl(cShape, 1f);
        BetterCharacterControl charCrtl = new BetterCharacterControl(0.5f, 2f, 1f);
        charNode.addControl(charCrtl);

        this.app.getStateManager().getState(BulletAppState.class).getPhysicsSpace().add(charCrtl);

        setHWSkinning(charNode);
    }

    private void setHWSkinning(Node nd) {
        for (Spatial sp : nd.getChildren()) {
            SkeletonControl skeletonControl = sp.getControl(SkeletonControl.class);

            if (skeletonControl == null && sp instanceof Node) {
                setHWSkinning((Node) sp);
            } else {
                skeletonControl.setHardwareSkinningPreferred(true);
                control = sp.getControl(AnimControl.class);

                channel = control.createChannel();
                channel.setAnim("base_stand");
                
                skeletonsLst.add(skeletonControl);
            }
        }



    }

    public Node getCharNode() {
        return charNode;
    }

    public BetterCharacterControl getCharacterCrtl() {
        return charCrtl;
    }
}
