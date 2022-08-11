package com.luncert.robotcontraption.foundation.mixin;

import com.luncert.robotcontraption.compat.create.AircraftContraption;
import com.luncert.robotcontraption.content.aircraft.AircraftEntity;
import com.simibubi.create.content.contraptions.components.structureMovement.Contraption;
import com.simibubi.create.content.contraptions.components.structureMovement.MovementContext;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = MovementContext.class, remap = false)
public class MovementContextMixin {

    @Shadow
    public Level world;

    @Shadow
    public Contraption contraption;

    @Inject(at = @At("HEAD"), method = "getAnimationSpeed", cancellable = true)
    public void getAnimationSpeedMixin(CallbackInfoReturnable<Float> cir) {
        System.out.println("it's working !!!!!!!!!!!");
        if (!world.isClientSide && contraption instanceof AircraftContraption) {
            AircraftEntity entity = (AircraftEntity) contraption.entity.getVehicle();
            if (entity != null) {
                cir.setReturnValue(entity.getKineticSpeed() * 4f);
                cir.cancel();
            }
        }
    }
}
