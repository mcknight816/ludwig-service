package com.bluntsoftware.ludwig.mapping;

import com.bluntsoftware.ludwig.dto.ApplicationDto;
import com.bluntsoftware.ludwig.domain.Application;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ApplicationMapper {
    ApplicationMapper MAPPER = Mappers.getMapper( ApplicationMapper.class );
    Application applicationDtoToApplication(ApplicationDto applicationDto);
    ApplicationDto applicationToApplicationDto(Application application);
}
