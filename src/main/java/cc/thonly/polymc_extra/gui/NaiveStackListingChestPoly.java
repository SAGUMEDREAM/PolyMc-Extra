package cc.thonly.polymc_extra.gui;

import java.util.List;

import cc.thonly.polymc_extra.api.PolyMcExtraGui;
import cc.thonly.polymc_extra.util.GuiUtils;
import cc.thonly.polymc_extra.util.StaticSlot;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class NaiveStackListingChestPoly implements PolyMcExtraGui {
    public NaiveStackListingChestPoly() {
    }

    public AbstractContainerMenu replaceScreenHandler(AbstractContainerMenu base, ServerPlayer player, int syncId) {
        return new NaiveStackListingScreenHandler(MenuType.GENERIC_9x3, 9, 3, syncId, player.getInventory(), base);
    }

    public static class NaiveStackListingScreenHandler extends AbstractContainerMenu {
        protected final AbstractContainerMenu base;
        protected final int totalSlots;
        protected final int fakedSlots;

        protected NaiveStackListingScreenHandler(MenuType<?> type, int width, int height, int syncId, Inventory playerInventory, AbstractContainerMenu base) {
            super(type, syncId);
            this.base = base;
            this.totalSlots = width * height;
            int fakedSlotsTemp = 0;
            List<Slot> baseSlots = GuiUtils.removePlayerSlots(base.slots);

            int hotbar;
            int x;
            for (hotbar = 0; hotbar < width; ++hotbar) {
                for (x = 0; x < height; ++x) {
                    int index = x + hotbar * width;
                    Slot slot;
                    if (baseSlots.size() > index) {
                        slot = baseSlots.get(index);
                    } else {
                        slot = new StaticSlot(new ItemStack(Items.BLACK_STAINED_GLASS_PANE));
                        ++fakedSlotsTemp;
                    }

                    this.addSlot(slot);
                }
            }

            for (hotbar = 0; hotbar < 3; ++hotbar) {
                for (x = 0; x < 9; ++x) {
                    this.addSlot(new Slot(playerInventory, x + hotbar * 9 + 9, 8 + x * 18, 84 + hotbar * 18));
                }
            }

            for (hotbar = 0; hotbar < 9; ++hotbar) {
                this.addSlot(new Slot(playerInventory, hotbar, 8 + hotbar * 18, 142));
            }

            this.fakedSlots = fakedSlotsTemp;
        }

        @Override
        public boolean stillValid(Player player) {
            return this.base.stillValid(player);
        }

        @Override
        public ItemStack quickMoveStack(Player player, int index) {
            if (index > this.totalSlots) {
                index -= this.fakedSlots;
            }

            return this.base.quickMoveStack(player, index);
        }
    }
}
