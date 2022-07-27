package com.luncert.robotcontraption.compat.computercraft;

import dan200.computercraft.api.lua.ILuaCallback;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.MethodResult;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class AircraftApiCallback implements ILuaCallback {

    private final int command;
    private final MethodResult callbackHook;

    public static MethodResult hook(int commandID, String respEventName) {
        return new AircraftApiCallback(commandID, respEventName).callbackHook;
    }

    private AircraftApiCallback(int command, String respEventName) {
        this.command = command;
        callbackHook = MethodResult.pullEvent(respEventName, this);
    }

    @NotNull
    @Override
    public MethodResult resume(Object[] response) throws LuaException {
        if (response.length >= 3 && response[1] instanceof Number && response[2] instanceof Boolean) {
            return ((Number) response[1]).intValue() != this.command ?
                    callbackHook : MethodResult.of(Arrays.copyOfRange(response, 2, response.length));
        } else {
            return callbackHook;
        }
    }
}
