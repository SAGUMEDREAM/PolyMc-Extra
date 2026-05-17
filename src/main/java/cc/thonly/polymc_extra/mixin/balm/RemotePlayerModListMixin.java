package cc.thonly.polymc_extra.mixin.balm;

import net.blay09.mods.balm.Balm;
import net.blay09.mods.balm.network.internal.CommonBalmNetworking;
import net.blay09.mods.balm.network.internal.NetworkVersions;
import net.blay09.mods.balm.network.internal.RemotePlayerModList;
import net.blay09.mods.balm.platform.BalmEnvironment;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.Map;

@Pseudo
@Mixin(RemotePlayerModList.class)
public class RemotePlayerModListMixin {
    @Inject(method = "validateRemoteMods",
            at = @At(value = "HEAD"),
            cancellable = true
    )
    private static void cancelKick(ServerPlayer player, Map<String, NetworkVersions> modList, CallbackInfo ci) {
        if (true) {
            ci.cancel();
            return;
        }
        modList = new HashMap<>(modList);
        final var networking = (CommonBalmNetworking) Balm.networking();
        for (final var entry : modList.entrySet()) {
            final var modId = entry.getKey();
            final var clientVersions = entry.getValue();
            final var serverVersionsOpt = networking.getNetworkVersions(modId, BalmEnvironment.DEDICATED_SERVER);
            if (serverVersionsOpt.isEmpty()) {
                if (clientVersions.requireRemote()) {
                    player.connection.disconnect(Component.translatable("disconnect.balm.mod_missing_on_server",
                            Component.literal(modId).withStyle(ChatFormatting.RED)));
                    return;
                } else {
                    continue;
                }
            }

            final var serverVersions = serverVersionsOpt.get();
            if (!clientVersions.networkVersion().equals(serverVersions.networkVersion())) {
                player.connection.disconnect(Component.translatable("disconnect.balm.mod_version_mismatch",
                        Component.literal(modId).withStyle(ChatFormatting.GOLD),
                        Component.literal(serverVersions.modVersion()).withStyle(ChatFormatting.GREEN),
                        Component.literal(clientVersions.modVersion()).withStyle(ChatFormatting.RED)));
                return;
            }
        }

        for (final var modId : networking.getRegisteredMods()) {
            final var serverVersions = networking.getNetworkVersions(modId, BalmEnvironment.DEDICATED_SERVER).orElseThrow();
            if (serverVersions.requireRemote() && !modList.containsKey(modId)) {
                final var serverModVersion = serverVersions.modVersion();
                player.connection.disconnect(Component.translatable("disconnect.balm.mod_missing_on_client",
                        Component.literal(modId).withStyle(ChatFormatting.RED),
                        Component.literal(modId).withStyle(ChatFormatting.GOLD),
                        Component.literal(serverModVersion).withStyle(ChatFormatting.GREEN)));
                return;
            }
        }
        ci.cancel();
    }
}
