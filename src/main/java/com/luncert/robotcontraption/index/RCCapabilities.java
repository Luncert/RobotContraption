package com.luncert.robotcontraption.index;

import com.luncert.robotcontraption.compat.aircraft.IAircraftComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import org.jetbrains.annotations.NotNull;

public class RCCapabilities {

    public static final Capability<IAircraftComponent> CAPABILITY_AIRCRAFT_COMPONENT = CapabilityManager.get(new CapabilityToken<>() {
    });

    public static boolean isAircraftComponent(@NotNull Capability<?> cap) {
        return cap == CAPABILITY_AIRCRAFT_COMPONENT;
    }

    private RCCapabilities() {
    }
}
