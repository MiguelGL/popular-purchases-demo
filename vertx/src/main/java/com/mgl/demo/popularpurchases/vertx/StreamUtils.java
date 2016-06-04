package com.mgl.demo.popularpurchases.vertx;

import java.util.Iterator;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

// Shanelessly taken from:
//   http://stackoverflow.com/questions/24511052/how-to-convert-an-iterator-to-a-stream
public final class StreamUtils {

    private StreamUtils() {}
    
    public static <T> Stream<T> asStream(Iterator<T> sourceIterator) {
        return asStream(sourceIterator, false);
    }

    public static <T> Stream<T> asStream(Iterator<T> sourceIterator, boolean parallel) {
        Iterable<T> iterable = () -> sourceIterator;
        return StreamSupport.stream(iterable.spliterator(), parallel);
    }

}