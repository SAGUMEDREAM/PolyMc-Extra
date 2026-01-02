package cc.thonly.polymc_extra.mixin.balm;

import cc.thonly.polymc_extra.PolyMcExtra;
import net.blay09.mods.balm.api.network.NetworkVersions;
import net.blay09.mods.balm.api.network.ServerboundModListMessage;
import net.blay09.mods.balm.fabric.FabricBalm;
import net.minecraft.network.RegistryFriendlyByteBuf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

@Pseudo
@Mixin(FabricBalm.class)
public class FabricBalmMixin {
    @Inject(method = "lambda$onInitialize$2", at = @At(value = "HEAD"), cancellable = true)
    private static void setNotRequired(RegistryFriendlyByteBuf buf, ServerboundModListMessage message, CallbackInfo ci) {
        Map<String, NetworkVersions> modList = message.modList();
        modList = new HashMap<>(modList);
        Class<NetworkVersions> networkVersionsClass = NetworkVersions.class;
        Field requireRemote = null;
        try {
            requireRemote = networkVersionsClass.getDeclaredField("requireRemote");
        } catch (Exception e) {
            PolyMcExtra.getLog().error("Can't find field requireRemote in NetworkVersions.class", e);
        } finally {
            if (requireRemote != null) {
                requireRemote.setAccessible(true);
            }
        }
        for (Map.Entry<String, NetworkVersions> entry : modList.entrySet()) {
            NetworkVersions networkVersions = entry.getValue();
            if (requireRemote == null) {
                continue;
            }
            try {
                requireRemote.set(networkVersions, false);
            } catch (Exception e) {
                PolyMcExtra.getLog().error("Can't set field requireRemote in NetworkVersions.class", e);
            }
        }
        Class<ServerboundModListMessage> clazz = ServerboundModListMessage.class;
        try {
            Field field = clazz.getDeclaredField("modList");
            field.setAccessible(true);
            field.set(message, modList);
        } catch (NoSuchFieldException e) {
            PolyMcExtra.getLog().error("Can't find field modList in ServerboundModListMessage.class", e);
        } catch (IllegalAccessException e) {
            PolyMcExtra.getLog().error("Can't set field modList in ServerboundModListMessage.class", e);
        }
    }
}
