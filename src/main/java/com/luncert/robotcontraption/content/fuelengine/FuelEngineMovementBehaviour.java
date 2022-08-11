package com.luncert.robotcontraption.content.fuelengine;

import com.jozufozu.flywheel.api.MaterialManager;
import com.jozufozu.flywheel.core.virtual.VirtualRenderWorld;
import com.simibubi.create.content.contraptions.components.structureMovement.MovementBehaviour;
import com.simibubi.create.content.contraptions.components.structureMovement.MovementContext;
import com.simibubi.create.content.contraptions.components.structureMovement.render.ActorInstance;
import com.simibubi.create.content.contraptions.components.structureMovement.render.ContraptionMatrices;
import com.simibubi.create.content.contraptions.components.structureMovement.render.ContraptionRenderDispatcher;
import net.minecraft.client.renderer.MultiBufferSource;

import javax.annotation.Nullable;

public class FuelEngineMovementBehaviour implements MovementBehaviour {


    @Override
    public void renderInContraption(MovementContext context, VirtualRenderWorld renderWorld,
                                    ContraptionMatrices matrices, MultiBufferSource buffers) {
        if (!ContraptionRenderDispatcher.canInstance())
            FuelEngineRenderer.renderInContraption(context, renderWorld, matrices, buffers);
    }

    @Nullable
    @Override
    public ActorInstance createInstance(MaterialManager materialManager,
                                        VirtualRenderWorld simulationWorld,
                                        MovementContext context) {
        return new FuelEngineActorInstance(materialManager, simulationWorld, context);
    }

    @Override
    public boolean hasSpecialInstancedRendering() {
        return true;
    }
}
