package com.spiritlight.invgui.objects;

import com.spiritlight.invgui.interfaces.annotations.Interrupts;

import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledFuture;

public class ScheduledData<T> extends ConcurrentInfo<T> {
    private final ScheduledFuture<T> future;

    public ScheduledData(ScheduledFuture<T> future, UUID submit_id) {
        super(future, submit_id);
        this.future = future;
    }

    @Override
    public ScheduledFuture<T> getFuture() {
        return this.future;
    }

    @Interrupts
    public T getResult() throws ExecutionException, InterruptedException {
        return this.getFuture().get();
    }
}
