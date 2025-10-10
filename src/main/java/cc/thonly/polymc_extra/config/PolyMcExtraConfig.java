package cc.thonly.polymc_extra.config;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Getter(AccessLevel.PROTECTED)
@Slf4j
public class PolyMcExtraConfig {
    private static PolyMcExtraConfig INSTANCE;
    private static final Path CONFIG_PATH = Path.of("config", "polymc-extra.json");
    private static final Gson GSON = new Gson();
    private static final Codec<PolyMcExtraConfig> CODEC = RecordCodecBuilder.create(x -> x.group(
            Codec.STRING.listOf()
                    .optionalFieldOf("DisabledOpaqueBlocks", new ArrayList<>())
                    .forGetter(PolyMcExtraConfig::getDisabledOpaqueBlocks),
            Codec.unboundedMap(Codec.STRING, Codec.STRING)
                    .optionalFieldOf("CustomModelTypeMappings", Map.of())
                    .forGetter(PolyMcExtraConfig::getCustomModelTypeMappings)
    ).apply(x, PolyMcExtraConfig::new));

    private final List<String> disabledOpaqueBlocks;
    private final Map<String, String> customModelTypeMappings;
    @Getter(AccessLevel.PUBLIC)
    private final ConfigService service;

    public PolyMcExtraConfig(List<String> disabledOpaqueBlocks, Map<String, String> customModelTypeMappings) {
        this.disabledOpaqueBlocks = new ArrayList<>(disabledOpaqueBlocks);
        this.customModelTypeMappings = new Object2ObjectOpenHashMap<>(customModelTypeMappings);
        this.service = new ConfigService(this);
    }

    private static synchronized void init() {
        try {
            Files.createDirectories(CONFIG_PATH.getParent());

            if (!Files.exists(CONFIG_PATH)) {
                INSTANCE = new PolyMcExtraConfig(List.of(), Map.of());
                save();
            } else {
                try (Reader reader = Files.newBufferedReader(CONFIG_PATH)) {
                    JsonElement json = GSON.fromJson(reader, JsonElement.class);
                    var decodeResult = CODEC.decode(JsonOps.INSTANCE, json);
                    INSTANCE = decodeResult.result()
                            .map(Pair::getFirst)
                            .orElseGet(() -> {
                                log.error("Failed to parse config, using defaults.");
                                return new PolyMcExtraConfig(List.of(), Map.of());
                            });
                }
            }
        } catch (IOException e) {
            log.error("Failed to load config: ",e);
            INSTANCE = new PolyMcExtraConfig(List.of(), Map.of());
        }
    }

    private static synchronized void save() {
        if (INSTANCE == null) return;
        try (Writer writer = Files.newBufferedWriter(CONFIG_PATH)) {
            var encodeResult = CODEC.encodeStart(JsonOps.INSTANCE, INSTANCE);
            var json = encodeResult.result().orElseThrow();
            GSON.toJson(json, writer);
        } catch (Exception e) {
            log.error("Failed to save config", e);
        }
    }

    public static synchronized PolyMcExtraConfig getConfig() {
        if (INSTANCE == null) {
            init();
        }
        return INSTANCE;
    }
}
