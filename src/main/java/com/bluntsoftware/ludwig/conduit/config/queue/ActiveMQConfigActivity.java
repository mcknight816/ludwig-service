package com.bluntsoftware.ludwig.conduit.config.queue;


import com.bluntsoftware.ludwig.conduit.config.ActivityConfigImpl;
import com.bluntsoftware.ludwig.conduit.config.ConfigTestResult;
import com.bluntsoftware.ludwig.conduit.config.queue.domain.ActiveMQConfig;
import org.springframework.stereotype.Service;

@Service
public class ActiveMQConfigActivity extends ActivityConfigImpl<ActiveMQConfig> {

    @Override
    public ConfigTestResult testConfig(ActiveMQConfig config) {
        return null;
    }
}
