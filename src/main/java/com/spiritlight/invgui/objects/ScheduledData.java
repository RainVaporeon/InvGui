package com.spiritlight.invgui.objects;

import com.spiritlight.invgui.interfaces.annotations.Interrupts;

import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledFuture;

public class ScheduledData extends AbstractConcurrentInfo {
    private final ScheduledFuture<?> future;

    public ScheduledData(ScheduledFuture<?> future, UUID submit_id) {
        super(future, submit_id);
        this.future = future;
    }

    @Override
    public ScheduledFuture<?> getFuture() {
        return this.future;
    }

    @Interrupts
    public Object getResult() throws ExecutionException, InterruptedException {
        return this.getFuture().get();
    }
}
