package cc.thonly.polymc_extra.mixin;

import cc.thonly.polymc_extra.PolyMcExtra;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.handler.codec.DecoderException;
import net.minecraft.network.VarInt;
import net.minecraft.network.codec.ByteBufCodecs;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ByteBufCodecs.class)
public interface ByteBufCodecsMixin {

//    @Mutable
//    @Shadow
//    @Final
//    public static StreamCodec<ByteBuf, PropertyMap> GAME_PROFILE_PROPERTIES;

//    @Inject(method = "<clinit>", at = @At("RETURN"))
//    private static void reset(CallbackInfo ci) {
//        GAME_PROFILE_PROPERTIES = new StreamCodec<ByteBuf, PropertyMap>() {
//            private static final int MAX_PROPERTY_NAME_LENGTH = 64;
//            private static final int MAX_PROPERTY_VALUE_LENGTH = Short.MAX_VALUE;
//            private static final int MAX_PROPERTY_SIGNATURE_LENGTH = 1024;
//            private static final int MAX_PROPERTIES = 16;
//
//            @Override
//            public PropertyMap decode(ByteBuf byteBuf2) {
//                int i = ByteBufCodecs.readCount(byteBuf2, 16);
//                ImmutableMultimap.Builder<String, Property> builder = ImmutableMultimap.builder();
//                for (int j = 0; j < i; ++j) {
//                    String string = Utf8String.read(byteBuf2, 64);
//                    String string2 = Utf8String.read(byteBuf2, Short.MAX_VALUE);
//                    String string3 = FriendlyByteBuf.readNullable(byteBuf2, byteBuf -> Utf8String.read(byteBuf, 1024));
//                    Property property = new Property(string, string2, string3);
//                    builder.put(property.name(), property);
//                }
//                return new PropertyMap(builder.build());
//            }
//
//            @Override
//            public void encode(ByteBuf byteBuf2, PropertyMap propertyMap) {
//                Logger log = PolyMcExtra.getLog();
//                if (propertyMap.size() > 16) {
//                    log.warn("Can't read player profiler, data: ");
//                    propertyMap.forEach((string, property) -> {
//                        log.warn("name: {}, value: {}, signature {}", property.name(), property.value(), property.signature());
//                    });
//                } else {
//                    log.info("profiler sure");
//                }
//                ByteBufCodecs.writeCount(byteBuf2, propertyMap.size(), 16);
//                for (Property property : propertyMap.values()) {
//                    Utf8String.write(byteBuf2, property.name(), 64);
//                    Utf8String.write(byteBuf2, property.value(), Short.MAX_VALUE);
//                    FriendlyByteBuf.writeNullable(byteBuf2, property.signature(), (byteBuf, string) -> Utf8String.write(byteBuf, string, 1024));
//                }
//            }
//        };
//    }

//    @Inject(method = "writeCount", at = @At("HEAD"), cancellable = true)
//    private static void onWriteCount(ByteBuf byteBuf, int i, int j, CallbackInfo ci) {
//        if (i > j) {
//            ByteBuf copy = byteBuf.copy();
//            try {
//                PolyMcExtra.getLog().error("List size {} > {}, readerIndex={}, writerIndex={}, capacity={}\n=== ByteBuf dump ===\n{}", i, j, byteBuf.readerIndex(), byteBuf.writerIndex(), byteBuf.capacity(), ByteBufUtil.prettyHexDump(copy));
//            } finally {
//                copy.release();
//            }
//        }
//        VarInt.write(byteBuf, j);
//        ci.cancel();
//    }
//
//    @Inject(method = "readCount", at = @At("HEAD"), cancellable = true, order = 10000)
//    private static void onReadCount(ByteBuf byteBuf, int i, CallbackInfoReturnable<Integer> cir) {
//        int j = VarInt.read(byteBuf);
//        if (j > i) {
//            ByteBuf copy = byteBuf.copy();
//            try {
//                PolyMcExtra.getLog().error("List size {} > {}, readerIndex={}, writerIndex={}, capacity={}\n=== ByteBuf dump ===\n{}", i, j, byteBuf.readerIndex(), byteBuf.writerIndex(), byteBuf.capacity(), ByteBufUtil.prettyHexDump(copy));
//            } finally {
//                copy.release();
//            }
//        }
//        if (j > i) {
//            throw new DecoderException(j + " elements exceeded max size of: " + i);
//        }
//        cir.setReturnValue(j);
//        cir.cancel();
//    }
}
