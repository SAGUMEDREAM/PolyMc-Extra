package cc.thonly.polymc_extra.mixin;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.network.protocol.common.ClientboundUpdateTagsPacket;

// 阻止空 Tag 被发给玩家而让玩家客户端崩溃
@Mixin(value = ClientboundUpdateTagsPacket.class, priority = 500)
public class ClientboundUpdateTagsPacketMixin {
//    @ModifyArg(
//            method = "write",
//            at = @At(
//                    value = "INVOKE",
//                    target = "Lnet/minecraft/network/PacketByteBuf;writeMap(Ljava/util/Map;Lnet/minecraft/network/codec/PacketEncoder;Lnet/minecraft/network/codec/PacketEncoder;)V"
//            )
//    )
//    private Map<RegistryKey<? extends Registry<?>>, TagPacketSerializer.Serialized> skipEmptyEntryTags(
//            Map<RegistryKey<? extends Registry<?>>, TagPacketSerializer.Serialized> groups
//    ) {
//        var filtered = new HashMap<RegistryKey<? extends Registry<?>>, TagPacketSerializer.Serialized>();
//
//        for (var entry : groups.entrySet()) {
//            RegistryKey<? extends Registry<?>> registryKey = entry.getKey();
//            TagPacketSerializer.Serialized original = entry.getValue();
//
//            var newSerialized = TagPacketSerializerAccessor.callInit(
//                    new Object2ObjectLinkedOpenHashMap<>(original.contents)
//            );
//
//            newSerialized.contents.entrySet().removeIf(e -> e.getValue().isEmpty());
//
//            if (!newSerialized.contents.isEmpty()) {
//                filtered.put(registryKey, newSerialized);
//            }
//        }
//
//        return filtered;
//    }
}
