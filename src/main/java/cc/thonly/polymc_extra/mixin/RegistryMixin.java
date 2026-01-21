package cc.thonly.polymc_extra.mixin;

import eu.pb4.polymer.core.api.item.PolymerItemGroupUtils;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Registry.class)
public interface RegistryMixin {

    @Inject(method = "register(Lnet/minecraft/core/Registry;Lnet/minecraft/resources/ResourceKey;Ljava/lang/Object;)Ljava/lang/Object;", at = @At("HEAD"), cancellable = true)
    private static <V, T extends V> void register(Registry<V> registry, ResourceKey<V> key, T entry, CallbackInfoReturnable<T> cir) {
        boolean isVanilla = key.identifier().getNamespace().equalsIgnoreCase("minecraft");
        if (registry == BuiltInRegistries.CREATIVE_MODE_TAB && !isVanilla && entry instanceof CreativeModeTab itemGroup) {
            PolymerItemGroupUtils.registerPolymerItemGroup(key.identifier(), itemGroup);
            cir.setReturnValue(entry);
            cir.cancel();
        }
    }

}
