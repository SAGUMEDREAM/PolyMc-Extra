package cc.thonly.polymc_extra.mixin;

import net.minecraft.block.AbstractBlock;
import org.spongepowered.asm.mixin.Mixin;

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
