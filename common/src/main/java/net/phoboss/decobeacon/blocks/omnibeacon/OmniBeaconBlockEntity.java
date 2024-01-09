package net.phoboss.decobeacon.blocks.omnibeacon;

import com.google.common.collect.Lists;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.Properties;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3f;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import net.phoboss.decobeacon.blocks.ModBlockEntities;
import net.phoboss.decobeacon.blocks.decobeacon.DecoBeaconBlock;
import net.phoboss.decobeacon.blocks.decobeacon.DecoBeaconBlockEntity;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class OmniBeaconBlockEntity extends DecoBeaconBlockEntity {
    public OmniBeaconBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.OMNI_BEACON.get(), pos, state); //DO NOT FORGET TO CHECK THE BLOCK TYPE!!! YOU WILL GO CRAZY TRYING TO FIGURE OUT WHY THE BLOCK DOESN'T CALL THE TICK METHOD
    }

    public OmniBeaconBlockEntity(BlockPos pos, BlockState state,Boolean isGhost) {
        super(ModBlockEntities.OMNI_BEACON.get(), pos, state,isGhost); //DO NOT FORGET TO CHECK THE BLOCK TYPE!!! YOU WILL GO CRAZY TRYING TO FIGURE OUT WHY THE BLOCK DOESN'T CALL THE TICK METHOD
    }

    @Override
    public int getBeamSegmentsTotalHeight() {
        int totalHeight = 0;
        for (DecoBeamSegment segment:getOmniBeamSegments()) {
            totalHeight += segment.getHeight();
        }
        return totalHeight;
    }

    @ExpectPlatform
    public static OmniBeaconBlockEntity createPlatformSpecific(BlockPos pos, BlockState state, Boolean isGhost){
        throw new AssertionError();
    }

    @ExpectPlatform
    public static OmniBeaconBlockEntity createPlatformSpecific(BlockPos pos, BlockState state){
        throw new AssertionError();
    }

    private Vec3f beamDirection = new Vec3f(0,1,0);
    public Vec3f getBeamDirection() {
        return this.beamDirection;
    }
    public Vec3i getBeamDirectionInt() { //:`(
        return new Vec3i(this.beamDirection.getX(),this.beamDirection.getY(),this.beamDirection.getZ());
    }
    public void setBeamDirection(Vec3f beamDirection) {
        this.beamDirection = beamDirection;
        markDirty();
    }
    public void setBeamDirection(Vec3i beamDirection) { // :`(
        this.beamDirection = new Vec3f(beamDirection.getX(),beamDirection.getY(),beamDirection.getZ());
        markDirty();
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        Vec3f beamDir = getBeamDirection();
        nbt.putFloat("beamDirectionX",beamDir.getX());
        nbt.putFloat("beamDirectionY",beamDir.getY());
        nbt.putFloat("beamDirectionZ",beamDir.getZ());
        super.writeNbt(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.beamDirection = new Vec3f(nbt.getFloat("beamDirectionX"),nbt.getFloat("beamDirectionY"),nbt.getFloat("beamDirectionZ"));
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return createNbt();
    }

    @Override
    public void markDirty() {
        world.updateListeners(getPos(), getCachedState(), getCachedState(), Block.NOTIFY_ALL);
        super.markDirty();
    }



    @Override
    public void setWorld(World world) {
        super.setWorld(world);
        this.prevY = world.getBottomY() - 1;
    }

    public List<DecoBeamSegment> omniSegmentsBuffer = Lists.newArrayList();

    public List<DecoBeamSegment> getOmniSegmentsBuffer() {
        return this.omniSegmentsBuffer;
    }

    public void setOmniSegmentsBuffer(List<DecoBeamSegment> omniSegmentsBuffer) {
        this.omniSegmentsBuffer = omniSegmentsBuffer;
    }

    public List<DecoBeamSegment> omniBeamSegments = Lists.newArrayList();

    public List<DecoBeamSegment> getOmniBeamSegments() {
        return omniBeamSegments;
    }

    public void setOmniBeamSegments(List<DecoBeamSegment> omniBeamSegments) {
        this.omniBeamSegments = omniBeamSegments;
    }

    public BlockPos prevBlockPos = getPos();

    public BlockPos getPrevBlockPos() {
        return this.prevBlockPos;
    }

    public void setPrevBlockPos(BlockPos prevBlockPos) {
        this.prevBlockPos = prevBlockPos;
    }

    public Vec3f prevBeamDirection = getBeamDirection();

    public Vec3f getPrevBeamDirection() {
        return this.prevBeamDirection;
    }
    public Vec3i getPrevBeamDirectionInt() {
        return new Vec3i(this.prevBeamDirection.getX(),this.prevBeamDirection.getY(),this.prevBeamDirection.getZ());
    }
    public void setPrevBeamDirection(Vec3f prevBeamDirection) {
        this.prevBeamDirection = prevBeamDirection;
    }

    public void setPrevBeamDirection(Vec3i prevBeamDirection) {
        this.prevBeamDirection = new Vec3f(prevBeamDirection.getX(),prevBeamDirection.getY(),prevBeamDirection.getZ());
    }

    private static int maxBeamLength = 512;

    //every tick a new segment is made
    public static void tick(World world, BlockPos beaconPos, BlockState beaconState, OmniBeaconBlockEntity beaconEntity) {
        int curColorID = beaconState.get(DecoBeaconBlock.COLOR);
        boolean opaqueBlockDetected = false;
        boolean isPowered = beaconEntity.isPowered();
        boolean passThruSolid = beaconEntity.isGhost();
        Vec3f curDirection = beaconEntity.getBeamDirection();
        Vec3i curDirectionInt = beaconEntity.getBeamDirectionInt();

        BlockPos blockPos = beaconEntity.getPrevBlockPos().add(curDirectionInt);



        if(!world.isClient()){// note to self only update state properties in server-side
            world.setBlockState(beaconPos,beaconState.with(Properties.LIT,isPowered),Block.NOTIFY_ALL);
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
            omniBeamSegment = new DecoBeamSegment(DyeColor.byId(beaconState.get(DecoBeaconBlock.COLOR)).getColorComponents());
            beaconEntity.omniSegmentsBuffer.add(omniBeamSegment);
            beaconEntity.setPrevBlockPos(beaconPos);
            blockPos = beaconPos.add(curDirectionInt);

        }

        for(int b=0; b<maxBeamLength && beaconPos.getSquaredDistance(blockPos)<maxBeamLength*maxBeamLength ;++b)
        {
            BlockState blockState = world.getBlockState(blockPos);
            BlockEntity blockEntity = world.getBlockEntity(blockPos);
            float[] colorMultiplier = getColorMultiplier(blockState);
            if (colorMultiplier != null) {

                if (blockState.getBlock() instanceof DecoBeaconBlock) {
                    if(blockEntity instanceof DecoBeaconBlockEntity be){
                        if(!be.isTransparent()){ // if selected block is not a transparent beacon...
                            if(!be.isGhost()) { // if selected block is not a ghost beacon then end beam at this position
                                beaconEntity.prevBlockPos = beaconPos.add(curDirectionInt.multiply(maxBeamLength*maxBeamLength));
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
                if (blockState.getOpacity(world, blockPos) >= 15 && !passThruSolid) {
                    beaconEntity.prevBlockPos = beaconPos.add(curDirectionInt.multiply(maxBeamLength*maxBeamLength));
                    opaqueBlockDetected = true;
                    break;
                }
                omniBeamSegment.increaseHeight();
            }
            beaconEntity.setPrevBlockPos(blockPos);
            blockPos = blockPos.add(curDirectionInt);
        }

        opaqueBlockDetected = !passThruSolid && opaqueBlockDetected;
        boolean beamsIsComplete = beaconPos.getSquaredDistance(blockPos)>=maxBeamLength*maxBeamLength || opaqueBlockDetected;
        if(beamsIsComplete){
            beaconEntity.prevColorID = curColorID - 1;// just to trigger reinitialization
            if (!opaqueBlockDetected && !beaconEntity.omniSegmentsBuffer.isEmpty()){
                //beaconEntity.omniSegmentsBuffer.get(beaconEntity.omniSegmentsBuffer.size()-1).overrideHeight(1024);
            }
            beaconEntity.omniBeamSegments = beaconEntity.omniSegmentsBuffer;
            if (!world.isClient) {
                if (!isPowered && beaconEntity.wasPowered()) {
                    playSound(world, beaconPos, SoundEvents.BLOCK_BEACON_DEACTIVATE);
                } else if (isPowered && !beaconEntity.wasPowered()) {
                    playSound(world, beaconPos, SoundEvents.BLOCK_BEACON_ACTIVATE);
                }
                beaconEntity.setWasPowered(isPowered);
                if (world.getTime() % 80L == 0L) {
                    if (isPowered) {
                        playSound(world, beaconPos, SoundEvents.BLOCK_BEACON_AMBIENT);
                    }
                }
            }
        }
    }
}
