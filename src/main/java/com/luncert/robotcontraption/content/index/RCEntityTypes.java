package com.luncert.robotcontraption.content.index;

import com.luncert.robotcontraption.RobotContraption;
import com.luncert.robotcontraption.compat.create.AircraftContraptionEntity;
import com.luncert.robotcontraption.compat.create.AircraftContraptionEntityRenderer;
import com.luncert.robotcontraption.content.aircraft.AircraftEntity;
import com.luncert.robotcontraption.content.aircraft.AircraftEntityRenderer;
import com.simibubi.create.content.contraptions.components.structureMovement.AbstractContraptionEntity;
import com.simibubi.create.foundation.data.CreateEntityBuilder;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.utility.Lang;
import com.simibubi.create.repack.registrate.util.entry.EntityEntry;
import com.simibubi.create.repack.registrate.util.nullness.NonNullConsumer;
import com.simibubi.create.repack.registrate.util.nullness.NonNullFunction;
import com.simibubi.create.repack.registrate.util.nullness.NonNullSupplier;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

public class RCEntityTypes {

    private static final CreateRegistrate REGISTRATE = RobotContraption.registrate();

    public static final EntityEntry<AircraftEntity> AIRCRAFT =
            register("robot", AircraftEntity::new, () -> AircraftEntityRenderer::new, MobCategory.MISC,
                    1, Integer.MAX_VALUE, false, true, AircraftEntity::build).register();

    public static final EntityEntry<AircraftContraptionEntity> AIRCRAFT_CONTRAPTION = contraption("aircraft_contraption",
            AircraftContraptionEntity::new, () -> AircraftContraptionEntityRenderer::new, 5, 3, true).register();

    private static <T extends Entity> CreateEntityBuilder<T, ?> contraption(String name,
                                                                            EntityType.EntityFactory<T> factory,
                                                                            NonNullSupplier<NonNullFunction<EntityRendererProvider.Context, EntityRenderer<? super T>>> renderer,
                                                                            int range, int updateFrequency, boolean sendVelocity) {
        return register(name, factory, renderer, MobCategory.MISC, range, updateFrequency, sendVelocity, true,
                AbstractContraptionEntity::build);
    }

    private static <T extends Entity> CreateEntityBuilder<T, ?> register(
            String name, EntityType.EntityFactory<T> factory,
            NonNullSupplier<NonNullFunction<EntityRendererProvider.Context, EntityRenderer<? super T>>> renderer,
            MobCategory group, int range, int updateFrequency, boolean sendVelocity, boolean immuneToFire,
            NonNullConsumer<EntityType.Builder<T>> propertyBuilder) {
        String id = Lang.asId(name);
        return (CreateEntityBuilder<T, ?>) REGISTRATE
                .entity(id, factory, group)
                .properties(b -> b.setTrackingRange(range)
                        .setUpdateInterval(updateFrequency)
                        .setShouldReceiveVelocityUpdates(sendVelocity))
                .properties(propertyBuilder)
                .properties(b -> {
                    if (immuneToFire)
                        b.fireImmune();
                })
                .renderer(renderer);
    }

    public static void register() {}
}
