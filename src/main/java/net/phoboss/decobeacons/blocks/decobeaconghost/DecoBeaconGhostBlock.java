package net.phoboss.decobeacons.blocks.decobeaconghost;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;

import net.phoboss.decobeacons.blocks.decobeacon.DecoBeaconBlock;
import net.phoboss.decobeacons.blocks.decobeacon.DecoBeaconBlockEntity;
import org.jetbrains.annotations.Nullable;

public class DecoBeaconGhostBlock extends DecoBeaconBlock {
    public DecoBeaconGhostBlock(Settings settings) {
        super(settings);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new DecoBeaconBlockEntity(pos,state,true);
    }
/*
    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return checkType(type, ModBlockEntities.DECO_BEACON.get(), DecoBeaconBlockEntity::tick);
    }

 */
}
