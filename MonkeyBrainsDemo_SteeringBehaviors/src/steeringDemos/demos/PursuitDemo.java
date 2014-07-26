//Copyright (c) 2014, Jesús Martín Berlanga. All rights reserved. Distributed under the BSD licence. Read "com/jme3/ai/license.txt".

package steeringDemos.demos;

import com.jme3.ai.agents.Agent;
import com.jme3.ai.agents.util.control.Game;
import com.jme3.app.SimpleApplication;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.material.Material;
import com.jme3.scene.Spatial;

import com.jme3.ai.agents.behaviours.npc.steering.SeparationBehaviour;
import com.jme3.ai.agents.behaviours.npc.SimpleMainBehaviour;
import com.jme3.ai.agents.behaviours.npc.steering.BalancedCompoundSteeringBehaviour;
import com.jme3.ai.agents.behaviours.npc.steering.CompoundSteeringBehaviour;
import com.jme3.ai.agents.behaviours.npc.steering.MoveBehaviour;
import com.jme3.ai.agents.behaviours.npc.steering.PursuitBehaviour;
import com.jme3.ai.agents.behaviours.npc.steering.WanderBehaviour;
import com.jme3.font.BitmapText;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import java.util.ArrayList;

import java.awt.event.ActionEvent;
import javax.swing.Timer;

import steeringDemos.control.CustomSteerControl;

import java.util.List;
import java.util.Arrays;

/**
 * AI Steer Test - Testing the pursuit and separation behaviours
 *
 * @author Jesús Martín Berlanga
 * @version 1.3
 */
public class PursuitDemo extends SimpleApplication {
    
    private SeparationBehaviour[] separation;
    private boolean isStrengthEscalar = true;
    private float escalarStrength = 0.1f;
    private BitmapText escalarStrengthHudText;
    private BitmapText escalarInfoHudtext;
    private final String ESCALAR_INFO_HUD_MESSAGE = "Press H to increase the separation escalar strength, L to decrease.";
    
    private Game game = Game.getInstance(); //creating game
    //TEST SETTINGS - START
    private final String BOID_MODEL_NAME = "Models/boid.j3o";
    private final String BOID_MATERIAL_NAME = "Common/MatDefs/Misc/Unshaded.j3md";
    private final ColorRGBA TARGET_COLOR = ColorRGBA.Red;
    private final float TARGET_MOVE_SPEED = 1f;
    private final float TARGET_ROTATION_SPEED = 30;
    private final float TARGET_MASS = 50;
    private final float TARGET_MAX_FORCE = 20;
    private final int NUMBER_NEIGHBOURS = 50;
    private final ColorRGBA NEIGHBOURS_COLOR = ColorRGBA.Blue;
    private final float NEIGHBOURS_MOVE_SPEED = 0.96f;
    private final float NEIGHBOURS_ROTATION_SPEED = 30;
    private final float NEIGHBOURS_MASS = 50;
    private final float NEIGHBOURS_MAX_FORCE = 20;
    //TEST SETTINGS - END

    MoveBehaviour targetMoveBehavior;

    WanderBehaviour targetWanderBehavior;
    
    private boolean turnDinamicMode = true;
    
    private java.awt.event.ActionListener changeDinamicMode = new java.awt.event.ActionListener()
    {
        public void actionPerformed(ActionEvent event)
        {
            if(turnDinamicMode)
            {
                targetMoveBehavior.setupStrengthControl(1);
                targetWanderBehavior.setupStrengthControl(0);
                turnDinamicMode = false;
            }
            else
            {
                targetMoveBehavior.setupStrengthControl(0.25f);
                targetWanderBehavior.setupStrengthControl(1);
                turnDinamicMode = true;
            }
        }
    };
    
    private Timer iterationTimer;
    
    public static void main(String[] args) {
        PursuitDemo app = new PursuitDemo();
        app.start();
    }
    
    @Override
    public void simpleInitApp() {
        
        //KEYS
        keys();
        
        //HUD TEXT
        BitmapText hudText = new BitmapText(guiFont, false);          
        hudText.setSize(guiFont.getCharSet().getRenderedSize() * 0.65f);      // font size
        hudText.setColor(ColorRGBA.Red);                             // font color
        hudText.setText("Press N to switch betwen escalar 'separation strength' and 'plane strength'.");             // the text
        hudText.setLocalTranslation(0, 475, 0); // position
        guiNode.attachChild(hudText);
        
        this.escalarStrengthHudText = new BitmapText(guiFont, false);          
        this.escalarStrengthHudText.setSize(guiFont.getCharSet().getRenderedSize() * 0.65f);      // font size
        this.escalarStrengthHudText.setColor(ColorRGBA.Orange);                             // font color
        this.escalarStrengthHudText.setText(String.valueOf(this.escalarStrength));             // the text
        this.escalarStrengthHudText.setLocalTranslation(0, 430, 0); // position
        guiNode.attachChild(escalarStrengthHudText); 
        
        this.escalarInfoHudtext = new BitmapText(guiFont, false);          
        this.escalarInfoHudtext.setSize(guiFont.getCharSet().getRenderedSize() * 0.65f);      // font size
        this.escalarInfoHudtext.setColor(ColorRGBA.Orange);                             // font color
        this.escalarInfoHudtext.setText(this.ESCALAR_INFO_HUD_MESSAGE);             // the text
        this.escalarInfoHudtext.setLocalTranslation(0, 450, 0); // position
        guiNode.attachChild(escalarInfoHudtext); 
        
        
        //defining rootNode for game processing
        game.setApp(this);
        game.setGameControl(new CustomSteerControl(5f));
        
        this.setupCamera();
        
        Vector3f[] spawnArea = null;
        
        Agent target = this.createBoid("Target", this.TARGET_COLOR);
        game.addAgent(target); //Add the target to the game
        this.setStats(target, this.TARGET_MOVE_SPEED,
                this.TARGET_ROTATION_SPEED, this.TARGET_MASS,
                this.TARGET_MAX_FORCE);
        game.getGameControl().spawn(target, new Vector3f());
        //this.setStats(target, this.TARGET_MOVE_SPEED, this.TARGET_ROTATION_SPEED, 
        //        this.TARGET_MASS, this.TARGET_MAX_FORCE);
        
        Agent[] neighbours = new Agent[this.NUMBER_NEIGHBOURS];
        
        for (int i = 0; i < this.NUMBER_NEIGHBOURS; i++) {
            neighbours[i] = this.createBoid("Neighbour " + i, this.NEIGHBOURS_COLOR);
            game.addAgent(neighbours[i]); //Add the neighbours to the game
            this.setStats(neighbours[i], this.NEIGHBOURS_MOVE_SPEED,
                    this.NEIGHBOURS_ROTATION_SPEED, this.NEIGHBOURS_MASS,
                    this.NEIGHBOURS_MAX_FORCE);
            game.getGameControl().spawn(neighbours[i], spawnArea);
        }
        
        List<Agent> obstacles = new ArrayList<Agent>();
        obstacles.addAll(Arrays.asList(neighbours));
        obstacles.add(target);
        
        SimpleMainBehaviour targetMainBehaviour = new SimpleMainBehaviour(target);
        targetMoveBehavior = new MoveBehaviour(target);
        targetMoveBehavior.setupStrengthControl(0.25f);
        targetWanderBehavior = new WanderBehaviour(target);
        targetMoveBehavior.setMoveDirection(new Vector3f(1, 1, 0)); //moves in x-y direction
        
        this.iterationTimer = new Timer(10000, this.changeDinamicMode); //10000ns = 10s
        this.iterationTimer.start();
        
        CompoundSteeringBehaviour targetSteer = new CompoundSteeringBehaviour(target);
        targetSteer.addSteerBehaviour(targetMoveBehavior);
        targetSteer.addSteerBehaviour(targetWanderBehavior);
        
        float randomDistance = ((float) Math.random()) * 1000f;
        
        targetMoveBehavior.setMoveDirection(new Vector3f(randomDistance, randomDistance, randomDistance));
        
        targetMainBehaviour.addBehaviour(targetSteer);
        target.setMainBehaviour(targetMainBehaviour);
        
        SimpleMainBehaviour[] neighboursMainBehaviour = new SimpleMainBehaviour[neighbours.length];
        
        separation = new SeparationBehaviour[neighbours.length];
        
        for (int i = 0; i < neighbours.length; i++) {
            neighboursMainBehaviour[i] = new SimpleMainBehaviour(neighbours[i]);
            
            PursuitBehaviour pursuit = new PursuitBehaviour(neighbours[i], target);
            
            this.separation[i] = new SeparationBehaviour(neighbours[i], obstacles);
            
            separation[i].setupStrengthControl(escalarStrength);

            BalancedCompoundSteeringBehaviour steer = new BalancedCompoundSteeringBehaviour(neighbours[i]);
            
            steer.addSteerBehaviour(separation[i]);
            steer.addSteerBehaviour(pursuit);
            
            neighboursMainBehaviour[i].addBehaviour(steer);

            neighbours[i].setMainBehaviour(neighboursMainBehaviour[i]);
        }
        
        game.start();
    }
    
    private void setupCamera() {
        getCamera().setLocation(new Vector3f(0, 20, 0));
        getCamera().lookAt(Vector3f.ZERO, Vector3f.UNIT_X);
        getFlyByCamera().setMoveSpeed(20);

        //flyCam.setDragToRotate(true);
        //flyCam.setEnabled(false);  
    }

    //Create an agent with a name and a color
    private Agent createBoid(String name, ColorRGBA color) {
        Spatial boidSpatial = assetManager.loadModel(this.BOID_MODEL_NAME);
        boidSpatial.setLocalScale(0.1f); //Resize
        
        Material mat = new Material(assetManager, this.BOID_MATERIAL_NAME);        
        mat.setColor("Color", color);
        boidSpatial.setMaterial(mat);
        
        return new Agent(name, boidSpatial);
    }

    //Setup the stats for an agent
    private void setStats(Agent myAgent, float moveSpeed, float rotationSpeed,
            float mass, float maxForce) {
        
        myAgent.setMoveSpeed(moveSpeed);
        myAgent.setRotationSpeed(rotationSpeed);
        myAgent.setMass(mass);
        myAgent.setMaxForce(maxForce);
    }
    
    @Override
    public void simpleUpdate(float tpf) {
        game.update(tpf);
        
        if(this.isStrengthEscalar)
        {
            escalarStrengthHudText.setText(String.valueOf(escalarStrength));
            this.escalarInfoHudtext.setText(this.ESCALAR_INFO_HUD_MESSAGE);
        }
        else
        {
            escalarStrengthHudText.setText("");
            this.escalarInfoHudtext.setText("");   
        }
    }
    
  /** Custom Keybinding: Map named actions to inputs. */
  private void keys() {
    // You can map one or several inputs to one named action
    inputManager.addMapping("Switch mode",  new KeyTrigger(KeyInput.KEY_N));
    inputManager.addMapping("Increase separation",   new KeyTrigger(KeyInput.KEY_H));
    inputManager.addMapping("Decrease separation",  new KeyTrigger(KeyInput.KEY_L));

    // Add the names to the action listener.
    inputManager.addListener(actionListener,"Switch mode");
    inputManager.addListener(analogListener,"Increase separation", "Decrease separation");
 
  }
  
   private ActionListener actionListener = new ActionListener() {
    public void onAction(String name, boolean keyPressed, float tpf) {
        
      if (name.equals("Switch mode") && !keyPressed) {
         changeMode();
      }
      
    }
  };
   
   private void changeMode() {
       
       if(this.isStrengthEscalar) 
       {
           for(SeparationBehaviour behaviour :  this.separation)
                behaviour.setupStrengthControl(1, 1, 0);
           
           this.isStrengthEscalar = false;
       }
       else
       {
           for(SeparationBehaviour behaviour :  this.separation)
                 behaviour.setupStrengthControl(escalarStrength);
           
           this.isStrengthEscalar = true;
       }
   }
 
  private AnalogListener analogListener = new AnalogListener() {
    public void onAnalog(String name, float value, float tpf) {

        
        if (name.equals("Increase separation")) {
          increaseSeparation();
        }
        else if (name.equals("Decrease separation")) {
          decreaseSeparation();
        }

    }
  };
  
  private void increaseSeparation() {
      
      if(this.isStrengthEscalar)
      {
          this.escalarStrength = this.escalarStrength + 0.05f;
          
          for(SeparationBehaviour behaviour :  this.separation)
          behaviour.setupStrengthControl(this.escalarStrength);
      }
      
  }
  
  private void decreaseSeparation() {
      
      if(this.isStrengthEscalar)
      {
          this.escalarStrength = this.escalarStrength - 0.075f;
          
          if(this.escalarStrength < 0)
              this.escalarStrength = 0;
          
          for(SeparationBehaviour behaviour :  this.separation)
             behaviour.setupStrengthControl(this.escalarStrength);
      }
      
  }
  
  
}
