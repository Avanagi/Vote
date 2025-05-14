package com.example.vote.mapper;

import java.util.List;

public interface BaseMapper<E, D> {
    D toDto(E e);

    E toEntity(D d);

    List<D> toDtoList(List<E> e);

    List<E> toEntityList(List<D> d);
}
