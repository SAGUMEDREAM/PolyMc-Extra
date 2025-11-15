package cc.thonly.polymc_extra.animation;

import de.tomalbrc.bil.api.AnimatedHolder;
import de.tomalbrc.bil.api.Animator;
import net.minecraft.world.entity.Entity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiPredicate;

public class AnimationEntry<T extends Entity> {

    private final List<Rule<T>> rules = new ArrayList<>();
    private final Set<String> animations = new HashSet<>();

    public AnimationEntry<T> register(BiPredicate<T, AnimatedHolder> predicate, String animation) {
        this.rules.add(new Rule<>(predicate, animation));
        this.animations.add(animation);
        return this;
    }

    public void update(T entity, AnimatedHolder holder) {
        Animator animator = holder.getAnimator();
        if (animator == null) {
            return;
        }

        for (Rule<T> rule : this.rules) {
            if (rule.predicate().test(entity, holder)) {

                String anim = rule.animation();

                for (String a : this.animations) {
                    if (!a.equals(anim) && animator.isPlaying(a)) {
                        animator.pauseAnimation(a);
                    }
                }

                animator.playAnimation(anim, 0);
                break;
            }
        }
    }
}
