package net.phoboss.decobeacons.rendering;


import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.phoboss.decobeacons.blocks.ModBlockEntities;
import net.phoboss.decobeacons.blocks.ModBlocks;
import net.phoboss.decobeacons.blocks.decobeacon.DecoBeaconBlockEntityRenderer;
import net.phoboss.decobeacons.blocks.omnibeacon.OmniBeaconBlockEntityRenderer;

public class ModRendering {

    public static void registerRenderType() {
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.DECO_BEACON.get(), RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.DECO_BEACON_FAKE.get(), RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.DECO_BEACON_GHOST.get(), RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.DECO_BEACON_GHOST_FAKE.get(), RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.OMNI_BEACON.get(), RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.OMNI_BEACON_GHOST.get(), RenderType.translucent());
    }

    public static void registerBlockEntityRenderers(final EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(ModBlockEntities.DECO_BEACON.get(), DecoBeaconBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.OMNI_BEACON.get(), OmniBeaconBlockEntityRenderer::new);
    }

    public static void registerAll() {
        registerRenderType();
    }
}
