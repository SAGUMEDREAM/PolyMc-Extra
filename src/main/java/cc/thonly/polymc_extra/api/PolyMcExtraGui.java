package cc.thonly.polymc_extra.api;

import cc.thonly.polymc_extra.gui.NaiveStackListingChestPoly;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;

import java.util.Map;


// source from PolyMc
public interface PolyMcExtraGui {
    Map<MenuType<?>, PolyMcExtraGui> MENU_TYPE_REGISTRY = new Object2ObjectOpenHashMap<>();

    AbstractContainerMenu replaceScreenHandler(AbstractContainerMenu var1, ServerPlayer var2, int var3);

    static void register(MenuType<?> menuType) {
        register(menuType, new NaiveStackListingChestPoly());
    }

    static void register(MenuType<?> menuType, PolyMcExtraGui gui) {
        MENU_TYPE_REGISTRY.put(menuType, gui);
    }
}
