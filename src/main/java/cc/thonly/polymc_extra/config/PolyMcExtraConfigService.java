package cc.thonly.polymc_extra.config;

import cc.thonly.polymc_extra.model.ExtraModelType;
import cc.thonly.polymc_extra.data.PolyMcExtraPacks;
import cc.thonly.polymc_extra.util.SpecialPolymerEntityType;
import eu.pb4.polymer.core.api.block.PolymerBlock;
import eu.pb4.polymer.core.api.entity.PolymerEntity;
import eu.pb4.polymer.core.api.entity.PolymerEntityUtils;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.nucleoid.packettweaker.PacketContext;

import java.util.*;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

@Getter
@Slf4j
public class PolyMcExtraConfigService {
    // 预设实体覆盖层映射
    private static final Map<Identifier, SpecialPolymerEntityType> SPECIAL_ENTITY_MAPPINGS = new Object2ObjectLinkedOpenHashMap<>();
    // 数据层
    private final PolyMcExtraConfig data;
    // 关闭的切割面的方块ID列表 (这个列表没方块ID的情况下, 如果该方块覆盖层展示方式为 屏障 + 展示实体 的话会导致方块无光线, 表现方式为全黑)
    private final List<Identifier> disabledOpaqueBlockIds = new ArrayList<>();
    // 自定义方块类型映射 (方块配置中的预设类型映射)
    private final Map<Block, String> customMappingBlockIds = new Object2ObjectLinkedOpenHashMap<>();
    // 自定义方块映射
    private final Map<Block, PolymerBlock> customPolymerBlockMappings = new Object2ObjectLinkedOpenHashMap<>();
    // 方块扩展像素映射
    private final Map<Block, Double> customBlockExpansionMappings = new Object2ObjectLinkedOpenHashMap<>();
    // 记录绕过魔改 ScreenHandlerType 的类型ID，用于显示模组UI (注意: 无模组的玩家打开该 UI 将被迫掉线)
    private final List<Identifier> disabledHackScreenHandlerIds = new ArrayList<>();

    // 注册
    public static SpecialPolymerEntityType registerSpecialReplacedType(Identifier id, SpecialPolymerEntityType factory) {
        SPECIAL_ENTITY_MAPPINGS.put(id, factory);
        return factory;
    }

    public PolyMcExtraConfigService(PolyMcExtraConfig data) {
        this.data = data;
    }

    protected void parseAll() {
        this.disabledOpaqueBlockIds.addAll(
                this.data.getDisabledOpaqueBlocks().stream()
                        .map(Identifier::tryParse)
                        .filter(Objects::nonNull)
                        .filter(b -> !this.disabledOpaqueBlockIds.contains(b))
                        .toList()
        );

        for (var entry : this.data.getCustomModelTypeMappings().entrySet()) {
            String key = entry.getKey();
            String type = entry.getValue();

            Set<Block> blockWithPatternOrId = this.getBlockWithPatternOrId(key);
            for (Block block : blockWithPatternOrId) {
                if (!this.customMappingBlockIds.containsKey(block)) {
                    this.customMappingBlockIds.put(block, type);
                }
            }
        }
        this.disabledHackScreenHandlerIds.addAll(
                this.data.getDisabledHackScreenHandlers()
                        .stream()
                        .map(Identifier::tryParse)
                        .filter(Objects::nonNull)
                        .filter(st -> !this.disabledHackScreenHandlerIds.contains(st))
                        .toList()
        );

        for (var sdEntry : this.data.getCustomBlockModelExpansionMappings()
                .entrySet()) {
            String keyStr = sdEntry.getKey();
            Double expansion = sdEntry.getValue();
            Set<Block> blockWithPatternOrId = this.getBlockWithPatternOrId(keyStr);
            for (Block block : blockWithPatternOrId) {
                if (!this.customBlockExpansionMappings.containsKey(block)) {
                    this.customBlockExpansionMappings.put(block, expansion);
                }
            }
        }

    }

    public Set<Block> getBlockWithPatternOrId(String key) {
        Set<Block> blocks = new LinkedHashSet<>();
        if (looksLikeRegex(key)) {
            try {
                Pattern pattern = Pattern.compile(key);
                for (ResourceKey<Block> blockKey : BuiltInRegistries.BLOCK.registryKeySet()) {
                    String blockId = blockKey.identifier().toString();
                    if (pattern.matcher(blockId).matches()) {
                        Block block = BuiltInRegistries.BLOCK.getValue(blockKey);
                        blocks.add(block);
                    }
                }
            } catch (PatternSyntaxException e) {
                log.error("Invalid regex in config: {}", key, e);
            }
            return blocks;
        }

        Identifier id = Identifier.tryParse(key);
        if (id != null && BuiltInRegistries.BLOCK.containsKey(id)) {
            Block block = BuiltInRegistries.BLOCK.getValue(id);
            return Set.of(block);
        }
        return blocks;
    }

    public <T extends AbstractContainerMenu> boolean shouldBypassPolyMcHandler(MenuType<T> type) {
        Identifier id = BuiltInRegistries.MENU.getKey(type);
        return this.disabledHackScreenHandlerIds.contains(id);
    }

    @Nullable
    public PolymerBlock getCustomBlockMapping(Block block) {
        return this.customPolymerBlockMappings.get(block);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public void startCustomEntityModelMappings() {
        Map<String, String> customEntityModelMappings = this.data.getCustomEntityModelMappings();
        var entries = customEntityModelMappings.entrySet();
        for (var entry : entries) {
            String targetEntityIdStr = entry.getKey().toLowerCase();
            String holderEntityIdStr = entry.getValue().toLowerCase();
            if (this.innerCustomEntityModelMappings(targetEntityIdStr, holderEntityIdStr)) {
                continue;
            }
            Identifier targetEntityId = Identifier.parse(targetEntityIdStr);
            Identifier holderEntityId = Identifier.parse(holderEntityIdStr);

            if (!(BuiltInRegistries.ENTITY_TYPE.containsKey(targetEntityId) && BuiltInRegistries.ENTITY_TYPE.containsKey(holderEntityId))) {
                continue;
            }

            EntityType targetEntityType = BuiltInRegistries.ENTITY_TYPE.getValue(targetEntityId);
            EntityType holderEntityType = BuiltInRegistries.ENTITY_TYPE.getValue(holderEntityId);

            PolymerEntityUtils.registerOverlay(targetEntityType, (Function<Entity, PolymerEntity>) entity -> new PolymerEntity() {
                @Override
                public EntityType<?> getPolymerEntityType(PacketContext context) {
                    return holderEntityType;
                }
            });
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private boolean innerCustomEntityModelMappings(String targetEntityIdStr, String holderEntityIdStr) {
        Identifier targetEntityId = Identifier.parse(targetEntityIdStr);
        Identifier holderEntityId = Identifier.parse(holderEntityIdStr);
        if (!(BuiltInRegistries.ENTITY_TYPE.containsKey(targetEntityId))) {
            return false;
        }
        EntityType targetEntityType = BuiltInRegistries.ENTITY_TYPE.getValue(targetEntityId);
        if (SPECIAL_ENTITY_MAPPINGS.containsKey(holderEntityId)) {
            SpecialPolymerEntityType factory = SPECIAL_ENTITY_MAPPINGS.get(holderEntityId);
            PolymerEntityUtils.registerOverlay(targetEntityType, (Function<Entity, PolymerEntity>) entity -> factory.get(targetEntityType));
            return true;
        }
        return false;
    }

    public void startCustomBlockMappings() {
        var entries = this.customMappingBlockIds.entrySet();
        for (Map.Entry<Block, String> entry : entries) {
            Block block = entry.getKey();
            String type = entry.getValue();
            if (type == null) {
                continue;
            }
            type = type.toUpperCase();
            ExtraModelType extraModelType = ExtraModelType.valueOf(type);
            if (extraModelType == null) {
                continue;
            }
            ExtraModelType.StateFactory factory = extraModelType.getFactory();
            PolymerBlock state = factory.getState(block, type);
            PolyMcExtraPacks.HOLDER_RESOURCES_SET.add(PolyMcExtraPacks.HolderResource.of(block));
            this.customPolymerBlockMappings.put(block, state);
        }
    }

    public boolean shouldDisabledOpaque(@NotNull ResourceKey<Block> registryKey) {
        Identifier blockId = registryKey.identifier();
        String blockIdStr = blockId.toString();
        boolean inArray = false;
        for (String expandable : PolyMcExtraPacks.EXPANDABLE) {
            if (blockIdStr.contains(expandable)) {
                inArray = true;
            }
            if (inArray) break;
        }
        return isMatch(registryKey) && !blockId.getNamespace().equals("minecraft") && inArray;
    }

    public boolean isMatch(@NotNull ResourceKey<Block> registryKey) {
        List<String> disabledOpaqueBlocks = this.data.getDisabledOpaqueBlocks();
        Identifier blockId = registryKey.identifier();
        if (this.disabledOpaqueBlockIds.contains(blockId)) {
            return true;
        }
        String blockIdStr = blockId.toString();
        for (String pattern : disabledOpaqueBlocks) {
            if (pattern.equals(blockIdStr)) {
                return true;
            }

            if (looksLikeRegex(pattern)) {
                try {
                    if (blockIdStr.matches(pattern)) {
                        return true;
                    }
                } catch (Exception e) {
                    log.error("Invalid regex: {}", pattern, e);
                }
            }
        }
        return false;
    }

    private static boolean looksLikeRegex(String s) {
        return s.contains(".*")
                || s.contains("[")
                || s.contains("]")
                || s.contains("(")
                || s.contains(")")
                || s.contains("|")
                || s.contains("?")
                || s.contains("+")
                || s.contains("{");
    }
}
