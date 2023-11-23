package com.bluntsoftware.ludwig.service;

import com.bluntsoftware.ludwig.conduit.activities.mongo.domain.MongoSettings;
import com.bluntsoftware.ludwig.domain.Flow;
import com.bluntsoftware.ludwig.domain.FlowTemplate;
import com.bluntsoftware.ludwig.service.template.MongoCrudFlowTemplate;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class FlowTemplateService {
    List<FlowTemplate> templates;
    final String MONGO_CRUD_FLOW =  "Mongo Crud Flow";

    ObjectMapper mapper = new ObjectMapper();
    public FlowTemplateService() {
        templates  = new ArrayList<>();
        templates.add(FlowTemplate.builder().name(MONGO_CRUD_FLOW).type(MONGO_CRUD_FLOW).schema(MongoSettings.getSchema()).build());
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    public List<FlowTemplate> findAll() {
        return templates;
    }

    public Flow createFlow(FlowTemplate flowTemplate) {

        if(MONGO_CRUD_FLOW.equalsIgnoreCase(flowTemplate.getType())){
            MongoCrudFlowTemplate mongoCrudFlowTemplate = new MongoCrudFlowTemplate();
            return mongoCrudFlowTemplate.createMongoCrudFlow("Untitled Crud Flow",mapper.convertValue(flowTemplate.getContext(),MongoSettings.class));
        }

        return null;
    }

}
