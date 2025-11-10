package cc.thonly.polymc_extra.util;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;

import java.util.List;
import java.util.stream.Collectors;

// Source from PolyMc
public class GuiUtils {
    public static List<Slot> removePlayerSlots(List<Slot> base) {
        return base.stream()
                .filter((slot) -> {
                    return !(slot.container instanceof Inventory);
                })
                .collect(Collectors.toList()
                );
    }

    public static void resyncPlayerInventory(Player player) {
        if (player instanceof ServerPlayer) {
            resyncPlayerInventory((ServerPlayer) player);
        }

    }

    public static void resyncPlayerInventory(ServerPlayer player) {
        player.containerMenu.sendAllDataToRemote();
    }
}
