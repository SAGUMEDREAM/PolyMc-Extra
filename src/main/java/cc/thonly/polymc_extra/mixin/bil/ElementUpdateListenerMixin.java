package cc.thonly.polymc_extra.mixin.bil;

import cc.thonly.polymc_extra.entity.bil.BBOverlayEntityHolder;
import de.tomalbrc.bil.core.extra.ElementUpdateListener;
import de.tomalbrc.bil.core.holder.base.AbstractAnimationHolder;
import de.tomalbrc.bil.core.holder.entity.EntityHolder;
import de.tomalbrc.bil.core.model.Pose;
import eu.pb4.polymer.virtualentity.api.VirtualEntityUtils;
import eu.pb4.polymer.virtualentity.api.elements.GenericEntityElement;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ElementUpdateListener.class, remap = false)
public abstract class ElementUpdateListenerMixin {
    @Shadow protected abstract void updateEntityBasedHolder(EntityHolder<?> holder, Pose pose);

    @Shadow @Final protected GenericEntityElement element;

    @Inject(method = "update", at = @At(value = "INVOKE", target = "Lde/tomalbrc/bil/core/extra/ElementUpdateListener;updateNonEntityBasedHolder(Lde/tomalbrc/bil/core/holder/base/AbstractAnimationHolder;Lde/tomalbrc/bil/core/model/Pose;)V"), cancellable = true)
    public void updateInvoke(ServerPlayer serverPlayer, AbstractAnimationHolder holder, Pose pose, CallbackInfo ci) {
        if (holder instanceof BBOverlayEntityHolder<?,?> entityHolder) {
            this.updateBBEntityBasedHolder(entityHolder, pose);
            ci.cancel();
        }
    }

    @Unique
    private void updateBBEntityBasedHolder(BBOverlayEntityHolder<?,?> holder, Pose pose) {
        Entity entity = holder.getEntity();
        float scale = holder.getScale();
        float yRot = entity.getYRot();
        float angle = yRot * Mth.DEG_TO_RAD;

        Vector3f offset = pose.translation();
        if (scale != 1F) {
            offset.mul(scale);
        }
        offset.rotateY(-angle);

        Vec3 pos = holder.getPos().add(offset.x, offset.y, offset.z);
        holder.sendPacket(VirtualEntityUtils.createMovePacket(
                this.element.getEntityId(),
                pos,
                pos,
                true,
                yRot,
                0F
        ));
    }
}
