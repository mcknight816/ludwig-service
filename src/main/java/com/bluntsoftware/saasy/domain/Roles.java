package com.bluntsoftware.saasy.domain;

import java.util.ArrayList;
import java.util.List;

public enum Roles {
    USER("ROLE_SASSY_USER"),
    ADMIN("ROLE_SASSY_ADMIN"),
    TENANT("ROLE_TENANT"),
    TENANT_USER("ROLE_TENANT_USER");
    public String getRoleName() {
        return roleName;
    }
    private final String roleName;
    Roles(String name) {
        this.roleName = name;
    }
    public static List<String> allRoles(){
        List<String> roles = new ArrayList<>();
        roles.add(Roles.USER.roleName);
        roles.add(Roles.ADMIN.roleName);
        roles.add(Roles.TENANT.roleName);
        roles.add(Roles.TENANT_USER.roleName);
        return roles;
    }

}
