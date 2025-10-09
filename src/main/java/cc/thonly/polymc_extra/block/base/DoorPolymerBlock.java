package cc.thonly.polymc_extra.block.base;

import eu.pb4.factorytools.api.block.FactoryBlock;
import eu.pb4.factorytools.api.block.model.generic.BSMMParticleBlock;
import eu.pb4.factorytools.api.block.model.generic.BlockStateModel;
import eu.pb4.polymer.blocks.api.BlockModelType;
import eu.pb4.polymer.blocks.api.PolymerBlockResourceUtils;
import eu.pb4.polymer.blocks.api.PolymerTexturedBlock;
import eu.pb4.polymer.virtualentity.api.ElementHolder;
import net.minecraft.block.BlockState;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.TrapdoorBlock;
import net.minecraft.block.enums.DoorHinge;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.Util;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import xyz.nucleoid.packettweaker.PacketContext;

import java.util.Locale;
import java.util.Map;

public record DoorPolymerBlock() implements FactoryBlock, PolymerTexturedBlock, BSMMParticleBlock {
    public static final DoorPolymerBlock INSTANCE = new DoorPolymerBlock();
    private static final Map<Direction, BlockState> STATES_REGULAR = Util.mapEnum(Direction.class, x -> PolymerBlockResourceUtils.requestEmpty(BlockModelType.valueOf(switch (x) {
        case UP -> "BOTTOM";
        case DOWN -> "TOP";
        default -> x.asString().toUpperCase(Locale.ROOT);
    } + "_TRAPDOOR")));

    @Override
    public BlockState getPolymerBlockState(BlockState blockState, PacketContext packetContext) {
        return STATES_REGULAR.get(blockState.get(DoorBlock.OPEN)
                ? (blockState.get(DoorBlock.HINGE) == DoorHinge.RIGHT ? blockState.get(TrapdoorBlock.FACING).rotateYCounterclockwise() : blockState.get(TrapdoorBlock.FACING).rotateYClockwise())
                : blockState.get(TrapdoorBlock.FACING));
    }

    @Override
    public ElementHolder createElementHolder(ServerWorld world, BlockPos pos, BlockState initialBlockState) {
        return BlockStateModel.midRange(initialBlockState, pos);
    }

    @Override
    public boolean isIgnoringBlockInteractionPlaySoundExceptedEntity(BlockState state, ServerPlayerEntity player, Hand hand, ItemStack stack, ServerWorld world, BlockHitResult blockHitResult) {
        return true;
    }

}
