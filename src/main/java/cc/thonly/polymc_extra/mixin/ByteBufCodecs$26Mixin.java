package cc.thonly.polymc_extra.mixin;

import cc.thonly.polymc_extra.PolyMcExtra;
import com.mojang.authlib.properties.PropertyMap;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// GAME_PROFILE_PROPERTIES
@Mixin(targets = "net.minecraft.network.codec.ByteBufCodecs$26", remap = true)
public abstract class ByteBufCodecs$26Mixin implements StreamCodec<ByteBuf, PropertyMap> {
    @Inject(method = "encode(Ljava/lang/Object;Ljava/lang/Object;)V", at = @At("HEAD"))
    public void encode(Object object, Object object2, CallbackInfo ci) {
        if (object instanceof ByteBuf byteBuf && object2 instanceof PropertyMap propertyMap) {
            Logger log = PolyMcExtra.getLog();
            if (propertyMap.size() > 16) {
                log.warn("Can't read player profiler, data: ");
                propertyMap.forEach((string, property) -> {
                    log.warn("name: {}, value: {}, signature {}", property.name(), property.value(), property.signature());
                });
            } else {
                log.info("profiler sure");
            }
        }
    }
}
