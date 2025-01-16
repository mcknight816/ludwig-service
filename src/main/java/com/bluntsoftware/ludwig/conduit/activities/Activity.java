package com.bluntsoftware.ludwig.conduit.activities;
import com.bluntsoftware.ludwig.conduit.impl.ActivityProperties;

import java.util.Map;
public interface Activity {
    Map<String,Object> getInput();
    Map<String,Object> getOutput();
    String getName();
    String getActivityClass();
    String getCategory();
    String getIcon();
    Boolean fireAndForget();

    ActivityProperties getActivityProperties();
}
