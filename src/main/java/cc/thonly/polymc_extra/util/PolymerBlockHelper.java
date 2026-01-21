package cc.thonly.polymc_extra.util;

import cc.thonly.polymc_extra.PolyMcExtra;
import cc.thonly.polymc_extra.block.BaseFactoryBlock;
import cc.thonly.polymc_extra.block.RealSingleStatePolymerBlock;
import cc.thonly.polymc_extra.block.StateCopyFactoryBlock;
import cc.thonly.polymc_extra.block.StatePolymerBlock;
import cc.thonly.polymc_extra.block.base.*;
import cc.thonly.polymc_extra.config.PolyMcExtraConfig;
import cc.thonly.polymc_extra.data.PolyMcExtraPacks;
import eu.pb4.factorytools.api.block.model.SignModel;
import eu.pb4.factorytools.api.block.model.generic.BlockStateModelManager;
import eu.pb4.polymer.blocks.api.BlockModelType;
import eu.pb4.polymer.common.api.PolymerCommonUtils;
import eu.pb4.polymer.core.api.block.PolymerBlock;
import eu.pb4.polymer.virtualentity.api.BlockWithElementHolder;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.CarpetBlock;
import net.minecraft.world.level.block.CeilingHangingSignBlock;
import net.minecraft.world.level.block.ChainBlock;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.IronBarsBlock;
import net.minecraft.world.level.block.LanternBlock;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.PressurePlateBlock;
import net.minecraft.world.level.block.RedstoneLampBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SignBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StainedGlassBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.StandingSignBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.VegetationBlock;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.WallHangingSignBlock;
import net.minecraft.world.level.block.WallSignBlock;
import net.minecraft.world.level.block.WaterloggedTransparentBlock;
import net.minecraft.world.level.block.state.BlockState;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;

@SuppressWarnings({"unchecked", "rawtypes"})
public class PolymerBlockHelper {
    private static final Map<Class<? extends Block>, BlockRegisterFactory<?>> BLOCK_MAP = new Object2ObjectLinkedOpenHashMap<>();
    private static final Map<Block, PolymerBlock> BLOCK_2_HOLDER = new Object2ObjectLinkedOpenHashMap<>();

    public static <T extends Block> void registerType(Class<T> type, BlockRegisterFactory<T> register) {
        BLOCK_MAP.put(type, register);
    }

    public static void registerHolder(Block block, PolymerBlock polymerBlock) {
        BLOCK_2_HOLDER.put(block, polymerBlock);
    }

    public static Map<Class<? extends Block>, BlockRegisterFactory<?>> getBlockMap() {
        return Map.copyOf(BLOCK_MAP);
    }

    public static Map<Block, PolymerBlock> getBlock2Holder() {
        return Map.copyOf(BLOCK_2_HOLDER);
    }

    public static boolean testJsonExist(Block block) {
        var id = BuiltInRegistries.BLOCK.getKey(block);
        FabricLoader instance = FabricLoader.getInstance();
        Collection<ModContainer> mods = instance.getAllMods();
        var target = "assets/" + id.getNamespace() + "/blockstates/" + id.getPath() + ".json";
        Path path = null;
        for (ModContainer mod : mods) {
            Optional<Path> path1 = mod.findPath(target);
            if (path1.isPresent()) {
                path = path1.get();
                break;
            }
        }
        return path != null;
    }

    public static PolymerBlock registerPolymerBlock(Block block) {
        Identifier id = BuiltInRegistries.BLOCK.getKey(block);
        PolymerBlock polymerBlock = requestPolymerBlock(block);
        if (polymerBlock == null) {
            UnknownPolymerBlock instance = UnknownPolymerBlock.INSTANCE;
            PolymerBlock.registerOverlay(block, instance);
            return instance;
        }
        PolymerBlock.registerOverlay(block, polymerBlock);
        PolyMcExtra.LATE_INIT.add(() -> {
            if (testJsonExist(block)) {
                BlockStateModelManager.addBlock(id, block);
            }
        });
        if (polymerBlock instanceof BlockWithElementHolder blockWithElementHolder) {
            BlockWithElementHolder.registerOverlay(block, blockWithElementHolder);
        }

        if (block instanceof StandingSignBlock signBlock) {
            PolyMcExtraPacks.SIGN_MODEL_IDS.add(id);
            PolyMcExtra.LATE_INIT.add(() -> {
                SignModel.setModel(block, Identifier.fromNamespaceAndPath(id.getNamespace(), "block_sign/" + id.getPath()));
            });
        }
        return polymerBlock;
    }

    public static PolymerBlock requestPolymerBlock(Block block) {
        Identifier id = BuiltInRegistries.BLOCK.getKey(block);
        BlockState defaultState = block.defaultBlockState();

        PolymerBlock polymerBlock = findFromBlockMap(block);
        if (polymerBlock == null) {
            polymerBlock = PolyMcExtraConfig.getConfig().getService().getCustomBlockMapping(block);
        }

        if (polymerBlock == null) {
            polymerBlock = switch (block) {
                case RedstoneLampBlock ignored -> StatePolymerBlock.of(block, BlockModelType.FULL_BLOCK);
                case StainedGlassBlock ignored -> BaseFactoryBlock.BARRIER;
                case StairBlock ignored -> StateCopyFactoryBlock.STAIR;
                case SlabBlock ignored -> SlabFactoryBlock.INSTANCE;
                case FenceGateBlock ignored -> StateCopyFactoryBlock.FENCE_GATE;
                case FenceBlock ignored -> StateCopyFactoryBlock.FENCE;
                case WallBlock ignored -> StateCopyFactoryBlock.WALL;
                case LeavesBlock ignored -> RealSingleStatePolymerBlock.of(block, BlockModelType.TRANSPARENT_BLOCK);
                case StandingSignBlock ignored -> StateCopyFactoryBlock.SIGN;
                case WallSignBlock ignored -> StateCopyFactoryBlock.WALL_SIGN;
                case CeilingHangingSignBlock ignored -> StateCopyFactoryBlock.HANGING_SIGN;
                case WallHangingSignBlock ignored -> StateCopyFactoryBlock.HANGING_WALL_SIGN;
                case DoorBlock ignored -> DoorPolymerBlock.INSTANCE;
                case TrapDoorBlock ignored -> TrapdoorPolymerBlock.INSTANCE;
                case ButtonBlock ignored -> StateCopyFactoryBlock.BUTTON;
                case PressurePlateBlock ignored -> StateCopyFactoryBlock.PRESSURE_PLATE;
                case VegetationBlock ignored -> BaseFactoryBlock.SAPLING;
                case FlowerPotBlock ignored -> new PottedPlantPolymerBlock(id);
                case IronBarsBlock ignored -> StateCopyFactoryBlock.PANE;
                case LanternBlock ignored -> StateCopyFactoryBlock.LANTERN;
                case HorizontalDirectionalBlock ignored -> BaseFactoryBlock.BARRIER;
                case CarpetBlock ignored -> StateCopyFactoryBlock.CARPET;
                case ChainBlock ignored -> StateCopyFactoryBlock.CHAIN;
                case RotatedPillarBlock ignored -> BaseFactoryBlock.BARRIER;
                case WaterloggedTransparentBlock ignored -> BaseFactoryBlock.BARRIER;
                default -> {
                    if (defaultState.isCollisionShapeFullBlock(PolymerCommonUtils.getFakeWorld(), BlockPos.ZERO)) {
                        yield StatePolymerBlock.of(block, BlockModelType.FULL_BLOCK);
                    } else {
                        yield BaseFactoryBlock.BARRIER;
                    }
                }
            };
        }
        return polymerBlock;
    }

    private static PolymerBlock findFromBlockMap(Block block) {
        for (Map.Entry<Class<? extends Block>, BlockRegisterFactory<?>> mapEntry : BLOCK_MAP.entrySet()) {
            Class typeClass = mapEntry.getKey();
            BlockRegisterFactory value = mapEntry.getValue();
            if (block.getClass().isAssignableFrom(typeClass)) {
                return value.apply(block);
            }
        }
        return null;
    }

    @FunctionalInterface
    public interface BlockRegisterFactory<T extends Block> {
        PolymerBlock apply(T block);
    }
}
