package cc.thonly.polymc_extra.mixin;

import cc.thonly.polymc_extra.PolyMcExtra;
import cc.thonly.polymc_extra.data.PolyMcExtraPacks;
import cc.thonly.polymc_extra.util.PolymerBuiltInRegistriesPatcher;
import net.minecraft.core.registries.BuiltInRegistries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = BuiltInRegistries.class, priority = 3000)
public class BuiltInRegistriesMixin {

    @Inject(method = "freeze", at = @At("HEAD"))
    private static void patchBuiltInRegistries(CallbackInfo ci) {
        PolymerBuiltInRegistriesPatcher.startPatching(BuiltInRegistries.REGISTRY);
        PolyMcExtraPacks.registers();
        PolyMcExtra.getLog().info("Automatic patching completed");
    }
}
