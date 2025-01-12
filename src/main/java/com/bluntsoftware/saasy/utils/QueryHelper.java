package com.bluntsoftware.saasy.utils;


import com.bluntsoftware.saasy.domain.IdName;
import com.bluntsoftware.saasy.domain.Tenant;
import com.bluntsoftware.saasy.domain.User;
import org.springframework.data.domain.Example;

public class QueryHelper {

    public static Example<Tenant> byEmailAndAppId(String email, String appId){
        Tenant tenantProbe = Tenant.builder()
                .app(IdName.builder().id(appId).build())
                .customer(User.builder().email(email).build()).build();
       return Example.of(tenantProbe);
    }
}
