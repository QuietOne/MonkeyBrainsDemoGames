package fps.robotfight;

import behaviours.FleeInsideTerrain;
import behaviours.SeekInsideTerrain;
import behaviours.SwitchWeaponsBehaviour;
import behaviours.WanderInsideTerrain;
import fps.robotfight.util.RoboFightSpatials;
import fps.robotfight.util.Cannon;
import fps.robotfight.util.LaserWeapon;
import com.jme3.ai.agents.util.control.MonkeyBrainsAppState;
import com.jme3.ai.agents.Agent;
import com.jme3.ai.agents.Team;
import com.jme3.ai.agents.behaviors.npc.SimpleAttackBehavior;
import com.jme3.ai.agents.behaviors.npc.SimpleLookBehavior;
import com.jme3.ai.agents.behaviors.npc.SimpleMainBehavior;
import com.jme3.ai.agents.behaviors.player.SimplePlayerAttackBehavior;
import com.jme3.ai.agents.behaviors.player.SimplePlayerMoveBehavior;
import com.jme3.ai.agents.util.systems.SimpleAgentHitPoints;
import com.jme3.app.SimpleApplication;
import com.jme3.font.BitmapText;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import fps.robotfight.control.FPS;
import fps.robotfight.control.HPControl;
import fps.robotfight.util.RobotFightInventory;
import fps.robotfight.util.Knife;

/**
 * Testing demo game for MonkeyBrains framework. Some introduction to game: -
 * this game is based on Robotfight game made by Ryu Battosai Kajiya in
 * jMonkeyEngine - this is the game where one robot fights with other robots -
 * there are three kinds of robots: - blue: have instant kill, no decreaseHitPoints range
 * and always chasing player - red: moves random and have laser - green: runs
 * away from player, have cannon and always shooting at player - all three
 * robots are in the same team - if tree robot are to easy for you you can add
 * more with just increasing initial enemies array size. The game will do the
 * rest.
 *
 * @author Tihomir Radosavljević
 * @version 1.0
 */
public class RobotFightGame extends SimpleApplication {

    //defining players
    private Agent player;
    private Agent[] enemies = new Agent[6];
    //Defining game
    private MonkeyBrainsAppState aiAppState = MonkeyBrainsAppState.getInstance();
    private float gameFinishCountDown = 5f;
    //game stats
    private final float terrainSize = 40f;

    public static void main(String[] args) {
        RobotFightGame app = new RobotFightGame();
        app.start();
    }

    @Override
    public void simpleInitApp() {
        //defining app for game processing
        aiAppState.setApp(this);
        //setting game control
        aiAppState.setGameControl(new FPS());
        //setting hp controls for game
        aiAppState.setHitPointsControl(new HPControl());
        //registering input
        aiAppState.getGameControl().setInputManagerMapping();
        aiAppState.setFriendlyFire(false);

        //DefinedSpatials for graphics for this game
        RoboFightSpatials.material = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        RoboFightSpatials.initializeFloor(terrainSize);
        viewPort.addProcessor(RoboFightSpatials.initializeBloom(assetManager));

        aiAppState.getGameControl().setFlyCameraSettings(flyCam);

        //initialization of Agents with their names and spatials
        player = new Agent("Player", RoboFightSpatials.initializeAgent("Player", ColorRGBA.Gray));
        for (int i = 0; i < enemies.length; i++) {
            if (i % 3 == 0) {
                enemies[i] = new Agent("Bot " + i, RoboFightSpatials.initializeAgent("Bot " + i, ColorRGBA.Red));
            } else {
                if (i % 3 == 1) {
                    enemies[i] = new Agent("Bot " + i, RoboFightSpatials.initializeAgent("Bot " + i, ColorRGBA.Green));
                } else {
                    enemies[i] = new Agent("Bot " + i, RoboFightSpatials.initializeAgent("Bot " + i, ColorRGBA.Cyan));
                }
            }

        }

        //adding them to game
        aiAppState.addAgent(player);
        for (int i = 0; i < enemies.length; i++) {
            aiAppState.getGameControl().spawn(enemies[i], new Vector3f(terrainSize * 2 - 5, 0, terrainSize * 2 - 5),
                    new Vector3f(-terrainSize * 2 + 5, 0, -terrainSize * 2 + 5));
        }

        //setting moveSpeed, rotationSpeed, mass
        player.setMoveSpeed(15);
        player.setRotationSpeed(30);
        for (int i = 0; i < enemies.length; i++) {
            enemies[i].setMoveSpeed(20);
            enemies[i].setRotationSpeed(30);
            enemies[i].setMass(40);
            enemies[i].setMaxForce(3);
        }

        //setting SimpleAgentHitPoints and RobotFightInventory to agents
        player.setHitPoints(new SimpleAgentHitPoints(player));
        player.setInventory(new RobotFightInventory());
        for (Agent enemy : enemies) {
            enemy.setHitPoints(new SimpleAgentHitPoints(enemy));
            enemy.setInventory(new RobotFightInventory());
        }

        //giving them weapons
        ((RobotFightInventory) player.getInventory()).setActiveWeapon(new Cannon("cannon", player));
        ((RobotFightInventory) player.getInventory()).setSecondaryWeapon(new LaserWeapon("laser", player));
        for (int i = 0; i < enemies.length; i++) {
            if (i % 3 == 0) {
                ((RobotFightInventory) enemies[i].getInventory()).setActiveWeapon(new LaserWeapon("laser", enemies[i]));
            } else {
                if (i % 3 == 1) {
                    ((RobotFightInventory) enemies[i].getInventory()).setActiveWeapon(new Cannon("cannon", enemies[i]));
                } else {
                    ((RobotFightInventory) enemies[i].getInventory()).setActiveWeapon(new Knife("knife", enemies[i]));
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

        //attaching camera to player
        RoboFightSpatials.attachCameraTo(player, cam);

        //making move behaviour for player
        SimplePlayerMoveBehavior playerMove = new SimplePlayerMoveBehavior(player, null);
        ((FPS) aiAppState.getGameControl()).addMoveListener(player, playerMove);
        playerMove.addSupportedOperations(((FPS) aiAppState.getGameControl()).getPlayerMoveSupportedOperations(player));

        //making decreaseHitPoints behaviour for player
        SimplePlayerAttackBehavior playerAttack = new SimplePlayerAttackBehavior(player, null);
        ((FPS) aiAppState.getGameControl()).addAttackListener(player, playerAttack);
        playerAttack.addSupportedOperations(((FPS) aiAppState.getGameControl()).getPlayerAttackSupportedOperations(player));

        //making switch weapon behaviour for player
        SwitchWeaponsBehaviour playerSwitch = new SwitchWeaponsBehaviour(player);
        ((FPS) aiAppState.getGameControl()).addSwitchListener(player, playerSwitch);

        //making main behaviour for player and adding behaviours to it
        SimpleMainBehavior playerMain = new SimpleMainBehavior(player);
        playerMain.addBehavior(playerMove);
        playerMain.addBehavior(playerAttack);
        playerMain.addBehavior(playerSwitch);
        player.setMainBehaviour(playerMain);

        //setting main behaviour to bots
        for (int i = 0; i < enemies.length; i++) {
            SimpleMainBehavior enemyMain = new SimpleMainBehavior(enemies[i]);
            SimpleAttackBehavior attack = new SimpleAttackBehavior(enemies[i]);
            if (i % 3 == 0) {
                SimpleLookBehavior look = new SimpleLookBehavior(enemies[i]);
                look.setTypeOfWatching(SimpleLookBehavior.TypeOfWatching.AGENT_WATCHING);
                look.addListener(attack);
                //setting visibility range for behavior
                look.setVisibilityRange(150f);
                enemyMain.addBehavior(look);
                enemyMain.addBehavior(new WanderInsideTerrain(enemies[i], terrainSize));
            } else {
                if (i % 3 == 1) {
                    attack.setTarget(player);
                    enemyMain.addBehavior(new FleeInsideTerrain(terrainSize, enemies[i], player));
                } else {
                    attack.setTarget(player);
                    enemyMain.addBehavior(new SeekInsideTerrain(terrainSize, enemies[i], player));
                }
            }
            enemyMain.addBehavior(attack);
            enemies[i].setMainBehaviour(enemyMain);
        }

        //starting agents
        aiAppState.start();
    }

    @Override
    public void simpleUpdate(float tpf) {
        if (aiAppState.getGameControl().finish()) {
            if (gameFinishCountDown <= 0) {
                this.stop();
            } else {
                gameFinishCountDown -= tpf;
                BitmapText hudText = new BitmapText(guiFont, false);
                hudText.setSize(guiFont.getCharSet().getRenderedSize()); // font size
                hudText.setColor(ColorRGBA.Red); // font color
                if (aiAppState.getGameControl().win(player)) {
                    hudText.setText(player.getTeam().getName() + " wins."); // the text
                } else {
                    hudText.setText(enemies[0].getTeam().getName() + " wins."); // the text
                }
                hudText.setLocalTranslation(settings.getWidth() / 2 - hudText.getLineWidth() / 2, settings.getHeight() / 2 - hudText.getLineHeight() / 2, 0); // position
                guiNode.attachChild(hudText);
            }
        }
        aiAppState.update(tpf);
    }
}
