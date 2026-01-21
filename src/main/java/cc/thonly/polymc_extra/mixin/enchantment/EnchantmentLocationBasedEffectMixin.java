package cc.thonly.polymc_extra.mixin.enchantment;

import cc.thonly.polymc_extra.util.PolymerBuiltInRegistriesPatcher;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mojang.serialization.Codec;
import eu.pb4.polymer.common.api.PolymerCommonUtils;
import net.minecraft.world.item.enchantment.effects.AllOf;
import net.minecraft.world.item.enchantment.effects.EnchantmentLocationBasedEffect;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import xyz.nucleoid.packettweaker.PacketContext;

import java.util.List;
import java.util.function.Function;

/** Source from PolyMc
 *  <a href="https://github.com/Patbox/PolyMc/blob/dev/1.21.6/src/main/java/io/github/theepicblock/polymc/mixins/enchantment/EnchantmentLocationBasedEffect.java">EnchantmentLocationBasedEffect.java</a>
 */
@Mixin(EnchantmentLocationBasedEffect.class)
public interface EnchantmentLocationBasedEffectMixin {
    @ModifyExpressionValue(method = "<clinit>", at = @At(value = "INVOKE", target = "Lcom/mojang/serialization/Codec;dispatch(Ljava/util/function/Function;Ljava/util/function/Function;)Lcom/mojang/serialization/Codec;"))
    private static Codec<EnchantmentLocationBasedEffect> patchCodec(Codec<EnchantmentLocationBasedEffect> codec) {
        return codec.xmap(Function.identity(), content -> { // Encode
            if (PolymerCommonUtils.isServerNetworkingThread()) {
                var ctx = PacketContext.get();
                if (ctx.getPacketListener() == null) {
                    return content;
                }

                if (PolymerBuiltInRegistriesPatcher.VANILLA_ENCHANTMENT_LOCATION_BASED_EFFECT_TYPE.contains(content.codec())) {
                    return new AllOf.LocationBasedEffects(List.of());
                } else {
                    return content;
                }
            }
            return content;
        });
    }
}
