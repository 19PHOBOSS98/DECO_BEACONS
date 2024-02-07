package net.phoboss.decobeacons;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.phoboss.decobeacons.rendering.ModRendering;

@Environment(EnvType.CLIENT)
public class DecoBeaconsClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ModRendering.registerAll();
    }
}
