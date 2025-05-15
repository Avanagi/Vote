package com.example.vote.mapper;

import com.example.vote.dto.OptionDto;
import com.example.vote.entity.OptionEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OptionMapper extends BaseMapper<OptionEntity, OptionDto> {

    @Mapping(target = "pollId", source = "poll.id")
    @Mapping(target = "Id", source = "id")
    OptionDto toDto(OptionEntity entity);

    OptionEntity toEntity(OptionDto dto);

    List<OptionDto> toDtoList(List<OptionEntity> entityList);
    List<OptionEntity> toEntityList(List<OptionDto> dtoList);
}