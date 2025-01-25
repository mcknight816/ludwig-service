package com.bluntsoftware.ludwig.conduit.config.telegram;

import com.bluntsoftware.ludwig.conduit.config.ActivityConfigImpl;
import com.bluntsoftware.ludwig.conduit.config.ConfigTestResult;
import com.bluntsoftware.ludwig.conduit.config.telegram.domain.TelegramConfig;
import org.springframework.stereotype.Service;
import java.util.Map;

@Service
public class TelegramConfigActivity extends ActivityConfigImpl<TelegramConfig> {

    @Override
    public ConfigTestResult test(Map<String, Object> config) {
        return null;
    }

    @Override
    public ConfigTestResult testConfig(TelegramConfig config) {
        return null;
    }
}
