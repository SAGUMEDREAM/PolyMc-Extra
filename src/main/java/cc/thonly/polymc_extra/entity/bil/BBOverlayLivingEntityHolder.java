package cc.thonly.polymc_extra.entity.bil;

import de.tomalbrc.bil.api.AnimatedEntity;
import de.tomalbrc.bil.core.element.CollisionElement;
import de.tomalbrc.bil.core.holder.wrapper.DisplayWrapper;
import de.tomalbrc.bil.core.model.Model;
import de.tomalbrc.bil.core.model.Pose;
import de.tomalbrc.bil.util.Constants;
import de.tomalbrc.bil.util.Utils;
import eu.pb4.polymer.virtualentity.api.elements.InteractionElement;
import eu.pb4.polymer.virtualentity.api.tracker.EntityTrackedData;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.*;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Leashable;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

@SuppressWarnings("unchecked")
public class BBOverlayLivingEntityHolder<E extends LivingEntity, A extends AnimatedEntity> extends BBOverlayEntityHolder<E, A> {
    protected final InteractionElement hitboxInteraction;
    protected final CollisionElement collisionElement;
    protected float deathAngle;
    protected float entityScale = 1F;

    public BBOverlayLivingEntityHolder(E entity, A animatedEntity, Model model) {
        super(entity, animatedEntity, model);

        this.hitboxInteraction = InteractionElement.redirect(entity);
        this.hitboxInteraction.setSendPositionUpdates(false);
        this.addElement(this.hitboxInteraction);

        this.collisionElement = CollisionElement.createWithRedirect(entity);
        this.collisionElement.setSendPositionUpdates(false);
        this.addElement(this.collisionElement);
    }

    @Override
    protected void onAsyncTick() {
        if (this.entity.deathTime > 0) {
            this.deathAngle = Math.min((float) Math.sqrt((this.entity.deathTime) / 20.0F * 1.6F), 1.f);
        }

        super.onAsyncTick();
    }

    @Override
    public void updateElement(ServerPlayer serverPlayer, DisplayWrapper<?> display, @Nullable Pose pose) {
        display.element().setYaw(this.entity.yBodyRot);
        super.updateElement(serverPlayer, display, pose);
    }

    @Override
    protected void applyPose(ServerPlayer serverPlayer, Pose pose, DisplayWrapper<?> display) {
        Vector3f translation = pose.translation();
        boolean isHead = display.isHead();
        boolean isDead = this.entity.deathTime > 0;

        if (isHead || isDead) {
            Quaternionf bodyRotation = new Quaternionf();
            if (isDead) {
                bodyRotation.rotateZ(-this.deathAngle * Mth.HALF_PI);
                translation.rotate(bodyRotation);
            }

            if (isHead) {
                bodyRotation.rotateY(Mth.DEG_TO_RAD * -Mth.rotLerp(0.5f, this.entity.yHeadRotO - this.entity.yBodyRotO, this.entity.yHeadRot - this.entity.yBodyRot));
                bodyRotation.rotateX(Mth.DEG_TO_RAD * Mth.lerp(0.5f, this.entity.xRotO, this.entity.getXRot()));
            }

            display.element().setLeftRotation(serverPlayer, bodyRotation.mul(pose.readOnlyLeftRotation()));
        } else {
            display.element().setLeftRotation(serverPlayer, pose.readOnlyLeftRotation());
        }

        if (this.entityScale != 1F) {
            translation.mul(this.entityScale);
            display.element().setScale(serverPlayer, pose.scale().mul(this.entityScale));
        } else {
            display.element().setScale(serverPlayer, pose.readOnlyScale());
        }

        display.element().setTranslation(serverPlayer, translation.sub(0, this.dimensions.height() - 0.01f, 0));
        display.element().setRightRotation(serverPlayer, pose.readOnlyRightRotation());

        display.element().startInterpolationIfDirty(serverPlayer);
    }

    @Override
    protected void startWatchingExtraPackets(ServerGamePacketListenerImpl player, Consumer<Packet<ClientGamePacketListener>> consumer) {
        super.startWatchingExtraPackets(player, consumer);

        for (var packet : Utils.updateClientInteraction(this.hitboxInteraction, this.dimensions)) {
            consumer.accept((Packet<ClientGamePacketListener>) packet);
        }

        if (this.entity.canBreatheUnderwater()) {
            consumer.accept(new ClientboundUpdateMobEffectPacket(this.collisionElement.getEntityId(), new MobEffectInstance(MobEffects.WATER_BREATHING, -1, 0, false, false), false));
        }

        if (this.entity instanceof Leashable leashable && leashable.getLeashData() != null && leashable.getLeashHolder() != null) {
            consumer.accept(new ClientboundSetEntityLinkPacket(this.entity, leashable.getLeashHolder()));
        }

        consumer.accept(new ClientboundSetPassengersPacket(this.entity));
    }

    @Override
    protected void addDirectPassengers(IntList passengers) {
        super.addDirectPassengers(passengers);
        passengers.add(this.hitboxInteraction.getEntityId());
        passengers.add(this.collisionElement.getEntityId());
    }

    @Override
    public int getDisplayVehicleId() {
        return this.hitboxInteraction.getEntityId();
    }

    @Override
    public int getVehicleId() {
        return this.hitboxInteraction.getEntityId();
    }

    @Override
    public int getCritParticleId() {
        return this.hitboxInteraction.getEntityId();
    }

    @Override
    public int getLeashedId() {
        return this.collisionElement.getEntityId();
    }

    @Override
    public int getEntityEventId() {
        return this.collisionElement.getEntityId();
    }

    @Override
    protected void updateCullingBox() {
        float scale = this.getScale();
        float width = scale * (this.dimensions.width() * 2);
        float height = -this.dimensions.height() - 1;

        for (int i = 0; i < this.bones.length; i++) {
            this.bones[i].element().setDisplaySize(width, height);
        }
    }

    @Override
    public void onDimensionsUpdated(EntityDimensions dimensions) {
        this.updateEntityScale(this.scale);
        super.onDimensionsUpdated(dimensions);

        var size = Utils.toSlimeSize(Math.min(dimensions.width(), dimensions.height()));
        if (size <= 0) {
            var attributeInstance = new AttributeInstance(Attributes.SCALE, (instance) -> {});
            attributeInstance.setBaseValue(0.01);
            var attributesPacket = new ClientboundUpdateAttributesPacket(this.collisionElement.getEntityId(), List.of(attributeInstance));
            this.sendPacket(attributesPacket);
            size = 1;
        }

        this.collisionElement.setSize(size);
        this.sendPacket(new ClientboundBundlePacket(Utils.updateClientInteraction(this.hitboxInteraction, dimensions)));
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key, Object object) {
        super.onSyncedDataUpdated(key, object);
        if (key.equals(Constants.DATA_EFFECT_PARTICLES)) {
            // noinspection unchecked
            this.collisionElement.getDataTracker().set(Constants.DATA_EFFECT_PARTICLES, (List<ParticleOptions>) object);
        }

        if (key.equals(EntityTrackedData.NAME_VISIBLE)) {
            this.hitboxInteraction.setCustomNameVisible((boolean) object);
        }

        if (key.equals(EntityTrackedData.CUSTOM_NAME)) {
            // noinspection unchecked
            this.hitboxInteraction.getDataTracker().set(EntityTrackedData.CUSTOM_NAME, (Optional<Component>) object);
        }
    }

    @Override
    protected void updateOnFire(boolean displayFire) {
        this.hitboxInteraction.setOnFire(displayFire);
        super.updateOnFire(displayFire);
    }

    @Override
    protected void updateInvisibility(boolean isInvisible) {
        this.hitboxInteraction.setInvisible(isInvisible);
        super.updateInvisibility(isInvisible);
    }

    @Override
    public float getScale() {
        return this.entityScale;
    }

    @Override
    public void setScale(float scale) {
        this.updateEntityScale(scale);
        super.setScale(scale);
    }

    protected void updateEntityScale(float scalar) {
        this.entityScale = this.entity.getScale() * scalar;
    }

    @Override
    public SoundSource getSoundSource() {
        return SoundSource.NEUTRAL;
    }
}
