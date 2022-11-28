package com.spiritlight.invgui.objects;

import com.spiritlight.invgui.interfaces.annotations.Interrupts;

import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class ConcurrentData extends AbstractConcurrentInfo {

    public ConcurrentData(Future<?> future, UUID submit_id) {
        super(future, submit_id);
    }

    @Interrupts
    public Object getResult() throws ExecutionException, InterruptedException {
        return this.getFuture().get();
    }
}
