package com.spiritlight.invgui.objects;

import com.spiritlight.invgui.interfaces.annotations.Interrupts;

import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public abstract class AbstractConcurrentInfo {
    private final Future<?> future;
    private final UUID submit_id;

    public AbstractConcurrentInfo(Future<?> future, UUID submit_id) {
        this.future = future;
        this.submit_id = submit_id;
    }

    public Future<?> getFuture() {
        return future;
    }

    public UUID getSubmitId() {
        return submit_id;
    }

    @Interrupts
    public Object getResult() throws ExecutionException, InterruptedException {
        return this.getFuture().get();
    }

    public boolean isFinished() {
        return this.future.isDone();
    }

    public boolean isCanceled() {
        return this.future.isCancelled();
    }
}