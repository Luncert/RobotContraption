package com.luncert.robotcontraption.common;

public class LocalVariable<T> {

    private T obj;

    public void set(T obj) {
        this.obj = obj;
    }

    public T get() {
        return obj;
    }

    public boolean isPresent() {
        return obj != null;
    }

    public boolean isEmpty() {
        return obj == null;
    }
}
