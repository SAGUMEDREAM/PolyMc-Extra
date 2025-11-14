package cc.thonly.polymc_extra.mixin.accessor;

import eu.pb4.polymer.resourcepack.api.ResourcePackCreator;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.nio.file.Path;
import java.util.Set;

@Mixin(ResourcePackCreator.class)
public interface ResourcePackCreatorAccessor {
    @Accessor("modIds")
    Set<String> polymcExtra$getModIds();

    @Accessor("modIdsNoCopy")
    Set<String> polymcExtra$getModIdsNoCopy();

    @Accessor("sourcePaths")
    Set<Path> polymcExtra$getSourcePaths();
}
