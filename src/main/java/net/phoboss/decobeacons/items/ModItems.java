package net.phoboss.decobeacons.items;


import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.phoboss.decobeacons.DecoBeacons;

public class ModItems {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, DecoBeacons.MOD_ID);

    public static void registerAll(IEventBus eventBus){
        DecoBeacons.LOGGER.debug("Registering Mod Items for "+ DecoBeacons.MOD_ID);
        ITEMS.register(eventBus);
    }
}
