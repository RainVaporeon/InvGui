package com.spiritlight.invgui.interfaces.annotations;

import java.lang.annotation.*;

/**
 * Methods annotated with this method indicates that this operation is <b>thread-blocking</b>.
 */
@Documented
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.METHOD)
public @interface Interrupts {
}
