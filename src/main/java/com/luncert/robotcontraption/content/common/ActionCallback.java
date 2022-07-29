package com.luncert.robotcontraption.content.common;

@FunctionalInterface
public interface ActionCallback {

    void accept(boolean done, Object... data);
}
