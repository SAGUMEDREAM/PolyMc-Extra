package cc.thonly.polymc_extra.command;

import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

public class PolymerExtraCommands {

    public static void bootstrap() {
        registerCommand(new PolymerExtraCommand());
    }

    public static void registerCommand(CommandRegistration registry) {
        CommandRegistrationCallback.EVENT.register(registry::register);
    }

    public interface CommandRegistration {
        void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess access, CommandManager.RegistrationEnvironment environment);
    }
}
