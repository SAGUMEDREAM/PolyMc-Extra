package cc.thonly.polymc_extra.util;

import de.tomalbrc.bil.api.AnimatedHolder;
import de.tomalbrc.bil.api.Animator;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class AnimationHelper {
    public static void updateWalkAnimation(LivingEntity entity, AnimatedHolder holder) {
        updateWalkAnimation(entity, holder, 0);
    }

    public static void updateWalkAnimation(LivingEntity entity, AnimatedHolder holder, int priority) {
        Animator animator = holder.getAnimator();
        if (isActuallyMoving(entity)) {
            animator.playAnimation("walk", priority);
            animator.pauseAnimation("idle");
        } else {
            animator.pauseAnimation("walk");
            animator.playAnimation("idle", priority, true);
        }
    }

    public static void updateHurtVariant(LivingEntity entity, AnimatedHolder holder) {
        updateHurtColor(entity, holder); // if you are using animated java, you could change to a different variant or use a color like we do here
    }

    public static void updateHurtColor(LivingEntity entity, AnimatedHolder holder) {
        if (entity.hurtTime > 0 || entity.deathTime > 0)
            holder.setColor(0xff7e7e);
        else
            holder.clearColor();
    }

    public static boolean isActuallyMoving(Entity entity) {
        var motion = entity.getDeltaMovement();
        double horizontalSpeedSq = motion.x * motion.x + motion.z * motion.z;
        boolean grounded = entity.onGround() || entity.isInWater();

        return grounded && horizontalSpeedSq > 0.0001;
    }

}
