package net.phoboss.decobeacons.blocks.decobeacon;

import com.google.common.collect.Lists;

import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BeaconBeamBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.extensions.IForgeBlockEntity;
import net.phoboss.decobeacons.DecoBeacons;
import net.phoboss.decobeacons.blocks.ModBlockEntities;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public class DecoBeaconBlockEntity extends BlockEntity implements IForgeBlockEntity {

    public DecoBeaconBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.DECO_BEACON.get(), pos, state);
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
        this.bookSettings.put("color", DyeColor.byId(colorID).getName());
        setChanged();
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
        setChanged();
    }

    public boolean isTransparent() {
        return this.isTransparent;
    }

    public void setTransparent(boolean transparent) {
        this.isTransparent = transparent;
        this.bookSettings.put("isTransparent",Boolean.toString(transparent));
        setChanged();
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
            active = getLevel().hasNeighborSignal(getBlockPos());
            //active = this.getCachedState().get(OmniBeaconBlock.ACTIVE_LOW) != active;
            active = this.isActiveLow() != active;
        }catch(Exception e){
            DecoBeacons.LOGGER.error("Error on isPowered() method: ",e);
        }
        return active;
    }


    @Override
    protected void saveAdditional(CompoundTag nbt) {
        nbt.putBoolean("activeLow",isActiveLow());
        nbt.putBoolean("isTransparent",isTransparent());
        nbt.putInt("color",getCurColorID());
        super.saveAdditional(nbt);
    }

    @Override
    public void load(CompoundTag nbt) {
        try {
            super.load(nbt);
            this.activeLow = nbt.getBoolean("activeLow");
            this.isTransparent = nbt.getBoolean("isTransparent");
            this.curColorID = nbt.getInt("color");

            this.bookSettings.put("activeLow",Boolean.toString(this.activeLow));
            this.bookSettings.put("isTransparent",Boolean.toString(this.isTransparent));
            this.bookSettings.put("color",DyeColor.byId(this.curColorID).getName());
        }catch(Exception e){
            DecoBeacons.LOGGER.error("Error on DecoBeacon load(...):",e);
        }
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag() {
        return saveWithoutMetadata();
    }

    @Override
    public void setChanged() {
        try {
            getLevel().sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), Block.UPDATE_ALL);
            super.setChanged();
        }catch (Exception e){
            DecoBeacons.LOGGER.error("Error on markDirt() method: ",e);
        }
    }

    public static void tick(Level world, BlockPos pos, BlockState state, DecoBeaconBlockEntity entity) {
        boolean isPowered = entity.isPowered();
        boolean passThruSolid = entity.isGhost();
        int curColorID = entity.getCurColorID();

        if(!world.isClientSide()){// note to self only update state properties in server-side
            world.setBlock(pos,state.setValue(BlockStateProperties.LIT,isPowered),Block.UPDATE_ALL);
            world.setBlock(pos,state.setValue(DecoBeaconBlock.COLOR,curColorID),Block.UPDATE_ALL);
        }

        int i = pos.getX();
        int j = pos.getY();
        int k = pos.getZ();
        BlockPos blockPos;

        if (entity.prevColorID != curColorID) {
            entity.prevY = world.getMinBuildHeight() - 1;
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

        int worldSurface = world.getHeight(Heightmap.Types.WORLD_SURFACE, i, k);
        boolean opaqueBlockDetected = false;

        if (entity.segmentsBuffer.size() < 1) {
            decoBeamSegment = new DecoBeamSegment(DyeColor.byId(state.getValue(DecoBeaconBlock.COLOR)).getTextureDiffuseColors());
            entity.segmentsBuffer.add(decoBeamSegment);
            blockPos = blockPos.above();
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

                if (blockState.getLightBlock(world, blockPos) >= 15 && !passThruSolid) {
                    entity.prevY = worldSurface;
                    //decoBeamSegment.increaseHeight();
                    opaqueBlockDetected = true;
                    break;
                }

                decoBeamSegment.increaseHeight();
            }

            blockPos = blockPos.above();
            ++entity.prevY;
        }

        if (world.getGameTime() % 80L == 0L) {
            if (isPowered) {
                playSound(world, pos, SoundEvents.BEACON_AMBIENT);
            }
        }

        opaqueBlockDetected = !passThruSolid && opaqueBlockDetected;

        if (entity.prevY >= worldSurface || opaqueBlockDetected) {
            entity.prevY = world.getMinBuildHeight() - 1;
            if (!opaqueBlockDetected && !entity.segmentsBuffer.isEmpty()){
                entity.segmentsBuffer.get(entity.segmentsBuffer.size()-1).overrideHeight(1024);
            }

            entity.decoBeamSegments = entity.segmentsBuffer;

            if (!world.isClientSide) {
                if (!isPowered && entity.wasPowered()) {
                    playSound(world, pos, SoundEvents.BEACON_DEACTIVATE);
                } else if (isPowered && !entity.wasPowered()) {
                    playSound(world, pos, SoundEvents.BEACON_ACTIVATE);
                }
                entity.setWasPowered(isPowered);
            }
        }


    }

    public static float[] getColorMultiplier(BlockState blockState)
    {
        Block block = blockState.getBlock();

        if (!(block instanceof BeaconBeamBlock)) {
            return null;
        }

        return block instanceof DecoBeaconBlock ?
                DyeColor.byId(blockState.getValue(DecoBeaconBlock.COLOR)).getTextureDiffuseColors()
                : ((BeaconBeamBlock) block).getColor().getTextureDiffuseColors();
    }

    public static void playSound(Level world, BlockPos pos, SoundEvent sound) {
        world.playSound(null, pos, sound, SoundSource.BLOCKS, 1.0F, 1.0F);
    }

    @Override
    public void setLevel(Level world) {
        super.setLevel(world);
        this.prevY = world.getMinBuildHeight() - 1;
    }

    @Override
    public AABB getRenderBoundingBox() {
        BlockPos pos = this.getBlockPos();
        return new AABB(pos,pos.offset(1,this.getBeamSegmentsTotalHeight(),1));
    }
}
