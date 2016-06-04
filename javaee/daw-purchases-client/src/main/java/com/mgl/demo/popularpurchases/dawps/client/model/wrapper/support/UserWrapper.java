package com.mgl.demo.popularpurchases.dawps.client.model.wrapper.support;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mgl.demo.popularpurchases.dawps.client.model.User;

public class UserWrapper extends BaseItemWrapper<User> {

    @JsonCreator
    public UserWrapper(@JsonProperty("user") final User user) {
        super(user);
    }

}
