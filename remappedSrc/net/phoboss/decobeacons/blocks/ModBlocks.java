package net.phoboss.decobeacons.blocks;


import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.MapColor;
import net.minecraft.block.Material;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.phoboss.decobeacons.DecoBeacons;
import net.phoboss.decobeacons.blocks.decobeacon.DecoBeaconBlock;
import net.phoboss.decobeacons.blocks.decobeaconghost.DecoBeaconGhostBlock;
import net.phoboss.decobeacons.blocks.omnibeacon.OmniBeaconBlock;
import net.phoboss.decobeacons.blocks.omnibeaconghost.OmniBeaconGhostBlock;
import net.phoboss.decobeacons.items.ModItemGroups;
import org.jetbrains.annotations.Nullable;

import javax.imageio.spi.RegisterableService;
import java.util.List;
import java.util.function.Supplier;

public class ModBlocks {
    public static AbstractBlock.Settings solidBlockBehaviour = AbstractBlock
            .Settings.of(Material.GLASS, MapColor.DIAMOND_BLUE)
            .luminance((state) -> state.get(Properties.LIT) ? 15 : 0)
            .nonOpaque();
    public static AbstractBlock.Settings ghostBlockBehaviour = AbstractBlock
            .Settings.of(Material.GLASS, MapColor.DIAMOND_BLUE)
            .luminance((state) -> state.get(Properties.LIT) ? 15 : 0)
            .nonOpaque()
            .noCollision();
    public static final Block DECO_BEACON = ModBlocks.registerBlock(
            "deco_beacon",
            new DecoBeaconBlock(solidBlockBehaviour),
            ModItemGroups.DECO_BEACON,
            new ExtraItemSettings()
                    //.setStackLimit(1)
                    .setTooltipKey("block.decobeacons.notghost.tooltip")
    );

    public static final Block DECO_BEACON_FAKE = ModBlocks.registerBlock(
            "deco_beacon_fake",
            new DecoBeaconBlock(solidBlockBehaviour),
            ModItemGroups.DECO_BEACON,
            new ExtraItemSettings()
                    //.setStackLimit(1)
                    .setTooltipKey("block.decobeacons.notghost.tooltip")
    );

    public static final Block DECO_BEACON_GHOST = ModBlocks.registerBlock(
            "deco_beacon_ghost",
            new DecoBeaconGhostBlock(ghostBlockBehaviour),
            ModItemGroups.DECO_BEACON,
            new ExtraItemSettings()
                    //.setStackLimit(1)
                    .setTooltipKey("block.decobeacons.ghost.tooltip")
                    .setTooltipShiftKey("block.decobeacons.ghost.tooltip.shift")
    );

    public static final Block DECO_BEACON_GHOST_FAKE = ModBlocks.registerBlock(
            "deco_beacon_fake_ghost",
            new DecoBeaconGhostBlock(ghostBlockBehaviour),
            ModItemGroups.DECO_BEACON,
            new ExtraItemSettings()
                    //.setStackLimit(1)
                    .setTooltipKey("block.decobeacons.ghost.tooltip")
                    .setTooltipShiftKey("block.decobeacons.ghost.tooltip.shift")
    );

    public static final Block OMNI_BEACON = ModBlocks.registerBlock(
            "omni_beacon",
            new OmniBeaconBlock(solidBlockBehaviour),
            ModItemGroups.DECO_BEACON,
            new ExtraItemSettings()
                    //.setStackLimit(1)
                    .setTooltipKey("block.decobeacons.omni_beacon.tooltip")
                    .setTooltipShiftKey("block.decobeacons.omni_beacon.tooltip.shift")
    );

    public static final Block OMNI_BEACON_GHOST = ModBlocks.registerBlock(
            "omni_beacon_ghost",
            new OmniBeaconGhostBlock(ghostBlockBehaviour),
            ModItemGroups.DECO_BEACON,
            new ExtraItemSettings()
                    //.setStackLimit(1)
                    .setTooltipKey("block.decobeacons.omni_beacon.tooltip")
                    .setTooltipShiftKey("block.decobeacons.omni_beacon.tooltip.shift")
    );



    public static Block registerBlock(String name, Block block, ItemGroup group){
        registerBlockItem(name, block, group);
        return Registry.register(Registry.BLOCK, new Identifier(DecoBeacons.MOD_ID,name),block);
    }
    public static Item registerBlockItem(String name, Block block, ItemGroup group){
        return Registry.register(Registry.ITEM, new Identifier(DecoBeacons.MOD_ID,name),new BlockItem(block, new FabricItemSettings().group(group)));
    }

    public static Block registerBlock(String name, Block block, ItemGroup group, ExtraItemSettings extraItemSettings){
        registerBlockItem(name, block, group, extraItemSettings);
        return Registry.register(Registry.BLOCK, new Identifier(DecoBeacons.MOD_ID,name),block);
    }
    public static Item registerBlockItem(String name, Block block, ItemGroup group, ExtraItemSettings extraItemSettings){
        return Registry.register(Registry.ITEM, new Identifier(DecoBeacons.MOD_ID,name),new BlockItem(block, new FabricItemSettings().group(group).maxCount(extraItemSettings.stackLimit)){
            @Override
            public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
                if (Screen.hasShiftDown()) {
                    if(extraItemSettings.tooltipShiftKey!=null){
                        tooltip.add(new TranslatableTextContent(extraItemSettings.tooltipShiftKey));
                    }
                } else {
                    if(extraItemSettings.tooltipKey!=null) {
                        tooltip.add(new TranslatableTextContent(extraItemSettings.tooltipKey));
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
