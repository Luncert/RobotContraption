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
import com.mojang.math.Vector3d;
import com.simibubi.create.content.contraptions.components.structureMovement.AssemblyException;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.ArrayDeque;
import java.util.Optional;
import java.util.Queue;

import static com.luncert.robotcontraption.content.aircraft.AircraftMovement.MOVEMENT_SERIALIZER;
import static com.simibubi.create.content.contraptions.base.HorizontalKineticBlock.HORIZONTAL_FACING;

public class AircraftEntity extends Entity {

    private static final double MIN_MOVE_LENGTH = 1.0E-7D;

    private static final EntityDataAccessor<Integer> SPEED =
            SynchedEntityData.defineId(AircraftEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Optional<AircraftMovement>> TARGET_MOVEMENT =
            SynchedEntityData.defineId(AircraftEntity.class, MOVEMENT_SERIALIZER);

    private BlockState blockState = RCBlocks.AIRCRAFT_STATION.get().defaultBlockState();

    private final Queue<ActionCallback> asyncCallbacks = new ArrayDeque<>();
    private boolean isStalled;
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
    public AircraftEntity(Level world, BlockPos stationPos, BlockState blockState) {
        super(RCEntityTypes.AIRCRAFT.get(), world);
        this.blockState = blockState;
        // following data will be synced automatically
        setPos(stationPos.getX() + .5f, stationPos.getY(), stationPos.getZ() + .5f);

        this.noPhysics = true;
        Direction blockDirection = blockState.getValue(HORIZONTAL_FACING);
        setYRot(blockDirection.toYRot());

        setDeltaMovement(Vec3.ZERO);
    }

    public static EntityType.Builder<?> build(EntityType.Builder<?> builder) {
        @SuppressWarnings("unchecked")
        EntityType.Builder<AircraftEntity> entityBuilder = (EntityType.Builder<AircraftEntity>) builder;
        return entityBuilder.sized(0.1f, 0.1f);
    }

    public void assemble(BlockPos pos, AircraftMovementMode mode) throws AircraftAssemblyException {
        AircraftContraption contraption = new AircraftContraption(mode);
        try {
            if (!contraption.assemble(level, pos)) {
                return;
            }
        } catch (AssemblyException e) {
            throw new AircraftAssemblyException(e);
        }

        contraption.removeBlocksFromWorld(level, BlockPos.ZERO);
        contraption.startMoving(level);
        contraption.expandBoundsAroundAxis(Direction.Axis.Y);

        Direction initialOrientation = blockState.getValue(HORIZONTAL_FACING);
        AircraftContraptionEntity entity = AircraftContraptionEntity.create(level, contraption, initialOrientation);

        entity.setPos(pos.getX() + .5f, pos.getY(), pos.getZ() + .5f);
        level.addFreshEntity(entity);
        entity.startRiding(this);
    }

    public void dissemble() {
        ejectPassengers();
        discard();
    }

    public void forward(int n, ActionCallback callback) throws AircraftMovementException {
        if (isMoving) {
            throw new AircraftMovementException("cannot_update_moving_aircraft");
        }

        Direction direction = Direction.fromYRot(getYRot());
        Direction.Axis axis = direction.getAxis();
        Direction.AxisDirection axisDirection = direction.getAxisDirection();
        int posDelta = axisDirection.getStep() * n;
        setWaitingMovement(new AircraftMovement(axis, axisDirection.equals(Direction.AxisDirection.POSITIVE),
                blockPosition().get(axis) + .5f + posDelta));
        isMoving = true;
        asyncCallbacks.add(callback);
    }

    public void turnLeft(int n, ActionCallback callback) throws AircraftMovementException {
        if (isMoving) {
            throw new AircraftMovementException("cannot_update_moving_aircraft");
        }

        rotate(-90);
        forward(n, callback);
    }

    public void turnRight(int n, ActionCallback callback) throws AircraftMovementException {
        if (isMoving) {
            throw new AircraftMovementException("cannot_update_moving_aircraft");
        }

        rotate(90);
        forward(n, callback);
    }

    public void stall() {
        isStalled = true;
        setDeltaMovement(0, 0, 0);
    }

    public void cancelStall(Vec3 motionBeforeStall) {
        isStalled = false;
        setDeltaMovement(motionBeforeStall);
    }

    private void rotate(int degree) {
        setYRot(wrapDegrees(getYRot() + degree));
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
        if (this.blockState.isAir()) {
            discard();
            return;
        }

        super.tick();
        tickLerp();

        if (!isStalled) {
            updateMotion().ifPresent(motion -> move(MoverType.SELF, this.getDeltaMovement().add(motion)));
        }

        // tickCollide();
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
            double d3 = Mth.wrapDegrees(this.lerpYRot - getYRot());
            setYRot((float)((double)getYRot() + d3 / (double)this.lerpSteps));
            setXRot((float)((double)getXRot() + (this.lerpXRot - (double)getXRot()) / (double)this.lerpSteps));
            --this.lerpSteps;
            this.setPos(d0, d1, d2);
            this.setRot(getYRot(), getXRot());
        }
    }

    private Optional<Vec3> updateMotion() {
        Optional<AircraftMovement> opt = getTargetMovement();
        if (opt.isPresent()) {
            AircraftMovement movement = opt.get();
            double v = position().get(movement.axis);
            if (v != movement.expectedPos) {
                double absDist = Math.abs(movement.expectedPos - v);
                if (absDist != 0) {
                    return updateDeltaMovement(absDist);
                }
            }
            setWaitingMovement(null);
        }

        setDeltaMovement(Vec3.ZERO);
        if (isMoving && isControlledByLocalInstance()) {
            asyncCallbacks.remove().accept(true);
        }
        isMoving = false;
        return Optional.empty();
    }

    private Optional<Vec3> updateDeltaMovement(double absDistance) {
        return getTargetMovement().map(movement -> {
            if (absDistance < MIN_MOVE_LENGTH) {
                Vector3d pos = Common.set(position(), movement.axis, movement.expectedPos);
                setPos(pos.x, pos.y, pos.z);
                return null;
            }

            double x = 0, z = 0;

            double speed = Math.min(getMovementSpeed(), absDistance);
            if (!movement.positive) {
                speed = -speed;
            }

            // movement over y axis is not supported for now
            if (Direction.Axis.Z.equals(movement.axis)) {
                z = speed;
            } else {
                x = speed;
            }

            return new Vec3(x, 0, z);
        });
    }

    private float wrapDegrees(float d) {
        return d % 360f;
    }

    private float getMovementSpeed() {
        return 1.5f * getLinearSpeed();
    }

    private float getLinearSpeed() {
        return getSpeed() / 512f;
    }

    public void setSpeed(int speed) {
        entityData.set(SPEED, Mth.clamp(Math.abs(speed), 0, 255));
    }

    public int getSpeed() {
        return entityData.get(SPEED);
    }

    public void setWaitingMovement(@Nullable AircraftMovement movement) {
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
        entityData.define(TARGET_MOVEMENT, Optional.empty());
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        if (compound.isEmpty())
            return;

        entityData.set(SPEED, compound.getInt("speed"));
        if (compound.getBoolean("hasWaitingMovement")) {
            compound = compound.getCompound("waitingMovement");
            setWaitingMovement(new AircraftMovement(Direction.Axis.values()[compound.getInt("axis")],
                    compound.getBoolean("positive"), compound.getFloat("expectedPos")));
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        compound.putInt("speed", getSpeed());
        Optional<AircraftMovement> opt = getTargetMovement();
        compound.putBoolean("hasWaitingMovement", opt.isPresent());
        opt.ifPresent(movement -> {
            CompoundTag n = new CompoundTag();
            n.putInt("axis", movement.axis.ordinal());
            n.putBoolean("positive", movement.positive);
            n.putFloat("expectedPos", movement.expectedPos);
            compound.put("waitingMovement", n);
        });
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
