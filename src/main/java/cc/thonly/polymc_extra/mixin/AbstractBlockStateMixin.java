package cc.thonly.polymc_extra.mixin;

import cc.thonly.polymc_extra.config.PolyMcExtraConfig;
import cc.thonly.polymc_extra.config.PolyMcExtraConfigService;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

@Mixin(BlockBehaviour.BlockStateBase.class)
public abstract class AbstractBlockStateMixin {
    @Shadow public abstract Block getBlock();

    @Inject(method = "canOcclude", at = @At("RETURN"), cancellable = true)
    public void modifyIsOpaque(CallbackInfoReturnable<Boolean> cir) {
        Block block = this.getBlock();
        Optional<ResourceKey<Block>> idOpt = BuiltInRegistries.BLOCK.getResourceKey(block);
        if (idOpt.isPresent()) {
            ResourceKey<Block> blockRegistryKey = idOpt.get();
            PolyMcExtraConfig config = PolyMcExtraConfig.getConfig();
            PolyMcExtraConfigService service = config.getService();
            if (service.shouldDisabledOpaque(blockRegistryKey)) {
                cir.setReturnValue(false);
            }

        }
    }
}
