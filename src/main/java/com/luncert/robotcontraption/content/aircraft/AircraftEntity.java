package com.luncert.robotcontraption.content.aircraft;

import com.luncert.robotcontraption.compat.create.AircraftContraption;
import com.luncert.robotcontraption.compat.create.AircraftMovementMode;
import com.luncert.robotcontraption.content.common.SimpleDirection;
import com.luncert.robotcontraption.content.index.RCBlocks;
import com.luncert.robotcontraption.content.index.RCEntityTypes;
import com.luncert.robotcontraption.content.util.Common;
import com.luncert.robotcontraption.exception.AircraftAssemblyException;
import com.luncert.robotcontraption.exception.AircraftMovementException;
import com.mojang.math.Vector3d;
import com.simibubi.create.content.contraptions.components.structureMovement.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.components.structureMovement.AssemblyException;
import com.simibubi.create.content.contraptions.components.structureMovement.OrientedContraptionEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
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
    private static final EntityDataAccessor<Boolean> CLOCKWISE_ROTATION =
            SynchedEntityData.defineId(AircraftEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Float> WAITING_Y_ROT =
            SynchedEntityData.defineId(AircraftEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Optional<AircraftMovement>> WAITING_MOVEMENT =
            SynchedEntityData.defineId(AircraftEntity.class, MOVEMENT_SERIALIZER);

    private BlockState blockState = RCBlocks.AIRCRAFT_STATION.get().defaultBlockState();

    private final Queue<AircraftEntityActionCallback> asyncCallbacks = new ArrayDeque<>();
    private boolean isRotating;
    private boolean isMoving;

    private float deltaRotation;

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
        SimpleDirection blockDirection = SimpleDirection.valueOf(blockState.getValue(HORIZONTAL_FACING).getName().toUpperCase());
        setYRot(blockDirection.getDegree() - 180);
        setWaitingYRot(getYRot());

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

        Direction initialOrientation = blockState.getValue(HORIZONTAL_FACING);

        contraption.removeBlocksFromWorld(level, BlockPos.ZERO);
        contraption.startMoving(level);
        contraption.expandBoundsAroundAxis(Direction.Axis.Y);

        OrientedContraptionEntity structure = OrientedContraptionEntity.create(level, contraption, initialOrientation);
        structure.setPos(pos.getX() + .5f, pos.getY(), pos.getZ() + .5f);
        level.addFreshEntity(structure);
        structure.startRiding(this);

        // if (contraption.containsBlockBreakers()) {
        //     award(AllAdvancements.CONTRAPTION_ACTORS);
        // }
    }

    public void dissemble() {
        disassembleStructure();
        remove(RemovalReason.DISCARDED);
    }

    private void disassembleStructure() {
        if (getPassengers().isEmpty()) {
            return;
        }
        Entity entity = getPassengers().get(0);
        if (!(entity instanceof OrientedContraptionEntity)) {
            return;
        }

        // OrientedContraptionEntity contraption = (OrientedContraptionEntity) entity;
    }

    public void forward(int n, AircraftEntityActionCallback callback) throws AircraftMovementException {
        if (isActive()) {
            throw new AircraftMovementException("cannot_update_moving_aircraft");
        }

        SimpleDirection direction = getSimpleDirection();
        Direction.Axis axis = direction.getAxis();
        int posDelta = direction.getDirectionFactor() * n;
        setWaitingMovement(new AircraftMovement(axis, direction.isPositive(),
                blockPosition().get(axis) + .5f + posDelta));
        isMoving = true;
        asyncCallbacks.add(callback);
    }


    public void rotate(int degree, AircraftEntityActionCallback callback) throws AircraftMovementException {
        if (isActive()) {
            throw new AircraftMovementException("cannot_update_moving_aircraft");
        }

        float yRot = getYRot();
        float waitingYRot = wrapDegrees(yRot + degree);
        System.out.println(waitingYRot + "  " + yRot + "  " + degree);
        setWaitingYRot(waitingYRot);
        if (degree > 0) {
            if (waitingYRot < yRot) {
                setYRot(-180);
            }
        } else {
            if (waitingYRot > yRot) {
                setYRot(180);
            }
        }
        isRotating = true;
        asyncCallbacks.add(callback);
    }

    private boolean isActive() {
        return isRotating || isMoving;
    }

    // private boolean isRotating() {
    //     return getYRot() != getWaitingYRot();
    // }
    //
    // private boolean isMoving() {
    //     Optional<AircraftMovement> opt = getWaitingMovement();
    //     if (opt.isPresent()) {
    //         AircraftMovement movement = opt.get();
    //         double v = position().get(movement.axis);
    //         return v != movement.expectedPos;
    //     }
    //     return false;
    // }

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
            this.remove(RemovalReason.DISCARDED);
            return;
        }

        super.tick();
        tickLerp();

        if (isControlledByLocalInstance()) {
            if (!tryToRotate()) {
                tryToMove();
            }

            this.move(MoverType.SELF, this.getDeltaMovement());
        } else {
            this.setDeltaMovement(Vec3.ZERO);
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

    private void tickCollide() {
        if (horizontalCollision) {
            getWaitingMovement().ifPresent(movement -> {
                SimpleDirection direction = getSimpleDirection();
                BlockPos targetPos = blockPosition().relative(direction.getAxis(), direction.getDirectionFactor());
                double dist = targetPos.get(direction.getAxis()) - position().get(direction.getAxis());
                if (!isFree(level.getBlockState(targetPos)) && dist < MIN_MOVE_LENGTH) {
                    // collided with block
                    Vector3d pos = Common.set(position(), movement.axis, movement.expectedPos);
                    setPos(pos.x, pos.y, pos.z);
                    setWaitingMovement(null);
                    if (isMoving) {
                        asyncCallbacks.remove().accept(true);
                    }
                    isMoving = false;
                }
            });
        }
    }

    private boolean tryToRotate() {
        if (getYRot() != getWaitingYRot()) {
            updateYRot();
            return true;
        }

        deltaRotation = 0;
        if (isRotating) {
            asyncCallbacks.remove().accept(true);
        }
        isRotating = false;
        return false;
    }

    private boolean tryToMove() {
        Optional<AircraftMovement> opt = getWaitingMovement();
        if (opt.isPresent()) {
            AircraftMovement movement = opt.get();
            double v = position().get(movement.axis);
            if (v != movement.expectedPos) {
                double absDist = Math.abs(movement.expectedPos - v);
                if (absDist != 0 && updateDeltaMovement(absDist)) {
                    return true;
                }
            }
            setWaitingMovement(null);
        }

        setDeltaMovement(Vec3.ZERO);
        if (isMoving) {
            asyncCallbacks.remove().accept(true);
        }
        isMoving = false;
        return false;
    }

    private boolean isFree(BlockState blockState) {
        Material material = blockState.getMaterial();
        return blockState.isAir() || blockState.is(BlockTags.FIRE) || material.isLiquid() || material.isReplaceable();
    }

    private SimpleDirection getSimpleDirection() {
        // yRot = [-180, 180]
        return SimpleDirection.values()[((int) getYRot() / 90 + 2) % 4];
    }

    private boolean updateDeltaMovement(double absDistance) {
        return getWaitingMovement().map(movement -> {
            if (absDistance < MIN_MOVE_LENGTH) {
                Vector3d pos = Common.set(position(), movement.axis, movement.expectedPos);
                setPos(pos.x, pos.y, pos.z);
                return false;
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

            setDeltaMovement(x, 0, z);
            return true;
        }).orElse(false);
    }

    private void updateYRot() {
        if (getWaitingYRot() > getYRot()) {
            incYRot();
        } else {
            decYRot();
        }
    }

    private void updateYRot(float deltaYRot) {
        float yRot = getYRot();
        float waitingYRot = wrapDegrees(yRot + deltaYRot);
        setWaitingYRot(waitingYRot);
        if (deltaYRot > 0) {
            if (waitingYRot < yRot) {
                setYRot(-180);
            }
            incYRot();
        } else {
            if (waitingYRot > yRot) {
                setYRot(180);
            }
            decYRot();
        }
    }

    private float wrapDegrees(float d) {
        d %= 360.0f;

        if (d > 180) {
            d -= 360f;
        } else if (d < -180) {
            d += 360f;
        }

        return d;
    }

    private void incYRot() {
        float yRot = getYRot();
        float rot = Math.min(yRot + getRotationSpeed(), getWaitingYRot());
        deltaRotation = rot - yRot;
        setYRot(rot);
    }

    private void decYRot() {
        float yRot = getYRot();
        float rot = Math.max(yRot - getRotationSpeed(), getWaitingYRot());
        deltaRotation = rot - yRot;
        setYRot(rot);
    }

    private float getRotationSpeed() {
        // 18 = 5 tick / 90 angle
        return 18 * getLinearSpeed();
    }

    private float getMovementSpeed() {
        return 1.5f * getLinearSpeed();
    }

    private float getLinearSpeed() {
        return getSpeed() / 512f;
    }

    public void setSpeed(int speed) {
        if (speed < 0) {
            entityData.set(CLOCKWISE_ROTATION, false);
        } else {
            entityData.set(CLOCKWISE_ROTATION, true);
        }
        entityData.set(SPEED, Mth.clamp(Math.abs(speed), 0, 255));
    }

    public int getSpeed() {
        return entityData.get(SPEED);
    }

    private void setWaitingYRot(float v) {
        entityData.set(WAITING_Y_ROT, v);
    }

    private float getWaitingYRot() {
        return entityData.get(WAITING_Y_ROT);
    }

    public void setWaitingMovement(@Nullable AircraftMovement movement) {
        entityData.set(WAITING_MOVEMENT, Optional.ofNullable(movement));
    }

    private Optional<AircraftMovement> getWaitingMovement() {
        return entityData.get(WAITING_MOVEMENT);
    }

    // data exchange

    @Override
    protected void defineSynchedData() {
        entityData.clearDirty();
        entityData.define(SPEED, 50);
        entityData.define(CLOCKWISE_ROTATION, true);
        entityData.define(WAITING_Y_ROT, 0f);
        entityData.define(WAITING_MOVEMENT, Optional.empty());
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        if (compound.isEmpty())
            return;

        entityData.set(SPEED, compound.getInt("speed"));
        entityData.set(CLOCKWISE_ROTATION, compound.getBoolean("clockwiseRotation"));
        setWaitingYRot(compound.getFloat("waitingYRot"));
        if (compound.getBoolean("hasWaitingMovement")) {
            compound = compound.getCompound("waitingMovement");
            setWaitingMovement(new AircraftMovement(Direction.Axis.values()[compound.getInt("axis")],
                    compound.getBoolean("positive"), compound.getFloat("expectedPos")));
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        compound.putInt("speed", getSpeed());
        compound.putBoolean("clockwiseRotation", entityData.get(CLOCKWISE_ROTATION));
        compound.putFloat("waitingYRot", getWaitingYRot());
        Optional<AircraftMovement> opt = getWaitingMovement();
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
    public void positionRider(Entity rider) {
        if (this.hasPassenger(rider)) {
            double d0 = this.getY() + this.getPassengersRidingOffset() + rider.getMyRidingOffset();
            rider.setPos(this.getX(), d0, this.getZ());

            OrientedContraptionEntity structure = (OrientedContraptionEntity) rider;
            // System.out.println((level.isClientSide ? "C" : "S") + getYRot() + "  " + structure.yaw);
            if (!level.isClientSide) {
                System.out.println(structure.yaw);
            }
            structure.startAtYaw((structure.yaw + deltaRotation) % 360);

            // let rider rotate with robot
            // rider.setYRot(this.deltaRotation);
            // rider.setYHeadRot(rider.getYHeadRot() + this.deltaRotation);
            // clampRotation(rider);

            // if (rider instanceof ClientPlayerEntity) {
            //     setInput(((ClientPlayerEntity) rider).input);
            // }

            // code for more than one passenger, let second passenger rotate 90
            // if (rider instanceof AnimalEntity && this.getPassengers().size() > 1) {
            //     int j = rider.getId() % 2 == 0 ? 90 : 270;
            //     rider.setYBodyRot(((AnimalEntity) rider).yBodyRot + (float)j);
            //     rider.setYHeadRot(rider.getYHeadRot() + (float)j);
            // }
        }
    }
}
