package cc.thonly.polymc_extra.mixin.accessor;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrowableItemProjectile;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ThrowableItemProjectile.class)
public interface ThrowableItemProjectileAccessor {
    @Accessor("DATA_ITEM_STACK")
    static EntityDataAccessor<ItemStack> getDataItemStack() {
        throw new IllegalArgumentException();
    }
}
