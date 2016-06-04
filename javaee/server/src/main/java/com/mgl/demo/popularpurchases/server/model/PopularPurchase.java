package com.mgl.demo.popularpurchases.server.model;

import java.io.Serializable;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mgl.demo.popularpurchases.dawps.client.model.Product;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;

@Data @Getter(AccessLevel.PROTECTED)
@JsonIgnoreProperties({"product", "usernames"})
public class PopularPurchase implements Serializable {

    private static final long serialVersionUID = 1L;

    private final Product product;

    private final Set<String> usernames;

    public long getId() {
        return getProduct().getId();
    }

    public String getFace() {
        return getProduct().getFace();
    }

    public int getPrice() {
        return getProduct().getPrice();
    }

    public int getSize() {
        return getProduct().getSize();
    }

    @JsonProperty("recent")
    public Set<String> getPurchasingUsernames() {
        return usernames;
    }

}
