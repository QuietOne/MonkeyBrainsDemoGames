package redmonkeyDemos;

import com.jme3.scene.Spatial;
import com.jme3.terrain.geomipmap.TerrainQuad;
import redmonkey.GameLogicHook;
import redmonkey.elements.monkey.RedMonkey;

class RedMonkeyNinja {

    int numkills;

    public RedMonkeyNinja(float x, float y, float z, TerrainQuad terrain, Spatial model, GameLogicHook gameLogic, float yTranslation, String... newTags) {
        RedMonkey redMonkey = new RedMonkey(x, y, z, terrain, model, gameLogic, yTranslation, newTags);
        redMonkey.container = this;

    }

    public void kill() {
        numkills++;
    }
}
