package com.bluntsoftware.ludwig.repository;

import com.bluntsoftware.ludwig.conduit.Activity;
import com.bluntsoftware.ludwig.conduit.impl.ActivityImpl;
import org.springframework.stereotype.Repository;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ActivityRepository {
    public List<Activity> findAll(){
        return new ArrayList<>(ActivityImpl.list().values());
    }

    public Activity getByKlass(String klass){
        return ActivityImpl.list().get(klass);
    }

}
