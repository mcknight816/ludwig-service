package com.bluntsoftware.ludwig.conduit.activities.input;

import com.bluntsoftware.ludwig.repository.FlowConfigRepository;
import org.springframework.stereotype.Service;
/**
 * Created by Alex Mcknight on 1/12/2017.
 */
@Service
public class ColumnsActivity extends InputActivity {
    public ColumnsActivity(FlowConfigRepository flowConfigRepository) {
        super(flowConfigRepository);
    }

    //
    @Override
    public String getIcon() {
        return "fa-list-ul";
    }

}