package cc.thonly.polymc_extra.mixin.accessor;

import cc.thonly.polymc_extra.accessor.IScreenHandlerTypeAccessor;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(MenuType.class)
public class MenuTypeAccessor<T extends AbstractContainerMenu> implements IScreenHandlerTypeAccessor<T> {

    @Shadow @Final private MenuType.MenuSupplier<T> constructor;

    @Override
    public MenuType.MenuSupplier<T> polyMcExtra$getFactory() {
        return this.constructor;
    }
}
