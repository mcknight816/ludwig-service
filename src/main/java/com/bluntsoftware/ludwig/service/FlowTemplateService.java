package com.bluntsoftware.ludwig.service;

import com.bluntsoftware.ludwig.conduit.activities.mongo.domain.MongoSettings;
import com.bluntsoftware.ludwig.domain.Flow;
import com.bluntsoftware.ludwig.domain.FlowTemplate;
import com.bluntsoftware.ludwig.service.template.MongoCrudFlowTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class FlowTemplateService {
    List<FlowTemplate> templates;
    final String MONGO_CRUD_FLOW =  "Mongo Crud Flow";
    public FlowTemplateService() {
        templates  = new ArrayList<>();
        templates.add(FlowTemplate.builder().name(MONGO_CRUD_FLOW).type(MONGO_CRUD_FLOW).schema(MongoSettings.getSchema()).build());
    }

    public List<FlowTemplate> findAll() {
        return templates;
    }

    public Flow createFlow(FlowTemplate flowTemplate) {

        if(MONGO_CRUD_FLOW.equalsIgnoreCase(flowTemplate.getType())){
            MongoCrudFlowTemplate mongoCrudFlowTemplate = new MongoCrudFlowTemplate();
            return mongoCrudFlowTemplate.createMongoCrudFlow("Untitled Crud Flow","Connection","Database","Collection");
        }

        return null;
    }

}
