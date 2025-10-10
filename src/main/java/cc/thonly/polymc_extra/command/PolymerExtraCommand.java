package cc.thonly.polymc_extra.command;

import cc.thonly.polymc_extra.util.ExtraModelType;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import eu.pb4.polymer.blocks.api.BlockModelType;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.*;

import java.net.URI;
import java.util.Arrays;
import java.util.stream.Stream;

public class PolymerExtraCommand implements PolymerExtraCommands.CommandRegistration {

    @Override
    public void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess access, CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(
                CommandManager.literal("polymc-extra")
                        .executes(this::run)
                        .then(
                                CommandManager.literal("get-model-types")
                                        .executes(this::availableTypeList)
                        )
        );
    }

    private int run(CommandContext<ServerCommandSource> context) {
        MutableText mutableText = Text.empty();
        mutableText.append(Text.literal("=== PolyMc-Extra ===\n"));
        mutableText.append(Text.literal("Contributors: 稀神灵梦\n"));
        mutableText.append(Text.literal("Github: ")
                .append(
                        Text.literal("https://github.com/SAGUMEDREAM/PolyMc-Extra")
                                .setStyle(
                                        Style.EMPTY
                                                .withColor(0x55AAFF)
                                                .withUnderline(true)
                                                .withClickEvent(new ClickEvent.OpenUrl(URI.create("https://github.com/SAGUMEDREAM/PolyMc-Extra")))
                                )
                ));
        return 1;
    }

    private int availableTypeList(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        MutableText mutableText = Text.empty();
        mutableText.append("ExtraModelType List:\n");
        Stream<ExtraModelType> values = ExtraModelType.values();
        values.forEach((modelType) -> {
            String name = modelType.getName();
            mutableText.append(Text.literal(name + "\n"));
            if (name.equalsIgnoreCase("POLYMER")) {
                String[] names = Arrays.stream(BlockModelType.values())
                        .map(Enum::name)
                        .toArray(String[]::new);
                for (String key : names) {
                    mutableText.append(Text.literal("POLYMER:" + key + "\n"));
                }
            }
        });
        source.sendFeedback(() -> mutableText, false);
        return 1;
    }
}
