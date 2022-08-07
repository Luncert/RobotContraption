package com.luncert.robotcontraption.common;

public class Signal {

    public void set() {
        synchronized (this) {
            notify();
        }
    }

    public void get() {
        synchronized (this) {
            try {
                wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
