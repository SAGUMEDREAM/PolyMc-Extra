package cc.thonly.polymc_extra.animation;

import de.tomalbrc.bil.api.AnimatedHolder;
import net.minecraft.world.entity.Entity;

import java.util.function.BiPredicate;

public record Rule<T extends Entity>(
        BiPredicate<T, AnimatedHolder> predicate,
        String animation
) {
}
