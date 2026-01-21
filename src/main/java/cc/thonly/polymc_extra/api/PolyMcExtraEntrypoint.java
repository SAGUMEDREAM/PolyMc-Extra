package cc.thonly.polymc_extra.api;

import cc.thonly.polymc_extra.PolyMcExtra;
import cc.thonly.polymc_extra.config.PolyMcExtraConfigService;
import cc.thonly.polymc_extra.model.ExtraModelType;
import cc.thonly.polymc_extra.util.PolymerBlockHelper;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

public interface PolyMcExtraEntrypoint {
    void onInitialize();

    default ExtraModelType registerExtraModelType(@NotNull String name, ExtraModelType.StateFactory factory) {
        return ExtraModelType.of(name, factory);
    }

    /**
     * When the service object is obtained, the configuration file has already been parsed, but you can still use Map.put to manipulate the Polymer overlay you want before the registry freezes.
     **/
    default PolyMcExtraConfigService getService() {
        return PolyMcExtra.getConfig().getService();
    }

    /**
     * Register a Polymer block overlay for a certain type of block.
     **/
    default <T extends Block> void registerType(Class<T> type, PolymerBlockHelper.BlockRegisterFactory<T> register) {
        PolymerBlockHelper.registerType(type, register);
    }

    /**
     * API for retrieving Namespace
     **/
    String getNamespace();

    /**
     * API for retrieving ID
     **/
    default Identifier id(String name) {
        return Identifier.fromNamespaceAndPath(this.getNamespace(), name);
    }

    /**
     * API for retrieving polymc-extra id
     **/
    default Identifier polymcExtraId(String name) {
        return PolyMcExtra.id(name);
    }
}
