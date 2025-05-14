package com.example.vote.mapper;

import com.example.vote.dto.PollDto;
import com.example.vote.entity.PollEntity;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PollMapper extends BaseMapper<PollEntity, PollDto> {

    @Named("toDtoWithoutOptions")
    @Override
    @Mappings({
            @Mapping(target = "options", ignore = true)
    })
    PollDto toDto(PollEntity entity);

    @Named("toEntityWithoutOptions")
    @Override
    @Mappings({
            @Mapping(target = "options", ignore = true),
            @Mapping(target = "createdAt", ignore = true)
    })
    PollEntity toEntity(PollDto dto);

    @Named("toDtoWithOptions")
    @Mappings({
            @Mapping(target = "options", source = "options")
    })
    PollDto toDtoWithOption(PollEntity entity);

    @IterableMapping(qualifiedByName = "toDtoWithoutOptions")
    List<PollDto> toDtoList(List<PollEntity> entityList);

    @IterableMapping(qualifiedByName = "toDtoWithOptions")
    List<PollDto> toDtoListWithOption(List<PollEntity> entityList);
}