package com.luncert.robotcontraption.compat.create;

import com.simibubi.create.content.contraptions.components.structureMovement.render.ContraptionEntityRenderer;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class AircraftContraptionEntityRenderer extends ContraptionEntityRenderer<AircraftContraptionEntity> {

    public AircraftContraptionEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public boolean shouldRender(AircraftContraptionEntity entity, Frustum p_225626_2_, double p_225626_3_,
                                double p_225626_5_, double p_225626_7_) {
        if (!super.shouldRender(entity, p_225626_2_, p_225626_3_, p_225626_5_, p_225626_7_))
            return false;
        if (entity.getVehicle() == null)
            return false;
        return true;
    }
}
