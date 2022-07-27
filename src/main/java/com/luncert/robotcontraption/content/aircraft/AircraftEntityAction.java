package com.luncert.robotcontraption.content.aircraft;

public class AircraftEntityAction {

    public final int executionId;
    public final String callbackEvent;

    public AircraftEntityAction(int executionId, String callbackEvent) {
        this.executionId = executionId;
        this.callbackEvent = callbackEvent;
    }
}
