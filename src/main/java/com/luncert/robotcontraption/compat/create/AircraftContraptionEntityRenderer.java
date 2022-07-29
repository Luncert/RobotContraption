// package com.luncert.robotcontraption.compat.create;
//
// import com.mojang.blaze3d.vertex.PoseStack;
// import com.simibubi.create.content.contraptions.components.structureMovement.Contraption;
// import com.simibubi.create.content.contraptions.components.structureMovement.render.ContraptionRenderDispatcher;
// import net.minecraft.client.renderer.MultiBufferSource;
// import net.minecraft.client.renderer.culling.Frustum;
// import net.minecraft.client.renderer.entity.EntityRenderer;
// import net.minecraft.client.renderer.entity.EntityRendererProvider;
// import net.minecraft.resources.ResourceLocation;
//
// public class AircraftContraptionEntityRenderer extends EntityRenderer<AircraftContraptionEntity> {
//
//     public AircraftContraptionEntityRenderer(EntityRendererProvider.Context context) {
//         super(context);
//     }
//
//     @Override
//     public ResourceLocation getTextureLocation(AircraftContraptionEntity p_114482_) {
//         return null;
//     }
//
//     @Override
//     public boolean shouldRender(AircraftContraptionEntity entity, Frustum clippingHelper, double cameraX, double cameraY, double cameraZ) {
//         if (entity.getVehicle() == null)
//             return false;
//         if (entity.getContraption() == null) {
//             return false;
//         } else if (!entity.isAliveOrStale()) {
//             return false;
//         } else {
//             return entity.isReadyForRender() && super.shouldRender(entity, clippingHelper, cameraX, cameraY, cameraZ);
//         }
//     }
//
//     @Override
//     public void render(AircraftContraptionEntity entity, float yaw, float partialTicks, PoseStack ms, MultiBufferSource buffers, int overlay) {
//         super.render(entity, yaw, partialTicks, ms, buffers, overlay);
//         Contraption contraption = entity.getContraption();
//         System.out.println(yaw + " " + entity.getYawOffset() + " " + entity.yaw + "_" + entity.targetYaw + " " + entity.yRotO + "_" + entity.getYRot() + " " + (contraption == null));
//         if (contraption != null) {
//             ContraptionRenderDispatcher.renderFromEntity(entity, contraption, buffers);
//         }
//     }
// }
