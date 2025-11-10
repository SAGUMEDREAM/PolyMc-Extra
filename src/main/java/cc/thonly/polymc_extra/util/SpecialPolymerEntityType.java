package cc.thonly.polymc_extra.util;

import eu.pb4.polymer.core.api.entity.PolymerEntity;
import net.minecraft.world.entity.EntityType;

@FunctionalInterface
@SuppressWarnings("rawtypes")
public interface SpecialPolymerEntityType {
    PolymerEntity get(EntityType type);
}