package simulation.evolution;

import com.jme3.ai.agents.Agent;
import com.jme3.ai.agents.util.control.Game;
import com.jme3.app.SimpleApplication;
import com.jme3.collision.CollisionResults;
import com.jme3.font.BitmapText;
import com.jme3.input.controls.ActionListener;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import java.util.List;
import java.util.Random;
import simulation.evolution.control.Simulation;
import simulation.evolution.util.ALifeEntity;
import simulation.evolution.util.EvolutionSpatials;
import simulation.evolution.util.Food;
import simulation.evolution.util.Statistics;

/**
 * Testing demo game for MonkeyBrains framework. Some introduction to game:
 * -this is simulation and not game - agents have their behaviours and they act
 * accordigly with those behaviours - user can move and rotate camera in any
 * direction - by clicking on agent, user can see what agent is currently doing
 *
 * @author Tihomir Radosavljević
 * @version 1.0
 */
public class EvolutionGame extends SimpleApplication implements ActionListener {

    //Defining game
    private Game game = Game.getInstance();
    //game stats
    private final int firstAgentPopulationSize = 10;
    private final int firstFoodCount = 10;
    private final float timeForNewFood = 10f;
    private final float terrainSize = 40f;
    private float timeUntilNextFood = timeForNewFood;
    private BitmapText agentStatusText;
    private BitmapText statisticsText;
    private Agent inspectedAgent;

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
        //defining input
        game.setInputManager(inputManager);
        game.getGameControl().loadInputManagerMapping();
        ((Simulation) game.getGameControl()).addSelectListener(this);
        ((Simulation) game.getGameControl()).addAddListener(this);
        //for texts description
        agentStatusText = new BitmapText(guiFont, false);
        agentStatusText.setSize(guiFont.getCharSet().getRenderedSize());
        statisticsText = new BitmapText(guiFont, false);
        statisticsText.setSize(guiFont.getCharSet().getRenderedSize());
        statisticsText.setColor(ColorRGBA.Yellow);
        statisticsText.setText("STARTING");
        statisticsText.setLocalTranslation(settings.getWidth() - statisticsText.getLineWidth()*10, settings.getHeight() - statisticsText.getLineHeight(), 0); // position
        guiNode.attachChild(statisticsText);

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
        //update agent text
        if (inspectedAgent != null) {
            agentStatusText.setText(((ALifeEntity) inspectedAgent.getModel()).toString());
        }
        statisticsText.setText(Statistics.getInstance().toString());
    }

    public void onAction(String name, boolean isPressed, float tpf) {
        if (name.equals("Select")) {
            CollisionResults results = new CollisionResults();
            Vector2f click2d = inputManager.getCursorPosition();
            Vector3f click3d = cam.getWorldCoordinates(new Vector2f(click2d.x, click2d.y), 0f).clone();
            Vector3f dir = cam.getWorldCoordinates(new Vector2f(click2d.x, click2d.y), 1f).subtractLocal(click3d).normalizeLocal();
            Ray ray = new Ray(click3d, dir);
            rootNode.collideWith(ray, results);
            if (results.size() > 0) {
                guiNode.detachChild(agentStatusText);
                String agentName = results.getClosestCollision().getGeometry().getParent().getName();
                List<Agent> agents = game.getAgents();
                for (int i = 0; i < agents.size(); i++) {
                    if (agentName.equals(agents.get(i).getName())) {
                        inspectedAgent = agents.get(i);
                        agentStatusText.setColor(((ALifeEntity) inspectedAgent.getModel()).getGender());
                        agentStatusText.setText(((ALifeEntity) inspectedAgent.getModel()).toString());
                        agentStatusText.setLocalTranslation(settings.getWidth() - agentStatusText.getLineWidth(), settings.getHeight() - agentStatusText.getLineHeight() / 4 * 3, 0); // position
                        guiNode.attachChild(agentStatusText);
                        break;
                    }
                }
            } else {
                inspectedAgent = null;
            }
        }
        if (name.equals("Add")) {
            CollisionResults results = new CollisionResults();
            Vector2f click2d = inputManager.getCursorPosition();
            Vector3f click3d = cam.getWorldCoordinates(new Vector2f(click2d.x, click2d.y), 0f).clone();
            Vector3f dir = cam.getWorldCoordinates(new Vector2f(click2d.x, click2d.y), 1f).subtractLocal(click3d).normalizeLocal();
            Ray ray = new Ray(click3d, dir);
            rootNode.collideWith(ray, results);
            if (results.size() > 0) {
                Vector3f position = new Vector3f(results.getClosestCollision().getContactPoint());
                position.y=0;
                ((Simulation)game.getGameControl()).spawnAgent(position);
            }
        }
    }
}
