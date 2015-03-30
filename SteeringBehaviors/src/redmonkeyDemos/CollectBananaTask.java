package redmonkeyDemos;

import com.badlogic.gdx.ai.btree.LeafTask;
import com.badlogic.gdx.ai.btree.Task;
import redmonkey.elements.monkey.RedMonkey;

/**
 *
 */
public class CollectBananaTask<T extends RedMonkey> extends LeafTask<T> {

    @Override
    public void run(T t) {
        RedMonkeyPirate pirate = (RedMonkeyPirate) t;
        pirate.collectBanana();
        success();
    }

    @Override
    protected Task<T> copyTo(Task<T> task) {
        CollectBananaTask cb = (CollectBananaTask) task;
        return task;
    }
}
