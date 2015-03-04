/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package redmonkeyDemos;

import com.badlogic.gdx.ai.btree.BehaviorTree;
import com.badlogic.gdx.ai.btree.utils.BehaviorTreeParser;
import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.app.state.AppState;
import com.jme3.light.AmbientLight;
import com.jme3.math.ColorRGBA;
import com.jme3.renderer.queue.RenderQueue;
import com.simsilica.lemur.GuiGlobals;
import redmonkey.RMItem;
import redmonkey.RMOmniSight;
import redmonkey.RedMonkeyAppState;
import redmonkey.RedMonkeyDebugAppState;
import redmonkey.elements.monkey.RMMonkey;
import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.material.Material;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;
import com.jme3.terrain.geomipmap.TerrainLodControl;
import com.jme3.terrain.heightmap.AbstractHeightMap;
import com.jme3.terrain.geomipmap.TerrainQuad;
import com.jme3.terrain.heightmap.ImageBasedHeightMap;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture.WrapMode;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class RMDemo extends SimpleApplication {

    private RedMonkeyAppState redMonkeyAppState;
    private BulletAppState bulletAppState;
    private RigidBodyControl landscape;
    private CharacterControl player;
    private TerrainQuad terrain;
    private Material mat_terrain;
    private BehaviorTree<RMMonkey> monkeyBehaviorTree;

    public static void main(String args[]) {
        new RMDemo().start();
    }

    @Override
    public void simpleInitApp() {
        /**
         * Set up Physics
         */
        flyCam.setMoveSpeed(100);
        bulletAppState = new BulletAppState();
        bulletAppState.setDebugEnabled(true);
        stateManager.attach(bulletAppState);
        /**
         * 1. Create terrain material and load four textures into it.
         */
        mat_terrain = new Material(assetManager,
                "Common/MatDefs/Terrain/Terrain.j3md");

        /**
         * 1.1) Add ALPHA map (for red-blue-green coded splat textures)
         */
        mat_terrain.setTexture("Alpha", assetManager.loadTexture(
                "Textures/Terrain/splat/alphamap.png"));

        /**
         * 1.2) Add GRASS texture into the red layer (Tex1).
         */
        Texture grass = assetManager.loadTexture(
                "Textures/Terrain/splat/grass.jpg");
        grass.setWrap(WrapMode.Repeat);
        mat_terrain.setTexture("Tex1", grass);
        mat_terrain.setFloat("Tex1Scale", 64f);

        /**
         * 1.3) Add DIRT texture into the green layer (Tex2)
         */
        Texture dirt = assetManager.loadTexture(
                "Textures/Terrain/splat/dirt.jpg");
        dirt.setWrap(WrapMode.Repeat);
        mat_terrain.setTexture("Tex2", dirt);
        mat_terrain.setFloat("Tex2Scale", 32f);

        /**
         * 1.4) Add ROAD texture into the blue layer (Tex3)
         */
        Texture rock = assetManager.loadTexture(
                "Textures/Terrain/splat/road.jpg");
        rock.setWrap(WrapMode.Repeat);
        mat_terrain.setTexture("Tex3", rock);
        mat_terrain.setFloat("Tex3Scale", 128f);

        /**
         * 2. Create the height map
         */
        AbstractHeightMap heightmap = null;
        Texture heightMapImage = assetManager.loadTexture(
                "Textures/Terrain/splat/mountains512.png");
        heightmap = new ImageBasedHeightMap(heightMapImage.getImage());
        heightmap.load();

        /**
         * 3. We have prepared material and heightmap. Now we create the actual
         * terrain: 3.1) Create a TerrainQuad and name it "my terrain". 3.2) A
         * good value for terrain tiles is 64x64 -- so we supply 64+1=65. 3.3)
         * We prepared a heightmap of size 512x512 -- so we supply 512+1=513.
         * 3.4) As LOD step scale we supply Vector3f(1,1,1). 3.5) We supply the
         * prepared heightmap itself.
         */
        terrain = new TerrainQuad("my terrain", 65, 513, heightmap.getHeightMap());

        /**
         * 4. We give the terrain its material, position & scale it, and attach
         * it.
         */
        terrain.setMaterial(mat_terrain);
        terrain.setLocalTranslation(0, -100, 0);
        terrain.setLocalScale(2f, 1f, 2f);
        rootNode.attachChild(terrain);

        /**
         * 5. The LOD (level of detail) depends on were the camera is:
         */
        List<Camera> cameras = new ArrayList<Camera>();
        cameras.add(getCamera());
        TerrainLodControl control = new TerrainLodControl(terrain, cameras);
        terrain.addControl(control);

        /**
         * 6. Add physics: // We set up collision detection for the scene by
         * creating a static RigidBodyControl with mass zero.
         */
        terrain.addControl(new RigidBodyControl(0));

        // We set up collision detection for the player by creating
        // a capsule collision shape and a CharacterControl.
        // The CharacterControl offers extra settings for
        // size, stepheight, jumping, falling, and gravity.
        // We also put the player in its starting position.

        // We attach the scene and the player to the rootnode and the physics space,
        // to make them appear in the game world.
        bulletAppState.getPhysicsSpace().add(terrain);

        cam.setLocation(new Vector3f(-133.55548f, -20.999119f, 44.5093f));
        cam.setRotation(new Quaternion(-3.7362854E-4f, 0.9974783f, -0.07077533f, -0.0052657295f));
        GuiGlobals.initialize(this);


        redMonkeyAppState = new RedMonkeyAppState();
        stateManager.attach(redMonkeyAppState);
        AmbientLight al = new AmbientLight();
        al.setColor(new ColorRGBA(10.1f, 1.1f, 1.1f, 1));
        rootNode.addLight(al);
        //assetManager.registerLoader(renderer, extensions);
        //	reader = new FileReader("nonjava/monkey.redmonkey").reader();
        stateManager.attach((AppState) (new RedMonkeyDebugAppState(redMonkeyAppState.getSpace(), rootNode, guiFont)));
        makeMonkey(-134f, -19f, 0f);
        makeBanana(-130f, -19f, 0f);

    }

    @Override
    public void simpleUpdate(float tpf) {
        monkeyBehaviorTree.step();
    }

    private void makeMonkey(float x, float y, float z) {
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
        CapsuleCollisionShape capsuleShape = new CapsuleCollisionShape(1.5f, 2f, 1);
        player = new CharacterControl(capsuleShape, 0.05f);
        player.setJumpSpeed(20);
        player.setFallSpeed(30);
        player.setGravity(30);
        Node jaime = (Node) assetManager.loadModel("Models/Jaime/Jaime.j3o");
        jaime.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);
        jaime.setLocalTranslation(x,y,z);
        jaime.addControl(player);
        rootNode.attachChild(jaime);
        AnimControl acontrol = jaime.getControl(AnimControl.class);
        AnimChannel channel = acontrol.createChannel();
        RMMonkey rm = new RMMonkey(jaime.getLocalTranslation());
        rm.setChannel(channel);
        rm.sense = new RMOmniSight();
        rm.setSpace(redMonkeyAppState.getSpace());
        BehaviorTreeParser<RMMonkey> parser = new BehaviorTreeParser<RMMonkey>(BehaviorTreeParser.DEBUG_NONE);
        monkeyBehaviorTree = parser.parse(rmFile, rm);
        bulletAppState.getPhysicsSpace().add(player);
    
    }
    
    private void makeBanana(float x, float y, float z) {
        Box box = new Box(1, 1, 1);
        Geometry cube = new Geometry("banana", box);
        cube.setLocalTranslation(x, y, z);
        Material mat1 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat1.setColor("Color", ColorRGBA.Blue);
        cube.setMaterial(mat1);
        RigidBodyControl rigidBodyControl = new RigidBodyControl(1f);
        cube.addControl(rigidBodyControl);
        rootNode.attachChild(cube);
        bulletAppState.getPhysicsSpace().add(rigidBodyControl);
        redMonkeyAppState.getSpace().addItems(new RMItem(cube.getLocalTranslation(), "Banana", "Tasty"));
    }
}
