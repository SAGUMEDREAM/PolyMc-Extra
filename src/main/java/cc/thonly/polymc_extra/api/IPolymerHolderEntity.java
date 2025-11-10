package cc.thonly.polymc_extra.api;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public interface IPolymerHolderEntity {
    List<IPolymerHolderEntity> HOLD_RENDER_QUEUE = new LinkedList<>();

    default void onCreated() {

    }

    static void addEntityHolderModel(IPolymerHolderEntity holder) {
        HOLD_RENDER_QUEUE.add(holder);
    }

    static void serverTick() {
        Iterator<IPolymerHolderEntity> iterator = IPolymerHolderEntity.HOLD_RENDER_QUEUE.iterator();
        while (iterator.hasNext()) {
            IPolymerHolderEntity next = iterator.next();
            next.onCreated();
            iterator.remove();
        }
    }
}
