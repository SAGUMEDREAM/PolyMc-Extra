package cc.thonly.polymc_extra.util;

import net.minecraft.block.Block;
import net.minecraft.util.Identifier;

@FunctionalInterface
public interface LateRunnable {
    void run(Block block, Identifier id);
}
