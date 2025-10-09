package cc.thonly.polymc_extra.mixin;

import cc.thonly.polymc_extra.config.Config;
import cc.thonly.polymc_extra.config.ConfigService;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(AbstractBlock.AbstractBlockState.class)
public abstract class AbstractBlockStateMixin {
    @Shadow public abstract Block getBlock();

    @Inject(method = "isOpaque", at = @At("RETURN"), cancellable = true)
    public void modifyIsOpaque(CallbackInfoReturnable<Boolean> cir) {
        Block block = this.getBlock();
        Optional<RegistryKey<Block>> idOpt = Registries.BLOCK.getKey(block);
        if (idOpt.isPresent()) {
            RegistryKey<Block> blockRegistryKey = idOpt.get();
            Config config = Config.getConfig();
            ConfigService service = config.getService();
            if (service.shouldDisabledOpaque(blockRegistryKey)) {
                cir.setReturnValue(false);
            }

        }
    }
}
