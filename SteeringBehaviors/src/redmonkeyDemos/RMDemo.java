/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package redmonkeyDemos;

import com.badlogic.gdx.ai.btree.BehaviorTree;
import com.badlogic.gdx.ai.btree.utils.BehaviorTreeParser;
import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AppState;
import com.jme3.light.AmbientLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Node;
import com.simsilica.lemur.GuiGlobals;
import redmonkey.RMItem;
import redmonkey.RMOmniSight;
import redmonkey.RMSpace;
import redmonkey.RedMonkeyAppState;
import redmonkey.RedMonkeyDebugAppState;
import redmonkey.elements.monkey.RMMonkey;

/**
 *
 */
public class RMDemo extends SimpleApplication {

    private BehaviorTree<RMMonkey> monkeyBehaviorTree;

    public static void main(String args[]) {
        new RMDemo().start();
    }

    @Override
    public void simpleInitApp() {
        String rmFile = "#\n"
                + "# Monkey tree\n"
                + "#\n"
                + "\n"
                + "# Alias definitions\n"
                + "import sense:\"redmonkey.elements.monkey.SenseTask\"\n"
                + "import goto:\"redmonkey.elements.monkey.GotoTask\"\n"
                + "import sleep:\"redmonkey.elements.monkey.SleepTask\"\n"
                + "\n"
                + "# Tree definition (note that root is optional)\n"
                + "root\n"
                + "  selector\n"
                + "    sequence\n"
                + "      sense tag:\"Banana,Tasty\" number:3\n"
                + "      goto\n"
                + "    sequence\n"
                + "      sleep times:5\n"
                + "";
        GuiGlobals.initialize(this);
        RedMonkeyAppState redMonkeyAppState=new RedMonkeyAppState();
        stateManager.attach(redMonkeyAppState);
        Node jaime = (Node) assetManager.loadModel("Models/Jaime/Jaime.j3o");
        jaime.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);
        rootNode.attachChild(jaime);
        AnimControl control = jaime.getControl(AnimControl.class);
        AnimChannel channel = control.createChannel();
        AmbientLight al = new AmbientLight();
        al.setColor(new ColorRGBA(10.1f, 1.1f, 1.1f, 1));
        rootNode.addLight(al);
        //assetManager.registerLoader(renderer, extensions);
        //	reader = new FileReader("nonjava/monkey.redmonkey").reader();
        RMSpace space = redMonkeyAppState.space;
        stateManager.attach((AppState)(new RedMonkeyDebugAppState(space,rootNode,guiFont)));
        RMMonkey rm = new RMMonkey(Vector3f.ZERO);
        rm.setChannel(channel);
        rm.sense = new RMOmniSight();
        rm.setSpace(space);
        space.addItems(new RMItem(new Vector3f(-1, 1, -3),"Banana","Tasty"));

        BehaviorTreeParser<RMMonkey> parser = new BehaviorTreeParser<RMMonkey>(BehaviorTreeParser.DEBUG_NONE);
        monkeyBehaviorTree = parser.parse(rmFile, rm);
    }

    @Override
    public void simpleUpdate(float tpf) {
        monkeyBehaviorTree.step();
    }
}
