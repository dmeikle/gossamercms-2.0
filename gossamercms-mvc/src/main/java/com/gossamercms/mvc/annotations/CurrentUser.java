package com.gossamercms.mvc.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to inject the current authenticated JwtUser into method parameters.
 *
 * Usage:
 *   @PostMapping
 *   public Object myEndpoint(@CurrentUser JwtUser user) {
 *       UUID userId = user.getUserId();
 *       // ...
 *   }
 *
 * Can be used in any Spring controller or handler method.
 * Throws an exception if no user is authenticated.
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface CurrentUser {
}

