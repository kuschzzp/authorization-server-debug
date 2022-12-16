package com.goodcol.muses.service;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.Map;


@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
@JsonDeserialize(using = SingletonMapDeserializer.class)
abstract class SingletonMapMixin {

    @JsonCreator
    SingletonMapMixin(Map<?, ?> map) {
    }

}
