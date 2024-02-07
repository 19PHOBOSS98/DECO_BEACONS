package net.phoboss.decobeacons.blocks;


import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.phoboss.decobeacons.DecoBeacons;
import net.phoboss.decobeacons.blocks.decobeacon.DecoBeaconBlock;
import net.phoboss.decobeacons.blocks.decobeaconghost.DecoBeaconGhostBlock;
import net.phoboss.decobeacons.blocks.omnibeacon.OmniBeaconBlock;
import net.phoboss.decobeacons.blocks.omnibeaconghost.OmniBeaconGhostBlock;
import net.phoboss.decobeacons.items.ModItemGroups;
import net.phoboss.decobeacons.items.ModItems;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, DecoBeacons.MOD_ID);



    public static BlockBehaviour.Properties solidBlockBehaviour = BlockBehaviour
            //.Properties.of(Material.GLASS, MaterialColor.DIAMOND)
            .Properties.copy(Blocks.GLASS)
            .lightLevel((state) -> state.getValue(BlockStateProperties.LIT) ? 15 : 0)
            .noOcclusion();
    public static BlockBehaviour.Properties ghostBlockBehaviour = BlockBehaviour
            //.Properties.of(Material.GLASS, MaterialColor.DIAMOND)
            .Properties.copy(Blocks.GLASS)
            .lightLevel((state) -> state.getValue(BlockStateProperties.LIT) ? 15 : 0)
            .noOcclusion()
            .noCollission();

    public static final RegistryObject<Block> DECO_BEACON = registerBlock(
            "deco_beacon",
            () -> new DecoBeaconBlock(solidBlockBehaviour),
            new ExtraItemSettings()
                    //.setStackLimit(1)
                    .setTooltipKey("block.decobeacons.notghost.tooltip")
    );

    public static final RegistryObject<Block> DECO_BEACON_FAKE = registerBlock(
            "deco_beacon_fake",
            () -> new DecoBeaconBlock(solidBlockBehaviour),
            new ExtraItemSettings()
                    //.setStackLimit(1)
                    .setTooltipKey("block.decobeacons.notghost.tooltip")
    );

    public static final RegistryObject<Block> DECO_BEACON_GHOST = registerBlock(
            "deco_beacon_ghost",
            () -> new DecoBeaconGhostBlock(ghostBlockBehaviour),
            new ExtraItemSettings()
                    //.setStackLimit(1)
                    .setTooltipKey("block.decobeacons.ghost.tooltip")
                    .setTooltipShiftKey("block.decobeacons.ghost.tooltip.shift")
    );

    public static final RegistryObject<Block> DECO_BEACON_GHOST_FAKE = registerBlock(
            "deco_beacon_fake_ghost",
            () -> new DecoBeaconGhostBlock(ghostBlockBehaviour),
            new ExtraItemSettings()
                    //.setStackLimit(1)
                    .setTooltipKey("block.decobeacons.ghost.tooltip")
                    .setTooltipShiftKey("block.decobeacons.ghost.tooltip.shift")
    );

    public static final RegistryObject<Block> OMNI_BEACON = registerBlock(
            "omni_beacon",
            () -> new OmniBeaconBlock(solidBlockBehaviour),
            new ExtraItemSettings()
                    //.setStackLimit(1)
                    .setTooltipKey("block.decobeacons.omni_beacon.tooltip")
                    .setTooltipShiftKey("block.decobeacons.omni_beacon.tooltip.shift")
    );

    public static final RegistryObject<Block> OMNI_BEACON_GHOST = registerBlock(
            "omni_beacon_ghost",
            () -> new OmniBeaconGhostBlock(ghostBlockBehaviour),
            new ExtraItemSettings()
                    //.setStackLimit(1)
                    .setTooltipKey("block.decobeacons.omni_beacon.tooltip")
                    .setTooltipShiftKey("block.decobeacons.omni_beacon.tooltip.shift")
    );



    public static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block){
        RegistryObject<T> toReturn = BLOCKS.register(name,block);
        registerBlockItem(name, toReturn);
        return toReturn;
    }
    public static <T extends Block> RegistryObject<Item> registerBlockItem(String name, RegistryObject<T> block){
        return ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }

    public static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block, ExtraItemSettings extraItemSettings){
        RegistryObject<T> toReturn = BLOCKS.register(name,block);
        registerBlockItem(name, toReturn,extraItemSettings);
        return toReturn;
    }
    public static <T extends Block> RegistryObject<Item> registerBlockItem(String name, RegistryObject<T> block, ExtraItemSettings extraItemSettings){
        return ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties().stacksTo(extraItemSettings.stackLimit)){
            @Override
            public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltip, TooltipFlag pFlag) {
                if (Screen.hasShiftDown()) {
                    if(extraItemSettings.tooltipShiftKey!=null){
                        pTooltip.add(Component.translatable(extraItemSettings.tooltipShiftKey));
                    }
                } else {
                    if(extraItemSettings.tooltipKey!=null) {
                        pTooltip.add(Component.translatable(extraItemSettings.tooltipKey));
                    }
                }
            }
        });
    }

    public static void registerAll(IEventBus eventBus) {
        DecoBeacons.LOGGER.info("Registering Mod Blocks for " + DecoBeacons.MOD_ID);
        BLOCKS.register(eventBus);
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
