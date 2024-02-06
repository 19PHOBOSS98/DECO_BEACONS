package net.phoboss.decobeacons.blocks.omnibeacon;

import com.mojang.math.Vector3f;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.phoboss.decobeacons.DecoBeacons;
import net.phoboss.decobeacons.blocks.ModBlockEntities;
import net.phoboss.decobeacons.blocks.decobeacon.DecoBeaconBlock;
import net.phoboss.decobeacons.blocks.decobeacon.DecoBeaconBlockEntity;
import net.phoboss.decobeacons.utility.ErrorResponse;
import org.jetbrains.annotations.Nullable;

import java.util.Map;


public class OmniBeaconBlock extends DecoBeaconBlock {
    public OmniBeaconBlock(BlockBehaviour.Properties settings) {
        super(settings);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new OmniBeaconBlockEntity(pos,state);
    }



    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
        return createTickerHelper(type, ModBlockEntities.OMNI_BEACON.get(), OmniBeaconBlockEntity::tick);
    }


    @Override
    public InteractionResult use(BlockState state,
                                 Level world,
                                 BlockPos pos,
                                 Player player,
                                 InteractionHand hand,
                                 BlockHitResult hit) {
        InteractionResult result = super.use(state,world,pos,player,hand,hit);

        ItemStack mainHandItemStack = player.getMainHandItem();
        Item mainHandItem = mainHandItemStack.getItem();

        if(hand == InteractionHand.MAIN_HAND){
            if(world.isClientSide()){
                return InteractionResult.SUCCESS;
            }
            OmniBeaconBlockEntity blockEntity = (OmniBeaconBlockEntity) world.getBlockEntity(pos);
            if(mainHandItem == Items.TORCH){
                Direction side = hit.getDirection();
                blockEntity.bookSettings.put("direction",side.getName());
                blockEntity.setBeamDirection(side.step());
                return InteractionResult.SUCCESS;
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
    public InteractionResult implementBookSettings(  BlockState state,
                                                Level world,
                                                BlockPos pos,
                                                Player player,
                                                BlockEntity blockEntity,
                                                Map<String,String> bookSettings){
        InteractionResult result = super.implementBookSettings(  state,
                                                            world,
                                                            pos,
                                                            player,
                                                            blockEntity,
                                                            bookSettings);
        if(result != InteractionResult.SUCCESS){
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
                    Vector3f beamDirection = BEAM_DIRECTION_DICTIONARY.get(direction).step();
                    omniBeaconBlockEntity.setBeamDirection(beamDirection);
                }
            } catch (Exception e) {
                //return onError(e, world, pos, player, "direction:"+direction);
                DecoBeacons.LOGGER.error("Error: ", e);
                return ErrorResponse.onErrorActionResult(world,pos,player,"Unrecognized value: direction:"+direction);
            }
        }
        return InteractionResult.SUCCESS;
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
