package cc.thonly.polymc_extra.command;

import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class PolymerExtraCommands {

    public static void bootstrap() {
        registerCommand(new PolymerExtraCommand());
    }

    public static void registerCommand(CommandRegistration registry) {
        CommandRegistrationCallback.EVENT.register(registry::register);
    }

    public interface CommandRegistration {
        void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext access, Commands.CommandSelection environment);
    }
}
