package cc.thonly.polymc_extra.mixin.accessor;

import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Optional;

@Mixin(Entity.class)
public interface EntityAccessor {
    @Accessor("DATA_CUSTOM_NAME")
    public static EntityDataAccessor<Optional<Component>> getDataCustomName() {
        throw new IllegalArgumentException();
    }
}
