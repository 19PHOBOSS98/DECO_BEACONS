package net.phoboss.decobeacons.blocks;


import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.phoboss.decobeacons.DecoBeacons;
import net.phoboss.decobeacons.blocks.decobeacon.DecoBeaconBlock;
import net.phoboss.decobeacons.blocks.decobeaconghost.DecoBeaconGhostBlock;
import net.phoboss.decobeacons.blocks.omnibeacon.OmniBeaconBlock;
import net.phoboss.decobeacons.blocks.omnibeaconghost.OmniBeaconGhostBlock;
import net.phoboss.decobeacons.items.ModItemGroups;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ModBlocks {

    public static AbstractBlock.Settings solidBlockBehaviour = FabricBlockSettings
            .copyOf(Blocks.GLASS).luminance((state) -> state.get(Properties.LIT) ? 15 : 0)
            .nonOpaque();
    public static AbstractBlock.Settings ghostBlockBehaviour = FabricBlockSettings
            .copyOf(Blocks.GLASS).luminance((state) -> state.get(Properties.LIT) ? 15 : 0)
            .nonOpaque()
            .noCollision();
    public static final Block DECO_BEACON = registerBlock(
            "deco_beacon",
            new DecoBeaconBlock(solidBlockBehaviour),

            new ExtraItemSettings()
                    //.setStackLimit(1)
                    .setTooltipKey("block.decobeacons.notghost.tooltip")
    );

    public static final Block DECO_BEACON_FAKE = registerBlock(
            "deco_beacon_fake",
            new DecoBeaconBlock(solidBlockBehaviour),

            new ExtraItemSettings()
                    //.setStackLimit(1)
                    .setTooltipKey("block.decobeacons.notghost.tooltip")
    );

    public static final Block DECO_BEACON_GHOST = registerBlock(
            "deco_beacon_ghost",
            new DecoBeaconGhostBlock(ghostBlockBehaviour),

            new ExtraItemSettings()
                    //.setStackLimit(1)
                    .setTooltipKey("block.decobeacons.ghost.tooltip")
                    .setTooltipShiftKey("block.decobeacons.ghost.tooltip.shift")
    );

    public static final Block DECO_BEACON_GHOST_FAKE = registerBlock(
            "deco_beacon_fake_ghost",
            new DecoBeaconGhostBlock(ghostBlockBehaviour),

            new ExtraItemSettings()
                    //.setStackLimit(1)
                    .setTooltipKey("block.decobeacons.ghost.tooltip")
                    .setTooltipShiftKey("block.decobeacons.ghost.tooltip.shift")
    );

    public static final Block OMNI_BEACON = registerBlock(
            "omni_beacon",
            new OmniBeaconBlock(solidBlockBehaviour),

            new ExtraItemSettings()
                    //.setStackLimit(1)
                    .setTooltipKey("block.decobeacons.omni_beacon.tooltip")
                    .setTooltipShiftKey("block.decobeacons.omni_beacon.tooltip.shift")
    );

    public static final Block OMNI_BEACON_GHOST = registerBlock(
            "omni_beacon_ghost",
            new OmniBeaconGhostBlock(ghostBlockBehaviour),

            new ExtraItemSettings()
                    //.setStackLimit(1)
                    .setTooltipKey("block.decobeacons.omni_beacon.tooltip")
                    .setTooltipShiftKey("block.decobeacons.omni_beacon.tooltip.shift")
    );



    public static Block registerBlock(String name, Block block){
        registerBlockItem(name, block);
        return Registry.register(Registries.BLOCK, new Identifier(DecoBeacons.MOD_ID,name),block);
    }
    public static Item registerBlockItem(String name, Block block){
        return Registry.register(Registries.ITEM, new Identifier(DecoBeacons.MOD_ID,name),new BlockItem(block, new FabricItemSettings()));
    }

    public static Block registerBlock(String name, Block block, ExtraItemSettings extraItemSettings){
        registerBlockItem(name, block, extraItemSettings);
        return Registry.register(Registries.BLOCK, new Identifier(DecoBeacons.MOD_ID,name),block);
    }
    public static Item registerBlockItem(String name, Block block, ExtraItemSettings extraItemSettings){
        return Registry.register(Registries.ITEM, new Identifier(DecoBeacons.MOD_ID,name),new BlockItem(block, new FabricItemSettings().maxCount(extraItemSettings.stackLimit)){
            @Override
            public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
                if (Screen.hasShiftDown()) {
                    if(extraItemSettings.tooltipShiftKey!=null){
                        tooltip.add(Text.translatable(extraItemSettings.tooltipShiftKey));
                    }
                } else {
                    if(extraItemSettings.tooltipKey!=null) {
                        tooltip.add(Text.translatable(extraItemSettings.tooltipKey));
                    }
                }
            }
        });
    }


    public static void registerAll() {
        DecoBeacons.LOGGER.info("Registering Mod Blocks for " + DecoBeacons.MOD_ID);
    }

    public static class ExtraItemSettings {
        public int stackLimit=64;
        public String tooltipShiftKey;

        public String tooltipKey;

        public ExtraItemSettings setStackLimit(int stackLimit) {
            this.stackLimit = stackLimit;
            return this;
        }


        public ExtraItemSettings setTooltipShiftKey(String tooltipShiftKey) {
            this.tooltipShiftKey = tooltipShiftKey;
            return this;
        }

        public ExtraItemSettings setTooltipKey(String tooltipKey) {
            this.tooltipKey = tooltipKey;
            return this;
        }

        public ExtraItemSettings() {
        }
    }
}
