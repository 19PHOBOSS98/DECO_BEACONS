package net.phoboss.decobeacons.blocks.decobeacon;

import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.DyeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Hand;
import net.minecraft.util.Util;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.phoboss.decobeacons.DecoBeacons;
import net.phoboss.decobeacons.blocks.ModBlockEntities;
import net.phoboss.decobeacons.utility.BookSettingsUtility;
import net.phoboss.decobeacons.utility.ErrorResponse;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class DecoBeaconBlock extends BlockWithEntity implements BlockEntityProvider, Stainable, BookSettingsUtility {
    public static final IntProperty COLOR = IntProperty.of("color",0,15);
    public static Map<String, DyeColor> COLOR_NAME_DICTIONARY = Util.make(new Object2ObjectLinkedOpenHashMap<>(), (map) -> {
        map.put("white",DyeColor.WHITE);
        map.put("orange",DyeColor.ORANGE);
        map.put("magenta",DyeColor.MAGENTA);
        map.put("light_blue",DyeColor.LIGHT_BLUE);
        map.put("yellow",DyeColor.YELLOW);
        map.put("lime",DyeColor.LIME);
        map.put("pink",DyeColor.PINK);
        map.put("gray",DyeColor.GRAY);
        map.put("light_gray",DyeColor.LIGHT_GRAY);
        map.put("cyan",DyeColor.CYAN);
        map.put("purple",DyeColor.PURPLE);
        map.put("blue",DyeColor.BLUE);
        map.put("brown",DyeColor.BROWN);
        map.put("green",DyeColor.GREEN);
        map.put("red",DyeColor.RED);
        map.put("black",DyeColor.BLACK);
    });
    public DecoBeaconBlock(Settings settings) {
        super(settings);
        this.setDefaultState(getDefaultState().with(COLOR, 0).with(Properties.LIT, false));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(COLOR)
                .add(Properties.LIT);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return checkType(type, ModBlockEntities.DECO_BEACON, DecoBeaconBlockEntity::tick);
    }

    @Override
    public DyeColor getColor() {
        return DyeColor.WHITE;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new DecoBeaconBlockEntity(pos,state);
    }
    @Override
    public ActionResult onUse(BlockState state,
                              World world,
                              BlockPos pos,
                              PlayerEntity player,
                              Hand hand,
                              BlockHitResult hit) {
        ItemStack mainHandItemStack = player.getMainHandStack();
        Item mainHandItem = mainHandItemStack.getItem();
        if(hand == Hand.MAIN_HAND){
            if(world.isClient()){
                return ActionResult.SUCCESS;
            }

            DecoBeaconBlockEntity blockEntity = (DecoBeaconBlockEntity) world.getBlockEntity(pos);
            if(blockEntity == null){
                return ActionResult.FAIL;
            }
            if(mainHandItem instanceof DyeItem itemDye){
                blockEntity.setCurColorID(itemDye.getColor().getId());
                return ActionResult.SUCCESS;

            }else if(mainHandItem == Items.AIR){
                int delta = player.isSneaking() ? -1 : 1;
                int currentColor = Math.floorMod((state.get(COLOR) + delta),16);
                blockEntity.setCurColorID(currentColor);
                return ActionResult.SUCCESS;

            }else if(mainHandItem == Items.REDSTONE_TORCH){
                blockEntity.setActiveLow(!blockEntity.isActiveLow());
                return ActionResult.SUCCESS;

            }else if(mainHandItem == Items.SAND){
                blockEntity.setTransparent(!blockEntity.isTransparent());
                return ActionResult.SUCCESS;

            }else if(mainHandItemStack.hasNbt() && mainHandItemStack.getNbt().contains("pages")){
                try {
                    ActionResult result = executeBookProtocol(mainHandItemStack, state, world, pos, player, blockEntity, blockEntity.bookSettings);
                    if(result == ActionResult.FAIL){
                        refreshBlockEntityBookSettings(state,blockEntity);
                    }
                    return result;
                }catch(Exception e){
                    DecoBeacons.LOGGER.error(e.getMessage(),e);
                    return ActionResult.FAIL;
                }
            }
        }
        return ActionResult.PASS;
    }


    @Override
    public ActionResult implementBookSettings(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockEntity blockEntity, Map<String, String> bookSettings) {
        DecoBeaconBlockEntity decoBeaconBlockEntity = (DecoBeaconBlockEntity)blockEntity;

        String color = bookSettings.get("color");
        String activeLow = bookSettings.get("activeLow");
        String isTransparent = bookSettings.get("isTransparent");
        try{
            if(!color.isEmpty()) {
                color = color.toLowerCase();
                //int colorID = DyeColor.byName(color, DyeColor.WHITE).getId();//this doesn't cause an error when it fails
                int colorID = COLOR_NAME_DICTIONARY.get(color).getId();//I need it to cause an error when it fails
                decoBeaconBlockEntity.setCurColorID(colorID);
            }
        }catch(Exception e){
            DecoBeacons.LOGGER.error("Error: ", e);
            return ErrorResponse.onErrorActionResult(world,pos,player,"Unrecognized value: color:"+color);
        }

        try{
            if(!activeLow.isEmpty()) {
                decoBeaconBlockEntity.setActiveLow(Boolean.parseBoolean(activeLow));
            }
        }catch(Exception e){
            DecoBeacons.LOGGER.error("Error: ", e);
            return ErrorResponse.onErrorActionResult(world,pos,player,"Unrecognized value: activeLow:"+activeLow);
        }

        try{
            if(!isTransparent.isEmpty()) {
                decoBeaconBlockEntity.setTransparent(Boolean.parseBoolean(isTransparent));
            }
        }catch(Exception e){
            DecoBeacons.LOGGER.error("Error: ", e);
            return ErrorResponse.onErrorActionResult(world,pos,player,"Unrecognized value: isTransparent:"+isTransparent);
        }

        return ActionResult.SUCCESS;
    }

    @Override
    public void refreshBlockEntityBookSettings(BlockState blockState, BlockEntity blockEntity) {
        if(blockEntity instanceof DecoBeaconBlockEntity decoBeaconBlockEntity) {
            decoBeaconBlockEntity.bookSettings.put("color", DyeColor.byId(decoBeaconBlockEntity.getCurColorID()).getName());
            decoBeaconBlockEntity.bookSettings.put("isTransparent", Boolean.toString(decoBeaconBlockEntity.isTransparent()));
            decoBeaconBlockEntity.bookSettings.put("activeLow", Boolean.toString(decoBeaconBlockEntity.isActiveLow()));
        }
    }

    /*public static void parsePages(NbtList pagesNbt, Map<String,String> bookSettings){
        *//*  //example//
            maxLength:int;
            direction:U/D/N/S/E/W;
            color:DyeColor names (i.e. red,blue,lime);

            moveX:int;
            moveY:int;
            moveZ:int;
            mirror:FB/LR;
            rotate:90/180/270;
            files:
            scheme1,
            scheme2,
            scheme3;
         *//*
        for(int i=0; i<pagesNbt.size(); ++i) {
            String page = pagesNbt.getString(i);
            page = StringUtils.normalizeSpace(page);
            page = page.replace(" ","");
            if(page.isEmpty()){
                continue;
            }
            String[] settings = page.split("[;]");
            for (String setting : settings) {
                String[] kv = setting.split("[:]");
                if (bookSettings.containsKey(kv[0])) {
                    bookSettings.put(kv[0], kv[1]);
                }else{
                    throw new NullPointerException();
                }
            }
        }
    }
    public static NbtList readPages(ItemStack bookStack){
        if (!bookStack.isEmpty() && bookStack.hasNbt()) {
            NbtCompound bookNbt = bookStack.getNbt();
            if(bookNbt.contains("pages")) {
                return bookNbt.getList("pages", 8).copy();
            }
        }
        return new NbtList();
    }
    public ActionResult implementBookSettings(  BlockState state,
                                                World world,
                                                BlockPos pos,
                                                PlayerEntity player,
                                                DecoBeaconBlockEntity blockEntity,
                                                Map<String,String> bookSettings){
        String color = bookSettings.get("color");
        String activeLow = bookSettings.get("activeLow");
        String isTransparent = bookSettings.get("isTransparent");
        try{
            if(!color.isEmpty()) {
                color = color.toLowerCase();
                //int colorID = DyeColor.byName(color, DyeColor.WHITE).getId();//this doesn't cause an error when it fails
                int colorID = COLOR_NAME_DICTIONARY.get(color).getId();//I need it to cause an error when it fails
                blockEntity.setCurColorID(colorID);
            }
        }catch(Exception e){
            //return onError(e,world,pos,player,"color:"+color);
            DecoBeacon.LOGGER.error("Error: ", e);
            return ErrorResponse.onErrorActionResult(world,pos,player,"color:"+color);
        }

        try{
            if(!activeLow.isEmpty()) {
                blockEntity.setActiveLow(Boolean.parseBoolean(activeLow));
            }
        }catch(Exception e){
            //return onError(e,world,pos,player,"activeLow:"+activeLow);
            DecoBeacon.LOGGER.error("Error: ", e);
            return ErrorResponse.onErrorActionResult(world,pos,player,"activeLow:"+activeLow);
        }

        try{
            if(!isTransparent.isEmpty()) {
                blockEntity.setTransparent(Boolean.parseBoolean(isTransparent));
            }
        }catch(Exception e){
            //return onError(e,world,pos,player,"isTransparent:"+isTransparent);
            DecoBeacon.LOGGER.error("Error: ", e);
            return ErrorResponse.onErrorActionResult(world,pos,player,"isTransparent:"+isTransparent);
        }

        return ActionResult.SUCCESS;
    }
    public ActionResult executeBookProtocol(ItemStack bookStack,
                                            BlockState state,
                                            World world,
                                            BlockPos pos,
                                            PlayerEntity player,
                                            DecoBeaconBlockEntity blockEntity){

        NbtList pagesNbt = readPages(bookStack);
        if(pagesNbt.isEmpty()){
            DecoBeaconBlockEntity.playSound(world, pos, SoundEvents.ENTITY_LIGHTNING_BOLT_THUNDER);
            player.sendMessage(new TranslatableText("empty_book_error_prompt"),false);
            return ActionResult.FAIL;
        }
        try {
            parsePages(pagesNbt, blockEntity.bookSettings);
        }catch(Exception e){
            DecoBeacon.LOGGER.error("Error: ", e);
            return ErrorResponse.onErrorActionResult(world,pos,player,"unrecognized settings...");
        }

        return implementBookSettings(state, world, pos, player, blockEntity,blockEntity.bookSettings);
    }
    public void refreshBlockEntityBookSettings(BlockState blockState,
                                               DecoBeaconBlockEntity blockEntity){
        blockEntity.bookSettings.put("color",DyeColor.byId(blockEntity.getCurColorID()).getName());
        blockEntity.bookSettings.put("isTransparent",Boolean.toString(blockEntity.isTransparent()));
        blockEntity.bookSettings.put("activeLow",Boolean.toString(blockEntity.isActiveLow()));
    }*/
}
