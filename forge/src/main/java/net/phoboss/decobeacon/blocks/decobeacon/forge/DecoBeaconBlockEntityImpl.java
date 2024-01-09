package net.phoboss.decobeacon.blocks.decobeacon.forge;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraftforge.common.extensions.IForgeBlockEntity;
import net.phoboss.decobeacon.blocks.decobeacon.DecoBeaconBlockEntity;

public class DecoBeaconBlockEntityImpl extends DecoBeaconBlockEntity implements IForgeBlockEntity {


    public DecoBeaconBlockEntityImpl(BlockPos pos, BlockState state, Boolean isGhost) {
        super(pos, state, isGhost);
    }

    public DecoBeaconBlockEntityImpl(BlockPos pos, BlockState state) {
        super(pos, state);
    }

    public static DecoBeaconBlockEntity createPlatformSpecific(BlockPos pos, BlockState state,Boolean isGhost){
        return new DecoBeaconBlockEntityImpl( pos, state, isGhost);
    }

    public static DecoBeaconBlockEntity createPlatformSpecific(BlockPos pos, BlockState state){
        return new DecoBeaconBlockEntityImpl( pos, state);
    }

    @Override
    public Box getRenderBoundingBox() {
        BlockPos pos = this.getPos();
        return new Box(pos,pos.add(1,this.getBeamSegmentsTotalHeight(),1));
    }
}
