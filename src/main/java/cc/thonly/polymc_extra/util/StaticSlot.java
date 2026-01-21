package cc.thonly.polymc_extra.util;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.NonNull;

// Source from PolyMc
public class StaticSlot extends Slot {
    public final ItemStack stack;

    public StaticSlot(ItemStack stack) {
        super(EmptyInventory.INSTANCE, 0, 0, 0);
        this.stack = stack;
    }

    @Override
    public void onQuickCraft(ItemStack originalItem, ItemStack itemStack) {
        throw new AssertionError("PolyMc: the contents of a static, unchangeable slot were changed. Containing: " + this.stack.toString());
    }

    @Override
    public void onTake(Player player, ItemStack stack) {
        throw new AssertionError("PolyMc: tried to take item out of an static, unchangeable slot. Containing: " + stack.toString());
    }

    @Override
    public ItemStack remove(int amount) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return false;
    }

    @Override
    public boolean mayPickup(Player playerEntity) {
        GuiUtils.resyncPlayerInventory(playerEntity);
        return false;
    }

    @Override
    public ItemStack getItem() {
        return this.stack == null ? ItemStack.EMPTY : this.stack;
    }

    @Override
    public void setByPlayer(ItemStack stack) {
    }

    @Override
    public void setChanged() {
    }

    @Override
    public int getMaxStackSize() {
        return this.getItem().getCount();
    }

    @Override
    public int getMaxStackSize(ItemStack stack) {
        return this.getMaxStackSize();
    }
}
