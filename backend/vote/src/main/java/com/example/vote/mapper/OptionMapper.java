package com.example.vote.mapper;

import com.example.vote.dto.OptionDto;
import com.example.vote.entity.OptionEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OptionMapper extends BaseMapper<OptionEntity, OptionDto> {

    @Override
    OptionDto toDto(OptionEntity entity);

    @Override
    OptionEntity toEntity(OptionDto dto);
}