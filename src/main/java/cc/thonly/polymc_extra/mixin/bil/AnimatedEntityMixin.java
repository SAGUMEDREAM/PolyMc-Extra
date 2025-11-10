package cc.thonly.polymc_extra.mixin.bil;

import cc.thonly.polymc_extra.api.ITickHolderEntity;
import de.tomalbrc.bil.api.AnimatedEntity;
import de.tomalbrc.bil.api.AnimatedEntityHolder;
import eu.pb4.polymer.virtualentity.api.tracker.DisplayTrackedData;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(value = AnimatedEntity.class, remap = false)
public interface AnimatedEntityMixin {
    @SuppressWarnings("SuspiciousMethodCalls")
    @Inject(method = "getHolder(Ljava/lang/Object;)Lde/tomalbrc/bil/api/AnimatedEntityHolder;", at = @At("HEAD"), cancellable = true)
    private static void get(Object obj, CallbackInfoReturnable<AnimatedEntityHolder> cir) {
        if (ITickHolderEntity.ELEMENT_BINDS.containsKey(obj)) {
            cir.setReturnValue(ITickHolderEntity.ELEMENT_BINDS.get(obj));
        }

    }

    @Inject(method = "modifyRawTrackedData", at = @At("HEAD"), cancellable = true)
    public default void onModifyRawTrackedData(List<SynchedEntityData.DataValue<?>> data, ServerPlayer player, boolean initial, CallbackInfo ci) {
        if (this instanceof ITickHolderEntity tickHolder) {
            Entity entity = tickHolder.getEntity();
            data.add(SynchedEntityData.DataValue.create(DisplayTrackedData.WIDTH, entity.getBbWidth()));
            data.add(SynchedEntityData.DataValue.create(DisplayTrackedData.HEIGHT, entity.getBbHeight()));
        }
    }

    @Inject(method = "getShadowRadius", at = @At("HEAD"), cancellable = true)
    public default void onGetShadowRadius(CallbackInfoReturnable<Float> cir) {
        if (this instanceof ITickHolderEntity tickHolder) {
            Entity entity = tickHolder.getEntity();
            cir.setReturnValue(entity.getBbWidth() * 0.6f);
        }
    }
}