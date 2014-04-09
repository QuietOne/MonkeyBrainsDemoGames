/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package simulation.evolution.util;

import com.jme3.ai.agents.util.PhysicalObject;
import com.jme3.ai.agents.util.control.Game;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;

/**
 *
 * @author Tihomir Radosavljević
 */
public class Food extends PhysicalObject{

    private float energy;

    public Food(float energy) {
        this.energy = energy;
        this.spatial = EvolutionSpatials.initializeFood(energy);
        Game.getInstance().getRootNode().attachChild(spatial);
    }
    
    @Override
    protected void controlUpdate(float tpf) {
        
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public float getEnergy() {
        return energy;
    }

    public void setEnergy(float energy) {
        this.energy = energy;
    }

    public boolean moreEnergy(){
        return energy>0;
    }
    
    public void beingEaten(float tpf){
        energy-=tpf;
        if (energy<=0) {
            Game.getInstance().removeGameObject(this);
        }
    }
}
