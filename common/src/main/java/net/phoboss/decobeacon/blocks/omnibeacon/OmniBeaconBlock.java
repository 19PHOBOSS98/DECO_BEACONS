package net.phoboss.decobeacon.blocks.omnibeacon;

import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Hand;
import net.minecraft.util.Util;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3f;
import net.minecraft.world.World;
import net.phoboss.decobeacon.DecoBeacon;
import net.phoboss.decobeacon.blocks.ModBlockEntities;
import net.phoboss.decobeacon.blocks.decobeacon.DecoBeaconBlock;
import net.phoboss.decobeacon.blocks.decobeacon.DecoBeaconBlockEntity;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

import java.util.Map;


public class OmniBeaconBlock extends DecoBeaconBlock {
    public OmniBeaconBlock(Settings settings) {
        super(settings);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return OmniBeaconBlockEntity.createPlatformSpecific(pos,state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return checkType(type, ModBlockEntities.OMNI_BEACON.get(), OmniBeaconBlockEntity::tick);
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
            if(!world.isClient()){
                OmniBeaconBlockEntity blockEntity = (OmniBeaconBlockEntity) world.getBlockEntity(pos);
                if(mainHandItem instanceof DyeItem itemDye){
                    int colorId = itemDye.getColor().getId();
                    blockEntity.bookSettings.put("color",DyeColor.byId(colorId).getName());
                    world.setBlockState(pos,state.with(COLOR,colorId),Block.NOTIFY_ALL);
                    return ActionResult.SUCCESS;

                }else if(mainHandItem == Items.AIR){
                    int delta = player.isSneaking() ? -1 : 1;
                    int currentColor = Math.floorMod((state.get(COLOR) + delta),16);
                    blockEntity.bookSettings.put("color",DyeColor.byId(currentColor).getName());
                    world.setBlockState(pos,state.with(COLOR,currentColor),Block.NOTIFY_ALL);
                    return ActionResult.SUCCESS;

                }else if(mainHandItem == Items.REDSTONE_TORCH){
                    blockEntity.setActiveLow(!blockEntity.isActiveLow());
                    return ActionResult.SUCCESS;

                }else if(mainHandItem == Items.SOUL_TORCH){
                    blockEntity.setTransparent(!blockEntity.isTransparent());
                    return ActionResult.SUCCESS;

                }else if(mainHandItem == Items.TORCH){
                    Direction side = hit.getSide();
                    blockEntity.bookSettings.put("direction",side.getName().substring(0,1));
                    blockEntity.setBeamDirection(side.getUnitVector());
                    return ActionResult.SUCCESS;

                }else if(mainHandItemStack.hasNbt()){
                    //blockEntity.setBeamDirection(hit.getSide().getUnitVector());
                    return executeBookProtocol(mainHandItemStack,state,world,pos,player,blockEntity);
                }
            }

        }
        return ActionResult.PASS;
    }
    public static Map<String, DyeColor> COLOR_NAME_DICTIONARY = Util.make(new Object2ObjectLinkedOpenHashMap(), (map) -> {
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
    public static Map<String, Direction> BEAM_DIRECTION_DICTIONARY = Util.make(new Object2ObjectLinkedOpenHashMap(), (map) -> {
        map.put("u",Direction.UP);
        map.put("d",Direction.DOWN);
        map.put("n",Direction.NORTH);
        map.put("s",Direction.SOUTH);
        map.put("e",Direction.EAST);
        map.put("w",Direction.WEST);
    });


    public static void parsePages(NbtList pagesNbt,Map<String,String> bookSettings){
        /*  //example//
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
         */
        for(int i=0; i<pagesNbt.size(); ++i) {
            String page = pagesNbt.getString(i);
            page = StringUtils.normalizeSpace(page);
            page = page.replace(" ","");
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
            return bookNbt.getList("pages", 8).copy();
        }
        return new NbtList();
    }
    public static ActionResult onError(Exception e,
                               World world,
                               BlockPos pos,
                               PlayerEntity player,
                               String field){
        DecoBeacon.LOGGER.error("Book Parsing Error: ",e);
        DecoBeaconBlockEntity.playSound(world, pos, SoundEvents.ENTITY_LIGHTNING_BOLT_THUNDER);
        player.sendMessage(new TranslatableText("invalid_page_entry"),false);
        player.sendMessage(new LiteralText(field),false);
        return ActionResult.FAIL;
    }
    public static ActionResult implementBookSettings(   BlockState state,
                                                        World world,
                                                        BlockPos pos,
                                                        PlayerEntity player,
                                                        OmniBeaconBlockEntity blockEntity,
                                                        Map<String,String> bookSettings){

        String maxLengthStr = bookSettings.get("maxBeamLength");
        String direction = bookSettings.get("direction");
        String color = bookSettings.get("color");

        try{
            if(!maxLengthStr.isEmpty()) {
                int maxLength = Math.max(0, Math.min(512, Integer.parseInt(maxLengthStr)));
                blockEntity.setMaxBeamLength(maxLength);
            }
        }catch(Exception e){
            return onError(e,world,pos,player,"maxBeamLength");
        }

        try{
            if(!direction.isEmpty()) {
                direction = direction.toLowerCase();
                Vec3f beamDirection = BEAM_DIRECTION_DICTIONARY.get(direction.substring(0,1)).getUnitVector();
                blockEntity.setBeamDirection(beamDirection);
            }
        }catch(Exception e){
            return onError(e,world,pos,player,"direction");
        }

        try{
            if(!color.isEmpty()) {
                color = color.toLowerCase();
                //int colorID = DyeColor.byName(color, DyeColor.WHITE).getId();//this doesn't cause an error when it fails
                int colorID = COLOR_NAME_DICTIONARY.get(color).getId();//I need it to cause an error when it fails
                world.setBlockState(pos, state.with(COLOR, colorID), Block.NOTIFY_ALL);
            }
        }catch(Exception e){
            return onError(e,world,pos,player,"color");
        }

        return ActionResult.SUCCESS;
    }
    public ActionResult executeBookProtocol(ItemStack bookStack,
                                            BlockState state,
                                            World world,
                                            BlockPos pos,
                                            PlayerEntity player,
                                            OmniBeaconBlockEntity blockEntity){

        NbtList pagesNbt = readPages(bookStack);
        if(pagesNbt.isEmpty()){
            DecoBeaconBlockEntity.playSound(world, pos, SoundEvents.ENTITY_LIGHTNING_BOLT_THUNDER);
            player.sendMessage(new TranslatableText("empty_book_error_prompt"),false);
            return ActionResult.FAIL;
        }
        try {
            parsePages(pagesNbt, blockEntity.bookSettings);
        }catch(Exception e){
            return onError(e,world,pos,player,"unrecognized settings...");
        }

        return implementBookSettings(state, world, pos, player, blockEntity,blockEntity.bookSettings);
    }


}
