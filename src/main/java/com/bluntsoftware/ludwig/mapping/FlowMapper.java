package com.bluntsoftware.ludwig.mapping;

import com.bluntsoftware.ludwig.dto.FlowDto;
import com.bluntsoftware.ludwig.domain.Flow;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface FlowMapper {
    FlowMapper MAPPER = Mappers.getMapper( FlowMapper.class );

    Flow flowDtoToFlow(FlowDto employeeDto);
    FlowDto flowToFlowDto(Flow employee);
}
