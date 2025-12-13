package cc.thonly.polymc_extra.data;

import cc.thonly.polymc_extra.PolyMcExtra;
import cc.thonly.polymc_extra.config.PolyMcExtraConfig;
import cc.thonly.polymc_extra.config.PolyMcExtraConfigService;
import cc.thonly.polymc_extra.mixin.accessor.ResourcePackCreatorAccessor;
import eu.pb4.factorytools.api.block.model.generic.BlockStateModelManager;
import eu.pb4.factorytools.api.resourcepack.ModelModifiers;
import eu.pb4.polymer.common.api.PolymerCommonUtils;
import eu.pb4.polymer.resourcepack.api.AssetPaths;
import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import eu.pb4.polymer.resourcepack.api.ResourcePackBuilder;
import eu.pb4.polymer.resourcepack.api.ResourcePackCreator;
import eu.pb4.polymer.resourcepack.extras.api.ResourcePackExtras;
import eu.pb4.polymer.resourcepack.extras.api.format.atlas.AtlasAsset;
import eu.pb4.polymer.resourcepack.extras.api.format.blockstate.StateModelVariant;
import eu.pb4.polymer.resourcepack.extras.api.format.model.ModelAsset;
import eu.pb4.polymer.resourcepack.extras.api.format.model.ModelElement;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.Vec3;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

@SuppressWarnings("removal")
@Slf4j
public class PolyMcExtraPacks {
    public static final List<ResourceLocation> SIGN_MODEL_IDS = new ArrayList<>();
    public static final Set<String> EXPANDABLE = new LinkedHashSet<>(Set.of(
            "wall", "fence", "slab", "stairs", "pressure_plate", "button",
            "glass_pane", "lattice", "bars", "carpet", "chain", "lantern"
    ));
    public static final Set<String> NAMESPACES = new LinkedHashSet<>();
    public static final Set<HolderResource> HOLDER_RESOURCES_SET = new LinkedHashSet<>();
    public static final double DEFAULT_EXPANSION_SIZE = 0.08;
    @SuppressWarnings({"unused", "FieldCanBeLocal"})
    private static PolyMcExtraConfig POLYMC_EXTRA_CONFIG = null;


    public static void registers() {
        var config = PolyMcExtraConfig.getConfig();
        POLYMC_EXTRA_CONFIG = config;
        for (Block block : BuiltInRegistries.BLOCK) {
            ResourceKey<Block> key = BuiltInRegistries.BLOCK.getResourceKey(block).orElse(null);
            if (key != null && config.getService().shouldDisabledOpaque(key)) {
                block.properties().noOcclusion();
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
            ResourcePackCreatorAccessor creatorAccessor = (ResourcePackCreatorAccessor) (Object) ResourcePackCreator.forDefault();
            if (creatorAccessor != null) {
                creatorAccessor.polymcExtra$getModIds().add(namespace);
            }
            PolymerResourcePackUtils.addModAssets(namespace);
            List<String> paths = List.of("block", "block/furniture", "item", "entity", "font", "effect", "misc", "gui");
            ResourcePackExtras extras = ResourcePackExtras.forDefault();

            for (String prefix : List.of(namespace, namespace + "_polymerify")) {
                Function<String, ResourceLocation> factory = id -> ResourceLocation.fromNamespaceAndPath(prefix, id);
                extras.addBridgedModelsFolder(
                        paths.stream().map(factory).toArray(ResourceLocation[]::new)
                );
            }

        }
        PolymerResourcePackUtils.markAsRequired();
        PolymerResourcePackUtils.RESOURCE_PACK_CREATION_EVENT.register(new Consumer<>() {
            @Override
            public void accept(ResourcePackBuilder resourcePackBuilder) {
                PolyMcExtra.getLog().info("Starting to build Global resource pack...");
                ResourceHelper.initGlobalAssets(resourcePackBuilder);
            }
        });
        PolymerResourcePackUtils.RESOURCE_PACK_AFTER_INITIAL_CREATION_EVENT.register(new Consumer<>() {
            @Override
            public void accept(ResourcePackBuilder resourcePackBuilder) {
                PolyMcExtra.getLog().info("Starting to build PolyMc-Extra resource pack...");
                buildVanillaLike(resourcePackBuilder);
                buildCustomHolder(resourcePackBuilder);
            }
        });
    }

    private static void buildVanillaLike(ResourcePackBuilder builder) {
        PolyMcExtra.getLog().info("Starting to build vanilla-like resource pack...");
        long start = System.nanoTime();
        var atlas = AtlasAsset.builder();

        builder.forEachFile(((string, bytes) -> {
            for (var expandable : EXPANDABLE) {
                for (var namespace : NAMESPACES) {
                    var polymerify_namespace = namespace + "_polymerify";
                    if (string.contains(expandable) && string.startsWith("assets/%s/models/block/".formatted(namespace))) {
                        var asset = ModelAsset.fromJson(new String(bytes, StandardCharsets.UTF_8));
                        final var expansion = new Vec3(0.08, 0.08, 0.08);
                        if (asset.parent().isPresent()) {
                            var parentId = asset.parent().get();
                            byte[] bty = builder.getDataOrSource(AssetPaths.model(parentId) + ".json");
                            if (bty == null) {
                                continue;
                            }
                            var parentAsset = ModelAsset.fromJson(new String(bty, StandardCharsets.UTF_8));

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
                final var expansion = new Vec3(0.08, 0.08, 0.08);
                var expand = EXPANDABLE.stream().anyMatch(expandable -> entry.getKey().contains(expandable) && entry.getKey().startsWith("block/")) ? expansion : Vec3.ZERO;
                for (var v : entry.getValue()) {
                    var suffix = "_uvlock_" + v.x() + "_" + v.y();
                    var modelId = v.model().withSuffix(suffix);
                    byte[] data = builder.getData(AssetPaths.model(v.model()) + ".json");
                    if (data == null) {
                        continue;
                    }
                    var asset = ModelAsset.fromJson(new String(data, StandardCharsets.UTF_8));

                    if (asset.parent().isPresent()) {
                        var parentId = asset.parent().get();
                        byte[] dtbt = builder.getDataOrSource(AssetPaths.model(parentId) + ".json");
                        if (dtbt == null) {
                            continue;
                        }
                        var parentAsset = ModelAsset.fromJson(new String(dtbt, StandardCharsets.UTF_8));
                        ModelAsset modelAssetExpanded = ModelModifiers.expandModelAndRotateUVLocked(parentAsset, expand, v.x(), v.y());
                        ModelAsset finalModelAsset = null;
                        boolean modelWithinBounds = isModelWithinBounds(modelAssetExpanded);

                        if (modelWithinBounds) {
                            finalModelAsset = modelAssetExpanded;
                        } else {
                            finalModelAsset = parentAsset;
                        }

                        builder.addData(AssetPaths.model(polymerify_namespace, parentId.getPath() + suffix) + ".json",
                                finalModelAsset);
                        builder.addData(AssetPaths.model(modelId) + ".json",
                                new ModelAsset(Optional.of(ResourceLocation.fromNamespaceAndPath(polymerify_namespace, parentId.getPath() + suffix)), asset.elements(),
                                        asset.textures(), asset.display(), asset.guiLight(), asset.ambientOcclusion()).toBytes());
                    }
                }
            }

            builder.addWriteConverter(((string, bytes) -> {
                if (!string.contains("_uvlock_")) {
                    for (var expandable : EXPANDABLE) {
                        if (string.contains(expandable) && string.startsWith("assets/%s/models/block/".formatted(namespace))) {
                            var asset = ModelAsset.fromJson(new String(bytes, StandardCharsets.UTF_8));
                            return new ModelAsset(asset.parent().map(x -> ResourceLocation.fromNamespaceAndPath(polymerify_namespace, x.getPath())), asset.elements(), asset.textures(), asset.display(), asset.guiLight(), asset.ambientOcclusion()).toBytes();
                        }
                    }
                }
                return bytes;
            }));
        }

        for (ResourceLocation signModelId : SIGN_MODEL_IDS) {
            try {
                ModelModifiers.createSignModel(builder, signModelId.getNamespace(), signModelId.getPath(), atlas);
            } catch (Exception err) {
                log.error("Can't read model namespace and id {}", signModelId, err);
            }
        }

        builder.addData("assets/minecraft/atlases/blocks.json", atlas.build());
        long end = System.nanoTime();
        long duration = end - start;
        PolyMcExtra.getLog().info("Total construction time of PolyMc-Extra: {} ms", duration / 1_000_000.0);
        PolyMcExtra.getLog().info("Succeed to build vanilla-like resource-packs");
    }

    private static void buildCustomHolder(ResourcePackBuilder builder) {
        PolyMcExtra.getLog().info("Starting to build custom-holder resource pack...");
        PolyMcExtraConfigService service = PolyMcExtra.getConfig().getService();
        long start = System.nanoTime();
        builder.forEachFile(((path, bytes) -> {
            for (HolderResource holderResource : HOLDER_RESOURCES_SET) {
                var namespace = holderResource.namespace();
                var modelPath = holderResource.modelPath();
                var polymerify_namespace = namespace + "_polymerify";
                if (path.startsWith(modelPath)) {
                    Double size = service.getCustomBlockExpansionMappings().getOrDefault(holderResource.block, DEFAULT_EXPANSION_SIZE);
                    final var expansion = new Vec3(size, size, size);
                    var asset = ModelAsset.fromJson(new String(bytes, StandardCharsets.UTF_8));
                    if (asset.parent().isPresent()) {
                        var parentId = asset.parent().get();
                        byte[] dataOrSource = builder.getDataOrSource(AssetPaths.model(parentId) + ".json");
                        if (dataOrSource==null) continue;
                        var parentAsset = ModelAsset.fromJson(new String(dataOrSource, StandardCharsets.UTF_8));

                        builder.addData(AssetPaths.model(polymerify_namespace, parentId.getPath()) + ".json", new ModelAsset(parentAsset.parent(), parentAsset.elements().map(x -> x.stream()
                                .map(element -> new ModelElement(element.from().subtract(expansion), element.to().add(expansion),
                                        element.faces(), element.rotation(), element.shade(), element.lightEmission())
                                ).toList()), parentAsset.textures(), parentAsset.display(), parentAsset.guiLight(), parentAsset.ambientOcclusion()).toBytes());
                    }

                    if (asset.elements().isPresent()) {
                        builder.addData(path, new ModelAsset(asset.parent(), asset.elements().map(x -> x.stream()
                                .map(element -> new ModelElement(element.from().subtract(expansion), element.to().add(expansion),
                                        element.faces(), element.rotation(), element.shade(), element.lightEmission())
                                ).toList()), asset.textures(), asset.display(), asset.guiLight(), asset.ambientOcclusion()).toBytes());
                    }
                }
            }
        }));
        for (HolderResource holderResource : HOLDER_RESOURCES_SET) {
            var namespace = holderResource.namespace;
            var modelPath = holderResource.modelPath();
            var polymerify_namespace = namespace + "_polymerify";
            Map<String, List<StateModelVariant>> map = BlockStateModelManager.UV_LOCKED_MODELS.get(namespace);
            if (map == null) {
                continue;
            }

            for (var entry : map.entrySet()) {
                var modelKey = entry.getKey();

                if (!modelKey.contains(modelPath.substring(modelPath.lastIndexOf("/") + 1))) {
                    continue;
                }

                Double size = service.getCustomBlockExpansionMappings().getOrDefault(holderResource.block, DEFAULT_EXPANSION_SIZE);
                final var expand = new Vec3(size, size, size);
                for (var v : entry.getValue()) {
                    var suffix = "_uvlock_" + v.x() + "_" + v.y();
                    var modelId = v.model().withSuffix(suffix);

                    var modelData = builder.getData(AssetPaths.model(v.model()) + ".json");
                    if (modelData == null) {
                        continue;
                    }

                    var asset = ModelAsset.fromJson(new String(modelData, StandardCharsets.UTF_8));
                    if (asset.parent().isPresent()) {
                        var parentId = asset.parent().get();
                        var parentBytes = builder.getDataOrSource(AssetPaths.model(parentId) + ".json");
                        if (parentBytes == null) {
                            continue;
                        }
                        var parentAsset = ModelAsset.fromJson(new String(parentBytes, StandardCharsets.UTF_8));

                        ModelAsset modelAsset = ModelModifiers.expandModelAndRotateUVLocked(parentAsset, expand, v.x(), v.y());
                        ModelAsset assetCompare = null;
                        boolean modelWithinBounds = isModelWithinBounds(modelAsset);

                        if (modelWithinBounds) {
                            assetCompare = modelAsset;
                        } else {
                            assetCompare = parentAsset;
                        }

                        builder.addData(
                                AssetPaths.model(polymerify_namespace, parentId.getPath() + suffix) + ".json",
                                assetCompare
                        );

                        builder.addData(
                                AssetPaths.model(modelId) + ".json",
                                new ModelAsset(
                                        Optional.of(ResourceLocation.fromNamespaceAndPath(polymerify_namespace, parentId.getPath() + suffix)),
                                        asset.elements(),
                                        asset.textures(),
                                        asset.display(),
                                        asset.guiLight(),
                                        asset.ambientOcclusion()
                                ).toBytes()
                        );
                    }
                }
            }

            builder.addWriteConverter(((string, bytes) -> {
                if (!string.contains("_uvlock_") && string.startsWith(modelPath)) {
                    var asset = ModelAsset.fromJson(new String(bytes, StandardCharsets.UTF_8));
                    return new ModelAsset(
                            asset.parent().map(x -> ResourceLocation.fromNamespaceAndPath(polymerify_namespace, x.getPath())),
                            asset.elements(),
                            asset.textures(),
                            asset.display(),
                            asset.guiLight(),
                            asset.ambientOcclusion()
                    ).toBytes();
                }
                return bytes;
            }));
        }

        long end = System.nanoTime();
        long duration = end - start;
        PolyMcExtra.getLog().info("Total construction time of PolyMc-Extra: {} ms", duration / 1_000_000.0);
        PolyMcExtra.getLog().info("Succeed to build custom-holder resource-packs");
    }

    private static boolean isModelWithinBounds(ModelAsset asset) {
        if (asset.elements().isEmpty()) {
            return true;
        }

        for (ModelElement element : asset.elements().get()) {
            Vec3 from = element.from();
            Vec3 to = element.to();
//            System.out.println("From: " + element.from() + " To: " + element.to());

            // 四舍五入到 3 位小数后比较
            double fx = Math.round(from.x * 1000) / 1000.0;
            double fy = Math.round(from.y * 1000) / 1000.0;
            double fz = Math.round(from.z * 1000) / 1000.0;
            double tx = Math.round(to.x * 1000) / 1000.0;
            double ty = Math.round(to.y * 1000) / 1000.0;
            double tz = Math.round(to.z * 1000) / 1000.0;

            if (fx < -16 || fy < -16 || fz < -16 || tx > 32 || ty > 32 || tz > 32) {
                return false;
            }
        }
        return true;
    }

    public record HolderResource(Block block, String namespace, String modelPath) {
        public static HolderResource of(Block block) {
            ResourceLocation blockId = BuiltInRegistries.BLOCK.getKey(block);
            String namespace = blockId.getNamespace();
            String path = blockId.getPath();
            String modelPath = "assets/%s/models/block/%s".formatted(namespace, path);
            return new HolderResource(block, namespace, modelPath);
        }
    }
}
