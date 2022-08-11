package com.luncert.robotcontraption.content.jetengine;

import com.luncert.robotcontraption.compat.aircraft.AircraftComponentType;
import com.luncert.robotcontraption.compat.aircraft.BaseAircraftComponent;
import com.luncert.robotcontraption.compat.aircraft.BlockDefaults;
import com.luncert.robotcontraption.content.geoscanner.GeoScannerComponent;
import com.simibubi.create.foundation.block.BlockStressDefaults;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import static com.luncert.robotcontraption.compat.aircraft.AircraftComponentType.JET_ENGINE;

public class JetEngineComponent extends BaseAircraftComponent {


    private boolean active;
    private int powerLevel = 1;

    @Override
    public AircraftComponentType getComponentType() {
        return JET_ENGINE;
    }

    @Override
    public void tickComponent() {
        if (powerLevel > 0) {
            double capacity = accessor.resources.getResource("capacity", 0);
            BlockState state = accessor.getComponentBlockState(name);
            Block block = state.getBlock();
            Double impact = BlockStressDefaults.DEFAULT_IMPACTS.get(block.getRegistryName());
            double requiredCapacity = impact * powerLevel * powerLevel;

            if (requiredCapacity <= capacity) {
                active = true;
                // consume capacity
                accessor.resources.updateResource("capacity", capacity - requiredCapacity);
                // generate thrust
                double thrust = powerLevel * BlockDefaults.DEFAULT_TRUSTS.get(block.getRegistryName());
                accessor.resources.updateResource("thrust", 0d, old -> old + thrust);
                return;
            }
        }

        active = false;
    }

    public boolean isActive() {
        return active;
    }

    @LuaFunction
    public void setPowerLevel(int powerLevel) throws LuaException {
        if (powerLevel < 0 || powerLevel > 4) {
            throw new LuaException("invalid power level");
        }
        this.powerLevel = powerLevel;
    }
}
