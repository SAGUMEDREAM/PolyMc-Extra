package cc.thonly.polymc_extra.mixin.balm;

import net.blay09.mods.balm.api.BalmEnvironment;
import net.blay09.mods.balm.api.network.NetworkVersions;
import net.blay09.mods.balm.fabric.network.FabricBalmNetworking;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Pseudo
@Mixin(FabricBalmNetworking.class)
public class FabricBalmNetworkingMixin {
    @Inject(method = "getNetworkVersions", at = @At("RETURN"), cancellable = true)
    public void setNetworkVersions(String modId, BalmEnvironment environment, CallbackInfoReturnable<Optional<NetworkVersions>> cir) {
        Optional<NetworkVersions> returnValue = cir.getReturnValue();
        if (returnValue.isEmpty()) {
            return;
        }
        NetworkVersions networkVersions = returnValue.get();
        cir.setReturnValue(Optional.of(new NetworkVersions(networkVersions.modVersion(), networkVersions.networkVersion(), false)));
    }
}
