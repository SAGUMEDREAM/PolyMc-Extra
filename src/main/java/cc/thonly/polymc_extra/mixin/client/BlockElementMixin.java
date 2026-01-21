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

    @Shadow
    private static Vector3f getVector3f(JsonObject jsonObject, String string) {
        return null;
    }

    @Inject(method = "getPosition", at = @At("HEAD"), cancellable = true)
    private static void getPosition(JsonObject jsonObject, String string, CallbackInfoReturnable<Vector3f> cir) {
        Vector3f vector3f = getVector3f(jsonObject, "from");
        cir.setReturnValue(vector3f);
        cir.cancel();
    }
}
