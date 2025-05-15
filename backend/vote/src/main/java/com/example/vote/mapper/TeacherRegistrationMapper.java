package com.example.vote.mapper;

import com.example.vote.dto.TeacherRegistrationDto;
import com.example.vote.entity.TeacherEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = SexMapper.class)
public interface TeacherRegistrationMapper extends BaseMapper<TeacherEntity, TeacherRegistrationDto> {

    @Override
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "lastUpdatedAt", ignore = true)
    TeacherEntity toEntity(TeacherRegistrationDto teacherRegistrationDto);

}
