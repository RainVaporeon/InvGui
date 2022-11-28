package com.spiritlight.invgui.exceptions;

/**
 * This exception should only be thrown when there is
 * an issue with the processing. Causes of this exception is often one of these: <br>
 * - Asserted condition was not met <br>
 * - Teapot error <br>
 * <br>
 * To prevent this exception from being thrown, one should check whether the method they are executing
 * (that throws this exception) have any conditions.
 */
public class ProcessException extends Exception {
    public ProcessException(String s) { super(s); }
}
