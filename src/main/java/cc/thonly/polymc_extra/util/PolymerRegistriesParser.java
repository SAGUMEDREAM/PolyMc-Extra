package cc.thonly.polymc_extra.util;

import cc.thonly.polymc_extra.PolyMcExtra;
import cc.thonly.polymc_extra.api.PolyMcExtraGui;
import cc.thonly.polymc_extra.config.PolyMcExtraConfigService;
import cc.thonly.polymc_extra.config.PolyMcExtraConfig;
import cc.thonly.polymc_extra.data.PolyMcExtraPacks;
import eu.pb4.polymer.core.api.block.PolymerBlock;
import eu.pb4.polymer.core.api.block.PolymerBlockUtils;
import eu.pb4.polymer.core.api.other.*;
import eu.pb4.polymer.core.api.utils.PolymerUtils;
import eu.pb4.polymer.rsm.api.RegistrySyncUtils;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.stats.StatFormatter;
import net.minecraft.stats.StatType;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.consume_effects.ConsumeEffect;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import java.lang.reflect.Field;
import java.util.*;

@Slf4j
@SuppressWarnings("rawtypes")
public class PolymerRegistriesParser {
    public static final Set<Item> VANILLA_ITEMS = new LinkedHashSet<>();
    public static final Set<Block> VANILLA_BLOCKS = new LinkedHashSet<>();
    public static final Set<BlockEntityType> VANILLA_BLOCK_ENTITY_TYPES = new LinkedHashSet<>();
    public static final Set<EntityType> VANILLA_ENTITY_TYPES = new LinkedHashSet<>();
    public static final Set<SoundEvent> VANILLA_SOUND_EVENTS = new LinkedHashSet<>();
    public static final Set<DataComponentType> VANILLA_COMPONENT_TYPES = new LinkedHashSet<>();
    public static final Set<DataComponentType> VANILLA_ENCHANTMENT_EFFECT_COMPONENT_TYPE = new LinkedHashSet<>();
    public static final Set<MobEffect> VANILLA_STATUS_EFFECTS = new LinkedHashSet<>();
    public static final Set<Potion> VANILLA_POTIONS = new LinkedHashSet<>();
    public static final Set<MenuType> VANILLA_SCREEN_HANDLERS = new LinkedHashSet<>();
    public static final Set<ConsumeEffect.Type> VANILLA_CONSUME_EFFECT_TYPES = new LinkedHashSet<>();
    public static final Set<StatType> VANILLA_STAT_TYPES = new LinkedHashSet<>();
    public static final Set<ResourceLocation> VANILLA_CUSTOM_STATS = new LinkedHashSet<>();
    public static final Set<CreativeModeTab> VANILLA_ITEM_GROUPS = new LinkedHashSet<>();
    public static final Set<MenuType> REPLACEABLE_SCREEN_HANDLERS = new LinkedHashSet<>();

    public static void parseVanillaRegistryEntries() {
        PolyMcExtra.getLog().info("Loading polymc-extra configs...");
        PolyMcExtraConfig config = PolyMcExtraConfig.getConfig();
        PolyMcExtraConfigService service = config.getService();
        PolyMcExtra.getLog().info("Loading custom block mappings...");
        service.startCustomBlockMappings();
        PolyMcExtra.getLog().info("Loading custom entity model mappings");
        service.startCustomEntityModelMappings();
        PolyMcExtra.getLog().info("Parsing the registries");

        // Items
        BuiltInRegistries.ITEM.stream()
                .filter(item -> BuiltInRegistries.ITEM.getKey(item).getNamespace().equals("minecraft"))
                .forEach(VANILLA_ITEMS::add);

        // Blocks
        BuiltInRegistries.BLOCK.stream()
                .filter(block -> BuiltInRegistries.BLOCK.getKey(block).getNamespace().equals("minecraft"))
                .forEach(VANILLA_BLOCKS::add);

        // BlockEntityTypes
        BuiltInRegistries.BLOCK_ENTITY_TYPE.stream()
                .filter(type -> Objects.requireNonNull(BuiltInRegistries.BLOCK_ENTITY_TYPE.getKey(type)).getNamespace().equals("minecraft"))
                .forEach(VANILLA_BLOCK_ENTITY_TYPES::add);

        // EntityTypes
        BuiltInRegistries.ENTITY_TYPE.stream()
                .filter(type -> BuiltInRegistries.ENTITY_TYPE.getKey(type).getNamespace().equals("minecraft"))
                .forEach(VANILLA_ENTITY_TYPES::add);

        // SoundEvents
        BuiltInRegistries.SOUND_EVENT.stream()
                .filter(se -> Objects.requireNonNull(BuiltInRegistries.SOUND_EVENT.getKey(se)).getNamespace().equals("minecraft"))
                .forEach(VANILLA_SOUND_EVENTS::add);

        // StatusEffects
        BuiltInRegistries.MOB_EFFECT.stream()
                .filter(se -> Objects.requireNonNull(BuiltInRegistries.MOB_EFFECT.getKey(se)).getNamespace().equals("minecraft"))
                .forEach(VANILLA_STATUS_EFFECTS::add);

        // ComponentTypes
        BuiltInRegistries.DATA_COMPONENT_TYPE.stream()
                .filter(ct -> Objects.requireNonNull(BuiltInRegistries.DATA_COMPONENT_TYPE.getKey(ct)).getNamespace().equals("minecraft"))
                .forEach(VANILLA_COMPONENT_TYPES::add);

        BuiltInRegistries.ENCHANTMENT_EFFECT_COMPONENT_TYPE.stream()
                .filter(ct -> Objects.requireNonNull(BuiltInRegistries.ENCHANTMENT_EFFECT_COMPONENT_TYPE.getKey(ct)).getNamespace().equals("minecraft"))
                .forEach(VANILLA_ENCHANTMENT_EFFECT_COMPONENT_TYPE::add);

        // Potions
        BuiltInRegistries.POTION.stream()
                .filter(potion -> Objects.requireNonNull(BuiltInRegistries.POTION.getKey(potion)).getNamespace().equals("minecraft"))
                .forEach(VANILLA_POTIONS::add);

        // ScreenHandlerTypes
        BuiltInRegistries.MENU.stream()
                .filter(sht -> Objects.requireNonNull(BuiltInRegistries.MENU.getKey(sht)).getNamespace().equals("minecraft"))
                .forEach(VANILLA_SCREEN_HANDLERS::add);

        // ConsumeEffect.Types
        BuiltInRegistries.CONSUME_EFFECT_TYPE.stream()
                .filter(ct -> Objects.requireNonNull(BuiltInRegistries.CONSUME_EFFECT_TYPE.getKey(ct)).getNamespace().equals("minecraft"))
                .forEach(VANILLA_CONSUME_EFFECT_TYPES::add);

        // StatTypes
        BuiltInRegistries.STAT_TYPE.stream()
                .filter(st -> Objects.requireNonNull(BuiltInRegistries.STAT_TYPE.getKey(st)).getNamespace().equals("minecraft"))
                .forEach(VANILLA_STAT_TYPES::add);

        // Custom Stats
        BuiltInRegistries.CUSTOM_STAT.stream()
                .filter(id -> id.getNamespace().equals("minecraft"))
                .forEach(VANILLA_CUSTOM_STATS::add);

        // Item Groups
        BuiltInRegistries.CREATIVE_MODE_TAB.stream()
                .filter(group -> Objects.requireNonNull(BuiltInRegistries.CREATIVE_MODE_TAB.getKey(group)).getNamespace().equals("minecraft"))
                .forEach(VANILLA_ITEM_GROUPS::add);
    }

    public static void parseAll(Registry<? extends Registry<?>> registries) {
        parseVanillaRegistryEntries();
        for (Registry<?> registry : registries) {
            for (Object object : registry) {
                tryRegisterOverlay(registry, object);
            }
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static void markNamespace(Registry registry, Object object) {
        ResourceLocation id = registry.getKey(object);
        if (id != null) {
            PolyMcExtraPacks.NAMESPACES.add(id.getNamespace());
        }
    }

    @SuppressWarnings("unchecked")
    public static void tryRegisterOverlay(Registry<?> registry, Object object) {
        if (object instanceof Item item) {
            boolean isServerOnly = PolymerUtils.isServerOnly(BuiltInRegistries.ITEM, item);
            boolean isVanillaObject = VANILLA_ITEMS.contains(item);
            if (isServerOnly || isVanillaObject) {
                return;
            }
            PolymerItemHelper.registerOverlay(item);
            markNamespace(registry, object);
        }
        if (object instanceof Block block) {
            boolean isServerOnly = PolymerUtils.isServerOnly(BuiltInRegistries.BLOCK, block);
            boolean isVanillaObject = VANILLA_BLOCKS.contains(block);
            if (isServerOnly || isVanillaObject) {
                return;
            }
            PolymerBlock polymerBlock = PolymerBlockHelper.registerPolymerBlock(block);
            PolymerBlockHelper.registerHolder(block, polymerBlock);
            markNamespace(registry, object);
        }
        if (object instanceof BlockEntityType blockEntityType) {
            boolean isServerOnly = PolymerUtils.isServerOnly(BuiltInRegistries.BLOCK_ENTITY_TYPE, blockEntityType);
            boolean isVanillaObject = VANILLA_BLOCK_ENTITY_TYPES.contains(blockEntityType);
            if (isServerOnly || isVanillaObject) {
                return;
            }
            PolymerBlockUtils.registerBlockEntity(blockEntityType);
            markNamespace(registry, object);
        }
        if (object instanceof SoundEvent soundEvent) {
            boolean isServerOnly = PolymerUtils.isServerOnly(BuiltInRegistries.SOUND_EVENT, soundEvent);
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
        if (object instanceof DataComponentType componentType && registry == BuiltInRegistries.DATA_COMPONENT_TYPE) {
            boolean isServerOnly = PolymerUtils.isServerOnly(BuiltInRegistries.DATA_COMPONENT_TYPE, componentType);
            boolean isVanillaObject = VANILLA_COMPONENT_TYPES.contains(componentType);
            if (isServerOnly || isVanillaObject) {
                return;
            }
            PolymerComponent.registerDataComponent(componentType);
            markNamespace(registry, object);
        }
        if (object instanceof DataComponentType componentType && registry == BuiltInRegistries.ENCHANTMENT_EFFECT_COMPONENT_TYPE) {
            boolean isServerOnly = PolymerUtils.isServerOnly(BuiltInRegistries.ENCHANTMENT_EFFECT_COMPONENT_TYPE, componentType);
            boolean isVanillaObject = VANILLA_ENCHANTMENT_EFFECT_COMPONENT_TYPE.contains(componentType);
            if (isServerOnly || isVanillaObject) {
                return;
            }
            PolymerComponent.registerEnchantmentEffectComponent(componentType);
            markNamespace(registry, object);
        }
        if (object instanceof MobEffect statusEffect) {
            boolean isServerOnly = PolymerUtils.isServerOnly(BuiltInRegistries.MOB_EFFECT, statusEffect);
            boolean isVanillaObject = VANILLA_STATUS_EFFECTS.contains(statusEffect);
            if (isServerOnly || isVanillaObject) {
                return;
            }
            PolymerStatusEffect.registerOverlay(statusEffect);
            markNamespace(registry, object);
        }
        if (object instanceof Potion potion) {
            boolean isServerOnly = PolymerUtils.isServerOnly(BuiltInRegistries.POTION, potion);
            boolean isVanillaObject = VANILLA_POTIONS.contains(potion);
            if (isServerOnly || isVanillaObject) {
                return;
            }
            RegistrySyncUtils.setServerEntry(BuiltInRegistries.POTION, potion);
            markNamespace(registry, object);
        }
        if (object instanceof MenuType menuType) {
            boolean isServerOnly = PolymerUtils.isServerOnly(BuiltInRegistries.MENU, menuType);
            boolean isVanillaObject = VANILLA_SCREEN_HANDLERS.contains(menuType);
            if (isServerOnly || isVanillaObject) {
                return;
            }
            PolyMcExtraGui.register(menuType);
//            REPLACEABLE_SCREEN_HANDLERS.add(screenHandlerType);
//            PolymerScreenHandlerUtils.registerType(screenHandlerType);
            markNamespace(registry, object);
        }
        if (object instanceof ConsumeEffect.Type type) {
            boolean isServerOnly = PolymerUtils.isServerOnly(BuiltInRegistries.CONSUME_EFFECT_TYPE, type);
            boolean isVanillaObject = VANILLA_CONSUME_EFFECT_TYPES.contains(type);
            if (isServerOnly || isVanillaObject) {
                return;
            }
            PolymerConsumeEffect.registerConsumeEffect(type);
            markNamespace(registry, object);
        }
        if (object instanceof StatType type) {
            boolean isServerOnly = PolymerUtils.isServerOnly(BuiltInRegistries.STAT_TYPE, type);
            boolean isVanillaObject = VANILLA_STAT_TYPES.contains(type);
            if (isServerOnly || isVanillaObject) {
                return;
            }
            ResourceLocation id = BuiltInRegistries.STAT_TYPE.getKey(type);
            if (id != null) {
                PolymerStat.registerStat(id, type.getDisplayName(), StatFormatter.DEFAULT);
                markNamespace(registry, object);
            }
        }
        if (object instanceof ResourceLocation identifier && registry == BuiltInRegistries.CUSTOM_STAT) {
            boolean isServerOnly = PolymerUtils.isServerOnly(BuiltInRegistries.CUSTOM_STAT, identifier);
            boolean isVanillaObject = VANILLA_CUSTOM_STATS.contains(identifier);
            if (isServerOnly || isVanillaObject) {
                return;
            }
            RegistrySyncUtils.setServerEntry((Registry<Object>) (Object) BuiltInRegistries.CUSTOM_STAT, (Object) identifier);
            try {
                Field declaredField = PolymerStat.class.getDeclaredField("NAMES");
                declaredField.setAccessible(true);
                Map<ResourceLocation, Component> obj = (Map<ResourceLocation, Component>) declaredField.get(null);
                obj.put(identifier, Component.translatable("stat." + identifier.toString().replace(':', '.')));
            } catch (Exception err) {
                log.error("can't get field in PolymerStat.class:", err);
            }
            markNamespace(registry, object);
        }
//        if (object instanceof ItemGroup itemGroup) {
//            boolean isServerOnly = PolymerUtils.isServerOnly(Registries.ITEM_GROUP, itemGroup);
//            boolean isPolymerify = PolymerItemGroupUtils.isPolymerItemGroup(itemGroup);
//            boolean isVanillaObject = VANILLA_ITEM_GROUPS.contains(itemGroup);
//            if (isServerOnly || isPolymerify || isVanillaObject) {
//                return;
//            }
//            try {
//                Identifier id = Registries.ITEM_GROUP.getId(itemGroup);
//                ISimpleRegistry iSimpleRegistry = (ISimpleRegistry) registry;
//                iSimpleRegistry.remove(itemGroup);
//                PolymerItemGroupUtils.registerPolymerItemGroup(id, itemGroup);
//            } catch (Exception e) {
//                log.error("Can't patch item group: {}", itemGroup, e);
//            }
//        }
    }
}
