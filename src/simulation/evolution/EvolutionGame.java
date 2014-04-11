package simulation.evolution;

import com.jme3.ai.agents.util.control.Game;
import com.jme3.app.SimpleApplication;
import com.jme3.material.Material;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import java.util.Random;
import simulation.evolution.control.Simulation;
import simulation.evolution.util.EvolutionSpatials;
import simulation.evolution.util.Food;

/**
 * Testing demo game for MonkeyBrains framework. Some introduction to game:
 * -this is simulation and not game - agents have their behaviours and they act
 * accordigly with those behaviours - user can move and rotate camera in any
 * direction - by clicking on agent, user can see what agent is currently doing
 *
 * @author Tihomir Radosavljević
 * @version 1.0
 */
public class EvolutionGame extends SimpleApplication {

    //Defining game
    private Game game = Game.getInstance();
    //game stats
    private final int firstAgentPopulationSize = 10;
    private final int firstFoodCount = 10;
    private final float timeForNewFood = 10f;
    private final float terrainSize = 40f;
    private float timeUntilNextFood = timeForNewFood;

    public static void main(String[] args) {
        EvolutionGame app = new EvolutionGame();
        app.start();
    }

    @Override
    public void simpleInitApp() {
        //defining rootNode for game processing
        game.setRootNode(rootNode);
        //setting game Genre
        game.setGameControl(new Simulation(terrainSize));
        game.setFriendlyFire(false);

        //setting camera
        flyCam.setEnabled(true);
        flyCam.setMoveSpeed(20);
        flyCam.setRotationSpeed(10);
        flyCam.setDragToRotate(true);
        cam.setLocation(new Vector3f(93.90004f, 76.03973f, -94.62378f));
        cam.setRotation(new Quaternion(0.29962486f, -0.37228543f, 0.12836115f, 0.8689947f));

        //Spatials for graphics for this game
        EvolutionSpatials.material = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        EvolutionSpatials.initializeFloor(terrainSize);
        viewPort.addProcessor(EvolutionSpatials.initializeBloom(assetManager));

        for (int i = 0; i < firstAgentPopulationSize; i++) {
            String name = String.valueOf((char) i + 65) + ((char) i * 2 + 65) + ((char) i * 3 + 65);
            ((Simulation) game.getGameControl()).spawnAgent(name,
                    new Vector3f(terrainSize * 2 - 5, 0, terrainSize * 2 - 5),
                    new Vector3f(-terrainSize * 2 + 5, 0, -terrainSize * 2 + 5));
        }

        for (int i = 0; i < firstFoodCount; i++) {
            ((Simulation) game.getGameControl()).spawn(new Food(60),
                    new Vector3f(terrainSize * 2 - 5, 0, terrainSize * 2 - 5),
                    new Vector3f(-terrainSize * 2 + 5, 0, -terrainSize * 2 + 5));
        }

    }

    @Override
    public void simpleUpdate(float tpf) {
        //spawn food occasionally
        if (timeUntilNextFood <= 0) {
            ((Simulation) game.getGameControl()).spawn(new Food(new Random().nextInt(80) + 20),
                    new Vector3f(terrainSize * 2 - 5, 0, terrainSize * 2 - 5),
                    new Vector3f(-terrainSize * 2 + 5, 0, -terrainSize * 2 + 5));
            timeUntilNextFood = timeForNewFood;
        } else {
            timeUntilNextFood -= tpf;
        }
        game.update(tpf);
        //if all agents are dead finish simultaion
        if (game.getGameControl().finish()) {
            this.stop();
        }
    }
}
