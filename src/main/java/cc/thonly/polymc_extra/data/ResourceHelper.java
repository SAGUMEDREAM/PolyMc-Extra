package cc.thonly.polymc_extra.data;

import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import eu.pb4.polymer.common.api.PolymerCommonUtils;
import eu.pb4.polymer.resourcepack.api.ResourcePackBuilder;
import eu.pb4.polymer.resourcepack.extras.api.format.blockstate.BlockStateAsset;
import eu.pb4.polymer.resourcepack.extras.api.format.model.ModelAsset;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.IoSupplier;
import nl.theepicblock.resourcelocatorapi.ResourceLocatorApi;
import nl.theepicblock.resourcelocatorapi.api.AssetContainer;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;

/**
 * Source code by Drex's polymer-patch-bundle
 * <a href="https://github.com/DrexHD/polymer-patch-bundle/blob/main/src/main/java/me/drex/ppb/res/ResourceHelper.java">ResourceHelper.java</a>
 * */
@Slf4j
public class ResourceHelper {
    public static AssetContainer GLOBAL_ASSETS;
    private static final FileSystem vanillaFilesystem;
    static {
        try {
            //noinspection DataFlowIssue
            vanillaFilesystem = FileSystems.newFileSystem(PolymerCommonUtils.getClientJar());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void initGlobalAssets(ResourcePackBuilder builder) {
        GLOBAL_ASSETS = ResourceLocatorApi.createGlobalAssetContainer();
        GLOBAL_ASSETS.locateFiles("").forEach(tuple -> {
            Identifier id = tuple.getA();
            String namespace = id.getNamespace();
//            if (!PolyMcExtraPacks.NAMESPACES.contains()) return;
            IoSupplier<InputStream> ioSupplier = tuple.getB();
            try {
                byte[] data = IOUtils.toByteArray(ioSupplier.get());
                builder.addData("assets/" + id.getNamespace() + "/" + id.getPath(), data);
            } catch (IOException e) {
                log.error("Failed to read resource {}: {}", id, e);
            }
        });
        if (GLOBAL_ASSETS != null) {
            try {
                GLOBAL_ASSETS.close();
            } catch (Exception e) {
                log.error("Can't release global assets");
            }
        }
    }

    public static IoSupplier<InputStream> getAsset(String namespace, String path) {
        IoSupplier<InputStream> supplier = ResourceHelper.GLOBAL_ASSETS.getAsset(namespace, path);
        if (supplier != null) {
            return supplier;
        }
        var vanillaPath = vanillaFilesystem.getPath("/assets/" + namespace + "/" + path);
        if (Files.exists(vanillaPath)) {
            return IoSupplier.create(vanillaPath);
        } else {
            return null;
        }
    }

    public static <T> T decodeAsset(Codec<T> codec, Identifier id, String type, String extension) throws IOException {
        IoSupplier<InputStream> supplier = getAsset(id.getNamespace(), type + "/" + id.getPath() + extension);
        return codec.decode(JsonOps.INSTANCE, JsonParser.parseReader(new JsonReader(new InputStreamReader(supplier.get())))).getOrThrow().getFirst();
    }

    public static BlockStateAsset decodeBlockState(Identifier id) throws IOException {
        return decodeAsset(BlockStateAsset.CODEC, id, "blockstates", ".json");
    }

    public static ModelAsset decodeModel(Identifier id) throws IOException {
        return decodeAsset(ModelAsset.CODEC, id, "models", ".json");
    }
}
