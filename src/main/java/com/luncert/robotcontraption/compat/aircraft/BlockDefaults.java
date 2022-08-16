package com.luncert.robotcontraption.compat.aircraft;

import com.simibubi.create.repack.registrate.builders.BlockBuilder;
import com.simibubi.create.repack.registrate.util.nullness.NonNullUnaryOperator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

import java.util.HashMap;
import java.util.Map;

public class BlockDefaults {

    public static final Map<ResourceLocation, Double> DEFAULT_TRUSTS = new HashMap<>();

    public static void setDefaultThrust(ResourceLocation blockId, double thrust) {
        DEFAULT_TRUSTS.put(blockId, thrust);
    }

    public static double getDefaultThrust(ResourceLocation blockId) {
        Double t = DEFAULT_TRUSTS.get(blockId);
        return t == null ? 0 : t;
    }

    public static <B extends Block, P> NonNullUnaryOperator<BlockBuilder<B, P>> setThrust(double impact) {
        return (b) -> {
            setDefaultThrust(new ResourceLocation(b.getOwner().getModid(), b.getName()), impact);
            return b;
        };
    }
}
