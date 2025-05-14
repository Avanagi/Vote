package com.example.vote.mapper;

import com.example.vote.dto.StudentResponseDto;
import com.example.vote.entity.StudentEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

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

}
