package cc.thonly.polymc_extra.block;

import cc.thonly.polymc_extra.block.base.BarrierPolymerBlock;
import com.google.gson.JsonParser;
import com.mojang.serialization.JsonOps;
import eu.pb4.factorytools.api.block.FactoryBlock;
import eu.pb4.factorytools.api.block.model.generic.BSMMParticleBlock;
import eu.pb4.factorytools.api.block.model.generic.BlockStateModelManager;
import eu.pb4.polymer.blocks.api.BlockModelType;
import eu.pb4.polymer.blocks.api.PolymerBlockModel;
import eu.pb4.polymer.blocks.api.PolymerBlockResourceUtils;
import eu.pb4.polymer.blocks.api.PolymerTexturedBlock;
import eu.pb4.polymer.resourcepack.extras.api.format.blockstate.BlockStateAsset;
import eu.pb4.polymer.resourcepack.extras.api.format.blockstate.StateModelVariant;
import eu.pb4.polymer.virtualentity.api.ElementHolder;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Tuple;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.predicate.BlockStatePredicate;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;
import xyz.nucleoid.packettweaker.PacketContext;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Predicate;

public class StatePolymerBlock implements FactoryBlock, PolymerTexturedBlock, BSMMParticleBlock {
    private final Map<BlockState, BlockState> map;
    private final FactoryBlock fallback;

    public StatePolymerBlock(Map<BlockState, BlockState> map, FactoryBlock fallback) {
        this.map = map;
        this.fallback = fallback;
    }

    public static StatePolymerBlock of(Block block, BlockModelType type) {
        return of(block, type, BaseFactoryBlock.BARRIER, x -> true);
    }

    public static StatePolymerBlock of(Block block, BlockModelType type, FactoryBlock fallback, Predicate<BlockState> canUseBlock) {
        var id = BuiltInRegistries.BLOCK.getKey(block);
        var mods = FabricLoader.getInstance().getAllMods();
        var target = "assets/" + id.getNamespace() + "/blockstates/" + id.getPath() + ".json";
        Path path = null;
        for (ModContainer mod : mods) {
            Optional<Path> path1 = mod.findPath(target);
            if (path1.isPresent()) {
                path = path1.get();
                break;
            }
        }
        if (path == null) {
            return BarrierPolymerBlock.INSTANCE;
        }

        BlockStateAsset decoded;
        try {
            decoded = BlockStateAsset.CODEC.decode(JsonOps.INSTANCE, JsonParser.parseString(Files.readString(path))).getOrThrow().getFirst();

            var list = new ArrayList<Tuple<BlockStatePredicate, List<StateModelVariant>>>();
            var cache = new HashMap<List<StateModelVariant>, BlockState>();


            BlockStateModelManager.parseVariants(block, decoded.variants().orElseThrow(), (a, b) -> list.add(new Tuple<>(a, b)));
            var map = new IdentityHashMap<BlockState, BlockState>();


            for (var state : block.getStateDefinition().getPossibleStates()) {
                for (var pair : list) {
                    if (pair.getA().test(state) && canUseBlock.test(state)) {
                        map.put(state, cache.computeIfAbsent(pair.getB(), c -> PolymerBlockResourceUtils.requestBlock(
                                type,
                                c.stream().map(x -> new PolymerBlockModel(x.model(), x.x(), x.y(), x.uvlock(), x.weigth())).toArray(PolymerBlockModel[]::new))));
                        break;
                    }
                }
            }

            return new StatePolymerBlock(map, fallback);
        } catch (Throwable e) {
            return BarrierPolymerBlock.INSTANCE;
        }
    }

    @Override
    public BlockState getPolymerBlockState(BlockState blockState, PacketContext packetContext) {
        var val = map.get(blockState);
        return val != null ? val : fallback.getPolymerBlockState(blockState, packetContext);
    }

    @Override
    public @Nullable ElementHolder createElementHolder(ServerLevel world, BlockPos pos, BlockState initialBlockState) {
        return map.containsKey(initialBlockState) ? null : fallback.createElementHolder(world, pos, initialBlockState);
    }

    @Override
    public boolean isIgnoringBlockInteractionPlaySoundExceptedEntity(BlockState state, ServerPlayer player, InteractionHand hand, ItemStack stack, ServerLevel world, BlockHitResult blockHitResult) {
        return true;
    }
}
