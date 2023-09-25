package com.bluntsoftware.ludwig.mapping;

import com.bluntsoftware.ludwig.dto.ConfigDto;
import com.bluntsoftware.ludwig.model.Config;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ConfigMapper {
    ConfigMapper MAPPER = Mappers.getMapper( ConfigMapper.class );
    Config configDtoToConfig(ConfigDto configDto);
    ConfigDto configToConfigDto(Config config);
}
