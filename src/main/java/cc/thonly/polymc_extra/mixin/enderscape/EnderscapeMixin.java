//package cc.thonly.polymc_extra.mixin.enderscape;
//
//import lombok.extern.slf4j.Slf4j;
//import net.bunten.enderscape.Enderscape;
//import net.bunten.enderscape.registry.EnderscapeBlocks;
//import net.minecraft.world.level.block.entity.BlockEntityType;
//import org.spongepowered.asm.mixin.Mixin;
//import org.spongepowered.asm.mixin.Pseudo;
//import org.spongepowered.asm.mixin.injection.At;
//import org.spongepowered.asm.mixin.injection.Inject;
//import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//
//@Slf4j
//@Pseudo
//@Mixin(Enderscape.class)
//public class EnderscapeMixin {
//    @Inject(method = "onInitialize", at = @At("TAIL"))
//    public void onInitializePatching(CallbackInfo ci) {
//        try {
//            BlockEntityType.SHELF.addValidBlock(EnderscapeBlocks.VEILED_SHELF);
//            BlockEntityType.SHELF.addValidBlock(EnderscapeBlocks.CELESTIAL_SHELF);
//            BlockEntityType.SHELF.addValidBlock(EnderscapeBlocks.MURUBLIGHT_SHELF);
//        } catch (Exception e) {
//            log.error("Error: ", e);
//        }
//    }
//}
