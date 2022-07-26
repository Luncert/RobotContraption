package com.luncert.robotcontraption.content.aircraft;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;

import java.util.Optional;

public class AircraftMovement {

    Direction.Axis axis;
    boolean positive;
    float expectedPos;

    public AircraftMovement(Direction.Axis axis, boolean positive, float expectedPos) {
        this.axis = axis;
        this.positive = positive;
        this.expectedPos = expectedPos;
    }

    public static final EntityDataSerializer<Optional<AircraftMovement>> MOVEMENT_SERIALIZER = new MovementSerializer();

    static {
        EntityDataSerializers.registerSerializer(MOVEMENT_SERIALIZER);
    }

    @MethodsReturnNonnullByDefault
    private static class MovementSerializer implements EntityDataSerializer<Optional<AircraftMovement>> {

        @Override
        public void write(FriendlyByteBuf buffer, Optional<AircraftMovement> opt) {
            buffer.writeBoolean(opt.isPresent());
            opt.ifPresent(movement -> {
                buffer.writeInt(movement.axis.ordinal());
                buffer.writeBoolean(movement.positive);
                buffer.writeFloat(movement.expectedPos);
            });
        }

        @Override
        public Optional<AircraftMovement> read(FriendlyByteBuf buffer) {
            boolean isPresent = buffer.readBoolean();
            return isPresent ? Optional.of(new AircraftMovement(Direction.Axis.values()[buffer.readInt()], buffer.readBoolean(), buffer.readFloat()))
                    : Optional.empty();
        }

        @Override
        public Optional<AircraftMovement> copy(Optional<AircraftMovement> opt) {
            return opt.map(movement -> new AircraftMovement(movement.axis, movement.positive, movement.expectedPos));
        }
    }
}
