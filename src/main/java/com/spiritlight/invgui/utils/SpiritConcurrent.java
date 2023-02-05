package com.spiritlight.invgui.utils;

import com.spiritlight.invgui.objects.ConcurrentData;
import com.spiritlight.invgui.objects.ScheduledData;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.*;

// Now that I look at it, CompletableFuture actually does better
public class SpiritConcurrent {
    private static final ExecutorService executors = Executors.newFixedThreadPool(16);
    private static final ScheduledExecutorService queueThread = Executors.newScheduledThreadPool(8);
    private static final ScheduledExecutorService cleanUpThread = Executors.newSingleThreadScheduledExecutor();
    private static final Map<UUID, Future<?>> executionID = new ConcurrentHashMap<>();

    static {
        cleanUpThread.scheduleAtFixedRate(SpiritConcurrent::cleanUp, 60, 90, TimeUnit.SECONDS);
    }

    private SpiritConcurrent() {}

    /**
     * Submits a {@link Runnable} for execution.
     * @param exec The runnable to submit
     * @return A {@link ConcurrentData} containing the returned {@link Future} and a UUID linked to it for operations.
     */
    public static ConcurrentData<?> submitAsync(Runnable exec) {
        Future<?> future = executors.submit(exec);
        UUID execUID = generateNewUUID();
        executionID.put(execUID, future);
        return new ConcurrentData<>(future, execUID);
    }

    public static ScheduledData<?> queueAsync(Runnable exec, long mills) {
        UUID execUID = generateNewUUID();
        ScheduledFuture<?> future = queueThread.schedule(exec, mills, TimeUnit.MILLISECONDS);
        return new ScheduledData<>(future, execUID);
    }

    private static UUID generateNewUUID() {
        return UUID.randomUUID();
    }

    /**
     * Terminates a task that might still be in progress and removes it from the execution list.
     * @param executionUID The UUID that was obtained from executing {@link SpiritConcurrent#submitAsync(Runnable)}
     * @return {@code true} if the task is cancelled, otherwise false (Usually due to completion)
     */
    public static boolean terminate(UUID executionUID) {
        executionID.entrySet().removeIf(data -> data.getKey().equals(executionUID));
        return executionID.get(executionUID).cancel(true);
    }

    /**
     * Terminates a task that might still be in progress and removes it from the execution list.
     * @param executionUID The UUID that was obtained from executing {@link SpiritConcurrent#submitAsync(Runnable)}
     * @param interrupt Decides whether the thread can be blocked in an attempt to terminate this execution.
     * @return {@code true} if the task is cancelled, otherwise false (Usually due to completion)
     */
    public static boolean terminate(UUID executionUID, boolean interrupt) {
        executionID.entrySet().removeIf(data -> data.getKey().equals(executionUID));
        return executionID.get(executionUID).cancel(interrupt);
    }

    private static void cleanUp() {
        executionID.entrySet().removeIf(data -> data.getValue().isDone());
    }

}
