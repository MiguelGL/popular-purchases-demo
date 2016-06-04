package com.mgl.demo.popularpurchases.dawps.client.model;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NonNull;

@Data
public class Purchase implements Serializable {

    private static final long serialVersionUID = 1L;

    private final long id;

    private final @NonNull String username;

    private final long productId;

    private final @NonNull Date date;

    @JsonCreator
    public Purchase(
            @JsonProperty("id") final long id,
            @JsonProperty("username") final String username,
            @JsonProperty("productId") final long productId,
            @JsonProperty("date") final Date date) {
        this.id = id;
        this.username = checkNotNull(username, "username");
        this.productId = productId;
        this.date = checkNotNull(date, "date");
    }

}
