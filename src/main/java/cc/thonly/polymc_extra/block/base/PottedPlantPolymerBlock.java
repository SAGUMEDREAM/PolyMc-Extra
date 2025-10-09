package cc.thonly.polymc_extra.block.base;

import eu.pb4.factorytools.api.block.FactoryBlock;
import eu.pb4.factorytools.api.virtualentity.BlockModel;
import eu.pb4.factorytools.api.virtualentity.ItemDisplayElementUtil;
import eu.pb4.polymer.blocks.api.PolymerTexturedBlock;
import eu.pb4.polymer.virtualentity.api.ElementHolder;
import eu.pb4.polymer.virtualentity.api.elements.ItemDisplayElement;
import lombok.Getter;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RotationAxis;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import xyz.nucleoid.packettweaker.PacketContext;

@Getter
public class PottedPlantPolymerBlock implements PolymerTexturedBlock, FactoryBlock {
    private final Identifier blockId;
    private final ItemStack MODEL;

    public PottedPlantPolymerBlock(Identifier blockId) {
        this(blockId, false);
    }
    public PottedPlantPolymerBlock(Identifier blockId, boolean useExtraModel) {
        this.blockId = blockId;
        MODEL = ItemDisplayElementUtil.getModel(Identifier.of(blockId.getNamespace(), "block/%s".formatted(useExtraModel ? blockId.getPath() : blockId.getPath().replace("potted_", ""))));
    }

    @Override
    public BlockState getPolymerBlockState(BlockState state, PacketContext context) {
        return Blocks.FLOWER_POT.getDefaultState();
    }


    @Override
    public @Nullable ElementHolder createElementHolder(ServerWorld world, BlockPos pos, BlockState initialBlockState) {
        return new ItemDisplayPottedPlantModel(pos);
    }

    public class ItemDisplayPottedPlantModel extends BlockModel {

        public ItemDisplayPottedPlantModel(BlockPos pos) {
            ItemDisplayElement main = ItemDisplayElementUtil.createSimple(MODEL);
            main.setScale(new Vector3f(0.98f));
            main.setDisplaySize(1, 1);
            int rotation = pos.hashCode() % 4 * 90;
            main.setRightRotation(RotationAxis.POSITIVE_Y.rotationDegrees(rotation));
            this.addElement(main);
        }
    }
}
