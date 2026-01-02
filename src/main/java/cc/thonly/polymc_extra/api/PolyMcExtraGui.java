package cc.thonly.polymc_extra.api;

import cc.thonly.polymc_extra.gui.NaiveStackListingChestPoly;
import eu.pb4.polymer.core.api.utils.PolymerSyncedObject;
import eu.pb4.polymer.rsm.api.RegistrySyncUtils;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import xyz.nucleoid.packettweaker.PacketContext;

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
        RegistrySyncUtils.setServerEntry(BuiltInRegistries.MENU, menuType);
    }
}
