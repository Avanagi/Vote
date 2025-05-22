package com.example.vote.mapper;

import com.example.vote.dto.PollDto;
import com.example.vote.entity.PollEntity;
import org.mapstruct.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Mapper(componentModel = "spring", uses = {OptionMapper.class})
public interface PollMapper {

    @Named("toDtoWithoutOptions")
    @Mappings({
            @Mapping(target = "options", ignore = true),
            @Mapping(target = "visibleFor", source = "visibleFor", qualifiedByName = "stringToList")
    })
    PollDto toDto(PollEntity entity);

    @Named("toEntityWithoutOptions")
    @Mappings({
            @Mapping(target = "options", ignore = true),
            @Mapping(target = "createdAt", ignore = true),
            @Mapping(target = "visibleFor", source = "visibleFor", qualifiedByName = "listToString"),
            @Mapping(target = "teacherId", source = "teacherId")
    })
    PollEntity toEntity(PollDto dto);

    @Named("toDtoWithOptions")
    @Mappings({
            @Mapping(target = "options", source = "options"),
            @Mapping(target = "visibleFor", source = "visibleFor", qualifiedByName = "stringToList")
    })
    PollDto toDtoWithOption(PollEntity entity);

    @IterableMapping(qualifiedByName = "toDtoWithoutOptions")
    List<PollDto> toDtoList(List<PollEntity> entityList);

    @IterableMapping(qualifiedByName = "toDtoWithOptions")
    List<PollDto> toDtoListWithOption(List<PollEntity> entityList);

    @Named("stringToList")
    public static List<String> stringToList(String visibleFor) {
        if (visibleFor == null || visibleFor.isEmpty()) {
            return Collections.emptyList();
        }
        return Arrays.asList(visibleFor.split(","));
    }

    @Named("listToString")
    public static String listToString(List<String> visibleForList) {
        if (visibleForList == null || visibleForList.isEmpty()) {
            return null;
        }
        return String.join(",", visibleForList);
    }
}
