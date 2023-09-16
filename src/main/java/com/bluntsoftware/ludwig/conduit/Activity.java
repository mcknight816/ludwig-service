package com.bluntsoftware.ludwig.conduit;
import java.util.Map;
public interface Activity {
    Map<String,Object> getInput();
    Map<String,Object> getOutput();
    String getName();
    String getActivityClass();
    String getCategory();
    String getIcon();
    Boolean fireAndForget();
}
