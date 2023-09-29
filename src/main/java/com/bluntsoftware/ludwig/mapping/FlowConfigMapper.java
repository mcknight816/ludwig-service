package com.bluntsoftware.ludwig.mapping;

import com.bluntsoftware.ludwig.dto.FlowConfigDto;
import com.bluntsoftware.ludwig.domain.FlowConfig;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface FlowConfigMapper {
    FlowConfigMapper MAPPER = Mappers.getMapper( FlowConfigMapper.class );
    FlowConfig flowConfigDtoToFlowConfig(FlowConfigDto configDto);
    FlowConfigDto flowConfigToFlowConfigDto(FlowConfig config);
}
