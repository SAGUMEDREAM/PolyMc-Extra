package cc.thonly.polymc_extra.block.base;

import cc.thonly.polymc_extra.block.StatePolymerBlock;
import eu.pb4.factorytools.api.block.FactoryBlock;
import eu.pb4.polymer.virtualentity.api.ElementHolder;
import org.jetbrains.annotations.Nullable;
import xyz.nucleoid.packettweaker.PacketContext;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class BarrierPolymerBlock extends StatePolymerBlock {
    public static final BarrierPolymerBlock INSTANCE = new BarrierPolymerBlock(new HashMap<>(), null);

    public BarrierPolymerBlock(Map<BlockState, BlockState> map, FactoryBlock fallback) {
        super(map, fallback);
    }

    @Override
    public BlockState getPolymerBlockState(BlockState blockState, PacketContext packetContext) {
        return Blocks.BARRIER.defaultBlockState();
    }

    @Override
    public @Nullable ElementHolder createElementHolder(ServerLevel world, BlockPos pos, BlockState initialBlockState) {
        return null;
    }
}
