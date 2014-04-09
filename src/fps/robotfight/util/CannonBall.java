package fps.robotfight.util;

import com.jme3.ai.agents.Agent;
import com.jme3.ai.agents.util.AbstractBullet;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Quad;
import com.jme3.ai.agents.util.AbstractWeapon;
import com.jme3.ai.agents.util.control.Game;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;

/**
 *
 * @author Tihomir Radosavljević
 */
public class CannonBall extends AbstractBullet{
    
    private Vector3f direction;
    private float bulletSpeed;
    
    public CannonBall(AbstractWeapon weapon, Spatial spatial, Vector3f direction) {
        super(weapon, spatial);
        bulletSpeed = 40f;
        this.direction = direction;
        spatial.setLocalRotation(weapon.getAgent().getLocalRotation());
        spatial.setLocalTranslation(weapon.getAgent().getLocalTranslation());
        spatial.getLocalTranslation().y = 3;
    }

    @Override
    public void controlUpdate(float tpf) {
        Game game = Game.getInstance();
        if (weapon.getAgent().getLocalTranslation().distance(spatial.getLocalTranslation())
                >weapon.getMaxAttackRange()) {
            weapon.setBullet(null);
            game.removeGameObject(this);
            return;
        }
        Vector3f click3d = new Vector3f(weapon.getAgent().getLocalTranslation());
        Vector3f dir = direction.subtract(click3d).normalizeLocal();
        spatial.move(dir.x*bulletSpeed * tpf, 0, dir.z*bulletSpeed * tpf);
        //this the part where it hurts
        for (Agent target : game.getAgents()) {
            if (hurts(target) && !weapon.getAgent().equals(target)) {
                game.agentAttack(weapon.getAgent(), target);
                ((Quad) ((Geometry) ((Node) target.getSpatial()).getChild("healthbar")).getMesh()).updateGeometry(target.getHitPoint() / 100 * 4, 0.2f);
                weapon.setBullet(null);
                game.removeGameObject(this);
            }
        }
    }

    private boolean hurts(Agent agent){
        if (!Game.getInstance().isFriendlyFire() && weapon.getAgent().isSameTeam(agent)) {
            return false;
        }
        if (spatial.getLocalTranslation().distance(agent.getLocalTranslation())< 5 && agent.isEnabled()) {
            return true;
        }
        return false;
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
