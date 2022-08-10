package com.luncert.robotcontraption.compat.create;

import com.luncert.robotcontraption.compat.aircraft.IAircraftComponent;
import com.luncert.robotcontraption.index.RCBlocks;
import com.luncert.robotcontraption.content.aircraft.AircraftStationBlock;
import com.luncert.robotcontraption.index.RCCapabilities;
import com.simibubi.create.content.contraptions.components.structureMovement.AssemblyException;
import com.simibubi.create.content.contraptions.components.structureMovement.Contraption;
import com.simibubi.create.content.contraptions.components.structureMovement.ContraptionType;
import com.simibubi.create.content.contraptions.components.structureMovement.NonStationaryLighter;
import com.simibubi.create.content.contraptions.components.structureMovement.render.ContraptionLighter;
import com.simibubi.create.foundation.utility.NBTHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.LazyOptional;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;

import static com.luncert.robotcontraption.index.RCContraptionTypes.AIRCRAFT;

public class AircraftContraption extends Contraption {

    private final Map<String, List<IAircraftComponent>> components = new HashMap<>();
    private final Map<String, StructureBlockInfo> componentBlockInfoMap = new HashMap<>();

    public EAircraftMovementMode rotationMode;

    public AircraftContraption() {
        this(EAircraftMovementMode.ROTATE);
    }

    public AircraftContraption(EAircraftMovementMode rotationMode) {
        this.rotationMode = rotationMode;
    }

    public Map<String, List<IAircraftComponent>> getComponents() {
        return components;
    }

    public StructureBlockInfo getComponentBlockInfo(String name) {
        return componentBlockInfoMap.get(name);
    }

    public BlockPos getAnchorPos() {
        return componentBlockInfoMap.get("anchor").pos;
    }

    @Override
    protected ContraptionType getType() {
        return AIRCRAFT;
    }

    @Override
    // TBD
    public boolean assemble(Level world, BlockPos pos) throws AssemblyException {
        if (!searchMovedStructure(world, pos, null))
            return false;

        addBlock(pos, Pair.of(new StructureBlockInfo(
                pos, RCBlocks.AIRCRAFT_ANCHOR.getDefaultState(), null), null));

        return blocks.size() != 1;
    }

    @Override
    protected boolean addToInitialFrontier(Level world, BlockPos pos, Direction direction, Queue<BlockPos> frontier) {
        frontier.clear();
        frontier.add(pos.above());
        return true;
    }

    @Override
    protected Pair<StructureBlockInfo, BlockEntity> capture(Level world, BlockPos pos) {
        Pair<StructureBlockInfo, BlockEntity> pair = super.capture(world, pos);
        StructureBlockInfo capture = pair.getKey();
        captureAircraftComponent(pair);
        if (!RCBlocks.AIRCRAFT_STATION.has(capture.state)) {
            return pair;
        }

        // replace aircraft station with anchor block
        StructureBlockInfo info = new StructureBlockInfo(pos, AircraftStationBlock.createAnchor(capture.state), null);
        componentBlockInfoMap.put("anchor", info);
        return Pair.of(info, pair.getValue());
    }

    private void captureAircraftComponent(Pair<StructureBlockInfo, BlockEntity> entry) {
        LazyOptional<IAircraftComponent> opt = entry.getValue().getCapability(RCCapabilities.CAPABILITY_AIRCRAFT_COMPONENT);
        opt.ifPresent(c ->
                components.compute(c.getComponentType(), (k, v) -> {
                   if (v == null) {
                       v = new LinkedList<>();
                   }

                   componentBlockInfoMap.put(c.getComponentType() + "-" + v.size(), entry.getKey());

                   v.add(c);
                   return v;
                }));
    }

    @Override
    protected boolean movementAllowed(BlockState state, Level world, BlockPos pos) {
        // if (!pos.equals(anchor) && RCBlocks.ROBOT_STATION.has(state))
        //     return testSecondaryCartAssembler(world, state, pos);
        return super.movementAllowed(state, world, pos);
    }

    @Override
    public CompoundTag writeNBT(boolean spawnPacket) {
        CompoundTag tag = super.writeNBT(spawnPacket);
        NBTHelper.writeEnum(tag, "RotationMode", rotationMode);
        return tag;
    }

    @Override
    public void readNBT(Level world, CompoundTag nbt, boolean spawnData) {
        rotationMode = NBTHelper.readEnum(nbt, "RotationMode", EAircraftMovementMode.class);
        super.readNBT(world, nbt, spawnData);
    }

    @Override
    protected boolean customBlockPlacement(LevelAccessor world, BlockPos pos, BlockState state) {
        return RCBlocks.AIRCRAFT_ANCHOR.has(state);
    }


    @Override
    protected boolean customBlockRemoval(LevelAccessor world, BlockPos pos, BlockState state) {
        return RCBlocks.AIRCRAFT_ANCHOR.has(state);
    }


    @Override
    public boolean canBeStabilized(Direction facing, BlockPos localPos) {
        return false;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public ContraptionLighter<?> makeLighter() {
        return new NonStationaryLighter<>(this);
    }
}
