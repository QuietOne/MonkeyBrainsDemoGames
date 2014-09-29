package fps.robotfight.util;

import com.jme3.ai.agents.util.systems.InventorySystem;
import com.jme3.ai.agents.util.weapons.AbstractWeapon;

/**
 *
 * @author Tihomir Radosavljevic
 * @version 1.0.0
 */
public class Inventory implements InventorySystem {

    private AbstractWeapon activeWeapon;
    private AbstractWeapon secondaryWeapon;

    public void update(float tpf) {
        if (activeWeapon != null) {
            activeWeapon.update(tpf);
        }
        if (secondaryWeapon != null) {
            secondaryWeapon.update(tpf);
        }
    }

    public float getInventoryMass() {
        float mass = 0;
        if (activeWeapon != null) {
            mass += activeWeapon.getMass();
        }
        if (secondaryWeapon != null) {
            mass += secondaryWeapon.getMass();
        }
        return mass;
    }

    public AbstractWeapon getActiveWeapon() {
        return activeWeapon;
    }

    public void setActiveWeapon(AbstractWeapon activeWeapon) {
        this.activeWeapon = activeWeapon;
    }

    public AbstractWeapon getSecondaryWeapon() {
        return secondaryWeapon;
    }

    public void setSecondaryWeapon(AbstractWeapon secondaryWeapon) {
        this.secondaryWeapon = secondaryWeapon;
    }

    public void switchWeapons() {
        AbstractWeapon tempWeapon = activeWeapon;
        activeWeapon = secondaryWeapon;
        secondaryWeapon = tempWeapon;
    }
}
