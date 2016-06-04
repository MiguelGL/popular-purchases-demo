package com.mgl.demo.popularpurchases.dawps.client.model.wrapper.support;

interface WrapsSomething<T> {

    T unwrap() throws NothingWrappedException;

}
