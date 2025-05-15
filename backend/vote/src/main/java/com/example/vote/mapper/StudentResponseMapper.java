package com.example.vote.mapper;

import com.example.vote.dto.StudentResponseDto;
import com.example.vote.entity.StudentEntity;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring", uses = SexMapper.class)
public interface StudentResponseMapper extends BaseMapper<StudentEntity, StudentResponseDto> {

    @Override
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "lastUpdatedAt", ignore = true)
    StudentEntity toEntity(StudentResponseDto studentDTO);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "lastUpdatedAt", ignore = true)
    void updateFromDto(StudentResponseDto studentDTO, @MappingTarget StudentEntity studentEntity);

    @Mapping(source = "studentGroup", target = "studentGroup")
    StudentResponseDto toDto(StudentEntity entity);

    @IterableMapping(elementTargetType = StudentResponseDto.class)
    List<StudentResponseDto> toDtoList(List<StudentEntity> entityList);
}
