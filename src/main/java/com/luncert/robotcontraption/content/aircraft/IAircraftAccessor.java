package com.luncert.robotcontraption.content.aircraft;

import java.util.Optional;

public interface IAircraftAccessor {

    <T extends IAircraftComponent> Optional<T> findComponent(Class<T> type);
}
