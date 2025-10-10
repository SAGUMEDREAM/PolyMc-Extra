package cc.thonly.polymc_extra.util;

import cc.thonly.polymc_extra.PolyMcExtra;
import cc.thonly.polymc_extra.config.PolyMcExtraConfig;
import eu.pb4.factorytools.api.block.model.generic.BlockStateModelManager;
import eu.pb4.factorytools.api.resourcepack.ModelModifiers;
import eu.pb4.polymer.resourcepack.api.AssetPaths;
import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import eu.pb4.polymer.resourcepack.api.ResourcePackBuilder;
import eu.pb4.polymer.resourcepack.extras.api.ResourcePackExtras;
import eu.pb4.polymer.resourcepack.extras.api.format.atlas.AtlasAsset;
import eu.pb4.polymer.resourcepack.extras.api.format.blockstate.StateModelVariant;
import eu.pb4.polymer.resourcepack.extras.api.format.model.ModelAsset;
import eu.pb4.polymer.resourcepack.extras.api.format.model.ModelElement;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.block.AbstractSignBlock;
import net.minecraft.block.Block;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Function;

@Slf4j
public class PolyMcExtraPacks {
    public static final List<AbstractSignBlock> SIGN_MODELS = new ArrayList<>();
    public static final Set<String> EXPANDABLE = new LinkedHashSet<>(Set.of(
            "wall", "fence", "slab", "stairs", "pressure_plate", "button",
            "glass_pane", "lattice", "bars", "carpet", "chain", "lantern"
    ));
    public static final Set<String> NAMESPACES = new LinkedHashSet<>();
    private static PolyMcExtraConfig POLYMC_EXTRA_CONFIG = null;

    public static void registers() {
        var config = PolyMcExtraConfig.getConfig();
        POLYMC_EXTRA_CONFIG = config;
        for (Block block : Registries.BLOCK) {
            RegistryKey<Block> key = Registries.BLOCK.getKey(block).orElse(null);
            if (key != null && config.getService().shouldDisabledOpaque(key)) {
                block.getSettings().nonOpaque();
            }
        }

        for (Runnable lateRunnable : PolyMcExtra.LATE_INIT) {
            try {
                lateRunnable.run();
            } catch (Exception err) {
                log.error("An error occurred during the PolyMcPacks generation process", err);
            }
        }
        PolyMcExtra.LATE_INIT.clear();

        for (String namespace : NAMESPACES) {
            PolymerResourcePackUtils.addModAssets(namespace);
            Function<String, Identifier> factory = (id) -> Identifier.of(namespace, id);
            Function<String, Identifier> factory2 = (id) -> Identifier.of(namespace + "_polymerify", id);
            ResourcePackExtras.forDefault().addBridgedModelsFolder(
                    factory.apply("block"),
                    factory.apply("item"),
                    factory.apply("entity"),
                    factory.apply("font"),
                    factory.apply("effect"),
                    factory.apply("misc"),
                    factory.apply("gui")
            );
            ResourcePackExtras.forDefault().addBridgedModelsFolder(
                    factory2.apply("block"),
                    factory2.apply("item"),
                    factory2.apply("entity"),
                    factory2.apply("font"),
                    factory2.apply("effect"),
                    factory2.apply("misc"),
                    factory2.apply("gui")
            );
        }
        PolymerResourcePackUtils.markAsRequired();
        PolymerResourcePackUtils.RESOURCE_PACK_AFTER_INITIAL_CREATION_EVENT.register(PolyMcExtraPacks::build);
    }

    private static void build(ResourcePackBuilder builder) {
        final var expansion = new Vec3d(0.08, 0.08, 0.08);
        var atlas = AtlasAsset.builder();

        builder.forEachFile(((string, bytes) -> {
            for (var expandable : EXPANDABLE) {
                for (var namespace : NAMESPACES) {
                    var polymerify_namespace = namespace + "_polymerify";
                    if (string.contains(expandable) && string.startsWith("assets/%s/models/block/".formatted(namespace))) {
                        var asset = ModelAsset.fromJson(new String(bytes, StandardCharsets.UTF_8));
                        if (asset.parent().isPresent()) {
                            var parentId = asset.parent().get();
                            var parentAsset = ModelAsset.fromJson(new String(Objects.requireNonNull(builder.getDataOrSource(AssetPaths.model(parentId) + ".json")), StandardCharsets.UTF_8));

                            builder.addData(AssetPaths.model(polymerify_namespace, parentId.getPath()) + ".json", new ModelAsset(parentAsset.parent(), parentAsset.elements().map(x -> x.stream()
                                    .map(element -> new ModelElement(element.from().subtract(expansion), element.to().add(expansion),
                                            element.faces(), element.rotation(), element.shade(), element.lightEmission())
                                    ).toList()), parentAsset.textures(), parentAsset.display(), parentAsset.guiLight(), parentAsset.ambientOcclusion()).toBytes());
                        }

                        if (asset.elements().isPresent()) {
                            builder.addData(string, new ModelAsset(asset.parent(), asset.elements().map(x -> x.stream()
                                    .map(element -> new ModelElement(element.from().subtract(expansion), element.to().add(expansion),
                                            element.faces(), element.rotation(), element.shade(), element.lightEmission())
                                    ).toList()), asset.textures(), asset.display(), asset.guiLight(), asset.ambientOcclusion()).toBytes());
                        }
                    }
                }
            }
        }));
        for (var namespace : NAMESPACES) {
            var polymerify_namespace = namespace + "_polymerify";
            Map<String, List<StateModelVariant>> map = BlockStateModelManager.UV_LOCKED_MODELS.get(namespace);
            if (map == null) {
                continue;
            }
            for (var entry : map.entrySet()) {
                var expand = EXPANDABLE.stream().anyMatch(expandable -> entry.getKey().contains(expandable) && entry.getKey().startsWith("block/")) ? expansion : Vec3d.ZERO;
                for (var v : entry.getValue()) {
                    var suffix = "_uvlock_" + v.x() + "_" + v.y();
                    var modelId = v.model().withSuffixedPath(suffix);
                    var asset = ModelAsset.fromJson(new String(Objects.requireNonNull(builder.getData(AssetPaths.model(v.model()) + ".json")), StandardCharsets.UTF_8));

                    if (asset.parent().isPresent()) {
                        var parentId = asset.parent().get();
                        var parentAsset = ModelAsset.fromJson(new String(Objects.requireNonNull(builder.getDataOrSource(AssetPaths.model(parentId) + ".json")), StandardCharsets.UTF_8));
                        builder.addData(AssetPaths.model(polymerify_namespace, parentId.getPath() + suffix) + ".json",
                                ModelModifiers.expandModelAndRotateUVLocked(parentAsset, expand, v.x(), v.y()));
                        builder.addData(AssetPaths.model(modelId) + ".json",
                                new ModelAsset(Optional.of(Identifier.of(polymerify_namespace, parentId.getPath() + suffix)), asset.elements(),
                                        asset.textures(), asset.display(), asset.guiLight(), asset.ambientOcclusion()).toBytes());
                    }
                }
            }

            builder.addWriteConverter(((string, bytes) -> {
                if (!string.contains("_uvlock_")) {
                    for (var expandable : EXPANDABLE) {
                        if (string.contains(expandable) && string.startsWith("assets/%s/models/block/".formatted(namespace))) {
                            var asset = ModelAsset.fromJson(new String(bytes, StandardCharsets.UTF_8));
                            return new ModelAsset(asset.parent().map(x -> Identifier.of(polymerify_namespace, x.getPath())), asset.elements(), asset.textures(), asset.display(), asset.guiLight(), asset.ambientOcclusion()).toBytes();
                        }
                    }
                }
                return bytes;
            }));
        }

        for (AbstractSignBlock signModel : SIGN_MODELS) {
            Identifier id = Registries.BLOCK.getId(signModel);
            try {
                String namespace = id.getNamespace();
                String name = signModel.getWoodType().name();
                if (name.contains(namespace + ":")) {
                    name = name.replaceAll(namespace + ":", "");
                }
                ModelModifiers.createSignModel(builder, namespace, name, atlas);
            } catch (Exception err) {
                log.error("Can't read model namespace and id {}", id, err);
            }
        }

        builder.addData("assets/minecraft/atlases/blocks.json", atlas.build());
    }
}
