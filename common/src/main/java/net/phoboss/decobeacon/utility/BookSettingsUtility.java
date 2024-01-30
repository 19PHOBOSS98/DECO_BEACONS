package net.phoboss.decobeacon.utility;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
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

import java.lang.reflect.Type;
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
    static void parsePages(NbtList pagesNbt, Map<String,String> bookSettings) throws Exception{
        if(pagesNbt.size()<1){
            return;
        }
        String pagesStr = "{";
        for(int i=0; i<pagesNbt.size(); ++i) {
            pagesStr = pagesStr + pagesNbt.getString(i);
        }
        pagesStr = pagesStr + "}";

        JsonObject settingsJSON;
        try {
            settingsJSON  = JsonParser.parseString(pagesStr).getAsJsonObject();
        }catch (Exception e){
            throw new Exception("Might need to recheck your book: "+e.getLocalizedMessage(),e);
        }

        for (Map.Entry<String, JsonElement> setting : settingsJSON.entrySet()) {
            String settingName = setting.getKey();
            if(bookSettings.containsKey(settingName)){
                bookSettings.put(settingName,setting.getValue().getAsString());
            }else{
                throw new Exception("unrecognized setting: " + settingName);
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
                                            Map<String,String> bookSettings) throws Exception{
        NbtList pagesNbt;
        try {
            pagesNbt = readPages(bookStack);
        }catch(Exception e){
            ErrorResponse.onError(world,pos,player,"can't find pages...");
            throw new Exception("can't find pages...",e);
        }

        if(pagesNbt.isEmpty()){
            SpecialEffects.playSound(world, pos, SoundEvents.ENTITY_LIGHTNING_BOLT_THUNDER);
            player.sendMessage(new TranslatableText("empty_book_error_prompt"),false);
            return ActionResult.FAIL;
        }
        try {
            parsePages(pagesNbt, bookSettings);
        }catch(Exception e){
            ErrorResponse.onError(world,pos,player,e.getMessage());
            throw new Exception(e.getMessage(),e);
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
