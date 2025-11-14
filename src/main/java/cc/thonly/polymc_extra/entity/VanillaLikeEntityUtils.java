package cc.thonly.polymc_extra.entity;

import cc.thonly.polymc_extra.mixin.accessor.EntityAccessor;
import cc.thonly.polymc_extra.mixin.accessor.ThrowableItemProjectileAccessor;
import eu.pb4.polymer.common.impl.entity.InternalEntityHelpers;
import eu.pb4.polymer.core.api.entity.PolymerEntity;
import eu.pb4.polymer.core.api.utils.PolymerUtils;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.AbstractFish;
import net.minecraft.world.entity.animal.FlyingAnimal;
import net.minecraft.world.entity.animal.horse.AbstractChestedHorse;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.piglin.AbstractPiglin;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.item.ItemStack;
import xyz.nucleoid.packettweaker.PacketContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

@SuppressWarnings("UnstableApiUsage")
public class VanillaLikeEntityUtils {
    public static final Object2ObjectLinkedOpenHashMap<Class<?>, EntityType<?>> MAP = new Object2ObjectLinkedOpenHashMap<>();
    private static boolean freeze = false;

    public static void generateMap() {
        if (freeze) {
            return;
        }
        for (EntityType<?> entityType : BuiltInRegistries.ENTITY_TYPE) {
            ResourceLocation key = BuiltInRegistries.ENTITY_TYPE.getKey(entityType);
            if (!key.getNamespace().equalsIgnoreCase("minecraft")) {
                continue;
            }
            if (PolymerUtils.isServerOnly(BuiltInRegistries.ENTITY_TYPE, entityType)) {
                continue;
            }
            if (entityType == EntityType.PLAYER) {
                continue;
            }
            if (entityType == EntityType.WIND_CHARGE) {
                continue;
            }
            if (entityType == EntityType.BREEZE_WIND_CHARGE) {
                continue;
            }
            Class<?> entityClass = InternalEntityHelpers.getEntityClass(entityType);
            if (entityClass != null && !MAP.containsKey(entityClass)) {
                MAP.put(entityClass, entityType);
            }
        }
        ArrayList<Map.Entry<Class<?>, EntityType<?>>> arrayList = new ArrayList<>(MAP.object2ObjectEntrySet());
        arrayList.sort((a, b) -> {
            Class<?> A = a.getKey();
            Class<?> B = b.getKey();
            if (A == B) {
                // A is a super type of B, sort it higher
                return 0;
            }
            if (A.isAssignableFrom(B)) {
                return 1;
            } else {
                // B is a super type of A
                return -1;
            }
        });

        MAP.clear();
        for (Map.Entry<Class<?>, EntityType<?>> entry : arrayList) {
            MAP.put(entry.getKey(), entry.getValue());
        }

        MAP.put(AbstractChestedHorse.class, EntityType.DONKEY);
        MAP.put(AbstractHorse.class, EntityType.HORSE);
        MAP.put(AbstractPiglin.class, EntityType.PIGLIN);
        MAP.put(AbstractSkeleton.class, EntityType.SKELETON);
        MAP.put(AbstractMinecart.class, EntityType.MINECART);
        MAP.put(Projectile.class, EntityType.ARROW);
        MAP.put(AbstractFish.class, EntityType.COD);
        MAP.put(FlyingAnimal.class, EntityType.PARROT);

        freeze = true;
    }

    public static <E extends Entity> Function<E, PolymerEntity> findLikePolymerEntityConstructor(EntityType<E> entityType) {
        Class<E> entityClass = InternalEntityHelpers.getEntityClass(entityType);
        EntityType<?> likeEntityType = findLikeEntityType(entityType);
        if (likeEntityType != null) {
            return entity -> new PolymerEntity() {
                @Override
                public EntityType<?> getPolymerEntityType(PacketContext packetContext) {
                    return likeEntityType;
                }

                @Override
                public void modifyRawTrackedData(List<SynchedEntityData.DataValue<?>> data, ServerPlayer player, boolean initial) {
                    PolymerEntity.super.modifyRawTrackedData(data, player, initial);
                    if (initial) {
                        data.add(SynchedEntityData.DataValue.create(EntityAccessor.getDataCustomName(), Optional.of(entity.getName())));
                    }
                }
            };
        }
        if (ItemSupplier.class.isAssignableFrom(entityClass)) {
            return entity -> new PolymerEntity() {
                @Override
                public EntityType<?> getPolymerEntityType(PacketContext packetContext) {
                    return EntityType.SNOWBALL;
                }

                @Override
                public void modifyRawTrackedData(List<SynchedEntityData.DataValue<?>> data, ServerPlayer player, boolean initial) {
                    PolymerEntity.super.modifyRawTrackedData(data, player, initial);
                    if (initial) {
                        if (entity instanceof ItemSupplier itemSupplier) {
                            ItemStack itemStack = itemSupplier.getItem();
                            data.add(SynchedEntityData.DataValue.create(ThrowableItemProjectileAccessor.getDataItemStack(), itemStack));
                        }
                    }
                }
            };
        }
        if (LivingEntity.class.isAssignableFrom(entityClass)) {
            if (entityType.getHeight() > 1.5) {
                if (Enemy.class.isAssignableFrom(entityClass)) {
                    return entity -> context -> EntityType.ZOMBIE;
                } else {
                    return entity -> context -> EntityType.ARMOR_STAND;
                }
            } else if (entityType.getHeight() > 0.5) {
                return entity -> context -> EntityType.PIG;
            } else {
                return entity -> context -> EntityType.SILVERFISH;
            }
        }
        return (entity) -> new PolymerEntity() {
            @Override
            public EntityType<?> getPolymerEntityType(PacketContext packetContext) {
                return EntityType.BLOCK_DISPLAY;
            }

            @Override
            public void modifyRawTrackedData(List<SynchedEntityData.DataValue<?>> data, ServerPlayer player, boolean initial) {
                PolymerEntity.super.modifyRawTrackedData(data, player, initial);
                if (initial) {
                    data.add(SynchedEntityData.DataValue.create(EntityAccessor.getDataCustomName(), Optional.of(entity.getName())));
                }
            }
        };
    }

    public static <E extends Entity> EntityType<?> findLikeEntityType(EntityType<E> type) {
        Class<E> entityClass = InternalEntityHelpers.getEntityClass(type);
        if (MAP.containsKey(entityClass)) {
            return MAP.get(entityClass);
        }
        var set = MAP.entrySet();
        for (var mapEntry : set) {
            if (entityClass.isAssignableFrom(mapEntry.getKey())) {
                return mapEntry.getValue();
            }
        }
        return null;
    }
}
