package com.luncert.robotcontraption.common;

import com.luncert.robotcontraption.content.aircraft.IAircraftComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import org.jetbrains.annotations.NotNull;

public class Capabilities {

    public static final Capability<IAircraftComponent> CAPABILITY_AIRCRAFT_COMPONENT = CapabilityManager.get(new CapabilityToken<>() {
    });

    public static boolean isAircraftComponent(@NotNull Capability<?> cap) {
        return cap == CAPABILITY_AIRCRAFT_COMPONENT;
    }

    private Capabilities() {
    }
}
