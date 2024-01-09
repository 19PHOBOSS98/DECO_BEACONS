package net.phoboss.decobeacon.blocks.omnibeacon.forge;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3i;
import net.minecraftforge.common.extensions.IForgeBlockEntity;
import net.phoboss.decobeacon.blocks.omnibeacon.OmniBeaconBlockEntity;

public class OmniBeaconBlockEntityImpl extends OmniBeaconBlockEntity implements IForgeBlockEntity {


    public OmniBeaconBlockEntityImpl(BlockPos pos, BlockState state, Boolean isGhost) {
        super(pos, state, isGhost);
    }

    public OmniBeaconBlockEntityImpl(BlockPos pos, BlockState state) {
        super(pos, state);
    }

    public static OmniBeaconBlockEntity createPlatformSpecific(BlockPos pos, BlockState state,Boolean isGhost){
        return new OmniBeaconBlockEntityImpl( pos, state, isGhost);
    }

    public static OmniBeaconBlockEntity createPlatformSpecific(BlockPos pos, BlockState state){
        return new OmniBeaconBlockEntityImpl( pos, state);
    }

    @Override
    public Box getRenderBoundingBox() {
        BlockPos pos = this.getPos();
        Vec3i offset = new Vec3i(this.getBeamSegmentsTotalHeight(),this.getBeamSegmentsTotalHeight(),this.getBeamSegmentsTotalHeight());
        return new Box(pos.subtract(offset),pos.add(offset));
    }

}
