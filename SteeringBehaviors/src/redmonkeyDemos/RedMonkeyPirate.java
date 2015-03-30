package redmonkeyDemos;

import com.badlogic.gdx.ai.btree.utils.BehaviorTreeParser;
import com.jme3.asset.AssetManager;
import com.jme3.scene.Spatial;
import com.jme3.terrain.geomipmap.TerrainQuad;
import redmonkey.GameLogicHook;
import redmonkey.elements.monkey.RedMonkey;

public class RedMonkeyPirate extends RedMonkey {

    int treasure;
    boolean eyePatch;

    public void collectBanana() {
        treasure++;
    }

    public RedMonkeyPirate(float x, float y, float z, TerrainQuad terrain, Spatial model, GameLogicHook gameLogic, float yTranslation, String... newTags) {
        super(x, y, z, terrain, model, gameLogic, yTranslation, newTags);
    }

    @Override
    public void setBehaviorTree(AssetManager assetManager, String tree) {
        BehaviorTreeParser<RedMonkeyPirate> parser = new BehaviorTreeParser<>(BehaviorTreeParser.DEBUG_NONE);
        behaviorTree = parser.parse((String) (assetManager.loadAsset(tree)), this);
    }
}
