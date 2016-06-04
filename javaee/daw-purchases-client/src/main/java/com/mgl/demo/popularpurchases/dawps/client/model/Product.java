package com.mgl.demo.popularpurchases.dawps.client.model;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NonNull;

@Data
public class Product implements Serializable {

    private static final long serialVersionUID = 1L;

    private final long id;

    private final @NonNull String face;

    private final int price;

    private final int size;

    @JsonCreator
    public Product(
            @JsonProperty("id") long id,
            @JsonProperty("face") String face,
            @JsonProperty("price") int price,
            @JsonProperty("size") int size) {
        this.id = id;
        this.face = checkNotNull(face, "face");
        this.price = price;
        this.size = size;
    }

}
