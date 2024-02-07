package net.phoboss.decobeacons.items;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import net.phoboss.decobeacons.DecoBeacons;
import net.phoboss.decobeacons.blocks.ModBlocks;


public class ModItemGroups {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS  = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, DecoBeacons.MOD_ID);

    public static final RegistryObject<CreativeModeTab> DECO_BEACON = CREATIVE_MODE_TABS.register("decobeacons_tab",
            ()-> CreativeModeTab.builder().icon(()-> new ItemStack(ModBlocks.OMNI_BEACON.get()))
                    .title(Component.translatable("itemGroup.decobeacons_tab"))
                    .displayItems((parameters,output) ->{
                        output.accept(ModBlocks.DECO_BEACON.get());
                        output.accept(ModBlocks.DECO_BEACON_FAKE.get());
                        output.accept(ModBlocks.OMNI_BEACON.get());
                        output.accept(ModBlocks.DECO_BEACON_GHOST.get());
                        output.accept(ModBlocks.DECO_BEACON_GHOST_FAKE.get());
                        output.accept(ModBlocks.OMNI_BEACON_GHOST.get());
                    })
                    .build());

    public static void registerAll(IEventBus eventBus){
        CREATIVE_MODE_TABS.register(eventBus);
    }
}
