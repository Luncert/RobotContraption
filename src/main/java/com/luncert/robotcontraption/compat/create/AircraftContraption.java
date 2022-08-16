package com.luncert.robotcontraption.compat.create;

import com.luncert.robotcontraption.RobotContraption;
import com.luncert.robotcontraption.compat.aircraft.AircraftAccessor;
import com.luncert.robotcontraption.compat.aircraft.AircraftComponentType;
import com.luncert.robotcontraption.compat.aircraft.BaseAircraftComponent;
import com.luncert.robotcontraption.compat.aircraft.IAircraftComponent;
import com.luncert.robotcontraption.content.aircraft.*;
import com.luncert.robotcontraption.index.RCBlocks;
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
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
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
import java.util.stream.Collectors;

import static com.luncert.robotcontraption.index.RCContraptionTypes.AIRCRAFT;

public class AircraftContraption extends Contraption {

    private final Map<String, List<IAircraftComponent>> components = new HashMap<>();
    private final Map<String, StructureBlockInfo> componentBlockInfoMap = new HashMap<>();
    private List<List<String>> componentTickOrders;
    private AircraftEntity aircraft;
    private AircraftAccessor accessor;

    public EAircraftMovementMode rotationMode;

    public AircraftContraption() {
        this(EAircraftMovementMode.ROTATE, null);
    }

    public AircraftContraption(EAircraftMovementMode rotationMode, AircraftEntity aircraft) {
        this.rotationMode = rotationMode;
        this.aircraft = aircraft;
    }

    public Map<String, List<IAircraftComponent>> getComponents() {
        return components;
    }

    public List<List<IAircraftComponent>> getOrderedComponents() {
        return componentTickOrders.stream().map(componentTypes -> {
            List<IAircraftComponent> c = new ArrayList<>();
            for (String componentType : componentTypes) {
                c.addAll(components.get(componentType));
            }
            return c;
        }).collect(Collectors.toList());
    }

    public StructureBlockInfo getComponentBlockInfo(String name) {
        return componentBlockInfoMap.get(name);
    }

    public BlockPos getWorldPos(BlockPos pos) {
        return aircraft.blockPosition().offset(pos.getX(), pos.getY(), pos.getZ());
    }

    public void initComponents(Level level, AircraftEntity aircraft) {
        if (accessor == null) {
            this.aircraft = aircraft;
            AircraftStationTileEntity station = (AircraftStationTileEntity) level.getBlockEntity(aircraft.getStationPosition());
            accessor = new AircraftAccessor(level, station.getPeripheral(), station, aircraft, this);

            Map<Integer, List<String>> tickOrders = new HashMap<>();

            for (Map.Entry<String, List<IAircraftComponent>> entry : components.entrySet()) {
                List<IAircraftComponent> components = entry.getValue();
                for (int i = 0; i < components.size(); i++) {
                    IAircraftComponent c = components.get(i);
                    String name = c.getComponentType().getName() + "-" + i;
                    c.init(accessor, name);
                }

                Class<? extends IAircraftComponent> type = components.get(0).getClass();
                int order = 0;
                if (type.isAnnotationPresent(TickOrder.class)) {
                    TickOrder tickOrder = type.getAnnotation(TickOrder.class);
                    order = tickOrder.value();
                }

                tickOrders.compute(order, (k, v) -> {
                    if (v == null) {
                        v = new LinkedList<>();
                    }
                    v.add(entry.getKey());
                    return v;
                });
            }

            componentTickOrders = tickOrders.entrySet().stream()
                    .sorted(Map.Entry.comparingByKey())
                    .map(Map.Entry::getValue)
                    .collect(Collectors.toList());
            RobotContraption.LOGGER.info("components order {}", componentTickOrders);
        }

        accessor.resources.clear();
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

        if (blocks.size() != 1) {
            initComponents(world, aircraft);
            return true;
        }

        return false;
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
        if (!RCBlocks.AIRCRAFT_STATION.has(capture.state)) {
            return pair;
        }

        // replace aircraft station with anchor block
        return Pair.of(new StructureBlockInfo(pos, AircraftStationBlock.createAnchor(capture.state), null), pair.getValue());
    }

    @Override
    protected void addBlock(BlockPos pos, Pair<StructureBlockInfo, BlockEntity> pair) {
        super.addBlock(pos, pair);

        if (pair.getValue() == null) {
            return;
        }

        BlockPos localPos = pos.subtract(anchor);
        LazyOptional<IAircraftComponent> opt = pair.getValue().getCapability(RCCapabilities.CAPABILITY_AIRCRAFT_COMPONENT);
        opt.ifPresent(c ->
                components.compute(c.getComponentType().getName(), (k, v) -> {
                    if (v == null) {
                        v = new LinkedList<>();
                    }

                    componentBlockInfoMap.put(c.getComponentType().getName() + "-" + v.size(), blocks.get(localPos));

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

        ListTag componentList = new ListTag();
        for (Map.Entry<String, List<IAircraftComponent>> entry : components.entrySet()) {
            List<IAircraftComponent> components = entry.getValue();
            for (int i = 0; i < components.size(); i++) {
                CompoundTag item = new CompoundTag();
                item.putString("name", entry.getKey() + "-" + i);

                IAircraftComponent component = components.get(i);
                Tag c = component.writeNBT();
                if (c != null) {
                    item.put("component", c);
                }

                componentList.add(item);
            }
        }

        ListTag componentInfoList = new ListTag();
        for (Map.Entry<String, StructureBlockInfo> entry : componentBlockInfoMap.entrySet()) {
            CompoundTag item = new CompoundTag();
            item.putString("name", entry.getKey());
            item.putLong("pos", entry.getValue().pos.asLong());
            componentInfoList.add(item);
        }

        tag.put("components", componentList);
        tag.put("componentInfoMappings", componentInfoList);

        // System.out.println("write -" + tag);
        return tag;
    }

    @Override
    public void readNBT(Level world, CompoundTag nbt, boolean spawnData) {
        super.readNBT(world, nbt, spawnData);
        // System.out.println("read - " + nbt);

        rotationMode = NBTHelper.readEnum(nbt, "RotationMode", EAircraftMovementMode.class);

        this.components.clear();
        ListTag componentList = nbt.getList("components", 10);
        for (Tag tag : componentList) {
            CompoundTag componentNbt = (CompoundTag) tag;
            Pair<String, Integer> name = BaseAircraftComponent.parseName(componentNbt.getString("name"));
            String componentType = name.getKey();
            int componentId = name.getValue();
            this.components.compute(componentType, (k, v) -> {
                if (v == null) {
                    v = new ArrayList<>();
                }
                for (int n = v.size(); n <= componentId; n++) {
                    v.add(null);
                }
                IAircraftComponent component = AircraftComponentType.createComponent(componentType);
                component.readNBT(world, componentNbt.get("component"));

                v.set(componentId, component);
                return v;
            });
        }

        this.componentBlockInfoMap.clear();
        ListTag componentInfoList = nbt.getList("componentInfoMappings", CompoundTag.TAG_COMPOUND);
        // RobotContraption.LOGGER.info("{}", blocks);
        for (Tag tag : componentInfoList) {
            CompoundTag componentNbt = (CompoundTag) tag;
            String name = componentNbt.getString("name");
            this.componentBlockInfoMap.put(name, blocks.get(BlockPos.of(componentNbt.getLong("pos"))));
        }
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
