package simulation.evolution;

import com.jme3.ai.agents.util.control.Game;
import com.jme3.app.SimpleApplication;
import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import java.util.Random;
import simulation.evolution.control.Simulation;
import simulation.evolution.util.EvolutionSpatials;
import simulation.evolution.util.Food;

/**
 * Testing demo game for MonkeyBrains framework. Some introduction to game:
 *
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
        //DefinedSpatials for graphics for this game
        EvolutionSpatials.material = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        EvolutionSpatials.initializeFloor(terrainSize);
        viewPort.addProcessor(EvolutionSpatials.initializeBloom(assetManager));

//        ((Simulation) game.getGameControl()).spawnAgent("Joker",
//                    new Vector3f(terrainSize * 2 - 5, 0, terrainSize * 2 - 5),
//                    new Vector3f(-terrainSize * 2 + 5, 0, -terrainSize * 2 + 5));
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

        //starting agents
        game.start();

    }

    @Override
    public void simpleUpdate(float tpf) {
        if (timeUntilNextFood <= 0) {
            ((Simulation) game.getGameControl()).spawn(new Food(new Random().nextInt(80)+20),
                    new Vector3f(terrainSize * 2 - 5, 0, terrainSize * 2 - 5),
                    new Vector3f(-terrainSize * 2 + 5, 0, -terrainSize * 2 + 5));
            timeUntilNextFood = timeForNewFood;
        } else {
            timeUntilNextFood -= tpf;
        }
        game.update(tpf);
        if (game.getGameControl().finish()) {
            this.stop();
        }
    }
}
