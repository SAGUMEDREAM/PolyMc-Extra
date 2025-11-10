package cc.thonly.polymc_extra.entity.bil;

import cc.thonly.polymc_extra.api.IPolymerHolderEntity;
import cc.thonly.polymc_extra.api.ITickHolderEntity;
import cc.thonly.polymc_extra.util.AnimationHelper;
import de.tomalbrc.bil.api.AnimatedEntity;
import de.tomalbrc.bil.api.AnimatedEntityHolder;
import de.tomalbrc.bil.core.model.Model;
import eu.pb4.polymer.virtualentity.api.attachment.EntityAttachment;
import net.minecraft.world.entity.LivingEntity;

// Dont extend it, This is just one example for code learning!
abstract class BBOverlaySimpleExampleEntity<T extends LivingEntity> implements AnimatedEntity,IPolymerHolderEntity, ITickHolderEntity {
    // ModelUtil.loadModel(location: ResourceLocation)
    // model file path: jar://model/${namespace}/${id}.bbmodel
    // Don't load the model too late, otherwise you won't have any model materials! You should use this MODEL in your main class!
    public static final Model MODEl = null;
    private final T entity;
    private BBOverlayLivingEntityHolder<T, BBOverlaySimpleExampleEntity<T>> holder;

    private BBOverlaySimpleExampleEntity(T entity) {
        this.entity = entity;
        IPolymerHolderEntity.addEntityHolderModel(this);
    }

    @Override
    public void onCreated() {
        this.holder = new BBOverlayLivingEntityHolder<>(this.entity, this, MODEl);
        ITickHolderEntity.addTickHolder(this);
        ITickHolderEntity.addElementBind(this.entity, this.holder);
        EntityAttachment.ofTicking(this.holder, this.entity);
    }

    @Override
    public void onTick() {
        if (this.holder == null) {
            return;
        }
        if (this.entity.tickCount % 2 == 0) {
            // You can create your own animation helper class.
            AnimationHelper.updateWalkAnimation(this.entity, this.holder);
            AnimationHelper.updateHurtVariant(this.entity, this.holder);
        }
    }

    @Override
    public T getEntity() {
        return this.entity;
    }

    @Override
    public AnimatedEntityHolder getHolder() {
        return this.holder;
    }
}
