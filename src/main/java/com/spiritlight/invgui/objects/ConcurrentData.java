package com.spiritlight.invgui.objects;

import com.spiritlight.invgui.interfaces.annotations.Interrupts;

import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class ConcurrentData<T> extends ConcurrentInfo<T> {

    public ConcurrentData(Future<T> future, UUID submit_id) {
        super(future, submit_id);
    }

    @Interrupts
    public T getResult() throws ExecutionException, InterruptedException {
        return this.getFuture().get();
    }
}
