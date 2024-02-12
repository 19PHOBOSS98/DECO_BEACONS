package net.phoboss.decobeacons.blocks.decobeacon;

import com.google.common.collect.Lists;

import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Stainable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.Properties;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;
import net.phoboss.decobeacons.DecoBeacons;
import net.phoboss.decobeacons.blocks.ModBlockEntities;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public class DecoBeaconBlockEntity extends BlockEntity {

    public DecoBeaconBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.DECO_BEACON, pos, state);
        this.bookSettings = this.setupBookSettings();
    }

    public DecoBeaconBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        this.bookSettings = this.setupBookSettings();
    }

    public DecoBeaconBlockEntity(BlockPos pos, BlockState state,Boolean isGhost) {
        this(pos, state);
        this.isGhost = isGhost;

    }

    public DecoBeaconBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state,Boolean isGhost) {
        this(type, pos, state);
        this.isGhost = isGhost;
    }

    public static class DecoBeamSegment {
        final float[] color;
        private int height;

        public DecoBeamSegment(float[] color) {
            this.color = color;
            this.height = 1;
        }

        public void increaseHeight() {
            ++this.height;
        }

        public void overrideHeight(int newHeight) {
            this.height = newHeight;
        }

        public float[] getColor() {
            return this.color;
        }

        public int getHeight() {
            return this.height;
        }
    }
    private boolean isGhost = false;
    private boolean activeLow = false;
    private boolean isTransparent = true;
    private int curColorID = 0;
    public int prevY;
    public int prevColorID = 0;
    public boolean wasPowered = false;
    public List<DecoBeamSegment> segmentsBuffer = Lists.newArrayList();
    public List<DecoBeamSegment> decoBeamSegments = Lists.newArrayList();

    public Object2ObjectLinkedOpenHashMap<String,String> bookSettings;

    public Object2ObjectLinkedOpenHashMap<String,String> setupBookSettings(){
        Object2ObjectLinkedOpenHashMap<String,String> map = new Object2ObjectLinkedOpenHashMap<>();
        map.put("color","white");//DyeColor names (i.e. red,blue,lime)
        map.put("activeLow","false");
        map.put("isTransparent","true");
        return map;
    }
    public int getCurColorID() {
        return this.curColorID;
    }
    public void setCurColorID(int colorID) {
        this.curColorID = colorID;
        this.bookSettings.put("color",DyeColor.byId(colorID).getName());
        markDirty();
    }
    public boolean isGhost() {
        return this.isGhost;
    }

    public boolean isActiveLow(){
        return this.activeLow;
    }

    public void setActiveLow(boolean activeLow){
        this.activeLow = activeLow;
        this.bookSettings.put("activeLow",Boolean.toString(activeLow));
        markDirty();
    }

    public boolean isTransparent() {
        return this.isTransparent;
    }

    public void setTransparent(boolean transparent) {
        this.isTransparent = transparent;
        this.bookSettings.put("isTransparent",Boolean.toString(transparent));
        markDirty();
    }

    public List<DecoBeamSegment> getDecoBeamSegments() {
        return decoBeamSegments;
    }

    public int getBeamSegmentsTotalHeight(){
        int totalHeight = 0;
        for (DecoBeamSegment segment:getDecoBeamSegments()) {
            totalHeight += segment.getHeight();
        }
        return totalHeight;
    }

    public boolean wasPowered() {
        return wasPowered;
    }

    public void setWasPowered(boolean wasPowered) {
        this.wasPowered = wasPowered;
    }

    public boolean isPowered() {
        boolean active = false;
        try {
            active = getWorld().isReceivingRedstonePower(getPos());
            //active = this.getCachedState().get(OmniBeaconBlock.ACTIVE_LOW) != active;
            active = this.isActiveLow() != active;
        }catch(Exception e){
            DecoBeacons.LOGGER.error("Error on isPowered() method: ",e);
        }
        return active;
    }


    @Override
    protected void writeNbt(NbtCompound nbt) {
        nbt.putBoolean("activeLow",isActiveLow());
        nbt.putBoolean("isTransparent",isTransparent());
        nbt.putInt("color",getCurColorID());
        super.writeNbt(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        try {
            super.readNbt(nbt);
            this.activeLow = nbt.getBoolean("activeLow");
            this.isTransparent = nbt.getBoolean("isTransparent");
            this.curColorID = nbt.getInt("color");

            this.bookSettings.put("activeLow",Boolean.toString(this.activeLow));
            this.bookSettings.put("isTransparent",Boolean.toString(this.isTransparent));
            this.bookSettings.put("color",DyeColor.byId(this.curColorID).getName());
        }catch(Exception e){
            DecoBeacons.LOGGER.error("Error on DecoBeacon readNbt(...):",e);
        }
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
        try {
            world.updateListeners(getPos(), getCachedState(), getCachedState(), Block.NOTIFY_ALL);
            super.markDirty();
        }catch (Exception e){
            DecoBeacons.LOGGER.error("Error on markDirt() method: ",e);
        }
    }

    public static void tick(World world, BlockPos pos, BlockState state, DecoBeaconBlockEntity entity) {
        boolean isPowered = entity.isPowered();
        boolean passThruSolid = entity.isGhost();
        int curColorID = entity.getCurColorID();

        if(!world.isClient()){// note to self only update state properties in server-side
            world.setBlockState(pos,state.with(Properties.LIT,isPowered).with(DecoBeaconBlock.COLOR,curColorID),Block.NOTIFY_ALL);
        }

        int i = pos.getX();
        int j = pos.getY();
        int k = pos.getZ();
        BlockPos blockPos;

        if (entity.prevColorID != curColorID) {
            entity.prevY = world.getBottomY() - 1;
            entity.prevColorID = curColorID;
        }


        if (entity.prevY < j) {
            blockPos = pos;
            entity.segmentsBuffer = Lists.newArrayList();
            entity.prevY = pos.getY() - 1;
        } else {
            blockPos = new BlockPos(i, entity.prevY + 1, k);
        }

        DecoBeamSegment decoBeamSegment = entity.segmentsBuffer.isEmpty()
                ? null
                : entity.segmentsBuffer.get(entity.segmentsBuffer.size() - 1);

        int worldSurface = world.getTopY(Heightmap.Type.WORLD_SURFACE, i, k);
        boolean opaqueBlockDetected = false;

        if (entity.segmentsBuffer.size() < 1) {
            decoBeamSegment = new DecoBeamSegment(DyeColor.byId(state.get(DecoBeaconBlock.COLOR)).getColorComponents());
            entity.segmentsBuffer.add(decoBeamSegment);
            blockPos = blockPos.up();
            ++entity.prevY;
        }

        for (int m = 0; m < 10 && blockPos.getY() <= worldSurface; ++m) {
            BlockState blockState = world.getBlockState(blockPos);
            BlockEntity blockEntity = world.getBlockEntity(blockPos);

            float[] colorMultiplier = getColorMultiplier(blockState);
            if (colorMultiplier != null) {

                if (blockState.getBlock() instanceof DecoBeaconBlock) {
                    if(blockEntity instanceof DecoBeaconBlockEntity be){
                        if(!be.isTransparent()){
                            if(!be.isGhost()) {
                                entity.prevY = worldSurface;
                                //decoBeamSegment.increaseHeight();
                                opaqueBlockDetected = true;
                                break;
                            }else{
                                colorMultiplier = decoBeamSegment.color;
                            }
                        }
                    }
                }

                if (decoBeamSegment != null) {
                    if (Arrays.equals(colorMultiplier, decoBeamSegment.color)) {
                        decoBeamSegment.increaseHeight();
                    } else {
                        decoBeamSegment = new DecoBeamSegment(new float[]{
                                (decoBeamSegment.color[0] + colorMultiplier[0]) / 2.0F,
                                (decoBeamSegment.color[1] + colorMultiplier[1]) / 2.0F,
                                (decoBeamSegment.color[2] + colorMultiplier[2]) / 2.0F});
                        entity.segmentsBuffer.add(decoBeamSegment);
                    }
                }
            } else {
                if (decoBeamSegment == null) {
                    entity.segmentsBuffer.clear();
                    entity.prevY = worldSurface;
                    break;
                }

                if (blockState.getOpacity(world, blockPos) >= 15 && !passThruSolid) {
                    entity.prevY = worldSurface;
                    //decoBeamSegment.increaseHeight();
                    opaqueBlockDetected = true;
                    break;
                }

                decoBeamSegment.increaseHeight();
            }

            blockPos = blockPos.up();
            ++entity.prevY;
        }

        if (world.getTime() % 80L == 0L) {
            if (isPowered) {
                playSound(world, pos, SoundEvents.BLOCK_BEACON_AMBIENT);
            }
        }

        opaqueBlockDetected = !passThruSolid && opaqueBlockDetected;

        if (entity.prevY >= worldSurface || opaqueBlockDetected) {
            entity.prevY = world.getBottomY() - 1;
            if (!opaqueBlockDetected && !entity.segmentsBuffer.isEmpty()){
                entity.segmentsBuffer.get(entity.segmentsBuffer.size()-1).overrideHeight(1024);
            }

            entity.decoBeamSegments = entity.segmentsBuffer;

            if (!world.isClient) {
                if (!isPowered && entity.wasPowered()) {
                    playSound(world, pos, SoundEvents.BLOCK_BEACON_DEACTIVATE);
                } else if (isPowered && !entity.wasPowered()) {
                    playSound(world, pos, SoundEvents.BLOCK_BEACON_ACTIVATE);
                }
                entity.setWasPowered(isPowered);
            }
        }


    }

    public static float[] getColorMultiplier(BlockState blockState)
    {
        Block block = blockState.getBlock();

        if (!(block instanceof Stainable)) {
            return null;
        }

        return block instanceof DecoBeaconBlock ?
                DyeColor.byId(blockState.get(DecoBeaconBlock.COLOR)).getColorComponents()
                : ((Stainable) block).getColor().getColorComponents();
    }

    public static void playSound(World world, BlockPos pos, SoundEvent sound) {
        world.playSound(null, pos, sound, SoundCategory.BLOCKS, 1.0F, 1.0F);
    }

    @Override
    public void setWorld(World world) {
        super.setWorld(world);
        this.prevY = world.getBottomY() - 1;
    }
}
