package cc.thonly.polymc_extra.util;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class EmptyInventory implements Container {
    public static final EmptyInventory INSTANCE = new EmptyInventory();

    public EmptyInventory() {
    }

    public int getContainerSize() {
        return 0;
    }

    public boolean isEmpty() {
        return true;
    }

    public ItemStack getItem(int slot) {
        return ItemStack.EMPTY;
    }

    public ItemStack removeItem(int slot, int amount) {
        return ItemStack.EMPTY;
    }

    public ItemStack removeItemNoUpdate(int slot) {
        return ItemStack.EMPTY;
    }

    public void setItem(int slot, ItemStack stack) {
    }

    public void setChanged() {
    }

    public boolean stillValid(Player player) {
        return true;
    }

    public void clearContent() {
    }
}
