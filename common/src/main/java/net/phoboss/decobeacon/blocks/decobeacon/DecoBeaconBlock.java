package net.phoboss.decobeacon.blocks.decobeacon;

import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.DyeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.phoboss.decobeacon.blocks.ModBlockEntities;
import net.phoboss.decobeacon.blocks.omnibeacon.OmniBeaconBlockEntity;
import org.jetbrains.annotations.Nullable;

public class DecoBeaconBlock extends BlockWithEntity implements BlockEntityProvider, Stainable  {
    public static final IntProperty COLOR = IntProperty.of("color",0,15);

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
    public ActionResult onUse(BlockState state,
                              World world,
                              BlockPos pos,
                              PlayerEntity player,
                              Hand hand,
                              BlockHitResult hit) {
        Item mainHandItem = player.getMainHandStack().getItem();
        if(hand == Hand.MAIN_HAND){
            if(!world.isClient()){
                DecoBeaconBlockEntity blockEntity = (DecoBeaconBlockEntity) world.getBlockEntity(pos);
                if(mainHandItem instanceof DyeItem itemDye){
                    world.setBlockState(pos,state.with(COLOR,itemDye.getColor().getId()),Block.NOTIFY_ALL);
                    return ActionResult.SUCCESS;

                }else if(mainHandItem == Items.AIR){
                    int delta = player.isSneaking() ? -1 : 1;
                    int currentColor = Math.floorMod((state.get(COLOR) + delta),16);
                    world.setBlockState(pos,state.with(COLOR,currentColor),Block.NOTIFY_ALL);
                    return ActionResult.SUCCESS;

                }else if(mainHandItem == Items.REDSTONE_TORCH){
                    blockEntity.setActiveLow(!blockEntity.isActiveLow());
                    return ActionResult.SUCCESS;

                }else if(mainHandItem == Items.SOUL_TORCH){
                    blockEntity.setTransparent(!blockEntity.isTransparent());
                    return ActionResult.SUCCESS;

                }
            }
        }
        return ActionResult.PASS;
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return DecoBeaconBlockEntity.createPlatformSpecific(pos,state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return checkType(type, ModBlockEntities.DECO_BEACON.get(), DecoBeaconBlockEntity::tick);
    }

    @Override
    public DyeColor getColor() {
        return DyeColor.WHITE;
    }
}
