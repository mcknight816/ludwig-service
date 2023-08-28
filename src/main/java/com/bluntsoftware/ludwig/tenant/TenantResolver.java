package com.bluntsoftware.ludwig.tenant;

public class TenantResolver {
    private static final ThreadLocal<String> currentTenant = new InheritableThreadLocal<>();

    public static String resolve() {
        return currentTenant.get() != null ? currentTenant.get(): "master";
    }

    public static void setCurrentTenant(String tenant) {
        currentTenant.set(tenant);
    }

    public static void clear() {
        currentTenant.set(null);
    }

}
