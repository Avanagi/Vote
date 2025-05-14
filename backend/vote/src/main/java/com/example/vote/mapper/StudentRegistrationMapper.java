package com.example.vote.mapper;

import com.example.vote.dto.StudentRegistrationDto;
import com.example.vote.entity.StudentEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = SexMapper.class)
public interface StudentRegistrationMapper extends BaseMapper<StudentEntity, StudentRegistrationDto>{

    @Override
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "lastUpdatedAt", ignore = true)
    StudentEntity toEntity(StudentRegistrationDto registerStudentDTO);

}
