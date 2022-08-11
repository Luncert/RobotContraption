package com.luncert.robotcontraption.content.geoscanner;

import com.google.common.collect.ImmutableMap;
import com.luncert.robotcontraption.common.LocalVariable;
import com.luncert.robotcontraption.compat.aircraft.AircraftComponentType;
import com.luncert.robotcontraption.compat.aircraft.BaseAircraftComponent;
import com.luncert.robotcontraption.compat.computercraft.EHarvestable;
import com.luncert.robotcontraption.content.fuelengine.FuelEngineComponent;
import com.luncert.robotcontraption.util.ScanUtils;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.MethodResult;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Arrays;

import static com.luncert.robotcontraption.compat.aircraft.AircraftComponentType.GEO_SCANNER;

public class GeoScannerComponent extends BaseAircraftComponent {
    @Override
    public AircraftComponentType getComponentType() {
        return GEO_SCANNER;
    }

    @LuaFunction(mainThread = true)
    public final MethodResult search(String harvestable) throws LuaException {
        EHarvestable target;
        try {
            target = EHarvestable.valueOf(harvestable.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new LuaException("Invalid argument, must be one of " + Arrays.toString(EHarvestable.values()));
        }

        BlockPos center = accessor.aircraft.blockPosition();

        LocalVariable<Pair<Vec3, Vec3>> ref = new LocalVariable<>();

        ScanUtils.traverseBlocks(accessor.world, center, 8, (state, pos) -> {
            if (target.test(state)) {
                ref.set(ScanUtils.calcShapeForAdjacentBlocks(accessor.world, pos));
                return false;
            }
            return true;
        });

        if (ref.isEmpty()) {
            return MethodResult.of(false);
        }

        Pair<Vec3, Vec3> locator = ref.get();
        Vec3 a = locator.getLeft();
        Vec3 b = locator.getRight();
        return MethodResult.of(
                ImmutableMap.of(
                        "x1", a.x,
                        "y1", a.y,
                        "z1", a.z,
                        "x2", b.x,
                        "y2", b.y,
                        "z2", b.z
                ));
    }
}
