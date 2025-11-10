package cc.thonly.polymc_extra.accessor;

import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;

public interface IScreenHandlerTypeAccessor<T extends AbstractContainerMenu> {
    MenuType.MenuSupplier<T> getFactory();
}
