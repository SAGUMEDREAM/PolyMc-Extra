package cc.thonly.polymc_extra.block.base;

import eu.pb4.factorytools.api.block.FactoryBlock;
import eu.pb4.factorytools.api.block.model.generic.BSMMParticleBlock;
import eu.pb4.factorytools.api.block.model.generic.BlockStateModel;
import eu.pb4.polymer.blocks.api.BlockModelType;
import eu.pb4.polymer.blocks.api.PolymerBlockResourceUtils;
import eu.pb4.polymer.blocks.api.PolymerTexturedBlock;
import eu.pb4.polymer.virtualentity.api.ElementHolder;
import org.jetbrains.annotations.Nullable;
import xyz.nucleoid.packettweaker.PacketContext;

import java.util.Locale;
import java.util.Map;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.phys.BlockHitResult;

public record TrapdoorPolymerBlock() implements FactoryBlock, PolymerTexturedBlock, BSMMParticleBlock {
    public static final TrapdoorPolymerBlock INSTANCE = new TrapdoorPolymerBlock();
    private static final Map<Direction, BlockState> STATES_REGULAR = Util.makeEnumMap(Direction.class, x -> PolymerBlockResourceUtils.requestEmpty(BlockModelType.valueOf(switch (x) {
        case UP -> "BOTTOM";
        case DOWN -> "TOP";
        default -> x.getSerializedName().toUpperCase(Locale.ROOT);
    } + "_TRAPDOOR")));
    private static final Map<Direction, BlockState> STATES_WATERLOGGED = Util.makeEnumMap(Direction.class, x -> PolymerBlockResourceUtils.requestEmpty(BlockModelType.valueOf(switch (x) {
        case UP -> "BOTTOM";
        case DOWN -> "TOP";
        default -> x.getSerializedName().toUpperCase(Locale.ROOT);
    } + "_TRAPDOOR_WATERLOGGED")));

    @Override
    public BlockState getPolymerBlockState(BlockState blockState, PacketContext packetContext) {
        var map = (blockState.getValue(TrapDoorBlock.WATERLOGGED) ? STATES_WATERLOGGED : STATES_REGULAR);

        if (blockState.getValue(TrapDoorBlock.OPEN)) {
            return map.get(blockState.getValue(TrapDoorBlock.FACING));
        }

        return map.get(blockState.getValue(TrapDoorBlock.HALF) == Half.BOTTOM ? Direction.UP : Direction.DOWN);
    }

    @Override
    public @Nullable ElementHolder createElementHolder(ServerLevel world, BlockPos pos, BlockState initialBlockState) {
        return BlockStateModel.midRange(initialBlockState, pos);
    }

    @Override
    public boolean isIgnoringBlockInteractionPlaySoundExceptedEntity(BlockState state, ServerPlayer player, InteractionHand hand, ItemStack stack, ServerLevel world, BlockHitResult blockHitResult) {
        return true;
    }
}
