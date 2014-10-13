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
import com.jme3.scene.Node;
import com.jme3.ai.agents.util.control.MonkeyBrainsAppState;
import com.jme3.ai.agents.util.weapons.AbstractBullet;
import com.jme3.ai.agents.util.weapons.AbstractFirearmWeapon;

/**
 * Weapon for this game. It throws green balls that hurts other game entities.
 *
 * @author Tihomir Radosavljević
 * @version 1.0.0
 */
public class Cannon extends AbstractFirearmWeapon {

    public Cannon(String name, Agent agent) {
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
        ((Node) MonkeyBrainsAppState.getInstance().getRootNode()).attachChild(cannonBall.getSpatial());
        return cannonBall;
    }
}
