package cc.thonly.polymc_extra.api;

import cc.thonly.polymc_extra.entity.bil.BBOverlayEntityHolder;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public interface ITickHolderEntity {
    List<ITickHolderEntity> LIST = new ArrayList<>();
    Map<Entity, BBOverlayEntityHolder<?, ?>> ELEMENT_BINDS = new Object2ObjectOpenHashMap<>();
    Predicate<ITickHolderEntity> REMOVE_PREDICATE = e -> {
        LivingEntity entity = e.getEntity();
        if (entity == null || entity.isDeadOrDying() || entity.isRemoved()) {
            ELEMENT_BINDS.remove(entity);
            return true;
        }
        e.onTick();
        return false;
    };

    static void addTickHolder(ITickHolderEntity entity) {
        LIST.add(entity);
    }

    static void addElementBind(Entity entity, BBOverlayEntityHolder<?, ?> holder) {
        ELEMENT_BINDS.put(entity, holder);
    }

    static void serverTick() {
        LIST.removeIf(REMOVE_PREDICATE);
    }

    LivingEntity getEntity();

    default void onTick() {

    }
}
