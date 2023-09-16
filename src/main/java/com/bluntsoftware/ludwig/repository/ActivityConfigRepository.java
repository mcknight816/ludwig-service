package com.bluntsoftware.ludwig.repository;

import com.bluntsoftware.ludwig.conduit.ActivityConfig;
import com.bluntsoftware.ludwig.conduit.impl.ActivityConfigImpl;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class ActivityConfigRepository {
    public List<ActivityConfig> findAll(){
        return new ArrayList<>(ActivityConfigImpl.list().values());
    }

    public ActivityConfig getByKlass(String klass){
        return ActivityConfigImpl.list().get(klass);
    }
}
