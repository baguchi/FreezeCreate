package baguchan.freeze_create.data;

import baguchan.freeze_create.FreezeCreate;
import baguchan.freeze_create.register.ModTags;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.data.ExistingFileHelper;

public class BlockTagGenerator extends BlockTagsProvider {
    public BlockTagGenerator(DataGenerator generator, ExistingFileHelper exFileHelper) {
        super(generator, FreezeCreate.MODID, exFileHelper);
    }

    @Override
    protected void addTags() {
        this.tag(ModTags.FREEZE_BLOCK).addTag(BlockTags.ICE);
        this.tag(ModTags.PASSES_FREEZE_FILL).addTag(BlockTags.WOODEN_FENCES).add(Blocks.IRON_BARS);
        this.tag(ModTags.HOT_BLOCK).add(Blocks.MAGMA_BLOCK).addTag(BlockTags.FIRE).addTag(BlockTags.CAMPFIRES);
    }
}