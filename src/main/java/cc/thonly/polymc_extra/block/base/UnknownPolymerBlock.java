package cc.thonly.polymc_extra.block.base;

import eu.pb4.polymer.core.api.block.PolymerBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import xyz.nucleoid.packettweaker.PacketContext;

public class UnknownPolymerBlock implements PolymerBlock {
    public static final UnknownPolymerBlock INSTANCE = new UnknownPolymerBlock();

    @Override
    public BlockState getPolymerBlockState(BlockState blockState, PacketContext packetContext) {
        return Blocks.STONE.defaultBlockState();
    }
}
