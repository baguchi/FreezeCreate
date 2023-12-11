package baguchan.freeze_create.data;

import baguchan.freeze_create.FreezeCreate;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = FreezeCreate.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DataGenerators {
    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        BlockTagsProvider blocktags = new BlockTagGenerator(event.getGenerator(), event.getExistingFileHelper());
        event.getGenerator().addProvider(event.includeServer(), blocktags);

        event.getGenerator().addProvider(event.includeServer(), new FluidTagGenerator(event.getGenerator(), event.getExistingFileHelper()));
    }
}