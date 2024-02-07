package net.phoboss.decobeacons.items;


import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.phoboss.decobeacons.blocks.ModBlocks;


public class ModItemGroups {

    public static ItemGroup DECO_BEACON  = FabricItemGroupBuilder.build(
            new Identifier("decobeacons", "deco_beacon"),
            () -> new ItemStack(
                    ModBlocks.OMNI_BEACON)
    );

}
