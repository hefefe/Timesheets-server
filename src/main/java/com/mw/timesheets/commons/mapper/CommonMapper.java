package com.mw.timesheets.commons.mapper;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public interface CommonMapper<E, D> {
    E toEntity(D dto);

    D toDto(E entity);

    default List<D> toDtos(List<E> entities) {
        if (entities == null) {
            return Lists.newArrayList();
        }

        return entities.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    default List<E> toEntities(List<D> dtos) {
        if (dtos == null) {
            return Lists.newArrayList();
        }

        return dtos.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }

    default Set<D> toSetDtos(Set<E> entities) {
        if (entities == null) {
            return Sets.newHashSet();
        }

        return entities.stream()
                .map(this::toDto)
                .collect(Collectors.toSet());
    }

    default Set<E> toSetEntities(Set<D> dtos) {
        if (dtos == null) {
            return Sets.newHashSet();
        }

        return dtos.stream()
                .map(this::toEntity)
                .collect(Collectors.toSet());
    }
}
