package cc.thonly.polymc_extra.mixin;

import cc.thonly.polymc_extra.config.Config;
import cc.thonly.polymc_extra.config.ConfigService;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.registry.RegistryKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractBlock.class)
public class AbstractBlockMixin {
//    @Inject(method = "<init>", at = @At("TAIL"))
//    private void onInit(AbstractBlock.Settings settings, CallbackInfo ci) {
//        RegistryKey<Block> registryKey = settings.registryKey;
//        if (registryKey != null) {
//            Config config = Config.getConfig();
//            ConfigService service = config.getService();
//            if (service.shouldDisabledOpaque(registryKey)) {
//                settings.nonOpaque();
//            }
//        }
//    }
}
