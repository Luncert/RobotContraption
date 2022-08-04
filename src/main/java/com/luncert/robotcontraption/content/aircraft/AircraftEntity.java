package com.luncert.robotcontraption.content.aircraft;

import com.luncert.robotcontraption.common.ActionCallback;
import com.luncert.robotcontraption.compat.create.AircraftContraption;
import com.luncert.robotcontraption.compat.create.AircraftContraptionEntity;
import com.luncert.robotcontraption.compat.create.AircraftMovementMode;
import com.luncert.robotcontraption.util.Common;
import com.luncert.robotcontraption.exception.AircraftAssemblyException;
import com.luncert.robotcontraption.exception.AircraftMovementException;
import com.luncert.robotcontraption.index.RCBlocks;
import com.luncert.robotcontraption.index.RCEntityTypes;
import com.simibubi.create.content.contraptions.components.structureMovement.AssemblyException;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.*;

import static com.luncert.robotcontraption.content.aircraft.AircraftMovement.MOVEMENT_SERIALIZER;
import static com.simibubi.create.content.contraptions.base.HorizontalKineticBlock.HORIZONTAL_FACING;

public class AircraftEntity extends Entity {

    private static final double MIN_MOVE_LENGTH = 0.001;

    private static final EntityDataAccessor<Integer> SPEED =
            SynchedEntityData.defineId(AircraftEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> TARGET_Y_ROT =
            SynchedEntityData.defineId(AircraftEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Optional<AircraftMovement>> TARGET_MOVEMENT =
            SynchedEntityData.defineId(AircraftEntity.class, MOVEMENT_SERIALIZER);

    private BlockPos stationPosition = BlockPos.ZERO;
    private BlockState stationBlockState = RCBlocks.AIRCRAFT_STATION.get().defaultBlockState();

    private final Queue<ActionCallback> asyncCallbacks = new ArrayDeque<>();
    public boolean isMoving;

    private int lerpSteps;
    private double lerpX;
    private double lerpY;
    private double lerpZ;
    private double lerpYRot;
    private double lerpXRot;

    // for client
    public AircraftEntity(EntityType<?> entity, Level world) {
        super(entity, world);
    }

    // for server
    public AircraftEntity(Level world, BlockPos stationPos, BlockState stationBlockState) {
        super(RCEntityTypes.AIRCRAFT.get(), world);
        this.stationPosition = stationPos;
        this.stationBlockState = stationBlockState;
        // following data will be synced automatically
        setPos(stationPos.getX() + .5f, stationPos.getY(), stationPos.getZ() + .5f);

        this.noPhysics = true;
        Direction blockDirection = stationBlockState.getValue(HORIZONTAL_FACING);
        setYRot(blockDirection.toYRot());
        setTargetYRot(getYRot());

        setDeltaMovement(Vec3.ZERO);
    }

    public static EntityType.Builder<?> build(EntityType.Builder<?> builder) {
        @SuppressWarnings("unchecked")
        EntityType.Builder<AircraftEntity> entityBuilder = (EntityType.Builder<AircraftEntity>) builder;
        return entityBuilder.sized(0.1f, 0.1f);
    }

    public boolean assemble(BlockPos pos, AircraftMovementMode mode) throws AircraftAssemblyException {
        AircraftContraption contraption = new AircraftContraption(mode);
        try {
            if (!contraption.assemble(level, pos)) {
                return false;
            }
        } catch (AssemblyException e) {
            throw new AircraftAssemblyException(e);
        }

        contraption.removeBlocksFromWorld(level, BlockPos.ZERO);
        contraption.startMoving(level);
        contraption.expandBoundsAroundAxis(Axis.Y);

        Direction initialOrientation = stationBlockState.getValue(HORIZONTAL_FACING);
        AircraftContraptionEntity entity = AircraftContraptionEntity.create(level, contraption, initialOrientation);

        entity.setPos(pos.getX() + .5f, pos.getY(), pos.getZ() + .5f);
        level.addFreshEntity(entity);
        entity.startRiding(this);

        return true;
    }

    public void dissemble() {
        ejectPassengers();
        discard();
    }

    public void up(int n, ActionCallback callback) throws AircraftMovementException {
        if (isMoving) {
            throw new AircraftMovementException("cannot_update_moving_aircraft");
        }

        setTargetMovement(new AircraftMovement(Axis.Y, true,
                (float) position().get(Axis.Y) + n));
        isMoving = true;
        asyncCallbacks.add(callback);
    }

    public void down(int n, ActionCallback callback) throws AircraftMovementException {
        if (isMoving) {
            throw new AircraftMovementException("cannot_update_moving_aircraft");
        }

        setTargetMovement(new AircraftMovement(Axis.Y, false,
                (float) position().get(Axis.Y) - n));
        isMoving = true;
        asyncCallbacks.add(callback);
    }

    public void forward(int n, ActionCallback callback) throws AircraftMovementException {
        if (isMoving) {
            throw new AircraftMovementException("cannot_update_moving_aircraft");
        }

        Direction direction = Direction.fromYRot(getTargetYRot());
        Axis axis = direction.getAxis();
        Direction.AxisDirection axisDirection = direction.getAxisDirection();
        int posDelta = axisDirection.getStep() * n;
        setTargetMovement(new AircraftMovement(axis, axisDirection.equals(Direction.AxisDirection.POSITIVE),
                (float) position().get(axis) + posDelta));
        isMoving = true;
        asyncCallbacks.add(callback);
    }

    public void turnLeft(ActionCallback callback) throws AircraftMovementException {
        rotate(-90, callback);
    }

    public void turnRight(ActionCallback callback) throws AircraftMovementException {
        rotate(90, callback);
    }

    private void rotate(int degree, ActionCallback callback) throws AircraftMovementException {
        if (isMoving) {
            throw new AircraftMovementException("cannot_update_moving_aircraft");
        }

        float targetYRot = getYRot() + degree;
        if (targetYRot < 0) {
            targetYRot += 360;
        } else if (targetYRot > 360) {
            targetYRot -= 360;
        }
        setTargetYRot(targetYRot);
        forward(1, callback);
    }

    public Vec3 getAircraftPosition() {
        BlockPos blockPos = blockPosition();
        // aircraft entity is invisible, so aircraft's position should be above the entity position
        return new Vec3(blockPos.getX(), blockPos.getY() + 1, blockPos.getZ());
    }

    @OnlyIn(Dist.CLIENT)
    public void lerpTo(double lerpX, double lerpY, double lerpZ, float lerpYRot, float lerpXRot,
                       int p_180426_9_, boolean p_180426_10_) {
        this.lerpX = lerpX;
        this.lerpY = lerpY;
        this.lerpZ = lerpZ;
        this.lerpYRot = lerpYRot;
        this.lerpXRot = lerpXRot;
        this.lerpSteps = 10;
    }

    @Override
    public void tick() {
        if (this.stationBlockState.isAir()) {
            discard();
            return;
        }

        if (!level.isClientSide && !(level.getBlockEntity(stationPosition) instanceof AircraftStationTileEntity)) {
            dissemble();
            return;
        }

        super.tick();
        tickLerp();

        boolean isStalled = false;
        List<Entity> passengers = getPassengers();
        if (!passengers.isEmpty()) {
            Entity entity = passengers.get(0);
            if (entity instanceof AircraftContraptionEntity contraptionEntity) {
                isStalled = contraptionEntity.isStalled();
            }
        }
        if (!isStalled) {
            updateMotion().ifPresent(motion -> {
                setDeltaMovement(motion);
                setPos(getX() + motion.x, getY() + motion.y, getZ() + motion.z);
            });
        }

        tickCollide();
    }

    private void tickLerp() {
        // isControlledByLocalInstance always return true in server side
        if (this.isControlledByLocalInstance()) {
            this.lerpSteps = 0;
            this.setPacketCoordinates(this.getX(), this.getY(), this.getZ());
            return;
        }

        // client only
        if (this.lerpSteps > 0) {
            double d0 = this.getX() + (this.lerpX - this.getX()) / (double)this.lerpSteps;
            double d1 = this.getY() + (this.lerpY - this.getY()) / (double)this.lerpSteps;
            double d2 = this.getZ() + (this.lerpZ - this.getZ()) / (double)this.lerpSteps;
            this.setPos(d0, d1, d2);

            double d3 = Mth.wrapDegrees(this.lerpYRot - getYRot());
            setYRot((float)((double)getYRot() + d3 / (double)this.lerpSteps));
            setXRot((float)((double)getXRot() + (this.lerpXRot - (double)getXRot()) / (double)this.lerpSteps));
            this.setRot(getYRot(), getXRot());

            --this.lerpSteps;
        }
    }

    private void tickCollide() {
        getTargetMovement().ifPresent(movement -> {
            // Direction direction = Direction.fromYRot(getTargetYRot());
            // Axis axis = direction.getAxis();
            // Direction.AxisDirection axisDirection = direction.getAxisDirection();
            //
            // BlockPos pos = blockPosition();
            // BlockPos targetPos = pos.relative(axis, axisDirection.getStep());
            // double dist = targetPos.get(axis) - pos.get(axis);
            if (Axis.Y.equals(movement.axis) && !movement.positive) {
                BlockPos pos = blockPosition();
                BlockPos targetPos = pos.below();
                double dist = targetPos.getY() - pos.getY();
                if (dist < MIN_MOVE_LENGTH && !isFree(level.getBlockState(targetPos))) {
                    setPos(Vec3.atBottomCenterOf(pos));
                    clearMotion();
                }
            }
        });
    }

    private boolean isFree(BlockState blockState) {
        return !blockState.is(Blocks.BEDROCK);
    }

    private Optional<Vec3> updateMotion() {
        Optional<AircraftMovement> opt = getTargetMovement();
        if (opt.isPresent()) {
            AircraftMovement movement = opt.get();
            double v = position().get(movement.axis);
            double absDist = Math.abs(movement.expectedPos - v);
            if (absDist > 0) {
                return Optional.ofNullable(updateMotion(absDist, movement));
            }
            setYRot(getTargetYRot());
            setTargetMovement(null);
        }

        clearMotion();
        return Optional.empty();
    }

    private Vec3 updateMotion(double absDistance, AircraftMovement movement) {
        if (absDistance < MIN_MOVE_LENGTH) {
            setPos(Common.set(position(), movement.axis, movement.expectedPos));
            return null;
        }

        float speed;
        if (getYRot() != getTargetYRot() || movement.axis.equals(Axis.Y)) {
            // if entity is rotating, set speed to 32
            speed = getMovementSpeed(32);
        } else {
            speed = getMovementSpeed();
        }

        double linearMotion = Math.min(speed, absDistance);
        if (!movement.positive) {
            linearMotion = -linearMotion;
        }

        return Common.linear(movement.axis, linearMotion);
    }

    private void clearMotion() {
        setTargetMovement(null);
        setDeltaMovement(Vec3.ZERO);
        if (isMoving && isControlledByLocalInstance()) {
            asyncCallbacks.remove().accept(true);
        }
        isMoving = false;
    }

    private float getMovementSpeed() {
        return getSpeed() / 512f * 1.5f;
    }

    private float getMovementSpeed(int speed) {
        return speed / 512f * 1.5f;
    }

    public void setSpeed(int speed) throws AircraftMovementException {
        if (isMoving) {
            throw new AircraftMovementException("cannot_update_moving_aircraft");
        }
        entityData.set(SPEED, Mth.clamp(Math.abs(speed), 0, 255));
    }

    public int getSpeed() {
        return entityData.get(SPEED);
    }

    public void setTargetYRot(float yRot) {
        entityData.set(TARGET_Y_ROT, yRot % 360);
    }

    public float getTargetYRot() {
        return entityData.get(TARGET_Y_ROT);
    }

    public void setTargetMovement(@Nullable AircraftMovement movement) {
        entityData.set(TARGET_MOVEMENT, Optional.ofNullable(movement));
    }

    private Optional<AircraftMovement> getTargetMovement() {
        return entityData.get(TARGET_MOVEMENT);
    }

    // data exchange

    @Override
    protected void defineSynchedData() {
        entityData.clearDirty();
        entityData.define(SPEED, 32);
        entityData.define(TARGET_Y_ROT, 0f);
        entityData.define(TARGET_MOVEMENT, Optional.empty());
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag root) {
        if (root.isEmpty())
            return;

        entityData.set(SPEED, root.getInt("speed"));
        entityData.set(TARGET_Y_ROT, root.getFloat("targetYRot"));

        if (root.getBoolean("hasTargetMovement")) {
            CompoundTag targetMovement = root.getCompound("targetMovement");
            setTargetMovement(new AircraftMovement(
                    Axis.values()[targetMovement.getInt("axis")],
                    targetMovement.getBoolean("positive"),
                    targetMovement.getFloat("expectedPos")));
        }

        CompoundTag stationPos = root.getCompound("stationPosition");
        stationPosition = new BlockPos(
                stationPos.getInt("x"),
                stationPos.getInt("y"),
                stationPos.getInt("z"));
        BlockEntity blockEntity = level.getBlockEntity(stationPosition);
        if (blockEntity instanceof AircraftStationTileEntity te) {
            te.rebindAircraftEntity(this);
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag root) {
        root.putInt("speed", getSpeed());
        root.putFloat("targetYRot", getTargetYRot());

        Optional<AircraftMovement> opt = getTargetMovement();
        root.putBoolean("hasTargetMovement", opt.isPresent());
        opt.ifPresent(movement -> {
            CompoundTag targetMovement = new CompoundTag();
            targetMovement.putInt("axis", movement.axis.ordinal());
            targetMovement.putBoolean("positive", movement.positive);
            targetMovement.putFloat("expectedPos", movement.expectedPos);
            root.put("targetMovement", targetMovement);
        });

        CompoundTag stationPos = new CompoundTag();
        stationPos.putInt("x", stationPosition.getX());
        stationPos.putInt("y", stationPosition.getY());
        stationPos.putInt("z", stationPosition.getZ());
        root.put("stationPosition", stationPos);
    }

    @Override
    public Packet<?> getAddEntityPacket() {
        return new ClientboundAddEntityPacket(this);
    }

    @Override
    public double getPassengersRidingOffset() {
        return 0;
    }
}
