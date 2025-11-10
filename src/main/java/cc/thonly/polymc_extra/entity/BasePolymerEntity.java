package cc.thonly.polymc_extra.entity;

import eu.pb4.factorytools.api.virtualentity.emuvanilla.PolyModelInstance;
import eu.pb4.factorytools.api.virtualentity.emuvanilla.model.EntityModel;
import eu.pb4.factorytools.api.virtualentity.emuvanilla.poly.ScalingEntityModel;
import eu.pb4.factorytools.api.virtualentity.emuvanilla.poly.SimpleEntityModel;
import eu.pb4.polymer.core.api.entity.PolymerEntity;
import eu.pb4.polymer.virtualentity.api.VirtualEntityUtils;
import eu.pb4.polymer.virtualentity.api.attachment.IdentifiedUniqueEntityAttachment;
import eu.pb4.polymer.virtualentity.api.attachment.UniqueIdentifiableAttachment;
import eu.pb4.polymer.virtualentity.api.tracker.DisplayTrackedData;
import it.unimi.dsi.fastutil.ints.IntList;
import lombok.Getter;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAnimatePacket;
import net.minecraft.network.protocol.game.ClientboundEntityEventPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityLinkPacket;
import net.minecraft.network.protocol.game.ClientboundSetPassengersPacket;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import xyz.nucleoid.packettweaker.PacketContext;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

@SuppressWarnings({"rawtypes","unchecked"})
@Getter
public class BasePolymerEntity implements PolymerEntity {
    public static final ElementHolderFactory<?> SCALING_ENTITY_MODEL = ScalingEntityModel::new;
    public static final ElementHolderFactory<?> SIMPLE_ENTITY_MODEL = SimpleEntityModel::new;
    private final LivingEntity entity;
    private final ElementHolderFactory factory;
    private final ResourceLocation model;

    public BasePolymerEntity(LivingEntity entity,
                             ElementHolderFactory factory) {
        ResourceLocation id = BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType());
        this.entity = entity;
        this.factory = factory;
        this.model = ResourceLocation.fromNamespaceAndPath(id.getNamespace(), "model");
        var model = (PolyModelInstance<EntityModel<LivingEntity>>) EntityModels.BY_TYPE.get(entity.getType());
        if (model != null) {
            IdentifiedUniqueEntityAttachment.ofTicking(this.model, this.factory.get(entity, model), entity);
        }
    }

    @Override
    public void onEntityPacketSent(Consumer<Packet<?>> consumer, Packet<?> packet) {
        if (packet instanceof ClientboundAnimatePacket) {
            return;
        }
        if (packet instanceof ClientboundSetPassengersPacket packet1 && packet1.getPassengers().length != 0) {
            var model = (SimpleEntityModel<?>) Objects.requireNonNull(UniqueIdentifiableAttachment.get(entity, this.model)).holder();
            consumer.accept(VirtualEntityUtils.createRidePacket(entity.getId(), IntList.of(model.rideAttachment.getEntityId())));
            consumer.accept(VirtualEntityUtils.createRidePacket(model.rideAttachment.getEntityId(), packet1.getPassengers()));
            return;
        }

        if (packet instanceof ClientboundSetEntityLinkPacket packet1) {
            var model = (SimpleEntityModel<?>) Objects.requireNonNull(UniqueIdentifiableAttachment.get(entity, this.model)).holder();
            consumer.accept(VirtualEntityUtils.createEntityAttachPacket(model.leadAttachment.getEntityId(), packet1.getDestId()));
            return;
        }

        if (packet instanceof ClientboundEntityEventPacket packet1 && PacketHandler.emulateHandleStatus(this.entity, packet1.getEventId())) {
            return;
        }

        PolymerEntity.super.onEntityPacketSent(consumer, packet);
    }

    @Override
    public EntityType<?> getPolymerEntityType(PacketContext packetContext) {
        return EntityType.ITEM_DISPLAY;
    }

    @Override
    public void modifyRawTrackedData(List<SynchedEntityData.DataValue<?>> data, ServerPlayer player, boolean initial) {
        PolymerEntity.super.modifyRawTrackedData(data, player, initial);
        if (initial) {
            data.add(SynchedEntityData.DataValue.create(DisplayTrackedData.TELEPORTATION_DURATION, 3));
        }
    }

    @FunctionalInterface
    public interface ElementHolderFactory<T extends LivingEntity> {
        SimpleEntityModel<T> get(T entity, PolyModelInstance<EntityModel<T>> model);
    }
}