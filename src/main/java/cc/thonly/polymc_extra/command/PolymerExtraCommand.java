package cc.thonly.polymc_extra.command;

import cc.thonly.polymc_extra.model.ExtraModelType;
import cc.thonly.polymc_extra.util.PolymerBlockHelper;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import eu.pb4.polymer.blocks.api.BlockModelType;
import eu.pb4.polymer.core.api.block.PolymerBlock;
import eu.pb4.polymer.core.api.block.PolymerBlockUtils;
import eu.pb4.polymer.core.api.utils.PolymerSyncedObject;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import xyz.nucleoid.packettweaker.PacketContext;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Stream;

@Slf4j
public class PolymerExtraCommand implements PolymerExtraCommands.CommandRegistration {

    @Override
    public void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext access, Commands.CommandSelection environment) {
        dispatcher.register(
                Commands.literal("polymc-extra")
                        .executes(this::run)
                        .then(
                                Commands.literal("get-model-types")
                                        .executes(this::availableTypeList)
                        )
                        .then(
                                Commands.literal("get-client-state")
                                        .executes(this::getClientBlockState)
                        )
                        .then(
                                Commands.literal("export-extra-block-models-mappings")
                                        .requires(source -> source.hasPermission(2))
                                        .executes(this::exportMappings)
                        )
        );
    }

    private int run(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        MutableComponent mutableText = Component.empty();
        mutableText.append(Component.literal("=== PolyMc-Extra ===\n"));
        mutableText.append(Component.literal("Contributors: 稀神灵梦\n"));
        mutableText.append(Component.literal("Github: ")
                .append(
                        Component.literal("https://github.com/SAGUMEDREAM/PolyMc-Extra")
                                .setStyle(
                                        Style.EMPTY
                                                .withColor(0x55AAFF)
                                                .withUnderlined(true)
                                                .withClickEvent(new ClickEvent.OpenUrl(URI.create("https://github.com/SAGUMEDREAM/PolyMc-Extra")))
                                )
                ));
        source.sendSuccess(() -> mutableText, false);
        return 1;
    }

    private int getClientBlockState(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        ServerPlayer player = source.getPlayer();
        if (player == null) {
            return 0;
        }
        HitResult raycast = player.pick(20.0, 0.0f, true);
        if (raycast == null) {
            return 0;
        }
        MutableComponent mutableText = Component.empty();
        Vec3 pos = raycast.getLocation();
        BlockState blockState = player.level.getBlockState(new BlockPos((int) pos.x, (int) pos.y, (int) pos.z));
        Block block = blockState.getBlock();
        PolymerSyncedObject<Block> syncedObject = PolymerSyncedObject.getSyncedObject(BuiltInRegistries.BLOCK, block);
        BlockState clientBlockState = blockState;
        if (syncedObject instanceof PolymerBlock polymerBlock) {
            clientBlockState = polymerBlock.getPolymerBlockState(blockState, PacketContext.create(player));
        }
        mutableText.append(Component.literal("ClientBlockState: %s => %s".formatted(blockState, clientBlockState)));
        source.sendSuccess(() -> mutableText, false);
        return 1;
    }

    private int availableTypeList(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        MutableComponent mutableText = Component.empty();
        mutableText.append("ExtraModelType List:\n");
        Stream<ExtraModelType> values = ExtraModelType.values();
        values.forEach((modelType) -> {
            String name = modelType.getName();
            mutableText.append(Component.literal("- " + name + "\n"));
            if (name.equalsIgnoreCase("POLYMER")) {
                String[] names = Arrays.stream(BlockModelType.values())
                        .map(Enum::name)
                        .toArray(String[]::new);
                for (String key : names) {
                    mutableText.append(Component.literal(" - POLYMER:" + key + "\n"));
                }
            }
        });
        source.sendSuccess(() -> mutableText, false);
        return 1;
    }

    private int exportMappings(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        try {
            List<String> contents = new ArrayList<>();
            var entries = PolymerBlockHelper.getBlock2Holder().entrySet();
            for (var entry : entries) {
                Block block = entry.getKey();
                PolymerBlock holder = entry.getValue();
                BlockState polymerBlockState = holder.getPolymerBlockState(block.defaultBlockState(), PacketContext.get());
                String line = "%s => %s (Client BlockState: %s)".formatted(block, holder, polymerBlockState);
                contents.add(line);
            }

            Path filePath = Paths.get("./ExtraBlockMapping.txt");
            Files.write(filePath, contents, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

            MutableComponent mutableText = Component.literal("ExtraBlockMapping.txt file export completed: " + filePath.toAbsolutePath());
            source.sendSuccess(() -> mutableText, false);
        } catch (Exception err) {
            log.error("Can't export ExtraBlockMapping.txt file", err);
            MutableComponent errorText = Component.literal("Failed to export ExtraBlockMapping.txt: " + err.getMessage());
            source.sendFailure(errorText);
        }
        return 1;
    }

}
