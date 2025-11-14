package cc.thonly.polymc_extra.mixin.carpet;

import carpet.patches.FakeClientConnection;
import cc.thonly.polymc_extra.PolyMcExtra;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.PacketFlow;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@SuppressWarnings({"deprecation", "UnstableApiUsage"})
@Mixin(FakeClientConnection.class)
public abstract class FakeClientConnectionMixin extends Connection {
    public FakeClientConnectionMixin(PacketFlow side) {
        super(side);
    }

    @SuppressWarnings("DataFlowIssue")
    @Inject(method = "<init>", at = @At("TAIL"))
    public void initPolyMap(PacketFlow p, CallbackInfo ci) {
//        if (PolyMcExtra.HAS_LOADED_POLYMC) {
//            FakeClientConnection connection = (FakeClientConnection) (Object) this;
//            io.github.theepicblock.polymc.api.misc.PolyMapProvider polyMapProvider = (io.github.theepicblock.polymc.api.misc.PolyMapProvider) connection;
//            polyMapProvider.setPolyMap(io.github.theepicblock.polymc.impl.NOPPolyMap.INSTANCE);
//        }
    }
}
