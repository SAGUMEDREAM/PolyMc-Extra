package cc.thonly.polymc_extra;

import cc.thonly.polymc_extra.api.IPolymerHolderEntity;
import cc.thonly.polymc_extra.api.ITickHolderEntity;
import cc.thonly.polymc_extra.api.PolyMcExtraEntrypoint;
import cc.thonly.polymc_extra.command.PolymerExtraCommands;
import cc.thonly.polymc_extra.config.PolyMcExtraConfig;
import cc.thonly.polymc_extra.config.PolyMcExtraConfigService;
import cc.thonly.polymc_extra.model.ExtraModelType;
import com.mojang.serialization.DataResult;
import eu.pb4.polymer.core.api.entity.PolymerEntity;
import lombok.extern.slf4j.Slf4j;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.slf4j.Logger;
import xyz.nucleoid.packettweaker.PacketContext;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
public class PolyMcExtra implements ModInitializer, DedicatedServerModInitializer {
    public static final String MOD_ID = "polymc-extra";
    public static final boolean HAS_LOADED_POLYMC = FabricLoader.getInstance().isModLoaded("polymc");
    public static final List<Runnable> LATE_INIT = new ArrayList<>();
    public static final ResourceLocation CHAIR = PolyMcExtra.id("chair");

    @Override
    public void onInitialize() {
        log.info("Loading PolyMc-Extra...");
        try {
            Class.forName(ExtraModelType.class.getName());
        } catch (Exception e) {
            log.error("Can't init ExtraModelType", e);
        }
        this.registerBuiltin();
        FabricLoader fabricLoader = FabricLoader.getInstance();
        List<PolyMcExtraEntrypoint> entrypoints = fabricLoader.getEntrypoints("polymc-extra", PolyMcExtraEntrypoint.class);
        for (PolyMcExtraEntrypoint entrypoint : entrypoints) {
            try {
                entrypoint.onInitialize();
            } catch (Exception err) {
                log.error("Can't load entrypoint {}", entrypoint.getClass(), err);
            }
        }
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            IPolymerHolderEntity.serverTick();
            ITickHolderEntity.serverTick();
        });
        log.info("Loading PolyMc-Extra Lifecycle Events...");
        this.registerLifecycleEvents();
        log.info("Loading PolyMc-Extra Commands...");
        PolymerExtraCommands.bootstrap();
        if (HAS_LOADED_POLYMC) {
            log.warn("PolyMc was detected in the mod list, installing this mod is not recommended.");
        }
    }

    private void registerLifecycleEvents() {
        ServerLifecycleEvents.END_DATA_PACK_RELOAD.register((minecraftServer, lifecycledResourceManager, b) -> {
            RegistryAccess.Frozen registryAccess = minecraftServer.registryAccess();
            Registry<Item> items = registryAccess.lookupOrThrow(Registries.ITEM);
            CompletableFuture.runAsync(() -> {
                log.info("Verifying item serialization...");

                int total = 0;
                int valid = 0;
                int invalid = 0;

                for (Item item : items) {
                    ItemStack stack = item.getDefaultInstance();
                    if (stack.isEmpty()) {
                        continue;
                    }

                    total++;

                    try {
                        DataResult<ItemStack> result = ItemStack.validateStrict(stack);

                        if (result.result().isPresent()) {
                            valid++;
                        } else if (result.error().isPresent()) {
                            invalid++;
                            String errorMessage = result.error().get().message();
                            log.warn("{} -> {}", items.getKey(item), errorMessage);
                        }
                    } catch (Exception err) {
                        invalid++;
                        log.error("Unable to validate item {}: {}", items.getKey(item), err.toString());
                    }
                }

                log.info("Item validation complete. Total: {}, Valid: {}, Invalid: {}", total, valid, invalid);
            });

        });
    }

    private void registerBuiltin() {
        PolyMcExtraConfigService.registerSpecialReplacedType(CHAIR, entityType -> new PolymerEntity() {
            @Override
            public EntityType<?> getPolymerEntityType(PacketContext context) {
                return EntityType.ARROW;
            }
        });
    }

    @Override
    public void onInitializeServer() {

    }

    public static PolyMcExtraConfig getConfig() {
        return PolyMcExtraConfig.getConfig();
    }

    public static ResourceLocation id(String name) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, name);
    }

    public static Logger getLog() {
        return log;
    }
}