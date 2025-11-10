package cc.thonly.polymc_extra.gui;

import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;

public class MenuTypeFactoryWrapperSoFabricApiDoesntDetectIt implements MenuProvider {
    private final MenuProvider inner;

    public MenuTypeFactoryWrapperSoFabricApiDoesntDetectIt(MenuProvider inner) {
        this.inner = inner;
    }

    public Component getDisplayName() {
        return this.inner.getDisplayName();
    }

    public AbstractContainerMenu createMenu(int syncId, Inventory inv, Player player) {
        return this.inner.createMenu(syncId, inv, player);
    }
}
