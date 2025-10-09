package cc.thonly.polymc_extra.config;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
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

@Getter(AccessLevel.PROTECTED)
@Slf4j
public class Config {
    private static Config INSTANCE;
    private static final Path CONFIG_PATH = Path.of("config", "polymc-extra.json");
    private static final Gson GSON = new Gson();
    private static final Codec<Config> CODEC = RecordCodecBuilder.create(x -> x.group(
            Codec.STRING.listOf()
                    .optionalFieldOf("DisabledOpaqueBlocks", new ArrayList<>())
                    .forGetter(Config::getDisabledOpaqueBlocks)
    ).apply(x, Config::new));

    private final List<String> disabledOpaqueBlocks;
    @Getter(AccessLevel.PUBLIC)
    private final ConfigService service;

    public Config(List<String> disabledOpaqueBlocks) {
        this.disabledOpaqueBlocks = new ArrayList<>(disabledOpaqueBlocks);
        this.service = new ConfigService(this);
    }

    private static synchronized void init() {
        try {
            Files.createDirectories(CONFIG_PATH.getParent());

            if (!Files.exists(CONFIG_PATH)) {
                INSTANCE = new Config(List.of());
                save();
            } else {
                try (Reader reader = Files.newBufferedReader(CONFIG_PATH)) {
                    JsonElement json = GSON.fromJson(reader, JsonElement.class);
                    var decodeResult = CODEC.decode(JsonOps.INSTANCE, json);
                    INSTANCE = decodeResult.result()
                            .map(Pair::getFirst)
                            .orElseGet(() -> {
                                log.error("Failed to parse config, using defaults.");
                                return new Config(List.of());
                            });
                }
            }
        } catch (IOException e) {
            log.error("Failed to load config: ",e);
            INSTANCE = new Config(List.of());
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

    public static synchronized Config getConfig() {
        if (INSTANCE == null) {
            init();
        }
        return INSTANCE;
    }
}
