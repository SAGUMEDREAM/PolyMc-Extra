package cc.thonly.polymc_extra.block.base;

import cc.thonly.polymc_extra.block.StatePolymerBlock;
import eu.pb4.factorytools.api.block.FactoryBlock;
import eu.pb4.polymer.virtualentity.api.ElementHolder;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;
import xyz.nucleoid.packettweaker.PacketContext;

import java.util.HashMap;
import java.util.Map;

public class BarrierPolymerBlock extends StatePolymerBlock {
    public static final BarrierPolymerBlock INSTANCE = new BarrierPolymerBlock(new HashMap<>(), null);

    public BarrierPolymerBlock(Map<BlockState, BlockState> map, FactoryBlock fallback) {
        super(map, fallback);
    }

    @Override
    public BlockState getPolymerBlockState(BlockState blockState, PacketContext packetContext) {
        return Blocks.BARRIER.getDefaultState();
    }

    @Override
    public @Nullable ElementHolder createElementHolder(ServerWorld world, BlockPos pos, BlockState initialBlockState) {
        return null;
    }
}
