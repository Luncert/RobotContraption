package com.luncert.robotcontraption.content.aircraft;

import com.luncert.robotcontraption.compat.create.AircraftContraption;
import com.luncert.robotcontraption.content.common.SimpleDirection;
import com.luncert.robotcontraption.content.index.RCBlocks;
import com.luncert.robotcontraption.content.index.RCEntityTypes;
import com.luncert.robotcontraption.content.util.Common;
import com.mojang.math.Vector3d;
import com.simibubi.create.content.contraptions.components.structureMovement.AssemblyException;
import com.simibubi.create.content.contraptions.components.structureMovement.OrientedContraptionEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.Optional;

import static com.luncert.robotcontraption.content.aircraft.AircraftMovement.MOVEMENT_SERIALIZER;

public class AircraftEntity extends Entity {

    private static final double MIN_MOVE_LENGTH = 1.0E-7D;

    private static final EntityDataAccessor<Integer> SPEED =
            new EntityDataAccessor<>(AircraftEntity.class.hashCode(), EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> CLOCKWISE_ROTATION =
            new EntityDataAccessor<>(AircraftEntity.class.hashCode(), EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Float> WAITING_Y_ROT =
            new EntityDataAccessor<>(AircraftEntity.class.hashCode(), EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Optional<AircraftMovement>> WAITING_MOVEMENT =
            new EntityDataAccessor<>(AircraftEntity.class.hashCode(), MOVEMENT_SERIALIZER);

    private BlockState blockState = RCBlocks.AIRCRAFT_STATION.get().defaultBlockState();

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
        initBasicProps();
    }

    // TODO for server
    public AircraftEntity(Level world, BlockState blockState) {
        super(RCEntityTypes.AIRCRAFT.get(), world);
        this.blockState = blockState;
        initBasicProps();
    }

    private void initBasicProps() {
        // this.blocksBuilding = true; // not allow building at entity's position
        this.setInvulnerable(true); // cannot be hurt
    }

    public void assembleStructure(BlockPos pos) throws AssemblyException {
        AircraftContraption contraption = new AircraftContraption();
        if (!contraption.assemble(level, pos)) {
            return;
        }

        Direction initialOrientation = blockState.getValue(DirectionalBlock.FACING);

        contraption.removeBlocksFromWorld(level, BlockPos.ZERO);
        contraption.startMoving(level);
        contraption.expandBoundsAroundAxis(Direction.Axis.Y);

        OrientedContraptionEntity structure = OrientedContraptionEntity.create(level, contraption, initialOrientation);
        structure.setPos(pos.getX() + .5f, pos.getY(), pos.getZ() + .5f);
        level.addFreshEntity(structure);
        structure.startRiding(this);
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
        super.tick();
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
        return null;
    }
}
