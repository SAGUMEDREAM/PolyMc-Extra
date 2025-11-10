package cc.thonly.polymc_extra.mixin;

import cc.thonly.polymc_extra.PolyMcExtra;
import cc.thonly.polymc_extra.config.PolyMcExtraConfig;
import cc.thonly.polymc_extra.config.PolyMcExtraConfigService;
import cc.thonly.polymc_extra.gui.MenuTypeFactoryWrapperSoFabricApiDoesntDetectIt;
import cc.thonly.polymc_extra.api.PolyMcExtraGui;
import eu.pb4.polymer.core.api.other.PolymerScreenHandlerUtils;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;

// source from PolyMc
@Mixin(value = ServerPlayer.class, priority = 600)
public class GuiPolyImplementationMixin {

    @Redirect(
            method = {"openMenu"},
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/MenuProvider;createMenu(ILnet/minecraft/world/entity/player/Inventory;Lnet/minecraft/world/entity/player/Player;)Lnet/minecraft/world/inventory/AbstractContainerMenu;"
            )
    )
    public AbstractContainerMenu handlerId(MenuProvider namedScreenHandlerFactory, int syncId, Inventory inv, Player player) {
        AbstractContainerMenu base = namedScreenHandlerFactory.createMenu(syncId, inv, player);
        if (base == null) {
            return null;
        } else {
            MenuType<?> baseType = base.getType();
            PolyMcExtraConfig config = PolyMcExtra.getConfig();
            PolyMcExtraConfigService service = config.getService();
            if (service.shouldBypassPolyMcHandler(baseType)) {
                return base;
            }
            if (PolymerScreenHandlerUtils.isPolymerType(baseType)) {
                return base;
            }

            PolyMcExtraGui poly = PolyMcExtraGui.MENU_TYPE_REGISTRY.get(baseType);
            return poly != null ? poly.replaceScreenHandler(base, (ServerPlayer) player, syncId) : base;
        }
    }

    @ModifyVariable(
            method = {"openMenu"},
            at = @At("HEAD"),
            argsOnly = true
    )
    private MenuProvider hackForFabricApi(MenuProvider factory) {
        return factory instanceof ExtendedScreenHandlerFactory ? new MenuTypeFactoryWrapperSoFabricApiDoesntDetectIt(factory) : factory;
    }
}
