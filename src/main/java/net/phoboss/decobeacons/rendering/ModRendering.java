package net.phoboss.decobeacons.rendering;

import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.minecraft.client.render.RenderLayer;
import net.phoboss.decobeacons.blocks.ModBlockEntities;
import net.phoboss.decobeacons.blocks.ModBlocks;
import net.phoboss.decobeacons.blocks.decobeacon.DecoBeaconBlockEntityRenderer;
import net.phoboss.decobeacons.blocks.omnibeacon.OmniBeaconBlockEntityRenderer;


public class ModRendering {

    public static void registerRenderType() {
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.DECO_BEACON, RenderLayer.getTranslucent());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.DECO_BEACON_FAKE, RenderLayer.getTranslucent());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.DECO_BEACON_GHOST, RenderLayer.getTranslucent());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.DECO_BEACON_GHOST_FAKE, RenderLayer.getTranslucent());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.OMNI_BEACON, RenderLayer.getTranslucent());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.OMNI_BEACON_GHOST, RenderLayer.getTranslucent());
    }

    public static void registerBlockEntityRenderers() {
        BlockEntityRendererRegistry.register(ModBlockEntities.DECO_BEACON, DecoBeaconBlockEntityRenderer::new);
        BlockEntityRendererRegistry.register(ModBlockEntities.OMNI_BEACON, OmniBeaconBlockEntityRenderer::new);
    }

    public static void registerAll() {
        registerRenderType();
        registerBlockEntityRenderers();
    }
}
