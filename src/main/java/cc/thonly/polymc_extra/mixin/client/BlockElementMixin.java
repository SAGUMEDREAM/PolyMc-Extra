package cc.thonly.polymc_extra.mixin.client;

import com.google.gson.JsonObject;
import net.minecraft.client.renderer.block.model.BlockElement;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockElement.Deserializer.class)
public abstract class BlockElementMixin {
    @Shadow protected abstract Vector3f getVector3f(JsonObject jsonObject, String string);

    @Inject(method = "getTo", at = @At("HEAD"), cancellable = true)
    public void getTo(JsonObject jsonObject, CallbackInfoReturnable<Vector3f> cir) {
        Vector3f vector3f = this.getVector3f(jsonObject, "to");
        cir.setReturnValue(vector3f);
        cir.cancel();
    }
    @Inject(method = "getFrom", at = @At("HEAD"), cancellable = true)
    public void getFrom(JsonObject jsonObject, CallbackInfoReturnable<Vector3f> cir) {
        Vector3f vector3f = this.getVector3f(jsonObject, "from");
        cir.setReturnValue(vector3f);
        cir.cancel();
    }
}
