package redmonkeyDemos;

import com.badlogic.gdx.ai.btree.LeafTask;
import com.badlogic.gdx.ai.btree.Task;
import redmonkey.elements.monkey.RedMonkey;

/**
 *
 */
public class KillTask extends LeafTask<RedMonkey> {

    @Override
    public void run(RedMonkey monkey) {
        RedMonkeyNinja ninja = (RedMonkeyNinja) monkey.container;
        ninja.kill();
        success();
    }

    @Override
    protected Task<RedMonkey> copyTo(Task<RedMonkey> task) {
        KillTask kill = (KillTask) task;
        return task;
    }
}
