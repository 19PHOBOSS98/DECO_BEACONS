package net.phoboss.decobeacons.blocks;


import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.phoboss.decobeacons.DecoBeacons;
import net.phoboss.decobeacons.blocks.decobeacon.DecoBeaconBlockEntity;
import net.phoboss.decobeacons.blocks.omnibeacon.OmniBeaconBlockEntity;


import java.util.function.Supplier;

public class ModBlockEntities {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES,DecoBeacons.MOD_ID);

    public static final RegistryObject<BlockEntityType<DecoBeaconBlockEntity>> DECO_BEACON = registerBlockEntities("deco_beacon",
            () ->   BlockEntityType.Builder.of(
                    DecoBeaconBlockEntity::new,
                    ModBlocks.DECO_BEACON.get(),
                    ModBlocks.DECO_BEACON_GHOST.get(),
                    ModBlocks.DECO_BEACON_FAKE.get(),
                    ModBlocks.DECO_BEACON_GHOST_FAKE.get()
            ).build(null));


    public static final RegistryObject<BlockEntityType<OmniBeaconBlockEntity>> OMNI_BEACON = registerBlockEntities("omni_beacon",
            () ->   BlockEntityType.Builder.of(
                    OmniBeaconBlockEntity::new,
                    ModBlocks.OMNI_BEACON.get(),
                    ModBlocks.OMNI_BEACON_GHOST.get()
            ).build(null));

    public static <T extends BlockEntityType> RegistryObject<T> registerBlockEntities(String name, Supplier<T> block){
        return BLOCK_ENTITIES.register(name,block);
    }

    public static void registerAll(IEventBus eventBus) {
        DecoBeacons.LOGGER.info("Registering Mod BlockEntities for " + DecoBeacons.MOD_ID);
        BLOCK_ENTITIES.register(eventBus);
    }
}
