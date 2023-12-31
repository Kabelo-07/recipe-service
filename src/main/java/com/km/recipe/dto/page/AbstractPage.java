package com.km.recipe.dto.page;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.validation.Valid;
import java.util.Set;

@EqualsAndHashCode
@ToString
@Getter
@Setter
@AllArgsConstructor
public abstract class AbstractPage<T> {

    @JsonProperty("content")
    @Valid
    private Set<T> content;

    @JsonProperty("empty")
    private boolean empty;

    @JsonProperty("first")
    private Boolean first;

    @JsonProperty("last")
    private Boolean last;

    @JsonProperty("number")
    private Integer number;

    @JsonProperty("size")
    private Integer size;

    @JsonProperty("totalElements")
    private Long totalElements;

    @JsonProperty("totalPages")
    private Integer totalPages;

}
