//Copyright (c) 2014, Jesús Martín Berlanga. All rights reserved. Distributed under the BSD licence. Read "com/jme3/ai/license.txt".

package steeringDemos.control;

import com.jme3.ai.agents.Agent;
import com.jme3.ai.agents.util.GameObject;
import com.jme3.ai.agents.util.control.Game;
import com.jme3.ai.agents.util.control.GameControl;
//import com.jme3.input.InputManager;
import com.jme3.math.Vector3f;

/**
 *
 * Custom steer control.
 * 
 * @author Jesús Martín Berlanga
 * @version 1.0
 */
public class CustomSteerControl implements GameControl {
    
    
    private float aleatoryFactor;
    private Game game;
    //private InputManager inputManager;

    public CustomSteerControl() {
        game = Game.getInstance();
        this.aleatoryFactor = 10f;
        //inputManager = game.getInputManager();
    }
    
    public CustomSteerControl(float aleatoryFactor) {
       game = Game.getInstance();
       this.aleatoryFactor = aleatoryFactor;
    }
    
    /**
     *  There is no imput mapping
     * @see GameControl#loadInputManagerMapping() 
     */
    public void loadInputManagerMapping(){
       //Void
    }
    
   /**
    * @see GameControl#finish() 
    * 
    * @return Always return false.
    */
    public boolean finish(){
        return false;
    }    
    
    /**
     * @see GameControl#win(com.jme3.ai.agents.Agent) 
     * 
     * @param agent
     * @return Always return false.
     */
    public boolean win(Agent agent){
        return false;
    }
  
    /**
     * There is no restart
     * 
     * @see GameControl#restart() 
     */
    public void restart(){
        //Void
    }
   
    /**
     * Method for creating objects in given area.
     * 
     * <b> PRE: </b> The area must be void or a point. <br>
     * <br>
     * 
     * @param gameObject object that should be created
     * @param area Null for random location
     *        and a point for a stablished location.
     * 
     * @see GameControl#spawn(com.jme3.ai.agents.util.GameObject, com.jme3.math.Vector3f[]) 
     */
    public void spawn(GameObject gameObject, Vector3f... area){
        
        if(area == null)  //Random location
            gameObject.setLocalTranslation(
                 ( (float)((Math.random()*2) - 1) )*this.aleatoryFactor, 
                 ( (float)((Math.random()*2) - 1) )*this.aleatoryFactor, 
                 ( (float)((Math.random()*2) - 1) )*this.aleatoryFactor);
        else if(area.length == 1){ //Spawn in a point
            gameObject.setLocalTranslation(area[0]);
        }
        
        if (gameObject instanceof Agent) {
            game.addAgent((Agent) gameObject);
        } else {
            game.addGameObject(gameObject);
        }
    }
}
