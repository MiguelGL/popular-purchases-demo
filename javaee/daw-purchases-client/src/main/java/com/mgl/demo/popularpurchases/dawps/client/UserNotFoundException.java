package com.mgl.demo.popularpurchases.dawps.client;

import static com.google.common.base.Preconditions.checkNotNull;

import lombok.Getter;

public class UserNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    @Getter
    private final String username;

    public UserNotFoundException(String username) {
        super(String.format("User with username '%s' does not exist", username));
        this.username = checkNotNull(username, "username");
    }

}
