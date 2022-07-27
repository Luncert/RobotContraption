package com.luncert.robotcontraption.content.aircraft;

import com.luncert.robotcontraption.Reference;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class AircraftEntityRenderer extends EntityRenderer<AircraftEntity> {

    // private final Model model;

    public AircraftEntityRenderer(EntityRendererProvider.Context p_174008_) {
        super(p_174008_);
        // model = new AircraftEntityModel(p_174008_.bakeLayer());
    }

    @Override
    public ResourceLocation getTextureLocation(AircraftEntity entity) {
        // return new ResourceLocation(Reference.MOD_ID, "textures/entity/air_craft.png");
        return null;
    }

    @Override
    public boolean shouldRender(AircraftEntity entity, Frustum p_114492_, double cameraX, double cameraY, double cameraZ) {
        return false;
    }
}
