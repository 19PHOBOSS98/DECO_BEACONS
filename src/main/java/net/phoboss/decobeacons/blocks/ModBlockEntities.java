package net.phoboss.decobeacons.blocks;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.phoboss.decobeacons.DecoBeacons;
import net.phoboss.decobeacons.blocks.decobeacon.DecoBeaconBlockEntity;
import net.phoboss.decobeacons.blocks.omnibeacon.OmniBeaconBlockEntity;


import java.util.function.Supplier;

public class ModBlockEntities {

    public static BlockEntityType<DecoBeaconBlockEntity> DECO_BEACON;

    public static BlockEntityType<OmniBeaconBlockEntity> OMNI_BEACON;


    public static void registerAll() {
        DECO_BEACON = Registry.register(
                Registry.BLOCK_ENTITY_TYPE,
                new Identifier(DecoBeacons.MOD_ID,"deco_beacon"),
                FabricBlockEntityTypeBuilder.create(
                        DecoBeaconBlockEntity::new,
                        ModBlocks.DECO_BEACON,
                        ModBlocks.DECO_BEACON_GHOST,
                        ModBlocks.DECO_BEACON_FAKE,
                        ModBlocks.DECO_BEACON_GHOST_FAKE
                        ).build(null));

        OMNI_BEACON = Registry.register(
                Registry.BLOCK_ENTITY_TYPE,
                new Identifier(DecoBeacons.MOD_ID,"omni_beacon"),
                FabricBlockEntityTypeBuilder.create(
                        OmniBeaconBlockEntity::new,
                        ModBlocks.OMNI_BEACON,
                        ModBlocks.OMNI_BEACON_GHOST
                ).build(null));

    }
}
