package fps.robotfight.util;

import com.jme3.scene.Spatial;
import com.jme3.ai.agents.util.control.AIAppState;
import com.jme3.ai.agents.util.weapons.AbstractBullet;
import com.jme3.ai.agents.util.weapons.AbstractFirearmWeapon;

/**
 *
 * @author Tihomir Radosavljević
 */
public class LaserBullet extends AbstractBullet {

    private float lifeTime;
    private float maxLifeTime = 0.25f;

    public LaserBullet(AbstractFirearmWeapon weapon, Spatial spatial) {
        super(weapon, spatial);
        this.lifeTime = maxLifeTime;
    }

    @Override
    public void controlUpdate(float tpf) {
        //i don't know to read bytecode, so I didn't include super.update(),
        //because it probably contains something that I don't need in this demo.
        if (lifeTime <= 0 && isEnabled()) {
            lifeTime = 0;
            //here bullet property was used as means that one agent can't shoot
            //more bullets than one at moment, but it should be used to define
            //what kind of bullets are in that kind of weapon
            weapon.setBullet(null);
            AIAppState.getInstance().removeGameEntity(this);
        } else {
            lifeTime -= tpf;
        }
    }

    public float getLifeTime() {
        return lifeTime;
    }

    public void setLifeTime(float lifeTime) {
        this.lifeTime = lifeTime;
    }
}
