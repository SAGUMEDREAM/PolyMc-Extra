package cc.thonly.polymc_extra.mixin;

import cc.thonly.polymc_extra.config.Config;
import cc.thonly.polymc_extra.util.PolyMcPacks;
import cc.thonly.polymc_extra.util.RegistriesUtil;
import net.minecraft.registry.Registries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Registries.class, priority = 3000)
public class RegistriesMixin {


    @Inject(method = "freezeRegistries", at = @At("HEAD"))
    private static void patchRegistries(CallbackInfo ci) {
        RegistriesUtil.parseAll(Registries.REGISTRIES);
        PolyMcPacks.registers();
    }
}
