package cc.thonly.polymc_extra.util;

import cc.thonly.polymc_extra.block.RealSingleStatePolymerBlock;
import cc.thonly.polymc_extra.block.StateCopyFactoryBlock;
import cc.thonly.polymc_extra.block.base.*;
import eu.pb4.polymer.blocks.api.BlockModelType;
import eu.pb4.polymer.core.api.block.PolymerBlock;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.stream.Stream;

@Getter
@Slf4j
public class ExtraModelType {
    private static final Map<String, ExtraModelType> TYPES = new Object2ObjectOpenHashMap<>();
    public static final ExtraModelType REAL_FULL_BLOCK = of("REAL_FULL_BLOCK", (block, input) -> RealSingleStatePolymerBlock.of(block, BlockModelType.FULL_BLOCK));
    public static final ExtraModelType SIGN = of("SIGN", (block, input) -> StateCopyFactoryBlock.SIGN);
    public static final ExtraModelType WALL_SIGN = of("WALL_SIGN", (block, input) -> StateCopyFactoryBlock.WALL_SIGN);
    public static final ExtraModelType HANGING_SIGN = of("HANGING_SIGN", (block, input) -> StateCopyFactoryBlock.HANGING_SIGN);
    public static final ExtraModelType HANGING_WALL_SIGN = of("HANGING_WALL_SIGN", (block, input) -> StateCopyFactoryBlock.HANGING_WALL_SIGN);
    public static final ExtraModelType WALL = of("WALL", (block, input) -> StateCopyFactoryBlock.WALL);
    public static final ExtraModelType STAIR = of("STAIR", (block, input) -> StateCopyFactoryBlock.STAIR);
    public static final ExtraModelType FENCE = of("FENCE", (block, input) -> StateCopyFactoryBlock.FENCE);
    public static final ExtraModelType FENCE_GATE = of("FENCE_GATE ", (block, input) -> StateCopyFactoryBlock.FENCE_GATE);
    public static final ExtraModelType BUTTON = of("BUTTON", (block, input) -> StateCopyFactoryBlock.BUTTON);
    public static final ExtraModelType PRESSURE_PLATE = of("PRESSURE_PLATE", (block, input) -> StateCopyFactoryBlock.PRESSURE_PLATE);
    public static final ExtraModelType PANE = of("PANE", (block, input) -> StateCopyFactoryBlock.PANE);
    public static final ExtraModelType CARPET = of("CARPET", (block, input) -> StateCopyFactoryBlock.CARPET);
    public static final ExtraModelType CHAIN = of("CHAIN", (block, input) -> StateCopyFactoryBlock.CHAIN);
    public static final ExtraModelType LANTERN = of("LANTERN", (block, input) -> StateCopyFactoryBlock.LANTERN);
    public static final ExtraModelType TRAPDOOR = of("TRAPDOOR", (block, input) -> TrapdoorPolymerBlock.INSTANCE);
    public static final ExtraModelType DOOR = of("DOOR", (block, input) -> DoorPolymerBlock.INSTANCE);
    public static final ExtraModelType SLAB = of("SLAB", (block, input) -> SlabFactoryBlock.INSTANCE);
    public static final ExtraModelType BLOCK_DISPLAY_ENTITY = of("BLOCK_DISPLAY_ENTITY", (block, input) -> BarrierPolymerBlock.INSTANCE);
    public static final ExtraModelType POLYMER = of("POLYMER", (block, input) -> {
        String cased = input.toUpperCase();
        String[] split = cased.split(":");
        if (split.length == 1) {
            return UnknownPolymerBlock.INSTANCE;
        }
        String blockModelTypeStr = split[1].toUpperCase();
        BlockModelType blockModelType = null;
        try {
            blockModelType = BlockModelType.valueOf(blockModelTypeStr);
        } catch (Exception err) {
            log.error("Unable to find BlockModelType constant via input String {}", input, err);
        }
        if (blockModelType == null) {
            return UnknownPolymerBlock.INSTANCE;
        } else {
            return RealSingleStatePolymerBlock.of(block, blockModelType);
        }
    });
    public static final ExtraModelType UNKNOWN = of("UNKNOWN", (block, input) -> UnknownPolymerBlock.INSTANCE);

    private final String name;
    private final StateFactory factory;

    ExtraModelType(@NotNull String name, StateFactory factory) {
        this.name = name.toUpperCase();
        this.factory = factory;
        TYPES.put(name, this);
    }

    public static ExtraModelType of(@NotNull String name, StateFactory factory) {
        return new ExtraModelType(name, factory);
    }

    public static ExtraModelType valueOf(@NotNull String name) {
        String cased = name.toUpperCase();
        String[] split = cased.split(":");
        return TYPES.get(split[0]);
    }

    public static Stream<ExtraModelType> values() {
        return TYPES.values().stream();
    }

    @FunctionalInterface
    public interface StateFactory {
        PolymerBlock getState(Block block, String value);
    }
}
