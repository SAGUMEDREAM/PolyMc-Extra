package cc.thonly.polymc_extra.animation;

import de.tomalbrc.bil.api.AnimatedHolder;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.world.entity.Entity;

import java.util.*;
import java.util.function.BiPredicate;

public class EntityAnimationHelper {
    private static final Map<Class<?>, AnimationEntry<?>> MAP = new Object2ObjectOpenHashMap<>();

    public static <T extends Entity> AnimationEntry<T> getOrCreate(Class<T> tClass) {
        //noinspection unchecked
        return (AnimationEntry<T>) MAP.computeIfAbsent(tClass, c -> new AnimationEntry<>());
    }

}
