package net.phoboss.decobeacons.utility;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionResult;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Map;

public interface BookSettingsUtility {
    static ListTag readPages(ItemStack bookStack){
        if (!bookStack.isEmpty() && bookStack.hasTag()) {
            CompoundTag bookNbt = bookStack.getTag();
            if(bookNbt.contains("pages")) {
                return bookNbt.getList("pages", 8).copy();
            }
        }
        return new ListTag();
    }
    static void parsePages(ListTag pagesNbt, Map<String,String> bookSettings) throws Exception{
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
    default InteractionResult executeBookProtocol(ItemStack bookStack,
                                                  BlockState state,
                                                  Level world,
                                                  BlockPos pos,
                                                  Player player,
                                                  BlockEntity blockEntity,
                                                  Map<String,String> bookSettings) throws Exception{
        ListTag pagesNbt;
        try {
            pagesNbt = readPages(bookStack);
        }catch(Exception e){
            ErrorResponse.onError(world,pos,player,"can't find pages...");
            throw new Exception("can't find pages...",e);
        }

        if(pagesNbt.isEmpty()){
            SpecialEffects.playSound(world, pos, SoundEvents.LIGHTNING_BOLT_THUNDER);
            player.displayClientMessage(new TranslatableComponent("empty_book_error_prompt"),false);
            return InteractionResult.FAIL;
        }
        try {
            parsePages(pagesNbt, bookSettings);
        }catch(Exception e){
            ErrorResponse.onError(world,pos,player,e.getMessage());
            throw new Exception(e.getMessage(),e);
        }

        return implementBookSettings(state,world,pos,player,blockEntity,bookSettings);
    }


    default InteractionResult implementBookSettings(BlockState state,
                                              Level world,
                                              BlockPos pos,
                                              Player player,
                                              BlockEntity blockEntity,
                                              Map<String,String> bookSettings){

        return InteractionResult.SUCCESS;
    }
    default void refreshBlockEntityBookSettings(BlockState blockState,
                                               BlockEntity blockEntity){


    }


}
