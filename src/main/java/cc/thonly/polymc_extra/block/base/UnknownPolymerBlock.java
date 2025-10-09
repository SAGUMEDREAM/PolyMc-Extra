package cc.thonly.polymc_extra.block.base;

import eu.pb4.polymer.core.api.block.PolymerBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import xyz.nucleoid.packettweaker.PacketContext;

public class UnknownPolymerBlock implements PolymerBlock {
    public static final UnknownPolymerBlock INSTANCE = new UnknownPolymerBlock();

    @Override
    public BlockState getPolymerBlockState(BlockState blockState, PacketContext packetContext) {
        return Blocks.STONE.getDefaultState();
    }
}
