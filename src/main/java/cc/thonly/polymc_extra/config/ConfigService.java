package cc.thonly.polymc_extra.config;

import cc.thonly.polymc_extra.util.PolyMcExtraPacks;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.block.Block;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@Slf4j
public class ConfigService {
    private final Config data;
    private final List<Identifier> disabledOpaqueBlockIds = new ArrayList<>();

    public ConfigService(Config data) {
        this.data = data;
        parseAll();
    }

    private void parseAll() {
        List<String> disabledOpaqueBlockIdStr = this.data.getDisabledOpaqueBlocks();
        this.disabledOpaqueBlockIds.addAll(disabledOpaqueBlockIdStr
                .stream()
                .map(Identifier::tryParse)
                .filter(Objects::nonNull).toList()
        );
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
