package com.mgl.demo.popularpurchases.dawps.client.model.wrapper.support;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@Getter(AccessLevel.PRIVATE)
public abstract class BaseItemWrapper<T> implements WrapsSomething<T> {

    private final T item; // can be null

    @Override
    public T unwrap() throws NothingWrappedException {
        if (item == null) {
            throw new NothingWrappedException();
        } else {
            return getItem();
        }
    }

}
