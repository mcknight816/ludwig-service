package com.bluntsoftware.ludwig.conduit.activities;

import com.bluntsoftware.ludwig.conduit.utils.schema.EntitySchema;
import com.bluntsoftware.ludwig.conduit.utils.schema.JsonSchema;
import com.bluntsoftware.ludwig.repository.ActivityConfigRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import java.util.Map;

public abstract class TypedActivity<I extends EntitySchema,O extends EntitySchema>  extends ActivityImpl {
    Class<I> inType;
    public TypedActivity(ActivityConfigRepository activityConfigRepository, Class<I> inType) {
        super(activityConfigRepository);
        this.inType = inType;
    }

    protected abstract I input();
    protected abstract O output();
    protected abstract O run(I input) throws Exception;

    @Override
    public JsonSchema getJsonSchema() {
        return input().getJsonSchema();
    }

    JsonSchema getOutputSchema(){
        return output().getJsonSchema();
    }

    @Override
    public Map<String, Object> getInput() {
        return getJsonSchema().getValue();
    }

    @Override
    public Map<String, Object> getOutput() {
        return getOutputSchema().getValue();
    }

    @Override
    public Map<String, Object> run(Map<String, Object> input) throws Exception {
        I in =  super.convertValue(input, inType);
        O out = this.run(in);
        return super.mapper().convertValue(out,new TypeReference<Map<String, Object>>() {});
    }
}
