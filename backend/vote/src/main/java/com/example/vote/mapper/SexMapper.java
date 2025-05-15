package com.example.vote.mapper;

import com.example.vote.entity.Sex;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SexMapper {

    default String toString(Sex sex) {
        return sex != null ? sex.name() : null;
    }

    default Sex toSex(String sex) {
        if (sex == null) return null;
        try {
            return Sex.valueOf(sex);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
