package fps.robotfight.util;

import com.jme3.ai.agents.Agent;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.ai.agents.util.AbstractWeapon;
import com.jme3.ai.agents.util.AbstractBullet;
import com.jme3.ai.agents.util.control.Game;

/**
 * Weapon for this game. It throws green balls that hurts other physical objects.
 * @author Tihomir Radosavljević
 */
public class Cannon extends AbstractWeapon{

     public Cannon(String name, Agent agent){
        this.name = name;
        this.agent = agent;
        this.maxAttackRange = 500f;
        this.minAttackRange = 0;
        this.attackDamage = 10f;
        this.numberOfBullets = -1;
        this.cooldown = 3f;
        this.mass = 30f;
    }

    @Override
    protected AbstractBullet controlAttack(Vector3f direction, float tpf) {
        AbstractBullet cannonBall = new CannonBall(this, RoboFightSpatials.initializeCannonball(), direction);
        bullet = cannonBall;
        ((Node) Game.getInstance().getRootNode()).attachChild(cannonBall.getSpatial());
        return cannonBall;
    }

    
}
