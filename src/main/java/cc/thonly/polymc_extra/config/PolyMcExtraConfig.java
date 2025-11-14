package cc.thonly.polymc_extra.config;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Function5;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import eu.pb4.polymer.resourcepack.api.ResourcePackCreator;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

@Getter(AccessLevel.PROTECTED)
@Slf4j
public class PolyMcExtraConfig {
    private static PolyMcExtraConfig INSTANCE;
    private static final Path CONFIG_PATH = Path.of("config", "polymc-extra.json");
    private static final Gson GSON = new Gson();
    private static final Codec<PolyMcExtraConfig> CODEC = RecordCodecBuilder.create(x -> x.group(
            Codec.STRING.listOf()
                    .optionalFieldOf("DisabledOpaqueBlocks", new LinkedList<>())
                    .forGetter(PolyMcExtraConfig::getDisabledOpaqueBlocks),
            Codec.STRING.listOf()
                    .optionalFieldOf("DisabledHackScreenHandlers", new LinkedList<>())
                    .forGetter(PolyMcExtraConfig::getDisabledHackScreenHandlers),
            Codec.STRING.listOf()
                    .optionalFieldOf("AddedModIds", new LinkedList<>())
                    .forGetter(PolyMcExtraConfig::getModIds),
            Codec.unboundedMap(Codec.STRING, Codec.STRING)
                    .optionalFieldOf("CustomBlockModelTypeMappings", new Object2ObjectLinkedOpenHashMap<>())
                    .forGetter(PolyMcExtraConfig::getCustomModelTypeMappings),
            Codec.unboundedMap(Codec.STRING, Codec.STRING)
                    .optionalFieldOf("CustomEntityModelMappings", new Object2ObjectLinkedOpenHashMap<>())
                    .forGetter(PolyMcExtraConfig::getCustomEntityModelMappings),
            Codec.unboundedMap(Codec.STRING, Codec.DOUBLE)
                    .optionalFieldOf("CustomModelExpansionMappings", new Object2ObjectLinkedOpenHashMap<>())
                    .forGetter(PolyMcExtraConfig::getCustomBlockModelExpansionMappings)
    ).apply(x, (a, b, c, d, e, f) -> new PolyMcExtraConfig(a, b, c, d, e, f, false)));
    private static final Codec<PolyMcExtraConfig> SUB_CODEC = RecordCodecBuilder.create(x -> x.group(
            Codec.STRING.listOf()
                    .optionalFieldOf("DisabledOpaqueBlocks", new LinkedList<>())
                    .forGetter(PolyMcExtraConfig::getDisabledOpaqueBlocks),
            Codec.STRING.listOf()
                    .optionalFieldOf("DisabledHackScreenHandlers", new LinkedList<>())
                    .forGetter(PolyMcExtraConfig::getDisabledHackScreenHandlers),
            Codec.STRING.listOf()
                    .optionalFieldOf("AddedModIds", new LinkedList<>())
                    .forGetter(PolyMcExtraConfig::getModIds),
            Codec.unboundedMap(Codec.STRING, Codec.STRING)
                    .optionalFieldOf("CustomBlockModelTypeMappings", new Object2ObjectLinkedOpenHashMap<>())
                    .forGetter(PolyMcExtraConfig::getCustomModelTypeMappings),
            Codec.unboundedMap(Codec.STRING, Codec.STRING)
                    .optionalFieldOf("CustomEntityModelMappings", new Object2ObjectLinkedOpenHashMap<>())
                    .forGetter(PolyMcExtraConfig::getCustomEntityModelMappings),
            Codec.unboundedMap(Codec.STRING, Codec.DOUBLE)
                    .optionalFieldOf("CustomModelExpansionMappings", new Object2ObjectLinkedOpenHashMap<>())
                    .forGetter(PolyMcExtraConfig::getCustomBlockModelExpansionMappings)
    ).apply(x, (a, b, c, d, e, f) -> new PolyMcExtraConfig(a, b, c, d, e, f, true)));

    private final List<String> disabledOpaqueBlocks;
    private final List<String> disabledHackScreenHandlers;
    private final List<String> modIds;
    private final Map<String, String> customModelTypeMappings;
    private final Map<String, String> customEntityModelMappings;
    private final Map<String, Double> customBlockModelExpansionMappings;
    @Getter(AccessLevel.PUBLIC)
    private final PolyMcExtraConfigService service;

    public PolyMcExtraConfig(List<String> disabledOpaqueBlocks,
                             List<String> disabledHackScreenHandlers,
                             List<String> modIds,
                             Map<String, String> customModelTypeMappings,
                             Map<String, String> customEntityModelMappings,
                             Map<String, Double> customBlockModelExpansionMappings,
                             boolean isSubMod
    ) {
        this.disabledOpaqueBlocks = new LinkedList<>(disabledOpaqueBlocks);
        this.disabledHackScreenHandlers = new LinkedList<>(disabledHackScreenHandlers);
        this.modIds = new LinkedList<>(modIds);
        this.customModelTypeMappings = new Object2ObjectLinkedOpenHashMap<>(customModelTypeMappings);
        this.customEntityModelMappings = new Object2ObjectLinkedOpenHashMap<>(customEntityModelMappings);
        this.customBlockModelExpansionMappings = new Object2ObjectLinkedOpenHashMap<>(customBlockModelExpansionMappings);
        this.loadFromAllMods();
        for (String modId : this.modIds) {
            ResourcePackCreator.forDefault().addAssetSource(modId);
        }
        if (isSubMod) {
            this.service = null;
        } else {
            this.service = new PolyMcExtraConfigService(this);
            this.service.parseAll();
        }
    }

    private synchronized void loadFromAllMods() {
        FabricLoader fabricLoader = FabricLoader.getInstance();
        Collection<ModContainer> mods = fabricLoader.getAllMods();
        for (ModContainer mod : mods) {
            Optional<Path> pathOpt = mod.findPath("./config/polymc-extra.json");
            if (pathOpt.isEmpty()) {
                continue;
            }
            Path path = pathOpt.get();
            if (!Files.exists(path)) {
                continue;
            }
            try (Reader reader = Files.newBufferedReader(path)) {
                JsonElement json = GSON.fromJson(reader, JsonElement.class);
                var decodeResult = SUB_CODEC.decode(JsonOps.INSTANCE, json);
                Pair<PolyMcExtraConfig, JsonElement> pair = decodeResult.getOrThrow();
                PolyMcExtraConfig first = pair.getFirst();
                this.replaceWrite(first.disabledOpaqueBlocks, this.disabledOpaqueBlocks);
                this.replaceWrite(first.disabledHackScreenHandlers, this.disabledHackScreenHandlers);
                this.replaceWrite(first.customModelTypeMappings, this.customModelTypeMappings);
                this.replaceWrite(first.customEntityModelMappings, this.customEntityModelMappings);
                this.replaceWrite(first.customBlockModelExpansionMappings, this.customBlockModelExpansionMappings);
            } catch (IOException e) {
                log.error("Failed to load config in mod {}: ", mod.getMetadata().getName(), e);
            }
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private void replaceWrite(Collection from, Collection to) {
        for (Object object : from) {
            if (!to.contains(object)) {
                to.add(object);
            }
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private void replaceWrite(Map from, Map to) {
        for (Object entryObj : from.entrySet()) {
            Map.Entry entry = (Map.Entry) entryObj;
            Object key = entry.getKey();
            Object value = entry.getValue();
            if (!to.containsKey(key)) {
                to.put(key, value);
            }
        }
    }

    private static synchronized void init() {
        try {
            Files.createDirectories(CONFIG_PATH.getParent());

            if (!Files.exists(CONFIG_PATH)) {
                INSTANCE = new PolyMcExtraConfig(List.of(), List.of(), List.of(), Map.of(), Map.of(), Map.of(), false);
                save();
            } else {
                try (Reader reader = Files.newBufferedReader(CONFIG_PATH)) {
                    JsonElement json = GSON.fromJson(reader, JsonElement.class);
                    var decodeResult = CODEC.decode(JsonOps.INSTANCE, json);
                    INSTANCE = decodeResult.result()
                            .map(Pair::getFirst)
                            .orElseGet(() -> {
                                log.error("Failed to parse config, using defaults.");
                                return new PolyMcExtraConfig(List.of(), List.of(), List.of(), Map.of(), Map.of(), Map.of(), false);
                            });
                }
            }
        } catch (IOException e) {
            log.error("Failed to load config: ", e);
            INSTANCE = new PolyMcExtraConfig(List.of(), List.of(), List.of(), Map.of(), Map.of(), Map.of(), false);
        }
    }

    private static synchronized void save() {
        if (INSTANCE == null) return;
        try (Writer writer = Files.newBufferedWriter(CONFIG_PATH)) {
            var encodeResult = CODEC.encodeStart(JsonOps.INSTANCE, INSTANCE);
            var json = encodeResult.result().orElseThrow();
            if (json instanceof JsonObject object) {
                if (!object.has("DisabledOpaqueBlocks")) {
                    object.add("DisabledOpaqueBlocks", GSON.toJsonTree(List.of()));
                }
                if (!object.has("DisabledHackScreenHandlers")) {
                    object.add("DisabledHackScreenHandlers", GSON.toJsonTree(List.of()));
                }
                if (!object.has("CustomBlockModelTypeMappings")) {
                    object.add("CustomBlockModelTypeMappings", new JsonObject());
                }
                if (!object.has("CustomEntityModelMappings")) {
                    object.add("CustomEntityModelMappings", new JsonObject());
                }
                if (!object.has("CustomModelExpansionMappings")) {
                    object.add("CustomModelExpansionMappings", new JsonObject());
                }
            }
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
