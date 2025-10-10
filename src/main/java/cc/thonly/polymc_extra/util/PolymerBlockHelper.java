package cc.thonly.polymc_extra.util;

import cc.thonly.polymc_extra.PolyMcExtra;
import cc.thonly.polymc_extra.block.BaseFactoryBlock;
import cc.thonly.polymc_extra.block.RealSingleStatePolymerBlock;
import cc.thonly.polymc_extra.block.StateCopyFactoryBlock;
import cc.thonly.polymc_extra.block.StatePolymerBlock;
import cc.thonly.polymc_extra.block.base.*;
import cc.thonly.polymc_extra.config.PolyMcExtraConfig;
import eu.pb4.factorytools.api.block.model.SignModel;
import eu.pb4.factorytools.api.block.model.generic.BlockStateModelManager;
import eu.pb4.polymer.blocks.api.BlockModelType;
import eu.pb4.polymer.common.api.PolymerCommonUtils;
import eu.pb4.polymer.core.api.block.PolymerBlock;
import eu.pb4.polymer.virtualentity.api.BlockWithElementHolder;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.block.*;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import java.nio.file.Path;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;

@SuppressWarnings({"unchecked", "rawtypes"})
public class PolymerBlockHelper {
    public static final Map<Class<? extends Block>, BlockRegister<?>> BLOCK_MAP = new Object2ObjectLinkedOpenHashMap<>();

    public static void registers() {

    }

    public static <T extends Block> void registerType(Class<T> type, BlockRegister<T> register) {
        BLOCK_MAP.put(type, register);
    }

    public static boolean testJsonExist(Block block) {
        var id = Registries.BLOCK.getId(block);
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

    public static void registerPolymerBlock(Block block) {
        Identifier id = Registries.BLOCK.getId(block);
        PolymerBlock polymerBlock = requestPolymerBlock(block);
        if (polymerBlock == null) {
            PolymerBlock.registerOverlay(block, UnknownPolymerBlock.INSTANCE);
            return;
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

        if (block instanceof AbstractSignBlock signBlock) {
            PolyMcExtraPacks.SIGN_MODELS.add(signBlock);
            PolyMcExtra.LATE_INIT.add(() -> {
                SignModel.setModel(block, Identifier.of(id.getNamespace(), "block_sign/" + id.getPath()));
            });
        }
    }

    public static PolymerBlock requestPolymerBlock(Block block) {
        Identifier id = Registries.BLOCK.getId(block);
        BlockState defaultState = block.getDefaultState();

        PolymerBlock polymerBlock = findFromBlockMap(block);
        if (polymerBlock == null) {
            polymerBlock = PolyMcExtraConfig.getConfig().getService().getCustomBlockMapping(block);
        }

        if (polymerBlock == null) {
            polymerBlock = switch (block) {
                case RedstoneLampBlock ignored -> StatePolymerBlock.of(block, BlockModelType.FULL_BLOCK);
                case StainedGlassBlock ignored -> BaseFactoryBlock.BARRIER;
                case StairsBlock ignored -> StateCopyFactoryBlock.STAIR;
                case SlabBlock ignored -> SlabFactoryBlock.INSTANCE;
                case FenceGateBlock ignored -> StateCopyFactoryBlock.FENCE_GATE;
                case FenceBlock ignored -> StateCopyFactoryBlock.FENCE;
                case WallBlock ignored -> StateCopyFactoryBlock.WALL;
                case LeavesBlock ignored -> RealSingleStatePolymerBlock.of(block, BlockModelType.TRANSPARENT_BLOCK);
                case SignBlock ignored -> StateCopyFactoryBlock.SIGN;
                case WallSignBlock ignored -> StateCopyFactoryBlock.WALL_SIGN;
                case HangingSignBlock ignored -> StateCopyFactoryBlock.HANGING_SIGN;
                case WallHangingSignBlock ignored -> StateCopyFactoryBlock.HANGING_WALL_SIGN;
                case DoorBlock ignored -> DoorPolymerBlock.INSTANCE;
                case TrapdoorBlock ignored -> TrapdoorPolymerBlock.INSTANCE;
                case ButtonBlock ignored -> StateCopyFactoryBlock.BUTTON;
                case PressurePlateBlock ignored -> StateCopyFactoryBlock.PRESSURE_PLATE;
                case PlantBlock ignored -> BaseFactoryBlock.SAPLING;
                case FlowerPotBlock ignored -> new PottedPlantPolymerBlock(id);
                case PaneBlock ignored -> StateCopyFactoryBlock.PANE;
                case LanternBlock ignored -> StateCopyFactoryBlock.LANTERN;
                case HorizontalFacingBlock ignored -> BaseFactoryBlock.BARRIER;
                case CarpetBlock ignored -> StateCopyFactoryBlock.CARPET;
                case ChainBlock ignored -> StateCopyFactoryBlock.CHAIN;
                case PillarBlock ignored -> BaseFactoryBlock.BARRIER;
                case GrateBlock ignored -> BaseFactoryBlock.BARRIER;
                default -> {
                    if (defaultState.isFullCube(PolymerCommonUtils.getFakeWorld(), BlockPos.ORIGIN)) {
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
        for (Map.Entry<Class<? extends Block>, BlockRegister<?>> mapEntry : BLOCK_MAP.entrySet()) {
            Class typeClass = mapEntry.getKey();
            BlockRegister value = mapEntry.getValue();
            if (block.getClass().isAssignableFrom(typeClass)) {
                return value.apply(block);
            }
        }
        return null;
    }

    @FunctionalInterface
    public interface BlockRegister<T extends Block> {
        PolymerBlock apply(T block);
    }
}
