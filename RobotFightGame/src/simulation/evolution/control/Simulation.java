package simulation.evolution.control;

import com.jme3.ai.agents.Agent;
import com.jme3.ai.agents.util.GameObject;
import com.jme3.ai.agents.util.control.Game;
import com.jme3.ai.agents.util.control.GameControl;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import java.util.Random;
import simulation.evolution.behaviours.LiveBehaviour;
import simulation.evolution.util.ALifeEntity;
import simulation.evolution.util.EvolutionSpatials;
import simulation.evolution.util.Food;
import simulation.evolution.util.Statistics;
import simulation.evolution.util.Weapon;

/**
 *
 * @author Tihomir Radosavljević
 */
public class Simulation implements GameControl {

    private Game game;
    private float terrainSize;
    
    public Simulation(float terrainSize) {
        game = Game.getInstance();
        this.terrainSize = terrainSize;
    }

    public void loadInputManagerMapping() {
        game.getInputManager().addMapping("Select", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        game.getInputManager().addMapping("Add", new MouseButtonTrigger(MouseInput.BUTTON_RIGHT));
    }
    
    public void addSelectListener(ActionListener actionListener){
        game.getInputManager().addListener(actionListener, "Select");
    }
    
    public void addAddListener(ActionListener actionListener){
        game.getInputManager().addListener(actionListener, "Add");
    }

    public boolean finish() {
        if (game.getAgents().isEmpty()) {
            game.setOver(true);
            return true;
        }
        return false;
    }

    public boolean win(Agent agent) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void restart() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void spawn(GameObject gameObject, Vector3f... area) {
        Random random = new Random();
        float x, z;
        int distance = (int) FastMath.abs(area[1].x - area[0].x);
        x = random.nextInt(distance / 2);
        if (random.nextBoolean()) {
            x *= -1;
        }
        distance = (int) FastMath.abs(area[1].z - area[0].z);
        z = random.nextInt(distance / 2);
        if (random.nextBoolean()) {
            z *= -1;
        }
        gameObject.setLocalTranslation(x, ((Food) gameObject).getEnergy()*0.01f, z);
        game.addGameObject(gameObject);
    }

    public void spawnAgent(String agentName, Vector3f... area) {
        Random random = new Random();
        //randomize location
        float x, z;
        int distance = (int) FastMath.abs(area[1].x - area[0].x);
        x = random.nextInt(distance / 2);
        if (random.nextBoolean()) {
            x *= -1;
        }
        distance = (int) FastMath.abs(area[1].z - area[0].z);
        z = random.nextInt(distance / 2);
        if (random.nextBoolean()) {
            z *= -1;
        }
        //randomize lifeSpan 
        float lifeSpan = random.nextInt(30)+50;
        //randomize maxFoodAmount
        float maxFoodAmount = random.nextInt(30)+10;
        //randomize hotness level
        float hotness = random.nextFloat();
        float eatPerTime = random.nextInt(20)+20;
        //randomize gender
        ColorRGBA gender;
        if (random.nextBoolean()) {
            gender = ColorRGBA.Blue;
            Statistics.getInstance().addCurrentNumberOfBlue();
            Statistics.getInstance().addTotalNumberOfBlue();
        } else {
            gender = ColorRGBA.Red;
            Statistics.getInstance().addCurrentNumberOfRed();
            Statistics.getInstance().addTotalNumberOfRed();
        }
        Agent<ALifeEntity> agent = new Agent<ALifeEntity>(agentName, EvolutionSpatials.initializeAgent(agentName, gender));
        ALifeEntity aLifeEntity = new ALifeEntity(agent, lifeSpan, maxFoodAmount, hotness, eatPerTime, gender);
        agent.setModel(aLifeEntity);
        agent.setLocalTranslation(x, 0, z);
        agent.setMaxHitPoint(random.nextInt(100)+100);
        agent.setMoveSpeed(random.nextInt(20)+10);
        agent.setRotationSpeed(20);
        agent.setVisibilityRange(random.nextInt(200)+100);
        agent.setMass(random.nextInt(40)+10);
        agent.setMaxForce(3);
        agent.setWeapon(new Weapon(agentName, agent));
        agent.setMainBehaviour(new LiveBehaviour(terrainSize, agent));
        game.addAgent(agent);
        agent.start();
    }
    
    public void spawnAgentChildren(){
        //children get name after their parents
        String name = String.valueOf(new Random().nextInt(100000));
        spawnAgent(name,
                    new Vector3f(terrainSize * 2 - 5, 0, terrainSize * 2 - 5),
                    new Vector3f(-terrainSize * 2 + 5, 0, -terrainSize * 2 + 5));
        System.out.println("New baby has been born " + name);
    }
    
    public void spawnAgent(Vector3f position){
        Random random = new Random();
        //randomize lifeSpan 
        float lifeSpan = random.nextInt(30)+50;
        //randomize maxFoodAmount
        float maxFoodAmount = random.nextInt(30)+10;
        //randomize hotness level
        float hotness = random.nextFloat();
        float eatPerTime = random.nextInt(20)+20;
        //randomize gender
        ColorRGBA gender;
        if (random.nextBoolean()) {
            gender = ColorRGBA.Blue;
            Statistics.getInstance().addCurrentNumberOfBlue();
            Statistics.getInstance().addTotalNumberOfBlue();
        } else {
            gender = ColorRGBA.Red;
            Statistics.getInstance().addCurrentNumberOfRed();
            Statistics.getInstance().addTotalNumberOfRed();
        }
        String agentName = String.valueOf(new Random().nextInt(100000));
        Agent<ALifeEntity> agent = new Agent<ALifeEntity>(agentName, EvolutionSpatials.initializeAgent(agentName, gender));
        ALifeEntity aLifeEntity = new ALifeEntity(agent, lifeSpan, maxFoodAmount, hotness, eatPerTime, gender);
        agent.setModel(aLifeEntity);
        agent.setLocalTranslation(position);
        agent.setMaxHitPoint(random.nextInt(100)+100);
        agent.setMoveSpeed(random.nextInt(20)+10);
        agent.setRotationSpeed(20);
        agent.setVisibilityRange(random.nextInt(200)+100);
        agent.setMass(random.nextInt(40)+10);
        agent.setMaxForce(3);
        agent.setWeapon(new Weapon(agentName, agent));
        agent.setMainBehaviour(new LiveBehaviour(terrainSize, agent));
        game.addAgent(agent);
        agent.start();
    }
}
