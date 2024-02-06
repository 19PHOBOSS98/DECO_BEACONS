package net.phoboss.decobeacons.items;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.phoboss.decobeacons.blocks.ModBlocks;


public class ModItemGroups {

    public static CreativeModeTab DECO_BEACON  = new CreativeModeTab("decobeacons_tab") {
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(ModBlocks.OMNI_BEACON.get());
        }
    };

}
