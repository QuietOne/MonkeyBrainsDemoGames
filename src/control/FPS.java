package control;

import com.jme3.ai.agents.Agent;
import com.jme3.ai.agents.behaviours.Behaviour;
import com.jme3.ai.agents.behaviours.npc.SimpleAttackBehaviour;
import com.jme3.ai.agents.util.PhysicalObject;
import com.jme3.ai.agents.util.control.Game;
import com.jme3.ai.agents.util.control.GameGenre;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

/**
 *
 * @author Tihomir Radosavljević
 */
public class FPS implements GameGenre {

    Game game;
    InputManager inputManager;

    public FPS() {
        game = Game.getInstance();
        inputManager = game.getInputManager();
    }

    public void loadInputManagerMapping() {
        inputManager.addMapping("moveForward", new KeyTrigger(KeyInput.KEY_UP), new KeyTrigger(KeyInput.KEY_W));
        inputManager.addMapping("moveBackward", new KeyTrigger(KeyInput.KEY_DOWN), new KeyTrigger(KeyInput.KEY_S));
        inputManager.addMapping("moveRight", new KeyTrigger(KeyInput.KEY_D), new KeyTrigger(KeyInput.KEY_RIGHT));
        inputManager.addMapping("moveLeft", new KeyTrigger(KeyInput.KEY_A), new KeyTrigger(KeyInput.KEY_LEFT));
        inputManager.addMapping("Shoot", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
    }

    public void addMoveListener(Agent agent, AnalogListener behaviour) {
        inputManager.addListener(behaviour, "moveForward", "moveBackward", "moveRight", "moveLeft");
    }


    public void addAttackListener(Agent agent, ActionListener behaviour) {
        inputManager.addListener(behaviour, "Shoot");
    }

    public HashMap<String, Behaviour> getPlayerMoveSupportedOperations(Agent agent) {
        HashMap<String, Behaviour> supportedOperations = new HashMap<String, Behaviour>();
        supportedOperations.put("moveForward", new Behaviour(agent) {
            @Override
            protected void controlUpdate(float tpf) {
                Vector3f oldPos = agent.getLocalTranslation().clone();
                agent.getSpatial().move(agent.getLocalRotation().mult(new Vector3f(0, 0, agent.getMoveSpeed() * tpf)));
                if (agent.getLocalTranslation().x > 40 * 2 || agent.getLocalTranslation().z > 40 * 2
                        || agent.getLocalTranslation().x < -40 * 2 || agent.getLocalTranslation().z < -40 * 2) {
                    agent.setLocalTranslation(oldPos);
                }
                enabled = false;
            }

            @Override
            protected void controlRender(RenderManager rm, ViewPort vp) {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        });
        supportedOperations.put("moveBackward", new Behaviour(agent) {
            @Override
            protected void controlUpdate(float tpf) {
                Vector3f oldPos = agent.getLocalTranslation().clone();
                agent.getSpatial().move(agent.getLocalRotation().mult(new Vector3f(0, 0, -agent.getMoveSpeed() * tpf)));
                if (agent.getLocalTranslation().x > 40 * 2 || agent.getLocalTranslation().z > 40 * 2
                        || agent.getLocalTranslation().x < -40 * 2 || agent.getLocalTranslation().z < -40 * 2) {
                    agent.setLocalTranslation(oldPos);
                }
                enabled = false;
            }

            @Override
            protected void controlRender(RenderManager rm, ViewPort vp) {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        });
        supportedOperations.put("moveRight", new Behaviour(agent) {
            @Override
            protected void controlUpdate(float tpf) {
                agent.getSpatial().rotate(0, -(FastMath.DEG_TO_RAD * tpf) * agent.getRotationSpeed(), 0);
                enabled = false;
            }

            @Override
            protected void controlRender(RenderManager rm, ViewPort vp) {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        });
        supportedOperations.put("moveLeft", new Behaviour(agent) {
            @Override
            protected void controlUpdate(float tpf) {
                agent.getSpatial().rotate(0, (FastMath.DEG_TO_RAD * tpf) * agent.getRotationSpeed(), 0);
                enabled = false;
            }

            @Override
            protected void controlRender(RenderManager rm, ViewPort vp) {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        });
        return supportedOperations;
    }

    public HashMap<String, Behaviour> getPlayerAttackSupportedOperations(Agent agent) {
        HashMap<String, Behaviour> supportedOperations = new HashMap<String, Behaviour>();
        supportedOperations.put("Shoot", new SimpleAttackBehaviour(agent));
        return supportedOperations;
    }

    public boolean finish() {
        //checking if one team left if it has, then game over
        int i = 0;
        List<Agent> agents = game.getAgents();
        //find first alive agent in list
        while (i < agents.size() && !agents.get(i).isEnabled()) {
            i++;
        }
        int j = i + 1;
        while (j < agents.size()) {
            //finding next agent that is alive
            if (!agents.get(j).isEnabled()) {
                j++;
                continue;
            }
            //if agent is not in the same team, game is certainly not over
            if (!agents.get(i).isSameTeam(agents.get(j))) {
                break;
            }
            j++;
        }
        if (j >= agents.size()) {
            game.setOver(true);
        }
        return game.isOver();
    }

    public boolean win(Agent agent) {
        if (game.isOver()) {
            if (agent.isEnabled()) {
                return true;
            }
        }
        return false;
    }

    public void restart() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void spawn(PhysicalObject gameObject, Vector3f... area) {
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
        gameObject.setLocalTranslation(x, 0, z);
        if (gameObject instanceof Agent) {
            game.addAgent((Agent) gameObject);
        } else {
            game.addGameObject(gameObject);
        }
    }

}
