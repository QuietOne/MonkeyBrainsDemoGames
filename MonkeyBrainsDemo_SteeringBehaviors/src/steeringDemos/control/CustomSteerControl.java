/*
Copyright (c) 2014, Jesús Martín Berlanga
All rights reserved.

Redistribution and use in source and binary forms, with or without modification, 
are permitted provided that the following conditions are met:

1. Redistributions of source code must retain the above copyright notice, this 
list of conditions and the following disclaimer.

2. Redistributions in binary form must reproduce the above copyright notice, 
this list of conditions and the following disclaimer in the documentation 
and/or other materials provided with the distribution.

3. Neither the name of the copyright holder nor the names of its contributors
may be used to endorse or promote products derived from this software without 
specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND 
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE 
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL 
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR 
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
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
