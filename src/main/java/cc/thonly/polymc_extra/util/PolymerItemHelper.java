package cc.thonly.polymc_extra.util;

import cc.thonly.polymc_extra.item.PolymerItemImpl;
import eu.pb4.polymer.core.api.item.PolymerItemUtils;
import net.minecraft.item.Item;

public class PolymerItemHelper {
    public static void registerOverlay(Item item) {
        PolymerItemUtils.registerOverlay(item, new PolymerItemImpl(item));
    }
}
