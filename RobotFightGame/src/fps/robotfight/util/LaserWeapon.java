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
import com.jme3.collision.CollisionResults;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Quad;
import com.jme3.ai.agents.util.control.MonkeyBrainsAppState;
import com.jme3.ai.agents.util.weapons.AbstractBullet;
import com.jme3.ai.agents.util.weapons.AbstractFirearmWeapon;

/**
 * Specific weapon for this game.
 *
 * @author Tihomir Radosavljević
 * @version 1.0.2
 */
public class LaserWeapon extends AbstractFirearmWeapon {

    public LaserWeapon(String name, Agent agent) {
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
        if (bullet != null) {
            return null;
        }
        float laserLength = maxAttackRange;
        MonkeyBrainsAppState brainsAppState = MonkeyBrainsAppState.getInstance();
        CollisionResults collsions = new CollisionResults();
        Vector3f click3d = new Vector3f(agent.getLocalTranslation());
        Vector3f dir = direction.subtract(click3d).normalizeLocal();
        click3d.y = 3;
        Ray ray = new Ray(click3d, dir);
        ray.setLimit(maxAttackRange);
        //this the part where it hurts
        for (Agent target : brainsAppState.getAgents()) {
            target.getSpatial().collideWith(ray, collsions);
            if (collsions.size() / 2 > 1 && !agent.equals(target)) {
                if (!brainsAppState.isFriendlyFire() && agent.isSameTeam(target)) {
                    break;
                }
                brainsAppState.decreaseHitPoints(target, this);
                ((Quad) ((Geometry) ((Node) target.getSpatial()).getChild("healthbar")).getMesh()).updateGeometry(target.getHitPoints().getCurrentHitPoints() / 100 * 4, 0.2f);
                laserLength = agent.getLocalTranslation().distance(target.getLocalTranslation());
                break;
            }
        }
        LaserBullet laserBullet = new LaserBullet(this, RoboFightSpatials.initializeLaserBullet(agent, direction, laserLength));
        //only one laser bullet can be active at the time
        bullet = laserBullet;
        ((Node) agent.getSpatial()).attachChild(laserBullet.getSpatial());
        return laserBullet;
    }
}
