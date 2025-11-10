package cc.thonly.polymc_extra.util;

import io.github.theepicblock.polymc.impl.poly.gui.EmptyInventory;
import io.github.theepicblock.polymc.impl.poly.gui.GuiUtils;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

// Source from PolyMc
public class StaticSlot extends Slot {
    public final ItemStack stack;

    public StaticSlot(ItemStack stack) {
        super(EmptyInventory.INSTANCE, 0, 0, 0);
        this.stack = stack;
    }

    public void onQuickCraft(ItemStack originalItem, ItemStack itemStack) {
        throw new AssertionError("PolyMc: the contents of a static, unchangeable slot were changed. Containing: " + this.stack.toString());
    }

    public void onTake(Player player, ItemStack stack) {
        throw new AssertionError("PolyMc: tried to take item out of an static, unchangeable slot. Containing: " + stack.toString());
    }

    public ItemStack remove(int amount) {
        return ItemStack.EMPTY;
    }

    public boolean mayPlace(ItemStack stack) {
        return false;
    }

    public boolean mayPickup(Player playerEntity) {
        GuiUtils.resyncPlayerInventory(playerEntity);
        return false;
    }

    public ItemStack getItem() {
        return this.stack == null ? ItemStack.EMPTY : this.stack;
    }

    public void setByPlayer(ItemStack stack) {
    }

    public void setChanged() {
    }

    public int getMaxStackSize() {
        return this.getItem().getCount();
    }

    public int getMaxStackSize(ItemStack stack) {
        return this.getMaxStackSize();
    }
}
