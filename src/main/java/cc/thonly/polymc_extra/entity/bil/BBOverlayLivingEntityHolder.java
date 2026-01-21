package cc.thonly.polymc_extra.entity.bil;

import de.tomalbrc.bil.api.AnimatedEntity;
import de.tomalbrc.bil.core.element.CollisionElement;
import de.tomalbrc.bil.core.holder.wrapper.Bone;
import de.tomalbrc.bil.core.holder.wrapper.DisplayWrapper;
import de.tomalbrc.bil.core.model.Model;
import de.tomalbrc.bil.core.model.Node;
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
import org.joml.Vector3fc;

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

    protected Vector3fc getModelSpaceOrigin(ServerPlayer player, Node node) {
        Bone<?> bone = this.getBone(node);
        return bone == null ? null : bone.getLastPose(player).translation();
    }

    public static @Nullable Node findHeadNode(Node node) {
        Node res = null;

        for(Node current = node; current != null; current = current.parent()) {
            if (current.tag() == Node.NodeTag.HEAD) {
                res = current;
            }
        }

        return res;
    }

    @Override
    protected void applyPose(ServerPlayer serverPlayer, Pose pose, DisplayWrapper<?> displayWrapper) {
        Vector3f translation = new Vector3f(pose.translation());
        Quaternionf leftRotation = new Quaternionf(pose.readOnlyLeftRotation());

        Node node = displayWrapper.node();
        boolean isHead = node.tag() == Node.NodeTag.HEAD;
        boolean isHeadChild = node.tag() == Node.NodeTag.HEAD_CHILD;
        boolean isDead = this.entity.deathTime > 0;

        if (!isDead && (isHead || isHeadChild)) {
            Node headNode = isHead ? node : findHeadNode(node);

            if (headNode != null) {
                Vector3fc pivot = getModelSpaceOrigin(serverPlayer, headNode);
                if (pivot == null) {
                    pivot = node.transform().globalTransform().getTranslation(new Vector3f());
                }

                float yawDiff = this.entity.yHeadRot - this.entity.yBodyRot;
                float yawDiffO = this.entity.yHeadRotO - this.entity.yBodyRotO;
                float netYaw = Mth.rotLerp(0.5f, yawDiffO, yawDiff);
                float netPitch = Mth.lerp(0.5f, this.entity.xRotO, this.entity.getXRot());

                Quaternionf lookRotation = new Quaternionf()
                        .rotateY(Mth.DEG_TO_RAD * -netYaw)
                        .rotateX(Mth.DEG_TO_RAD * netPitch);

                lookRotation.mul(leftRotation, leftRotation);

                translation.sub(pivot).rotate(lookRotation).add(pivot);
            }
        }

        if (isDead) {
            Quaternionf deathRotation = new Quaternionf();
            deathRotation.rotateZ(-this.deathAngle * Mth.HALF_PI);
            translation.rotate(deathRotation);
            deathRotation.mul(leftRotation, leftRotation);
        }

        if (this.entityScale != 1F) {
            translation.mul(this.entityScale);
            displayWrapper.element().setScale(serverPlayer, pose.scale().mul(this.entityScale));
        } else {
            displayWrapper.element().setScale(serverPlayer, pose.readOnlyScale());
        }

        displayWrapper.element().setLeftRotation(serverPlayer, leftRotation);
        displayWrapper.element().setTranslation(serverPlayer, translation.sub(0, this.dimensions.height() - 0.01f, 0));
        displayWrapper.element().setRightRotation(serverPlayer, pose.readOnlyRightRotation());
        displayWrapper.element().startInterpolationIfDirty(serverPlayer);
    }

    @SuppressWarnings("unchecked")
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
