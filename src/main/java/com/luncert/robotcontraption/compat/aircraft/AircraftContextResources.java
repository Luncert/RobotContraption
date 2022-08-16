package com.luncert.robotcontraption.compat.aircraft;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class AircraftContextResources {

    private final Map<String, Object> resourceMap = new HashMap<>();

    /**
     * Resource will be refreshed before each tick.
     */
    public <T> void updateResource(String name, T newValue) {
        resourceMap.put(name, newValue);
    }

    /**
     * Resource will be refreshed before each tick.
     */
    @SuppressWarnings("unchecked")
    public <T> void updateResource(String name, Function<T, T> updater) {
        resourceMap.compute(name, (k, v) -> updater.apply((T) v));
    }

    @SuppressWarnings("unchecked")
    public <T> void updateResource(String name, T defaultValue, Function<T, T> updater) {
        resourceMap.compute(name, (k, v) -> {
            if (v == null) {
                v = defaultValue;
            }
            return updater.apply((T) v);
        });
    }

    @SuppressWarnings("unchecked")
    public <T> T getResource(String name) {
        return (T) resourceMap.get(name);
    }

    @SuppressWarnings("unchecked")
    public <T> T getResource(String name, T defaultValue) {
        Object v = resourceMap.get(name);
        return v == null ? defaultValue : (T) v;
    }

    public void clear() {
        resourceMap.clear();
    }
}
