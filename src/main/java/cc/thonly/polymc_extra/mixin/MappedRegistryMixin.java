package cc.thonly.polymc_extra.mixin;

import cc.thonly.polymc_extra.PolyMcExtra;
import cc.thonly.polymc_extra.interfaces.IMappedRegistry;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.Reference2IntMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.Map;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.RegistrationInfo;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

@Mixin(MappedRegistry.class)
public abstract class MappedRegistryMixin<T> implements IMappedRegistry<T> {
    @Shadow private boolean frozen;

    @Shadow @Nullable private Map<T, Holder.Reference<T>> unregisteredIntrusiveHolders;

    @Shadow @Nullable public abstract ResourceLocation getKey(T value);

    @Shadow @Final private Map<ResourceLocation, Holder.Reference<T>> byLocation;

    @Shadow public abstract Holder<T> wrapAsHolder(T value);

    @Shadow @Final private Map<T, Holder.Reference<T>> byValue;

    @Shadow @Final private Map<ResourceKey<T>, Holder.Reference<T>> byKey;

    @Shadow public abstract Optional<ResourceKey<T>> getResourceKey(T entry);

    @Shadow @Final private ObjectList<Holder.Reference<T>> byId;

    @Shadow @Final private Reference2IntMap<T> toId;

    @Shadow @Final private Map<ResourceKey<T>, RegistrationInfo> registrationInfos;

    @Shadow public abstract int getId(@Nullable T value);

    @Override
    @Unique
    public void unfreeze() {
        if (this.frozen) {
            this.unregisteredIntrusiveHolders = new Reference2ObjectOpenHashMap<>();
            this.frozen = false;
        }
    }

    @Override
    @Unique
    public void remove(T value) {
        ResourceLocation id = this.getKey(value);
        Optional<ResourceKey<T>> keyOptional = this.getResourceKey(value);
        if (keyOptional.isEmpty()) {
            return;
        }

        ResourceKey<T> registryKey = keyOptional.get();
        Holder<T> entry = this.wrapAsHolder(value);
        int rawId = this.getId(value);

        this.byLocation.remove(id);
        this.byValue.remove(value);
        if (this.unregisteredIntrusiveHolders != null) {
            this.unregisteredIntrusiveHolders.remove(value);
        }
        this.byKey.remove(registryKey);
        this.byLocation.remove(registryKey.location());
        this.toId.removeInt(entry);
        this.registrationInfos.remove(registryKey);

        if (rawId >= 0 && rawId < this.byId.size()) {
            this.byId.remove(rawId);
        } else {
            PolyMcExtra.getLog().warn(
                    "Tried to remove entry {} with invalid rawId {} (list size={})",
                    id, rawId, this.byId.size()
            );
        }
    }

}
