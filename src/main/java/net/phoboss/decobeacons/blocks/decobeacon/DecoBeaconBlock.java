package net.phoboss.decobeacons.blocks.decobeacon;

import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;

import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.phoboss.decobeacons.DecoBeacons;
import net.phoboss.decobeacons.blocks.ModBlockEntities;
import net.phoboss.decobeacons.utility.BookSettingsUtility;
import net.phoboss.decobeacons.utility.ErrorResponse;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class DecoBeaconBlock extends BaseEntityBlock implements EntityBlock, BeaconBeamBlock, BookSettingsUtility {
    public static final IntegerProperty COLOR = IntegerProperty.create("color",0,15);
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
    public DecoBeaconBlock(BlockBehaviour.Properties settings) {
        super(settings);
        this.registerDefaultState(defaultBlockState().setValue(COLOR, 0).setValue(BlockStateProperties.LIT, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(COLOR)
                .add(BlockStateProperties.LIT);
    }

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

    @Override
    public DyeColor getColor() {
        return DyeColor.WHITE;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        return createTickerHelper(pBlockEntityType, ModBlockEntities.DECO_BEACON.get(), DecoBeaconBlockEntity::tick);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new DecoBeaconBlockEntity(pos,state);
    }
    @Override
    public InteractionResult use(BlockState state,
                                 Level world,
                                 BlockPos pos,
                                 Player player,
                                 InteractionHand hand,
                                 BlockHitResult hit) {
        ItemStack mainHandItemStack = player.getMainHandItem();
        Item mainHandItem = mainHandItemStack.getItem();
        if(hand == InteractionHand.MAIN_HAND){
            if(world.isClientSide()){
                return InteractionResult.SUCCESS;
            }

            DecoBeaconBlockEntity blockEntity = (DecoBeaconBlockEntity) world.getBlockEntity(pos);
            if(blockEntity == null){
                return InteractionResult.FAIL;
            }
            if(mainHandItem instanceof DyeItem itemDye){
                blockEntity.setCurColorID(itemDye.getDyeColor().getId());
                return InteractionResult.SUCCESS;

            }else if(mainHandItem == Items.AIR){
                int delta = player.isShiftKeyDown() ? -1 : 1;
                int currentColor = Math.floorMod((state.getValue(COLOR) + delta),16);
                blockEntity.setCurColorID(currentColor);
                return InteractionResult.SUCCESS;

            }else if(mainHandItem == Items.REDSTONE_TORCH){
                blockEntity.setActiveLow(!blockEntity.isActiveLow());
                return InteractionResult.SUCCESS;

            }else if(mainHandItem == Items.SAND){
                blockEntity.setTransparent(!blockEntity.isTransparent());
                return InteractionResult.SUCCESS;

            }else if(mainHandItemStack.hasTag() && mainHandItemStack.getTag().contains("pages")){
                try {
                    InteractionResult result = executeBookProtocol(mainHandItemStack, state, world, pos, player, blockEntity, blockEntity.bookSettings);
                    if(result == InteractionResult.FAIL){
                        refreshBlockEntityBookSettings(state,blockEntity);
                    }
                    return result;
                }catch(Exception e){
                    DecoBeacons.LOGGER.error(e.getMessage(),e);
                    return InteractionResult.FAIL;
                }
            }
        }
        return InteractionResult.PASS;
    }


    @Override
    public InteractionResult implementBookSettings(BlockState state, Level world, BlockPos pos, Player player, BlockEntity blockEntity, Map<String, String> bookSettings) {
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

        return InteractionResult.SUCCESS;
    }

    @Override
    public void refreshBlockEntityBookSettings(BlockState blockState, BlockEntity blockEntity) {
        if(blockEntity instanceof DecoBeaconBlockEntity decoBeaconBlockEntity) {
            decoBeaconBlockEntity.bookSettings.put("color", DyeColor.byId(decoBeaconBlockEntity.getCurColorID()).getName());
            decoBeaconBlockEntity.bookSettings.put("isTransparent", Boolean.toString(decoBeaconBlockEntity.isTransparent()));
            decoBeaconBlockEntity.bookSettings.put("activeLow", Boolean.toString(decoBeaconBlockEntity.isActiveLow()));
        }
    }

    /*public static void parsePages(ListTag pagesNbt, Map<String,String> bookSettings){
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
    public static ListTag readPages(ItemStack bookStack){
        if (!bookStack.isEmpty() && bookStack.hasTag()) {
            CompoundTag bookNbt = bookStack.getTag();
            if(bookNbt.contains("pages")) {
                return bookNbt.getList("pages", 8).copy();
            }
        }
        return new ListTag();
    }
    public InteractionResult implementBookSettings(  BlockState state,
                                                Level world,
                                                BlockPos pos,
                                                Player player,
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
            DecoBeacons.LOGGER.error("Error: ", e);
            return ErrorResponse.onErrorActionResult(world,pos,player,"color:"+color);
        }

        try{
            if(!activeLow.isEmpty()) {
                blockEntity.setActiveLow(Boolean.parseBoolean(activeLow));
            }
        }catch(Exception e){
            //return onError(e,world,pos,player,"activeLow:"+activeLow);
            DecoBeacons.LOGGER.error("Error: ", e);
            return ErrorResponse.onErrorActionResult(world,pos,player,"activeLow:"+activeLow);
        }

        try{
            if(!isTransparent.isEmpty()) {
                blockEntity.setTransparent(Boolean.parseBoolean(isTransparent));
            }
        }catch(Exception e){
            //return onError(e,world,pos,player,"isTransparent:"+isTransparent);
            DecoBeacons.LOGGER.error("Error: ", e);
            return ErrorResponse.onErrorActionResult(world,pos,player,"isTransparent:"+isTransparent);
        }

        return InteractionResult.SUCCESS;
    }
    public InteractionResult executeBookProtocol(ItemStack bookStack,
                                            BlockState state,
                                            Level world,
                                            BlockPos pos,
                                            Player player,
                                            DecoBeaconBlockEntity blockEntity){

        ListTag pagesNbt = readPages(bookStack);
        if(pagesNbt.isEmpty()){
            DecoBeaconBlockEntity.playSound(world, pos, SoundEvents.ENTITY_LIGHTNING_BOLT_THUNDER);
            player.sendMessage(new TranslatableText("empty_book_error_prompt"),false);
            return InteractionResult.FAIL;
        }
        try {
            parsePages(pagesNbt, blockEntity.bookSettings);
        }catch(Exception e){
            DecoBeacons.LOGGER.error("Error: ", e);
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
