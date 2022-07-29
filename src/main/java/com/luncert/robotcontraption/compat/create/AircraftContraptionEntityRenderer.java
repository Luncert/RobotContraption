package com.luncert.robotcontraption.compat.create;

import com.simibubi.create.content.contraptions.components.structureMovement.render.ContraptionEntityRenderer;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class AircraftContraptionEntityRenderer extends ContraptionEntityRenderer<AircraftContraptionEntity> {

    public AircraftContraptionEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public boolean shouldRender(AircraftContraptionEntity entity, Frustum clippingHelper, double cameraX, double cameraY, double cameraZ) {
        if (!super.shouldRender(entity, clippingHelper, cameraX, cameraY, cameraZ))
            return false;
        return entity.getVehicle() != null;
    }
}
