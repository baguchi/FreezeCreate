package baguchan.freeze_create;

import baguchan.freeze_create.capablity.FreezeCapability;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = FreezeCreate.MODID)
public class CommonEvents {
    @SubscribeEvent
    public static void onRegisterEntityCapabilities(RegisterCapabilitiesEvent event) {
        event.register(FreezeCapability.class);
    }

    @SubscribeEvent
    public static void onAttachEntityCapabilities(AttachCapabilitiesEvent<BlockEntity> event) {
        event.addCapability(new ResourceLocation(FreezeCreate.MODID, "freeze"), new FreezeCapability());
    }

    @SubscribeEvent
    public static void toolTip(ItemTooltipEvent event){
        if(event.getEntity() != null) {
            CompoundTag compoundtag = event.getItemStack().getTag();
            if (compoundtag != null) {
                if (compoundtag.contains("Rotten")) {
                    event.getToolTip().add(Component.translatable("freeze_create.rotten"));
                }
                if (compoundtag.contains("Freeze")) {
                    event.getToolTip().add(Component.translatable("freeze_create.freeze"));
                }
                if (!compoundtag.contains("Rotten")) {
                    if (compoundtag.contains("FoodDay")) {
                        long foodDay = compoundtag.contains("FoodDay") ? compoundtag.getInt("FoodDay") : 0;
                        int day = FreezeConfigs.COMMON.rottenDay.get();
                        event.getToolTip().add(Component.translatable("freeze_create.food_day", (foodDay + day) - (event.getEntity().level.getGameTime() / 24000)));
                    }
                }
            }
        }
    }
}
