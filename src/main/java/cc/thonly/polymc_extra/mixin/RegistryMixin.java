package cc.thonly.polymc_extra.mixin;

import eu.pb4.polymer.core.api.item.PolymerItemGroupUtils;
import net.minecraft.item.ItemGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Registry.class)
public interface RegistryMixin {
//    @Inject(at = @At("HEAD"), cancellable = true, method = "register(Lnet/minecraft/registry/Registry;Lnet/minecraft/registry/RegistryKey;Ljava/lang/Object;)Ljava/lang/Object;")
//    private static <V, T extends V> void modifyRegister(Registry<V> registry, RegistryKey<V> key, T entry, CallbackInfoReturnable<T> cir) {
//        if (shouldModifyItemGroup(registry, key.getValue().toString())) {
//            PolymerItemGroupUtils.registerPolymerItemGroup(key.getValue(), (ItemGroup) entry);
//            cir.setReturnValue(entry);
//            cir.cancel();
//        }
//    }
//
//    @Inject(at = @At("HEAD"), cancellable = true, method = "register(Lnet/minecraft/registry/Registry;Ljava/lang/String;Ljava/lang/Object;)Ljava/lang/Object;")
//    private static <V, T extends V> void modifyRegister(Registry<? super T> registry, String id, T entry, CallbackInfoReturnable<T> cir) {
//        if (shouldModifyItemGroup(registry, id)) {
//            PolymerItemGroupUtils.registerPolymerItemGroup(Identifier.of(id), (ItemGroup) entry);
//            cir.setReturnValue(entry);
//            cir.cancel();
//        }
//    }
//
//    @Inject(at = @At("HEAD"), cancellable = true, method = "register(Lnet/minecraft/registry/Registry;Lnet/minecraft/util/Identifier;Ljava/lang/Object;)Ljava/lang/Object;")
//    private static <V, T extends V> void modifyRegister(Registry<V> registry, Identifier id, T entry, CallbackInfoReturnable<T> cir) {
//        if (shouldModifyItemGroup(registry, id.toString())) {
//            PolymerItemGroupUtils.registerPolymerItemGroup(id, (ItemGroup) entry);
//            cir.setReturnValue(entry);
//            cir.cancel();
//        }
//    }
//
//    @Unique
//    private static <V> boolean shouldModifyItemGroup(Registry<V> registry, String id) {
//        return registry == Registries.ITEM_GROUP && !id.startsWith("minecraft:");
//    }
}
