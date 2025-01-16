package com.bluntsoftware.ludwig.service;


import com.bluntsoftware.ludwig.domain.Flow;
import com.bluntsoftware.ludwig.domain.FlowTemplate;
import com.bluntsoftware.ludwig.repository.ActivityConfigRepository;
import com.bluntsoftware.ludwig.service.template.MongoCrudFlowTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class FlowTemplateService {
    Map<String,FlowTemplate> templates;

    private final ActivityConfigRepository activityConfigRepository;

    public FlowTemplateService(ActivityConfigRepository activityConfigRepository) {
        this.activityConfigRepository = activityConfigRepository;
        templates  = new ConcurrentHashMap<>();
        templates.put(MongoCrudFlowTemplate.getType(),MongoCrudFlowTemplate.getFlowTemplate());
    }

    public Collection<FlowTemplate> findAll() {
        return templates.values();
    }

    public Flow createFlow(FlowTemplate flowTemplate) {
        if(MongoCrudFlowTemplate.getType().equalsIgnoreCase(flowTemplate.getType())){
            return MongoCrudFlowTemplate.createFlow("Untitled Crud Flow",flowTemplate,activityConfigRepository);
        }
        return null;
    }

}
