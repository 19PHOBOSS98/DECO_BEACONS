package net.phoboss.decobeacons.blocks.omnibeaconghost;


import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.phoboss.decobeacons.blocks.omnibeacon.OmniBeaconBlock;
import net.phoboss.decobeacons.blocks.omnibeacon.OmniBeaconBlockEntity;
import org.jetbrains.annotations.Nullable;


public class OmniBeaconGhostBlock extends OmniBeaconBlock {
    public OmniBeaconGhostBlock(BlockBehaviour.Properties settings) {
        super(settings);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new OmniBeaconBlockEntity(pos,state,true);
    }

}
