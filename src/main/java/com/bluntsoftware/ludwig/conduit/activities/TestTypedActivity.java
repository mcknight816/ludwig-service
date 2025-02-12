package com.bluntsoftware.ludwig.conduit.activities;

import com.bluntsoftware.ludwig.conduit.activities.ai.domain.AITextRequest;
import com.bluntsoftware.ludwig.conduit.activities.ai.domain.AITextResponse;
import com.bluntsoftware.ludwig.repository.ActivityConfigRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TestTypedActivity extends TypedActivity<AITextRequest, AITextResponse> {

    public TestTypedActivity(ActivityConfigRepository activityConfigRepository) {
        super(activityConfigRepository,AITextRequest.class);
    }

    @Override
    protected AITextRequest input() {
        return AITextRequest.builder().build();
    }

    @Override
    protected AITextResponse output() {
        return AITextResponse.builder().build();
    }

    @Override
    protected AITextResponse run(AITextRequest input) {
        return null;
    }

    public static void main(String[] args) {

       TestTypedActivity activity = new TestTypedActivity(new ActivityConfigRepository(null));
        log.info("{} {}",activity.getInput(),activity.getOutput());
        log.info("{} {}",activity.getJsonSchema(),activity.getOutputSchema());
        log.info("{} {}",activity.getJsonSchema().getValue(),activity.getOutputSchema().getValue());
    }
}
