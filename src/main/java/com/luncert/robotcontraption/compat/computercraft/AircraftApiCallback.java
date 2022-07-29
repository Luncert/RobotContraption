package com.luncert.robotcontraption.compat.computercraft;

import dan200.computercraft.api.lua.ILuaCallback;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.MethodResult;
import org.jetbrains.annotations.NotNull;

public class AircraftApiCallback implements ILuaCallback {

    private final int executionId;
    private final MethodResult callbackHook;

    public static MethodResult hook(int executionId, String respEventName) {
        return new AircraftApiCallback(executionId, respEventName).callbackHook;
    }

    private AircraftApiCallback(int executionId, String respEventName) {
        this.executionId = executionId;
        callbackHook = MethodResult.pullEvent(respEventName, this);
    }

    @NotNull
    @Override
    public MethodResult resume(Object[] response) throws LuaException {
        // 0 is event name
        if (response.length == 3 && response[1] instanceof Number
                && response[2] != null
                && response[2].getClass().isArray()) {
            int responseId = ((Number) response[1]).intValue();
            if (responseId == this.executionId) {
                Object[] executionResult = (Object[]) response[2];
                return MethodResult.of(executionResult);
            }
        }

        return callbackHook;
    }
}
