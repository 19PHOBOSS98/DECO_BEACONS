package net.phoboss.decobeacons.blocks.omnibeacon;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.extensions.IForgeBlockEntity;
import net.phoboss.decobeacons.DecoBeacons;
import net.phoboss.decobeacons.blocks.ModBlockEntities;
import net.phoboss.decobeacons.blocks.decobeacon.DecoBeaconBlock;
import net.phoboss.decobeacons.blocks.decobeacon.DecoBeaconBlockEntity;
import org.joml.Vector3f;


import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class OmniBeaconBlockEntity extends DecoBeaconBlockEntity implements IForgeBlockEntity {
    public OmniBeaconBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.OMNI_BEACON.get(), pos, state); //DO NOT FORGET TO CHECK THE BLOCK TYPE!!! YOU WILL GO CRAZY TRYING TO FIGURE OUT WHY THE BLOCK DOESN'T CALL THE TICK METHOD
    }

    public OmniBeaconBlockEntity(BlockPos pos, BlockState state,Boolean isGhost) {
        super(ModBlockEntities.OMNI_BEACON.get(), pos, state,isGhost); //DO NOT FORGET TO CHECK THE BLOCK TYPE!!! YOU WILL GO CRAZY TRYING TO FIGURE OUT WHY THE BLOCK DOESN'T CALL THE TICK METHOD
    }

    private int maxBeamLength = 512;
    private Vector3f beamDirection = new Vector3f(0,1,0);
    public List<DecoBeamSegment> omniSegmentsBuffer = Lists.newArrayList();
    public List<DecoBeamSegment> omniBeamSegments = Lists.newArrayList();
    public BlockPos prevBlockPos = getBlockPos();
    public Vector3f prevBeamDirection = getBeamDirection();

    @Override
    public Object2ObjectLinkedOpenHashMap<String, String> setupBookSettings() {
        Object2ObjectLinkedOpenHashMap<String,String> map = super.setupBookSettings();
        map.put("maxBeamLength","512");
        map.put("direction","up");//u/d/n/s/e/w
        return map;
    }

    public Vector3f getBeamDirection() {
        return this.beamDirection;
    }
    public String getBeamDirectionName() {
        return Direction.fromDelta(
                (int)this.beamDirection.x(),
                (int)this.beamDirection.y(),
                (int)this.beamDirection.z()).getName();
    }
    public Vec3i getBeamDirectionInt() { //:`(
        return new Vec3i(
                (int)this.beamDirection.x(),
                (int)this.beamDirection.y(),
                (int)this.beamDirection.z());
    }
    public void setBeamDirection(Vector3f beamDirection) {
        this.beamDirection = beamDirection;
        this.bookSettings.put("direction",
                Direction.fromDelta(
                        (int) beamDirection.x(),
                        (int) beamDirection.y(),
                        (int) beamDirection.z()).getName());
        setChanged();
    }
    public List<DecoBeamSegment> getOmniBeamSegments() {
        return omniBeamSegments;
    }
    public BlockPos getPrevBlockPos() {
        return this.prevBlockPos;
    }
    public void setPrevBlockPos(BlockPos prevBlockPos) {
        this.prevBlockPos = prevBlockPos;
    }
    public Vec3i getPrevBeamDirectionInt() {
        return new Vec3i(
                (int)this.prevBeamDirection.x(),
                (int)this.prevBeamDirection.y(),
                (int)this.prevBeamDirection.z());
    }
    public void setPrevBeamDirection(Vec3i prevBeamDirection) {
        this.prevBeamDirection = new Vector3f(prevBeamDirection.getX(),prevBeamDirection.getY(),prevBeamDirection.getZ());
    }
    public int getMaxBeamLength() {
        return maxBeamLength;
    }
    public void setMaxBeamLength(int maxBeamLength) {
        this.maxBeamLength = maxBeamLength;
        this.bookSettings.put("maxBeamLength", Integer.toString(maxBeamLength));
        setChanged();
    }
    public int getMaxBeamLengthSquared() {
        return maxBeamLength*maxBeamLength;
    }

    @Override
    public int getBeamSegmentsTotalHeight() {
        int totalHeight = 0;
        for (DecoBeamSegment segment:getOmniBeamSegments()) {
            totalHeight += segment.getHeight();
        }
        return totalHeight;
    }
    @Override
    protected void saveAdditional(CompoundTag nbt) {
        Vector3f beamDir = getBeamDirection();
        nbt.putFloat("beamDirectionX",beamDir.x());
        nbt.putFloat("beamDirectionY",beamDir.y());
        nbt.putFloat("beamDirectionZ",beamDir.z());
        nbt.putInt("maxBeamLength",this.maxBeamLength);
        super.saveAdditional(nbt);
    }
    @Override
    public void load(CompoundTag nbt) {
        try {
            super.load(nbt);
            this.beamDirection = new Vector3f(nbt.getFloat("beamDirectionX"), nbt.getFloat("beamDirectionY"), nbt.getFloat("beamDirectionZ"));
            this.maxBeamLength = nbt.getInt("maxBeamLength");//make sure all fields are initialized properly. this was missing and caused the game to freeze on "Saving worlds" without logs

            this.bookSettings.put("maxBeamLength", Integer.toString(this.maxBeamLength));
            this.bookSettings.put("direction",
                    Direction.fromDelta(
                            (int) this.beamDirection.x(),
                            (int) this.beamDirection.y(),
                            (int) this.beamDirection.z()).getName());
        }catch(Exception e){
            DecoBeacons.LOGGER.error("Error on OmniBeacon load(...):",e);
        }
    }

    //every tick a new segment is made
    public static void tick(Level world, BlockPos beaconPos, BlockState beaconState, OmniBeaconBlockEntity beaconEntity) {

        boolean opaqueBlockDetected = false;
        boolean isPowered = beaconEntity.isPowered();
        boolean passThruSolid = beaconEntity.isGhost();
        int curColorID = beaconEntity.getCurColorID();
        Vec3i curDirectionInt = beaconEntity.getBeamDirectionInt();
        int maxLength = beaconEntity.getMaxBeamLength();
        int maxLength2 = beaconEntity.getMaxBeamLengthSquared();
        BlockPos blockPos = beaconEntity.getPrevBlockPos().offset(curDirectionInt);



        if(!world.isClientSide()){// note to self only update state properties in server-side
            world.setBlock(beaconPos,beaconState.setValue(BlockStateProperties.LIT,isPowered), Block.UPDATE_ALL);
            world.setBlock(beaconPos,beaconState.setValue(DecoBeaconBlock.COLOR,curColorID),Block.UPDATE_ALL);
        }

        //if this beacon switched color or set direction changed then redo construction
        if (beaconEntity.prevColorID != curColorID || !beaconEntity.getPrevBeamDirectionInt().equals(curDirectionInt)) {
            beaconEntity.prevColorID = curColorID;
            beaconEntity.setPrevBeamDirection(curDirectionInt);
            beaconEntity.omniSegmentsBuffer.clear();
        }

        DecoBeamSegment omniBeamSegment = beaconEntity.omniSegmentsBuffer.isEmpty()
                ? null
                : beaconEntity.omniSegmentsBuffer.get(beaconEntity.omniSegmentsBuffer.size() - 1);

        //init buffer
        if (beaconEntity.omniSegmentsBuffer.isEmpty()) {
            omniBeamSegment = new DecoBeamSegment(DyeColor.byId(beaconState.getValue(DecoBeaconBlock.COLOR)).getTextureDiffuseColors());
            beaconEntity.omniSegmentsBuffer.add(omniBeamSegment);
            beaconEntity.setPrevBlockPos(beaconPos);
            blockPos = beaconPos.offset(curDirectionInt);

        }

        for(int b=0; b<maxLength && beaconPos.distSqr(blockPos)<maxLength2 ;++b)
        {
            BlockState blockState = world.getBlockState(blockPos);
            BlockEntity blockEntity = world.getBlockEntity(blockPos);
            float[] colorMultiplier = getColorMultiplier(blockState);
            if (colorMultiplier != null) {

                if (blockState.getBlock() instanceof DecoBeaconBlock) {
                    if(blockEntity instanceof DecoBeaconBlockEntity be){
                        if(!be.isTransparent()){ // if selected block is not a transparent beacon...
                            if(!be.isGhost()) { // if selected block is not a ghost beacon then end beam at this position
                                beaconEntity.prevBlockPos = beaconPos.offset(curDirectionInt.multiply(maxLength2));
                                opaqueBlockDetected = true;
                                break;
                            }else{// if selected block is a ghost beacon then keep previous segment color
                                colorMultiplier = omniBeamSegment.getColor();
                            }
                        }
                    }
                }

                if (Arrays.equals(colorMultiplier, omniBeamSegment.getColor())) {
                    //if current stainable block is the same color as the previous then step segment length
                    omniBeamSegment.increaseHeight();
                } else {
                    //else start new segment
                    omniBeamSegment = new DecoBeamSegment(new float[]{
                            (omniBeamSegment.getColor()[0] + colorMultiplier[0]) / 2.0F,
                            (omniBeamSegment.getColor()[1] + colorMultiplier[1]) / 2.0F,
                            (omniBeamSegment.getColor()[2] + colorMultiplier[2]) / 2.0F});
                    beaconEntity.omniSegmentsBuffer.add(omniBeamSegment);
                }
            } else {
                if (blockState.getLightBlock(world, blockPos) >= 15 && !passThruSolid) {
                    beaconEntity.prevBlockPos = beaconPos.offset(curDirectionInt.multiply(maxLength2));
                    opaqueBlockDetected = true;
                    break;
                }
                omniBeamSegment.increaseHeight();
            }
            beaconEntity.setPrevBlockPos(blockPos);
            blockPos = blockPos.offset(curDirectionInt);
        }

        opaqueBlockDetected = !passThruSolid && opaqueBlockDetected;
        boolean beamsIsComplete = beaconPos.distSqr(blockPos)>=maxLength2 || opaqueBlockDetected;
        if(beamsIsComplete){
            beaconEntity.prevColorID = curColorID - 1;// just to trigger reinitialization
            if (!opaqueBlockDetected && !beaconEntity.omniSegmentsBuffer.isEmpty()){
                //beaconEntity.omniSegmentsBuffer.get(beaconEntity.omniSegmentsBuffer.size()-1).overrideHeight(1024);
            }
            beaconEntity.omniBeamSegments = beaconEntity.omniSegmentsBuffer;
            if (!world.isClientSide) {
                if (!isPowered && beaconEntity.wasPowered()) {
                    playSound(world, beaconPos, SoundEvents.BEACON_DEACTIVATE);
                } else if (isPowered && !beaconEntity.wasPowered()) {
                    playSound(world, beaconPos, SoundEvents.BEACON_ACTIVATE);
                }
                beaconEntity.setWasPowered(isPowered);
                if (world.getGameTime() % 80L == 0L) {
                    if (isPowered) {
                        playSound(world, beaconPos, SoundEvents.BEACON_AMBIENT);
                    }
                }
            }
        }
    }

    @Override
    public AABB getRenderBoundingBox() {
        BlockPos pos = this.getBlockPos();
        Vec3i offset = new Vec3i(this.getBeamSegmentsTotalHeight(),this.getBeamSegmentsTotalHeight(),this.getBeamSegmentsTotalHeight());
        return new AABB(pos.subtract(offset),pos.offset(offset));
    }
}
