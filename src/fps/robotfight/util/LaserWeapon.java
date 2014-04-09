package util;

import com.jme3.ai.agents.Agent;
import com.jme3.collision.CollisionResults;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Quad;
import com.jme3.ai.agents.util.AbstractWeapon;
import com.jme3.ai.agents.util.AbstractBullet;
import com.jme3.ai.agents.util.control.Game;

/**
 * Specific weapon for this game. 
 * @author Tihomir Radosavljević
 * @version 1.0
 */
public class LaserWeapon extends AbstractWeapon{

    public LaserWeapon(String name, Agent agent){
        this.name = name;
        this.agent = agent;
        this.maxAttackRange = 40f;
        this.minAttackRange = 0;
        this.attackDamage = 10f;
        this.numberOfBullets = -1;
        cooldown = 0.25f;
    }

    @Override
    //laser is instant weapon so damage part can be put into weapon, so
    //bullet won't check it everytime on update
    protected AbstractBullet controlAttack(Vector3f direction, float tpf) {
        if (bullet!=null) {
            return null;
        }
        float laserLength = maxAttackRange;
        Game game = Game.getInstance();
        CollisionResults collsions = new CollisionResults();
        Vector3f click3d = new Vector3f(agent.getLocalTranslation());
        Vector3f dir = direction.subtract(click3d).normalizeLocal();
        click3d.y = 3;
        Ray ray = new Ray(click3d, dir);
        ray.setLimit(maxAttackRange);
        //this the part where it hurts
        for (Agent target : game.getAgents()) {
            target.getSpatial().collideWith(ray, collsions);
            if (collsions.size() / 2 > 1 && !agent.equals(target)) {
                if (!game.isFriendlyFire() && agent.isSameTeam(target)) {
                    break;
                }
                game.agentAttack(agent, target);
                ((Quad) ((Geometry) ((Node) target.getSpatial()).getChild("healthbar")).getMesh()).updateGeometry(target.getHitPoint() / 100 * 4, 0.2f);
                laserLength = agent.getLocalTranslation().distance(target.getLocalTranslation());
                break;
            }
        }
        LaserBullet laserBullet = new LaserBullet(this, DefinedSpatials.initializeLaserBullet(agent, direction, laserLength));
        //only one laser bullet can be active at the time
        bullet = laserBullet;
        ((Node) agent.getSpatial()).attachChild(laserBullet.getSpatial());
        return laserBullet;
    }
}
