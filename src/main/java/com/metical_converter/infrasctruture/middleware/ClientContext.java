package com.metical_converter.infrasctruture.middleware;


public class ClientContext {


    private static final ThreadLocal<String> CURRENT_TENANT = new ThreadLocal<>();
    public static void setCurrentTenant(String tenant) {
        CURRENT_TENANT.set(tenant);
    }

    public static String getCurrentTenant() {
        return CURRENT_TENANT.get();
    }

    public static void clear() {
        CURRENT_TENANT.remove();
    }
}
