package com.mgl.demo.popularpurchases.dawps.client.model.wrapper.support;

import java.util.List;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@Getter(AccessLevel.PRIVATE)
public abstract class BaseListWrapper<T> implements WrapsSomething<List<T>> {

    private final @NonNull List<T> elements;

    @Override
    public List<T> unwrap() {
        return getElements();
    }

}
