package com.spiritlight.invgui.objects;

import com.spiritlight.invgui.interfaces.annotations.Interrupts;

import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class ConcurrentInfo<T> {
    private final Future<T> future;
    private final UUID submit_id;

    public ConcurrentInfo(Future<T> future, UUID submit_id) {
        this.future = future;
        this.submit_id = submit_id;
    }

    public Future<T> getFuture() {
        return future;
    }

    public UUID getSubmitId() {
        return submit_id;
    }

    @Interrupts
    public T getResult() throws ExecutionException, InterruptedException {
        return this.getFuture().get();
    }

    public boolean isFinished() {
        return this.future.isDone();
    }

    public boolean isCanceled() {
        return this.future.isCancelled();
    }
}