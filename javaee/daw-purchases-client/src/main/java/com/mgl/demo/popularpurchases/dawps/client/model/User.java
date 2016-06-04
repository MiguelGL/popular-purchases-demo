package com.mgl.demo.popularpurchases.dawps.client.model;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NonNull;

@Data
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    private final @NonNull String username;
    
    private final @NonNull String email;

    @JsonCreator
    public User(
            @JsonProperty("username") String username,
            @JsonProperty("email") String email) {
        this.username = checkNotNull(username, "username");
        this.email = checkNotNull(email, "email");
    }

}
