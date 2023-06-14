package com.km.recipe.mappers;

import java.util.Collection;
import java.util.Set;

public interface AbstractMapper<D, E> {

    D toDto(E entity);

    Set<D> toDto(Collection<E> entities);

    E toEntity(D dto);

    Set<E> toEntity(Collection<D> dtos);

}
