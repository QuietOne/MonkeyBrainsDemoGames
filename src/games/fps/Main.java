package games.fps;

import behaviours.npc.FleeInsideTerrain;
import behaviours.npc.SeekInsideTerrain;
import behaviours.npc.WanderInsideTerrain;
import util.DefinedSpatials;
import util.Cannon;
import util.LaserWeapon;
import com.jme3.ai.agents.util.control.Game;
import com.jme3.ai.agents.Agent;
import com.jme3.ai.agents.Team;
import com.jme3.ai.agents.behaviours.npc.SimpleAttackBehaviour;
import com.jme3.ai.agents.behaviours.npc.SimpleLookBehaviour;
import com.jme3.ai.agents.behaviours.npc.SimpleMainBehaviour;
import com.jme3.ai.agents.behaviours.player.SimplePlayerAttackBehaviour;
import com.jme3.ai.agents.behaviours.player.SimplePlayerMoveBehaviour;
import com.jme3.app.SimpleApplication;
import com.jme3.font.BitmapText;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import control.FPS;
import util.Knife;

/**
 * Testing demo game for this agent framework.
 */
public class Main extends SimpleApplication {

    //defining players
    private Agent player;
    private Agent[] enemies = new Agent[3];
    //Defining game
    private Game game = Game.getInstance();
    private float gameFinishCountDown = 5f;
    //game stats
    private final float terrainSize = 40f;

    public static void main(String[] args) {
        Main app = new Main();
        app.start();
    }

    @Override
    public void simpleInitApp() {
        //defining rootNode for game processing
        game.setRootNode(rootNode);
        //defining input manager
        game.setInputManager(inputManager);
        //setting game Genre
        game.setGenre(new FPS());
        //registering input
        game.getGenre().loadInputManagerMapping();
        game.setFriendlyFire(false);

        //DefinedSpatials for graphics for this game
        DefinedSpatials.material = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        DefinedSpatials.initializeFloor(terrainSize);
        viewPort.addProcessor(DefinedSpatials.initializeBloom(assetManager));
        flyCam.setMoveSpeed(20);
        //disable the default flyby cam
        flyCam.setEnabled(false);


        //initialization of Agents with their names and spatials
        player = new Agent("Player", DefinedSpatials.initializeAgent("Player", ColorRGBA.Gray));
        for (int i = 0; i < enemies.length; i++) {
            if (i % 3 == 0) {
                enemies[i] = new Agent("Bot " + i, DefinedSpatials.initializeAgent("Bot " + i, ColorRGBA.Red));
            } else {
                if (i % 3 == 1) {
                    enemies[i] = new Agent("Bot " + i, DefinedSpatials.initializeAgent("Bot " + i,ColorRGBA.Green));
                } else {
                    enemies[i] = new Agent("Bot " + i, DefinedSpatials.initializeAgent("Bot " + i,ColorRGBA.Cyan));
                }
            }
            
        }

        //adding them to game
        game.addAgent(player);
        for (int i = 0; i < enemies.length; i++) {
            game.getGenre().spawn(enemies[i], new Vector3f(terrainSize * 2 - 5, 0, terrainSize * 2 - 5),
                    new Vector3f(-terrainSize * 2 + 5, 0, -terrainSize * 2 + 5));

        }

        //setting moveSpeed, rotationSpeed, mass
        player.setMoveSpeed(15);
        player.setRotationSpeed(30);
        for (int i = 0; i < enemies.length; i++) {
            enemies[i].setMoveSpeed(10);
            enemies[i].setRotationSpeed(30);
            enemies[i].setMass(40);
            enemies[i].setMaxForce(3);
        }

        //giving them weapons
        //player.setWeapon(new Knife("knife", player));
        player.setWeapon(new Cannon("cannon", player));
        //player.setWeapon(new LaserWeapon("laser", player));
        for (int i = 0; i < enemies.length; i++) {
            if (i % 3 == 0) {
                enemies[i].setWeapon(new LaserWeapon("laser", enemies[i]));
            } else {
                if (i % 3 == 1) {
                    enemies[i].setWeapon(new Cannon("cannon", enemies[i]));
                } else {
                    enemies[i].setWeapon(new Knife("knife", enemies[i]));
                }
            }
        }

        //making teams
        Team playerTeam = new Team("Player");
        Team botTeam = new Team("Computer");

        //adding game teams
        player.setTeam(playerTeam);
        for (int i = 0; i < enemies.length; i++) {
            enemies[i].setTeam(botTeam);
        }

        //set visibility range
        for (int i = 0; i < enemies.length; i++) {
            if (i % 3 == 0) {
                enemies[i].setVisibilityRange(150f);
            } else {
                if (i % 3 == 1) {
                    enemies[i].setVisibilityRange(400f);
                } else {
                    enemies[i].setVisibilityRange(500f);
                }
            }

        }

        //attaching camera to player
        DefinedSpatials.attachCameraTo(player, cam);

        //making move behaviour for player
        SimplePlayerMoveBehaviour playerMove = new SimplePlayerMoveBehaviour(player, null);
        ((FPS) game.getGenre()).addMoveListener(player, playerMove);
        playerMove.addSupportedOperations(((FPS) game.getGenre()).getPlayerMoveSupportedOperations(player));

        //making attack behaviour for player
        SimplePlayerAttackBehaviour playerAttack = new SimplePlayerAttackBehaviour(player, null);
        ((FPS) game.getGenre()).addAttackListener(player, playerAttack);
        playerAttack.addSupportedOperations(((FPS) game.getGenre()).getPlayerAttackSupportedOperations(player));

        //making main behaviour for player and adding behaviours to it
        SimpleMainBehaviour playerMain = new SimpleMainBehaviour(player);
        playerMain.addBehaviour(playerMove);
        playerMain.addBehaviour(playerAttack);
        player.setMainBehaviour(playerMain);

        //setting main behaviour to bots
        for (int i = 0; i < enemies.length; i++) {
            if (i % 3 == 0) {
                SimpleMainBehaviour enemyMain = new SimpleMainBehaviour(enemies[i]);
                SimpleLookBehaviour look = new SimpleLookBehaviour(enemies[i]);
                SimpleAttackBehaviour attack = new SimpleAttackBehaviour(enemies[i]);
                look.addListener(attack);
                enemyMain.addBehaviour(look);
                enemyMain.addBehaviour(attack);
                enemyMain.addBehaviour(new WanderInsideTerrain(enemies[i], terrainSize));
                enemies[i].setMainBehaviour(enemyMain);
            } else {
                if (i % 3 == 1) {
                    SimpleMainBehaviour enemyMain = new SimpleMainBehaviour(enemies[i]);
                    SimpleAttackBehaviour attack = new SimpleAttackBehaviour(enemies[i]);
                    attack.setTarget(player);
                    enemyMain.addBehaviour(attack);
                    enemyMain.addBehaviour(new FleeInsideTerrain(enemies[i], player));
                    enemies[i].setMainBehaviour(enemyMain);
                } else {
                    SimpleMainBehaviour enemyMain = new SimpleMainBehaviour(enemies[i]);
                    SimpleLookBehaviour look = new SimpleLookBehaviour(enemies[i]);
                    SimpleAttackBehaviour attack = new SimpleAttackBehaviour(enemies[i]);
                    look.addListener(attack);
                    enemyMain.addBehaviour(look);
                    enemyMain.addBehaviour(attack);
                    enemyMain.addBehaviour(new SeekInsideTerrain(enemies[i], player));
                    enemies[i].setMainBehaviour(enemyMain);
                }
            }
        }

        //starting agents
        game.start();

    }

    @Override
    public void simpleUpdate(float tpf) {
        if (game.getGenre().finish()) {
            if (gameFinishCountDown <= 0) {
                this.stop();
            } else {
                gameFinishCountDown -= tpf;
                BitmapText hudText = new BitmapText(guiFont, false);
                hudText.setSize(guiFont.getCharSet().getRenderedSize()); // font size
                hudText.setColor(ColorRGBA.Red); // font color
                if (game.getGenre().win(player)) {
                    hudText.setText(player.getTeam().getName() + " wins."); // the text
                } else {
                    hudText.setText(enemies[0].getTeam().getName() + " wins."); // the text
                }
                hudText.setLocalTranslation(settings.getWidth() / 2 - hudText.getLineWidth() / 2, settings.getHeight() / 2 - hudText.getLineHeight() / 2, 0); // position
                guiNode.attachChild(hudText);
            }
        }
        game.update(tpf);
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }
}
