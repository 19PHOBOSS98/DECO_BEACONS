package net.phoboss.decobeacons.items;



import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.phoboss.decobeacons.DecoBeacons;
import net.phoboss.decobeacons.blocks.ModBlocks;


public class ModItemGroups {
    public static ItemGroup DECO_BEACON = Registry.register(Registries.ITEM_GROUP, new Identifier(DecoBeacons.MOD_ID,"deco_beacon"),
            FabricItemGroup.builder()
                    .displayName(Text.translatable("itemGroup.decobeacons.deco_beacon"))
                    .icon(() -> new ItemStack(ModBlocks.OMNI_BEACON))
                    .entries(((displayContext, entries) -> {
                        entries.add(ModBlocks.DECO_BEACON);
                        entries.add(ModBlocks.DECO_BEACON_FAKE);
                        entries.add(ModBlocks.OMNI_BEACON);
                        entries.add(ModBlocks.DECO_BEACON_GHOST);
                        entries.add(ModBlocks.DECO_BEACON_GHOST_FAKE);
                        entries.add(ModBlocks.OMNI_BEACON_GHOST);
                    }))
                    .build()
    );

    public static void registerAll(){

    }

}
