/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package redmonkeyDemos;

import com.badlogic.gdx.ai.btree.LeafTask;
import com.jme3.animation.AnimControl;
import com.jme3.light.AmbientLight;
import com.jme3.math.ColorRGBA;
import com.jme3.renderer.queue.RenderQueue;
import com.simsilica.lemur.GuiGlobals;
import redmonkey.RMItem;
import redmonkey.senses.RMOmniSight;
import redmonkey.RedMonkeyAppState;
import redmonkey.elements.monkey.RedMonkey;
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
import redmonkey.GameLogicHook;
import redmonkey.RMLoader;
import redmonkey.elements.monkey.EatTask;

/**
 *
 */
public class RMDemo extends SimpleApplication implements GameLogicHook{

    private RedMonkeyAppState redMonkeyAppState;
    private BulletAppState bulletAppState;
    private CharacterControl player;
    private TerrainQuad terrain;
    private Material mat_terrain;

    public static void main(String args[]) {
        RMDemo app = new RMDemo();
        //app.setShowSettings(false);
        app.start();
    }

    @Override
    public void simpleInitApp() {

        assetManager.registerLoader(RMLoader.class, "redmonkey");
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

        cam.setLocation(new Vector3f(281.3848f, -50.665142f, -180.4371f));
        cam.setRotation(new Quaternion(0.12662832f, 0.49964494f, -0.07411138f, 0.85371405f));
        GuiGlobals.initialize(this);


        redMonkeyAppState = new RedMonkeyAppState(rootNode, guiFont);
        redMonkeyAppState.setDebugEnabled(true);
        stateManager.attach(redMonkeyAppState);
        AmbientLight al = new AmbientLight();
        al.setColor(new ColorRGBA(10.1f, 1.1f, 1.1f, 1));
        rootNode.addLight(al);
        makeMonkey(339.57977f, -54.48287f, -172.30641f);
        makeBanana(342.89056f, -48.94442f, -109.037506f);
        makeHome(343.57977f, -54.48287f, -172.30641f);

    }

    @Override
    public void simpleUpdate(float tpf) {
    }

    public void endedTask(LeafTask o){
        if (o instanceof EatTask)
            System.out.println("yum!");
        else
            System.out.println("???");
    }
    
    private void makeMonkey(float x, float y, float z) {
        CapsuleCollisionShape capsuleShape = new CapsuleCollisionShape(0.5f, 1f, 1);
        player = new CharacterControl(capsuleShape, 0.05f);
        player.setJumpSpeed(20);
        player.setFallSpeed(30);
        player.setGravity(30);
        Node jaime = (Node) assetManager.loadModel("Models/Jaime/Jaime.j3o");
        jaime.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);
        jaime.setLocalTranslation(x, y, z);
        jaime.addControl(player);
        rootNode.attachChild(jaime);
        RedMonkey rm = new RedMonkey(jaime.getLocalTranslation(), terrain, jaime,this);
        rm.setChannel(jaime.getControl(AnimControl.class));
        rm.setSense(new RMOmniSight());
        rm.setSpace(redMonkeyAppState.getSpace());
        rm.setCharacterControl(player);
        rm.setBehaviorTree(assetManager, "Scripts/monkey.redmonkey");
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

    private void makeHome(float x, float y, float z) {
        Box box = new Box(1, 1, 1);
        Geometry cube = new Geometry("home", box);
        cube.setLocalTranslation(x, y, z);
        Material mat1 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat1.setColor("Color", ColorRGBA.Green);
        cube.setMaterial(mat1);
        RigidBodyControl rigidBodyControl = new RigidBodyControl(1f);
        cube.addControl(rigidBodyControl);
        rootNode.attachChild(cube);
        bulletAppState.getPhysicsSpace().add(rigidBodyControl);
        redMonkeyAppState.getSpace().addItems(new RMItem(cube.getLocalTranslation(), "Home"));
    }
}
