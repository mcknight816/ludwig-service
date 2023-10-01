package com.bluntsoftware.ludwig.conduit.activities.input;

import com.bluntsoftware.ludwig.repository.ActivityConfigRepository;
import org.springframework.stereotype.Service;
/**
 * Created by Alex Mcknight on 1/12/2017.
 */
@Service
public class ColumnsActivity extends InputActivity {
    public ColumnsActivity(ActivityConfigRepository activityConfigRepository) {
        super(activityConfigRepository);
    }

    //
    @Override
    public String getIcon() {
        return "fa-list-ul";
    }

}