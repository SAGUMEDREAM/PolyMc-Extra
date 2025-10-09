package cc.thonly.polymc_extra.util;

import eu.pb4.polymer.core.api.block.PolymerBlockUtils;
import eu.pb4.polymer.core.api.other.*;
import eu.pb4.polymer.core.api.utils.PolymerUtils;
import eu.pb4.polymer.rsm.api.RegistrySyncUtils;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.component.ComponentType;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.item.Item;
import net.minecraft.item.consume.ConsumeEffect;
import net.minecraft.potion.Potion;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.sound.SoundEvent;
import net.minecraft.stat.StatFormatter;
import net.minecraft.stat.StatType;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.lang.reflect.Field;
import java.util.*;

@Slf4j
//@SuppressWarnings("rawtypes")
public class RegistriesUtil {
    public static final Set<Item> VANILLA_ITEMS = new LinkedHashSet<>();
    public static final Set<Block> VANILLA_BLOCKS = new LinkedHashSet<>();
    public static final Set<BlockEntityType> VANILLA_BLOCK_ENTITY_TYPES = new LinkedHashSet<>();
    public static final Set<EntityType> VANILLA_ENTITY_TYPES = new LinkedHashSet<>();
    public static final Set<SoundEvent> VANILLA_SOUND_EVENTS = new LinkedHashSet<>();
    public static final Set<ComponentType> VANILLA_COMPONENT_TYPES = new LinkedHashSet<>();
    public static final Set<ComponentType> VANILLA_ENCHANTMENT_EFFECT_COMPONENT_TYPE = new LinkedHashSet<>();
    public static final Set<StatusEffect> VANILLA_STATUS_EFFECTS = new LinkedHashSet<>();
    public static final Set<Potion> VANILLA_POTIONS = new LinkedHashSet<>();
    public static final Set<ScreenHandlerType> VANILLA_SCREEN_HANDLERS = new LinkedHashSet<>();
    public static final Set<ConsumeEffect.Type> VANILLA_CONSUME_EFFECT_TYPES = new LinkedHashSet<>();
    public static final Set<StatType> VANILLA_STAT_TYPES = new LinkedHashSet<>();
    public static final Set<Identifier> VANILLA_CUSTOM_STATS = new LinkedHashSet<>();

    public static void parseVanillaRegistryEntries() {
        // Items
        Registries.ITEM.stream()
                .filter(item -> Registries.ITEM.getId(item).getNamespace().equals("minecraft"))
                .forEach(VANILLA_ITEMS::add);

        // Blocks
        Registries.BLOCK.stream()
                .filter(block -> Registries.BLOCK.getId(block).getNamespace().equals("minecraft"))
                .forEach(VANILLA_BLOCKS::add);

        // BlockEntityTypes
        Registries.BLOCK_ENTITY_TYPE.stream()
                .filter(type -> Objects.requireNonNull(Registries.BLOCK_ENTITY_TYPE.getId(type)).getNamespace().equals("minecraft"))
                .forEach(VANILLA_BLOCK_ENTITY_TYPES::add);

        // EntityTypes
        Registries.ENTITY_TYPE.stream()
                .filter(type -> Registries.ENTITY_TYPE.getId(type).getNamespace().equals("minecraft"))
                .forEach(VANILLA_ENTITY_TYPES::add);

        // SoundEvents
        Registries.SOUND_EVENT.stream()
                .filter(se -> Objects.requireNonNull(Registries.SOUND_EVENT.getId(se)).getNamespace().equals("minecraft"))
                .forEach(VANILLA_SOUND_EVENTS::add);

        // StatusEffects
        Registries.STATUS_EFFECT.stream()
                .filter(se -> Objects.requireNonNull(Registries.STATUS_EFFECT.getId(se)).getNamespace().equals("minecraft"))
                .forEach(VANILLA_STATUS_EFFECTS::add);

        // ComponentTypes
        Registries.DATA_COMPONENT_TYPE.stream()
                .filter(ct -> Objects.requireNonNull(Registries.DATA_COMPONENT_TYPE.getId(ct)).getNamespace().equals("minecraft"))
                .forEach(VANILLA_COMPONENT_TYPES::add);

        Registries.ENCHANTMENT_EFFECT_COMPONENT_TYPE.stream()
                .filter(ct -> Objects.requireNonNull(Registries.ENCHANTMENT_EFFECT_COMPONENT_TYPE.getId(ct)).getNamespace().equals("minecraft"))
                .forEach(VANILLA_ENCHANTMENT_EFFECT_COMPONENT_TYPE::add);

        // Potions
        Registries.POTION.stream()
                .filter(potion -> Objects.requireNonNull(Registries.POTION.getId(potion)).getNamespace().equals("minecraft"))
                .forEach(VANILLA_POTIONS::add);

        // ScreenHandlerTypes
        Registries.SCREEN_HANDLER.stream()
                .filter(sht -> Objects.requireNonNull(Registries.SCREEN_HANDLER.getId(sht)).getNamespace().equals("minecraft"))
                .forEach(VANILLA_SCREEN_HANDLERS::add);

        // ConsumeEffect.Types
        Registries.CONSUME_EFFECT_TYPE.stream()
                .filter(ct -> Objects.requireNonNull(Registries.CONSUME_EFFECT_TYPE.getId(ct)).getNamespace().equals("minecraft"))
                .forEach(VANILLA_CONSUME_EFFECT_TYPES::add);

        // StatTypes
        Registries.STAT_TYPE.stream()
                .filter(st -> Objects.requireNonNull(Registries.STAT_TYPE.getId(st)).getNamespace().equals("minecraft"))
                .forEach(VANILLA_STAT_TYPES::add);

        // Custom Stats
        Registries.CUSTOM_STAT.stream()
                .filter(id -> id.getNamespace().equals("minecraft"))
                .forEach(VANILLA_CUSTOM_STATS::add);
    }

    public static void parseAll(Registry<? extends Registry<?>> registries) {
        parseVanillaRegistryEntries();
        for (Registry<?> registry : registries) {
            for (Object object : registry) {
                tryRegisterOverlay(registry, object);
            }
        }
    }

    public static void markNamespace(Registry registry, Object object) {
        Identifier id = registry.getId(object);
        if (id!=null) {
            PolyMcExtraPacks.NAMESPACES.add(id.getNamespace());
        }
    }

    public static void tryRegisterOverlay(Registry<?> registry, Object object) {
        if (object instanceof Item item) {
            boolean isServerOnly = PolymerUtils.isServerOnly(Registries.ITEM, item);
            boolean isVanillaObject = VANILLA_ITEMS.contains(item);
            if (isServerOnly || isVanillaObject) {
                return;
            }
            PolymerItemHelper.registerOverlay(item);
            markNamespace(registry, object);
        }
        if (object instanceof Block block) {
            boolean isServerOnly = PolymerUtils.isServerOnly(Registries.BLOCK, block);
            boolean isVanillaObject = VANILLA_BLOCKS.contains(block);
            if (isServerOnly || isVanillaObject) {
                return;
            }
            PolymerBlockHelper.registerPolymerBlock(block);
            markNamespace(registry, object);
        }
        if (object instanceof BlockEntityType blockEntityType) {
            boolean isServerOnly = PolymerUtils.isServerOnly(Registries.BLOCK_ENTITY_TYPE, blockEntityType);
            boolean isVanillaObject = VANILLA_BLOCK_ENTITY_TYPES.contains(blockEntityType);
            if (isServerOnly || isVanillaObject) {
                return;
            }
            PolymerBlockUtils.registerBlockEntity(blockEntityType);
            markNamespace(registry, object);
        }
        if (object instanceof SoundEvent soundEvent) {
            boolean isServerOnly = PolymerUtils.isServerOnly(Registries.SOUND_EVENT, soundEvent);
            boolean isVanillaObject = VANILLA_SOUND_EVENTS.contains(soundEvent);
            if (isServerOnly || isVanillaObject) {
                return;
            }
            PolymerSoundEvent.registerOverlay(soundEvent);
            markNamespace(registry, object);
        }
//        if (object instanceof EntityType entityType) {
//            boolean isServerOnly = PolymerUtils.isServerOnly(Registries.ENTITY_TYPE, entityType);
//            boolean isVanillaObject = VANILLA_ENTITY_TYPES.contains(entityType);
//            if (isServerOnly || isVanillaObject) {
//                return;
//            }
//            PolymerEntityHelper.registerOverlay(entityType);
//            markNamespace(registry, object);
//        }
        if (object instanceof ComponentType componentType && registry == Registries.DATA_COMPONENT_TYPE) {
            boolean isServerOnly = PolymerUtils.isServerOnly(Registries.DATA_COMPONENT_TYPE, componentType);
            boolean isVanillaObject = VANILLA_COMPONENT_TYPES.contains(componentType);
            if (isServerOnly || isVanillaObject) {
                return;
            }
            PolymerComponent.registerDataComponent(componentType);
            markNamespace(registry, object);
        }
        if (object instanceof ComponentType componentType && registry == Registries.ENCHANTMENT_EFFECT_COMPONENT_TYPE) {
            boolean isServerOnly = PolymerUtils.isServerOnly(Registries.ENCHANTMENT_EFFECT_COMPONENT_TYPE, componentType);
            boolean isVanillaObject = VANILLA_ENCHANTMENT_EFFECT_COMPONENT_TYPE.contains(componentType);
            if (isServerOnly || isVanillaObject) {
                return;
            }
            PolymerComponent.registerEnchantmentEffectComponent(componentType);
            markNamespace(registry, object);
        }
        if (object instanceof StatusEffect statusEffect) {
            boolean isServerOnly = PolymerUtils.isServerOnly(Registries.STATUS_EFFECT, statusEffect);
            boolean isVanillaObject = VANILLA_STATUS_EFFECTS.contains(statusEffect);
            if (isServerOnly || isVanillaObject) {
                return;
            }
            PolymerStatusEffect.registerOverlay(statusEffect);
            markNamespace(registry, object);
        }
        if (object instanceof Potion potion) {
            boolean isServerOnly = PolymerUtils.isServerOnly(Registries.POTION, potion);
            boolean isVanillaObject = VANILLA_POTIONS.contains(potion);
            if (isServerOnly || isVanillaObject) {
                return;
            }
            RegistrySyncUtils.setServerEntry(Registries.POTION, potion);
            markNamespace(registry, object);
        }
//        if (object instanceof ScreenHandlerType screenHandlerType) {
//            boolean isServerOnly = PolymerUtils.isServerOnly(Registries.SCREEN_HANDLER, screenHandlerType);
//            boolean isVanillaObject = VANILLA_SCREEN_HANDLERS.contains(screenHandlerType);
//            if (isServerOnly || isVanillaObject) {
//                return;
//            }
//            PolymerScreenHandlerUtils.registerType(screenHandlerType);
//            markNamespace(registry, object);
//        }
        if (object instanceof ConsumeEffect.Type type) {
            boolean isServerOnly = PolymerUtils.isServerOnly(Registries.CONSUME_EFFECT_TYPE, type);
            boolean isVanillaObject = VANILLA_CONSUME_EFFECT_TYPES.contains(type);
            if (isServerOnly || isVanillaObject) {
                return;
            }
            PolymerConsumeEffect.registerConsumeEffect(type);
            markNamespace(registry, object);
        }
        if (object instanceof StatType type) {
            boolean isServerOnly = PolymerUtils.isServerOnly(Registries.STAT_TYPE, type);
            boolean isVanillaObject = VANILLA_STAT_TYPES.contains(type);
            if (isServerOnly || isVanillaObject) {
                return;
            }
            Identifier id = Registries.STAT_TYPE.getId(type);
            if (id != null) {
                PolymerStat.registerStat(id, type.getName(), StatFormatter.DEFAULT);
                markNamespace(registry, object);
            }
        }
        if (object instanceof Identifier identifier && registry == Registries.CUSTOM_STAT) {
            boolean isServerOnly = PolymerUtils.isServerOnly(Registries.CUSTOM_STAT, identifier);
            boolean isVanillaObject = VANILLA_CUSTOM_STATS.contains(identifier);
            if (isServerOnly || isVanillaObject) {
                return;
            }
            RegistrySyncUtils.setServerEntry((Registry<Object>) (Object) Registries.CUSTOM_STAT, (Object) identifier);
            try {
                Field declaredField = PolymerStat.class.getDeclaredField("NAMES");
                declaredField.setAccessible(true);
                Map<Identifier, Text> obj = (Map<Identifier, Text>) declaredField.get(null);
                obj.put(identifier, Text.translatable("stat." + identifier.toString().replace(':', '.')));
            } catch (Exception err) {
                log.error("can't get field in PolymerStat.class:", err);
            }
            markNamespace(registry, object);
        }
    }
}
