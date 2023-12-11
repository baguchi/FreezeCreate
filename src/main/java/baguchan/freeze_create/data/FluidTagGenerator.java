package baguchan.freeze_create.data;

import baguchan.freeze_create.FreezeCreate;
import baguchan.freeze_create.register.ModTags;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.FluidTagsProvider;
import net.minecraft.tags.FluidTags;
import net.minecraftforge.common.data.ExistingFileHelper;

public class FluidTagGenerator extends FluidTagsProvider {
    public FluidTagGenerator(DataGenerator generator, ExistingFileHelper exFileHelper) {
        super(generator, FreezeCreate.MODID, exFileHelper);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void addTags() {
        this.tag(ModTags.HOT_FLUID).addTag(FluidTags.LAVA);
    }
}