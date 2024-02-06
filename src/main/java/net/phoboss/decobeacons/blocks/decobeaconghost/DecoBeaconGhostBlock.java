package net.phoboss.decobeacons.blocks.decobeaconghost;


import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.phoboss.decobeacons.blocks.decobeacon.DecoBeaconBlock;
import net.phoboss.decobeacons.blocks.decobeacon.DecoBeaconBlockEntity;
import org.jetbrains.annotations.Nullable;

public class DecoBeaconGhostBlock extends DecoBeaconBlock {
    public DecoBeaconGhostBlock(BlockBehaviour.Properties settings) {
        super(settings);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new DecoBeaconBlockEntity(pos,state,true);
    }


/*
    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
        return checkType(type, ModBlockEntities.DECO_BEACON.get(), DecoBeaconBlockEntity::tick);
    }

 */
}
