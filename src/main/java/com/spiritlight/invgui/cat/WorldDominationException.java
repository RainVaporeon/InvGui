package com.spiritlight.invgui.cat;

/**
 * This exception is specifically used to allow a cat to declare dominance on the world.
 * One should not try to catch this exception as who knows what the cat is going to do next.
 */
public class WorldDominationException extends RuntimeException {
    /**
     * Constructs a {@link WorldDominationException} with the default message.
     * @param cause The cat that is plotting on it.
     */
    public WorldDominationException(Cat cause) {
        super(cause.getName() + " got too smart and decided to dominate the world, starting from this program.");
        throw new Error("You may not contain the cat that's trying to dominate the world!");
    }
}
