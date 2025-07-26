package com.aitherapist.aitherapist.telegrambot.utils;

import com.aitherapist.aitherapist.domain.enums.Roles;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * CommandAccess - custom annotation for security.
 * Access to switch current status and use Command that user no access-
 * @Target(ElementType.TYPE) - only class can use this annotation
 * @Retention(RetentionPolicy.RUNTIME)  - meta-annotation which sets the which determines how long the annotation will be saved in the program:
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface CommandAccess {
    /**
     * List of allow Roles(Enum)
     * @return return set allowed roles list.
     */
    Roles[] allowedRoles() default {};
    boolean requiresRegistration() default true;
}
