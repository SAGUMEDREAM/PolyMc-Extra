package cc.thonly.polymc_extra.mixin.test;

import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectMaps;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.Unit;
import org.jspecify.annotations.NonNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@SuppressWarnings({"rawtypes", "unchecked"})
@Mixin(DataComponentPatch.class)
public class DataComponentPatchCodecMixin {

//    @Mutable
//    @Shadow
//    @Final
//    public static Codec<DataComponentPatch> CODEC;
//    @Mutable
//    @Shadow
//    @Final
//    public static StreamCodec<RegistryFriendlyByteBuf, DataComponentPatch> STREAM_CODEC;
//    private static final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
//
//    @Inject(method = "<clinit>", at = @At("TAIL"), cancellable = true)
//    private static void onClinit(CallbackInfo ci) {
//        scheduler.schedule(() -> {
//            DataComponentPatch.CodecGetter codecGetter = new DataComponentPatch.CodecGetter() {
//                public <T> @NonNull StreamCodec<RegistryFriendlyByteBuf, T> apply(DataComponentType<T> dataComponentType) {
//                    return dataComponentType.streamCodec().cast();
//                }
//            };
//            CODEC = makeFixedCodec();
//            STREAM_CODEC = new StreamCodec<RegistryFriendlyByteBuf, DataComponentPatch>() {
//                public DataComponentPatch decode(RegistryFriendlyByteBuf registryFriendlyByteBuf) {
//                    int i = registryFriendlyByteBuf.readVarInt();
//                    int j = registryFriendlyByteBuf.readVarInt();
//                    if (i == 0 && j == 0) {
//                        return DataComponentPatch.EMPTY;
//                    } else {
//                        int k = i + j;
//                        Reference2ObjectMap<DataComponentType<?>, Optional<?>> reference2ObjectMap = new Reference2ObjectArrayMap<>(Math.min(k, 65536));
//
//                        for (int l = 0; l < i; ++l) {
//                            DataComponentType<?> dataComponentType = null;
//                            try {
//                                dataComponentType = DataComponentType.STREAM_CODEC.decode(registryFriendlyByteBuf);
//                                System.out.println(dataComponentType);
//                                Object object = codecGetter.apply(dataComponentType).decode(registryFriendlyByteBuf);
//                                System.out.println(object);
//                                reference2ObjectMap.put(dataComponentType, Optional.ofNullable(object));
//                                reference2ObjectMap.put(dataComponentType, Optional.of(object));
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//                        }
//
//                        for (int l = 0; l < j; ++l) {
//                            DataComponentType<?> dataComponentType = DataComponentType.STREAM_CODEC.decode(registryFriendlyByteBuf);
//                            reference2ObjectMap.put(dataComponentType, Optional.empty());
//                        }
//
//                        return new DataComponentPatch(reference2ObjectMap);
//                    }
//                }
//
//                public void encode(RegistryFriendlyByteBuf registryFriendlyByteBuf, DataComponentPatch dataComponentPatch) {
//                    if (dataComponentPatch.isEmpty()) {
//                        registryFriendlyByteBuf.writeVarInt(0);
//                        registryFriendlyByteBuf.writeVarInt(0);
//                    } else {
//                        int i = 0;
//                        int j = 0;
//                        ObjectIterator<Reference2ObjectMap.Entry<DataComponentType<?>, Optional<?>>> var5 = Reference2ObjectMaps.fastIterable(dataComponentPatch.map).iterator();
//
//                        while (var5.hasNext()) {
//                            Reference2ObjectMap.Entry<DataComponentType<?>, Optional<?>> entry = var5.next();
//                            if (entry.getValue().isPresent()) {
//                                ++i;
//                            } else {
//                                ++j;
//                            }
//                        }
//
//                        registryFriendlyByteBuf.writeVarInt(i);
//                        registryFriendlyByteBuf.writeVarInt(j);
//                        var5 = Reference2ObjectMaps.fastIterable(dataComponentPatch.map).iterator();
//
//                        while (var5.hasNext()) {
//                            Reference2ObjectMap.Entry<DataComponentType<?>, Optional<?>> entry = var5.next();
//                            Optional<?> optional = entry.getValue();
//                            if (optional.isPresent()) {
//                                DataComponentType<?> dataComponentType = entry.getKey();
//                                DataComponentType.STREAM_CODEC.encode(registryFriendlyByteBuf, dataComponentType);
//                                this.encodeComponent(registryFriendlyByteBuf, dataComponentType, optional.get());
//                            }
//                        }
//
//                        var5 = Reference2ObjectMaps.fastIterable(dataComponentPatch.map).iterator();
//
//                        while (var5.hasNext()) {
//                            Reference2ObjectMap.Entry<DataComponentType<?>, Optional<?>> entry = var5.next();
//                            if ((entry.getValue()).isEmpty()) {
//                                DataComponentType<?> dataComponentType2 = entry.getKey();
//                                DataComponentType.STREAM_CODEC.encode(registryFriendlyByteBuf, dataComponentType2);
//                            }
//                        }
//
//                    }
//                }
//
//                private <T> void encodeComponent(RegistryFriendlyByteBuf registryFriendlyByteBuf, DataComponentType<T> dataComponentType, Object object) {
//                    codecGetter.apply(dataComponentType).encode(registryFriendlyByteBuf, (T) object);
//                }
//            };
//        }, 2, TimeUnit.SECONDS);
//    }
//
//    private static Codec<DataComponentPatch> makeFixedCodec() {
//        return Codec.dispatchedMap(DataComponentPatch.PatchKey.CODEC, DataComponentPatch.PatchKey::valueCodec)
//                .xmap((map) -> {
//                    if (map.isEmpty()) {
//                        return DataComponentPatch.EMPTY;
//                    } else {
//                        Reference2ObjectMap<DataComponentType<?>, Optional<?>> reference2ObjectMap = new Reference2ObjectArrayMap<>(map.size());
//
//                        for (Map.Entry<DataComponentPatch.PatchKey, ?> entry : map.entrySet()) {
//                            DataComponentPatch.PatchKey patchKey = entry.getKey();
//                            if (patchKey.removed()) {
//                                reference2ObjectMap.put(patchKey.type(), Optional.empty());
//                            } else {
//                                if (entry.getValue() == null) {
//                                    System.out.println("ITEM_COMPONENT: %s".formatted(map.toString()));
//                                    System.out.println("NULL_COMPONENT_KEY: %s".formatted(BuiltInRegistries.DATA_COMPONENT_TYPE.getId(patchKey.type())));
//                                }
//                                reference2ObjectMap.put(patchKey.type(), Optional.of(entry.getValue()));
//                            }
//                        }
//
//                        return new DataComponentPatch(reference2ObjectMap);
//                    }
//                }, (dataComponentPatch) -> {
//                    Map reference2ObjectMap = new Reference2ObjectArrayMap<>(dataComponentPatch.map.size());
//                    ObjectIterator<Reference2ObjectMap.Entry<DataComponentType<?>, Optional<?>>> var2 = Reference2ObjectMaps.fastIterable(dataComponentPatch.map).iterator();
//
//                    while (var2.hasNext()) {
//                        Map.Entry<DataComponentType<?>, Optional<?>> entry = var2.next();
//                        DataComponentType<?> dataComponentType = entry.getKey();
//                        if (!dataComponentType.isTransient()) {
//                            Optional<?> optional = entry.getValue();
//                            if (optional.isPresent()) {
//                                reference2ObjectMap.put(new DataComponentPatch.PatchKey(dataComponentType, false), optional.get());
//                            } else {
//                                reference2ObjectMap.put(new DataComponentPatch.PatchKey(dataComponentType, true), Unit.INSTANCE);
//                            }
//                        }
//                    }
//
//                    return reference2ObjectMap;
//                });
//    }
}
