package cc.thonly.polymc_extra.config;

import cc.thonly.polymc_extra.util.ExtraModelType;
import cc.thonly.polymc_extra.util.PolyMcExtraPacks;
import eu.pb4.polymer.core.api.block.PolymerBlock;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.block.Block;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

@Getter
@Slf4j
public class ConfigService {
    private final PolyMcExtraConfig data;
    private final List<Identifier> disabledOpaqueBlockIds = new ArrayList<>();
    private final Map<Block, String> customMappingBlockIds = new Object2ObjectOpenHashMap<>();
    private final Map<Block, PolymerBlock> customPolymerBlockMappings = new Object2ObjectOpenHashMap<>();

    public ConfigService(PolyMcExtraConfig data) {
        this.data = data;
        parseAll();
    }

    private void parseAll() {
        this.disabledOpaqueBlockIds.addAll(
                this.data.getDisabledOpaqueBlocks().stream()
                        .map(Identifier::tryParse)
                        .filter(Objects::nonNull)
                        .toList()
        );

        for (var entry : this.data.getCustomModelTypeMappings().entrySet()) {
            String key = entry.getKey();
            String type = entry.getValue();
            Identifier id = Identifier.tryParse(key);

            if (looksLikeRegex(key)) {
                try {
                    Pattern pattern = Pattern.compile(key);
                    for (RegistryKey<Block> blockKey : Registries.BLOCK.getKeys()) {
                        String blockId = blockKey.getValue().toString();
                        if (pattern.matcher(blockId).matches()) {
                            this.customMappingBlockIds.put(Registries.BLOCK.get(blockKey), type);
                        }
                    }
                } catch (PatternSyntaxException e) {
                    log.error("Invalid regex in config: {}", key, e);
                }
                continue;
            }

            if (id != null && Registries.BLOCK.containsId(id)) {
                this.customMappingBlockIds.put(Registries.BLOCK.get(id), type);
            }
        }
    }

    @Nullable
    public PolymerBlock getCustomBlockMapping(Block block) {
        return this.customPolymerBlockMappings.get(block);
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
            Identifier blockId = Registries.BLOCK.getId(block);
            PolyMcExtraPacks.NAMESPACES.add(blockId.getNamespace());
            PolyMcExtraPacks.EXPANDABLE.add(blockId.getPath());
            this.customPolymerBlockMappings.put(block, state);
        }
    }

    public boolean shouldDisabledOpaque(@NotNull RegistryKey<Block> registryKey) {
        Identifier blockId = registryKey.getValue();
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

    public boolean isMatch(@NotNull RegistryKey<Block> registryKey) {
        List<String> disabledOpaqueBlocks = this.data.getDisabledOpaqueBlocks();
        Identifier blockId = registryKey.getValue();
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
