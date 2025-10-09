package cc.thonly.polymc_extra.block;

import eu.pb4.factorytools.api.block.FactoryBlock;
import eu.pb4.factorytools.api.block.model.SignModel;
import eu.pb4.factorytools.api.block.model.generic.ShiftyBlockStateModel;
import eu.pb4.factorytools.api.virtualentity.BlockModel;
import eu.pb4.polymer.blocks.api.PolymerTexturedBlock;
import eu.pb4.polymer.virtualentity.api.ElementHolder;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;
import xyz.nucleoid.packettweaker.PacketContext;

import java.util.function.BiFunction;

public record StateCopyFactoryBlock(Block clientBlock, BiFunction<BlockState, BlockPos, BlockModel> modelFunction) implements FactoryBlock, PolymerTexturedBlock {


    public static final StateCopyFactoryBlock SIGN = new StateCopyFactoryBlock(Blocks.BIRCH_SIGN, SignModel::new);
    public static final StateCopyFactoryBlock WALL_SIGN = new StateCopyFactoryBlock(Blocks.BIRCH_WALL_SIGN, SignModel::new);
    public static final StateCopyFactoryBlock HANGING_SIGN = new StateCopyFactoryBlock(Blocks.BIRCH_HANGING_SIGN, SignModel::new);
    public static final StateCopyFactoryBlock HANGING_WALL_SIGN = new StateCopyFactoryBlock(Blocks.BIRCH_WALL_HANGING_SIGN, SignModel::new);

    public static final StateCopyFactoryBlock WALL = new StateCopyFactoryBlock(Blocks.SANDSTONE_WALL, ShiftyBlockStateModel::longRange);
    public static final StateCopyFactoryBlock STAIR = new StateCopyFactoryBlock(Blocks.SANDSTONE_STAIRS, ShiftyBlockStateModel::longRange);
    public static final StateCopyFactoryBlock FENCE = new StateCopyFactoryBlock(Blocks.BIRCH_FENCE, ShiftyBlockStateModel::longRange);
    public static final StateCopyFactoryBlock FENCE_GATE = new StateCopyFactoryBlock(Blocks.BIRCH_FENCE_GATE, ShiftyBlockStateModel::longRange);
    public static final StateCopyFactoryBlock BUTTON = new StateCopyFactoryBlock(Blocks.STONE_BUTTON, ShiftyBlockStateModel::longRange);
    public static final StateCopyFactoryBlock PRESSURE_PLATE = new StateCopyFactoryBlock(Blocks.STONE_PRESSURE_PLATE, ShiftyBlockStateModel::longRange);
    public static final StateCopyFactoryBlock PANE = new StateCopyFactoryBlock(Blocks.GLASS_PANE, ShiftyBlockStateModel::longRange);
    public static final StateCopyFactoryBlock CARPET = new StateCopyFactoryBlock(Blocks.WHITE_CARPET, ShiftyBlockStateModel::longRange);
    public static final StateCopyFactoryBlock CHAIN = new StateCopyFactoryBlock(Blocks.CHAIN, ShiftyBlockStateModel::longRange);
    public static final StateCopyFactoryBlock LANTERN = new StateCopyFactoryBlock(Blocks.LANTERN, ShiftyBlockStateModel::longRange);

    @Override
    public BlockState getPolymerBlockState(BlockState state, PacketContext context) {
        return clientBlock.getStateWithProperties(state);
    }

    @Override
    public @Nullable ElementHolder createElementHolder(ServerWorld world, BlockPos pos, BlockState initialBlockState) {
        return this.modelFunction.apply(initialBlockState, pos);
    }

    @Override
    public boolean isIgnoringBlockInteractionPlaySoundExceptedEntity(BlockState state, ServerPlayerEntity player, Hand hand, ItemStack stack, ServerWorld world, BlockHitResult blockHitResult) {
        return true;
    }
}
