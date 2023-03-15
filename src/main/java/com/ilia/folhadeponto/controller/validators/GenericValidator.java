package com.ilia.folhadeponto.controller.validators;

public interface GenericValidator<T> {

    default void validate(T obj) throws Exception{};

}
