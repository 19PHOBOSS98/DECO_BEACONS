package net.phoboss.decobeacon.utility;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import net.phoboss.decobeacon.DecoBeacon;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

public interface BookSettingsUtility {
    static NbtList readPages(ItemStack bookStack){
        if (!bookStack.isEmpty() && bookStack.hasNbt()) {
            NbtCompound bookNbt = bookStack.getNbt();
            if(bookNbt.contains("pages")) {
                return bookNbt.getList("pages", 8).copy();
            }
        }
        return new NbtList();
    }
    static void parsePages(NbtList pagesNbt, Map<String,String> bookSettings){
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
    static String convertToString(Vec3i vec){
        try {
            return vec.getX()+","+vec.getY()+","+vec.getZ();
        }catch (Exception e){
            throw e;
        }
    }

    static Vec3i parseBookVec3i(String vec){
        try {
            String[] vecArray = vec.split(",");
            return new Vec3i(   Integer.parseInt(vecArray[0]),
                    Integer.parseInt(vecArray[1]),
                    Integer.parseInt(vecArray[2]));
        }catch (Exception e){
            throw e;
        }
    }
    default ActionResult executeBookProtocol(ItemStack bookStack,
                                            BlockState state,
                                            World world,
                                            BlockPos pos,
                                            PlayerEntity player,
                                            BlockEntity blockEntity,
                                            Map<String,String> bookSettings){
        NbtList pagesNbt;
        try {
            pagesNbt = readPages(bookStack);
        }catch(Exception e){
            return ErrorResponse.onErrorActionResult(world,pos,player,"can't find pages...");
        }

        if(pagesNbt.isEmpty()){
            SpecialEffects.playSound(world, pos, SoundEvents.ENTITY_LIGHTNING_BOLT_THUNDER);
            player.sendMessage(new TranslatableText("empty_book_error_prompt"),false);
            return ActionResult.FAIL;
        }
        try {
            parsePages(pagesNbt, bookSettings);
        }catch(Exception e){
            return ErrorResponse.onErrorActionResult(world,pos,player,"unrecognized settings...");
        }

        return implementBookSettings(state,world,pos,player,blockEntity,bookSettings);
    }


    default ActionResult implementBookSettings(BlockState state,
                                              World world,
                                              BlockPos pos,
                                              PlayerEntity player,
                                              BlockEntity blockEntity,
                                              Map<String,String> bookSettings){

        return ActionResult.SUCCESS;
    }
    default void refreshBlockEntityBookSettings(BlockState blockState,
                                               BlockEntity blockEntity){


    }


}
