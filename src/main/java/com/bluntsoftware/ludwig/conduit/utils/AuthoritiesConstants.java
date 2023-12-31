package com.bluntsoftware.ludwig.conduit.utils;

/**
 * Constants for Spring Security authorities.
 */
public final class AuthoritiesConstants {

    private AuthoritiesConstants() {
    }

    public static final String ADMIN = "ROLE_ADMIN";

    public static final String SUPER_USER = "ROLE_SUPER_USER";

    public static final String USER = "ROLE_USER";

    public static final String ANONYMOUS = "ROLE_ANONYMOUS";

    public static final String LUDWIG_ADMIN = "ROLE_LUDWIG_ADMIN";
}
