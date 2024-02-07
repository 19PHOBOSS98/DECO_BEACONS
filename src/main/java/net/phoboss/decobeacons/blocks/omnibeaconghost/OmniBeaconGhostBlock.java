package net.phoboss.decobeacons.blocks.omnibeaconghost;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.phoboss.decobeacons.blocks.omnibeacon.OmniBeaconBlock;
import net.phoboss.decobeacons.blocks.omnibeacon.OmniBeaconBlockEntity;
import org.jetbrains.annotations.Nullable;


public class OmniBeaconGhostBlock extends OmniBeaconBlock {
    public OmniBeaconGhostBlock(Settings settings) {
        super(settings);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new OmniBeaconBlockEntity(pos,state,true);
    }

}
