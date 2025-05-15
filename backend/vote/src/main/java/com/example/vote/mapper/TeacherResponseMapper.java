package com.example.vote.mapper;

import com.example.vote.dto.TeacherResponseDto;
import com.example.vote.entity.TeacherEntity;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring", uses = SexMapper.class)
public interface TeacherResponseMapper extends BaseMapper<TeacherEntity, TeacherResponseDto> {

    @Override
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "lastUpdatedAt", ignore = true)
    TeacherEntity toEntity(TeacherResponseDto teacherResponseDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "lastUpdatedAt", ignore = true)
    void updateFromDto(TeacherResponseDto teacherResponseDto, @MappingTarget TeacherEntity teacherEntity);

    TeacherResponseDto toDto(TeacherEntity teacherEntity);

    @IterableMapping(elementTargetType = TeacherResponseDto.class)
    List<TeacherResponseDto> toDtoList(List<TeacherEntity> entityList);

}
