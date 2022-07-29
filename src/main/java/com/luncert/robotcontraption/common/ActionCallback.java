package com.luncert.robotcontraption.common;

@FunctionalInterface
public interface ActionCallback {

    void accept(Object... data);
}
