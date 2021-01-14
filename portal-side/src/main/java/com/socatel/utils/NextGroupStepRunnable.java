package com.socatel.utils;


import com.socatel.components.Methods;

public class NextGroupStepRunnable implements Runnable {

    private Integer id;
    private Methods methods;

    public NextGroupStepRunnable (Methods methods, Integer id) {
        this.methods = methods;
        this.id = id;
    }

    @Override
    public void run() {
        methods.nextStep(id);
    }

}
