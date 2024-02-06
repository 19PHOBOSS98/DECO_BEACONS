package net.phoboss.decobeacons.blocks.omnibeacon;

import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Util;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3f;
import net.minecraft.world.World;
import net.phoboss.decobeacons.DecoBeacons;
import net.phoboss.decobeacons.blocks.ModBlockEntities;
import net.phoboss.decobeacons.blocks.decobeacon.DecoBeaconBlock;
import net.phoboss.decobeacons.utility.ErrorResponse;
import org.jetbrains.annotations.Nullable;

import java.util.Map;


public class OmniBeaconBlock extends DecoBeaconBlock {
    public OmniBeaconBlock(Settings settings) {
        super(settings);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new OmniBeaconBlockEntity(pos,state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return checkType(type, ModBlockEntities.OMNI_BEACON, OmniBeaconBlockEntity::tick);
    }


    @Override
    public ActionResult onUse(BlockState state,
                              World world,
                              BlockPos pos,
                              PlayerEntity player,
                              Hand hand,
                              BlockHitResult hit) {
        ActionResult result = super.onUse(state,world,pos,player,hand,hit);

        ItemStack mainHandItemStack = player.getMainHandStack();
        Item mainHandItem = mainHandItemStack.getItem();

        if(hand == Hand.MAIN_HAND){
            if(world.isClient()){
                return ActionResult.SUCCESS;
            }
            OmniBeaconBlockEntity blockEntity = (OmniBeaconBlockEntity) world.getBlockEntity(pos);
            if(mainHandItem == Items.TORCH){
                Direction side = hit.getSide();
                blockEntity.bookSettings.put("direction",side.getName());
                blockEntity.setBeamDirection(side.getUnitVector());
                return ActionResult.SUCCESS;
            }
        }
        return result;
    }
    public static Map<String, Direction> BEAM_DIRECTION_DICTIONARY = Util.make(new Object2ObjectLinkedOpenHashMap(), (map) -> {
        map.put("up",Direction.UP);
        map.put("down",Direction.DOWN);
        map.put("north",Direction.NORTH);
        map.put("south",Direction.SOUTH);
        map.put("east",Direction.EAST);
        map.put("west",Direction.WEST);
    });

    @Override
    public ActionResult implementBookSettings(  BlockState state,
                                                World world,
                                                BlockPos pos,
                                                PlayerEntity player,
                                                BlockEntity blockEntity,
                                                Map<String,String> bookSettings){
        ActionResult result = super.implementBookSettings(  state,
                                                            world,
                                                            pos,
                                                            player,
                                                            blockEntity,
                                                            bookSettings);
        if(result != ActionResult.SUCCESS){
            return result;
        }

        if(blockEntity instanceof OmniBeaconBlockEntity omniBeaconBlockEntity) {
            String maxLengthStr = bookSettings.get("maxBeamLength");
            String direction = bookSettings.get("direction");
            try {
                if (!maxLengthStr.isEmpty()) {
                    int maxLength = Math.max(0, Math.min(512, Integer.parseInt(maxLengthStr)));
                    omniBeaconBlockEntity.setMaxBeamLength(maxLength);
                }
            } catch (Exception e) {
                //return onError(e, world, pos, player, "maxBeamLength:"+maxLengthStr);
                DecoBeacons.LOGGER.error("Error: ", e);
                return ErrorResponse.onErrorActionResult(world,pos,player,"Unrecognized value: maxBeamLength:"+maxLengthStr);
            }

            try {
                if (!direction.isEmpty()) {
                    direction = direction.toLowerCase();
                    Vec3f beamDirection = BEAM_DIRECTION_DICTIONARY.get(direction).getUnitVector();
                    omniBeaconBlockEntity.setBeamDirection(beamDirection);
                }
            } catch (Exception e) {
                //return onError(e, world, pos, player, "direction:"+direction);
                DecoBeacons.LOGGER.error("Error: ", e);
                return ErrorResponse.onErrorActionResult(world,pos,player,"Unrecognized value: direction:"+direction);
            }
        }
        return ActionResult.SUCCESS;
    }

    @Override
    public void refreshBlockEntityBookSettings(BlockState blockState,
                                               BlockEntity blockEntity){
        super.refreshBlockEntityBookSettings(blockState,blockEntity);
        if(blockEntity instanceof OmniBeaconBlockEntity omni){
            omni.bookSettings.put("direction",omni.getBeamDirectionName());
            omni.bookSettings.put("maxBeamLength",Integer.toString(omni.getMaxBeamLength()));
        }

    }
}
