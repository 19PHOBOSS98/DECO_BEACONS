package net.phoboss.decobeacons;

import com.mojang.logging.LogUtils;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.phoboss.decobeacons.blocks.ModBlockEntities;
import net.phoboss.decobeacons.blocks.ModBlocks;
import net.phoboss.decobeacons.items.ModItems;
import net.phoboss.decobeacons.rendering.ModRendering;
import org.slf4j.Logger;

import java.nio.file.Path;
import java.util.stream.Collectors;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(DecoBeacons.MOD_ID)
public class DecoBeacons
{
    public static final String MOD_ID = "decobeacons";
    public static final Logger LOGGER = LogUtils.getLogger();

    public DecoBeacons()
    {
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        ModBlocks.registerAll(eventBus);
        ModItems.registerAll(eventBus);

        ModBlockEntities.registerAll(eventBus);

        eventBus.addListener(this::setup);
        eventBus.addListener(this::setupClient);
        MinecraftForge.EVENT_BUS.register(this);
    }
    private void setupClient(final FMLCommonSetupEvent event)
    {
        ModRendering.registerAll();
    }
    private void setup(final FMLCommonSetupEvent event)
    {
        // some preinit code
        LOGGER.info("HELLO FROM PREINIT");
        LOGGER.info("DIRT BLOCK >> {}", Blocks.DIRT.getRegistryName());
    }

    @Mod.EventBusSubscriber(modid = DecoBeacons.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public class ModEventClientBusEvents {
        @SubscribeEvent
        public static void registerRenderers(final EntityRenderersEvent.RegisterRenderers event){
            ModRendering.registerBlockEntityRenderers(event);
        }
    }
}
