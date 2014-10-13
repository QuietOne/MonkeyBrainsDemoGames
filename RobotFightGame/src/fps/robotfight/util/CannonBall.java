/**
 * Copyright (c) 2014, jMonkeyEngine All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * Neither the name of 'jMonkeyEngine' nor the names of its contributors may be
 * used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package fps.robotfight.util;

import com.jme3.ai.agents.Agent;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Quad;
import com.jme3.ai.agents.util.control.MonkeyBrainsAppState;
import com.jme3.ai.agents.util.weapons.AbstractBullet;
import com.jme3.ai.agents.util.weapons.AbstractFirearmWeapon;

/**
 *
 * @author Tihomir Radosavljević
 * @version 1.0.0
 */
public class CannonBall extends AbstractBullet {

    private Vector3f direction;
    private float bulletSpeed;

    public CannonBall(AbstractFirearmWeapon weapon, Spatial spatial, Vector3f direction) {
        super(weapon, spatial);
        bulletSpeed = 40f;
        this.direction = direction;
        spatial.setLocalRotation(weapon.getAgent().getLocalRotation());
        spatial.setLocalTranslation(weapon.getAgent().getLocalTranslation());
        spatial.getLocalTranslation().y = 3;
    }

    @Override
    public void controlUpdate(float tpf) {
        MonkeyBrainsAppState brainsAppState = MonkeyBrainsAppState.getInstance();
        if (weapon.getAgent().getLocalTranslation().distance(spatial.getLocalTranslation())
                > weapon.getMaxAttackRange()) {
            weapon.setBullet(null);
            brainsAppState.removeGameEntity(this);
            return;
        }
        Vector3f click3d = new Vector3f(weapon.getAgent().getLocalTranslation());
        Vector3f dir = direction.subtract(click3d).normalizeLocal();
        spatial.move(dir.x * bulletSpeed * tpf, 0, dir.z * bulletSpeed * tpf);
        //this the part where it hurts
        for (Agent target : brainsAppState.getAgents()) {
            if (hurts(target) && !weapon.getAgent().equals(target)) {
                brainsAppState.decreaseHitPoints(target, weapon);
                ((Quad) ((Geometry) ((Node) target.getSpatial()).getChild("healthbar"))
                        .getMesh()).updateGeometry(target.getHitPoints().getCurrentHitPoints() / 100 * 4, 0.2f);
                weapon.setBullet(null);
                brainsAppState.removeGameEntity(this);
            }
        }
    }

    private boolean hurts(Agent agent) {
        if (!MonkeyBrainsAppState.getInstance().isFriendlyFire() && weapon.getAgent().isSameTeam(agent)) {
            return false;
        }
        if (spatial.getLocalTranslation().distance(agent.getLocalTranslation()) < 5 && agent.isEnabled()) {
            return true;
        }
        return false;
    }
}
