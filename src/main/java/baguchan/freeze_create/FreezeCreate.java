package baguchan.freeze_create;

import baguchan.freeze_create.capablity.FreezeCapability;
import baguchan.freeze_create.create.FreezeFanBehaviour;
import baguchan.freeze_create.message.FreezeMessage;
import com.mojang.logging.LogUtils;
import com.simibubi.create.api.event.BlockEntityBehaviourEvent;
import com.simibubi.create.content.kinetics.fan.EncasedFanBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(FreezeCreate.MODID)
public class FreezeCreate
{
    // Define mod id in a common place for everything to reference
    public static final String MODID = "freeze_create";
    public static final Capability<FreezeCapability> FREEZE_CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {
    });
    public static final String NETWORK_PROTOCOL = "2";
    public static final SimpleChannel CHANNEL = NetworkRegistry.ChannelBuilder.named(new ResourceLocation(MODID, "net"))
            .networkProtocolVersion(() -> NETWORK_PROTOCOL)
            .clientAcceptedVersions(NETWORK_PROTOCOL::equals)
            .serverAcceptedVersions(NETWORK_PROTOCOL::equals)
            .simpleChannel();
    public FreezeCreate()
    {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        setupMessages();
        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);
        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.addGenericListener(EncasedFanBlockEntity.class, (BlockEntityBehaviourEvent<EncasedFanBlockEntity> event) -> event
                .attach(new FreezeFanBehaviour(event.getBlockEntity())));
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {
    }
    private void setupMessages() {
        CHANNEL.messageBuilder(FreezeMessage.class, 0)
                .encoder(FreezeMessage::writePacketData).decoder(FreezeMessage::readPacketData)
                .consumerMainThread(FreezeMessage::handle)
                .add();
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event)
    {
    }
    
}
