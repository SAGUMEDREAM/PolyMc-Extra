package cc.thonly.polymc_extra.mixin.accessor;

import it.unimi.dsi.fastutil.ints.IntList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.Map;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagNetworkSerialization;

@Mixin(TagNetworkSerialization.NetworkPayload.class)
public interface TagPacketSerializerAccessor {
    @Invoker("<init>")
    static TagNetworkSerialization.NetworkPayload callInit(Map<Identifier, IntList> contents) {
        throw new AssertionError();
    }
}
