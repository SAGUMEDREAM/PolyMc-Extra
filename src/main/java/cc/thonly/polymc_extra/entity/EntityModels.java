package cc.thonly.polymc_extra.entity;

import eu.pb4.factorytools.api.virtualentity.emuvanilla.PolyModelInstance;
import eu.pb4.factorytools.api.virtualentity.emuvanilla.model.EntityModel;
import eu.pb4.factorytools.api.virtualentity.emuvanilla.model.ModelPart;
import eu.pb4.factorytools.api.virtualentity.emuvanilla.model.TexturedModelData;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.function.Function;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;

public class EntityModels {
    public static final List<PolyModelInstance<?>> ALL = new ArrayList<>();
    public static final IdentityHashMap<EntityType<?>, PolyModelInstance<?>> BY_TYPE = new IdentityHashMap<>();

    public static <T extends EntityModel<?>> PolyModelInstance<T> create(Function<ModelPart, T> modelCreator, TexturedModelData data, Identifier texture) {
        var instance = PolyModelInstance.create(modelCreator, data, texture);
        ALL.add(instance);
        return instance;
    }

    public static <T extends EntityModel<?>> PolyModelInstance<T> withTexture(PolyModelInstance<T> original, Identifier texture) {
        var instance = original.withTexture(texture);
        ALL.add(instance);
        return instance;
    }

    public static PolyModelInstance<?> registerEntityTypeModel(EntityType<?> entityType, PolyModelInstance<?> model) {
        BY_TYPE.put(entityType, model);
        return model;
    }
}
