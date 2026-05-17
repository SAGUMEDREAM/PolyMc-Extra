package cc.thonly.polymc_extra.mixin.accessor;

import net.fabricmc.fabric.impl.object.builder.FabricEntityDataRegistryImpl;
import net.minecraft.core.Registry;
import net.minecraft.network.syncher.EntityDataSerializer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@SuppressWarnings("UnstableApiUsage")
@Mixin(FabricEntityDataRegistryImpl.class)
public interface FabricEntityDataRegistryImplAccessor {
    @Accessor("handlerRegistry")
    static Registry<EntityDataSerializer<?>> getHandlerRegistry() {
        throw new AssertionError();
    }
}