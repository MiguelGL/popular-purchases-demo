package com.mgl.demo.popularpurchases.dawps.client.model.wrapper;


import com.mgl.demo.popularpurchases.dawps.client.model.wrapper.support.BaseListWrapper;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mgl.demo.popularpurchases.dawps.client.model.User;

public class UsersWrapper extends BaseListWrapper<User> {

    @JsonCreator
    public UsersWrapper(@JsonProperty("users") final List<User> users) {
        super(users);
    }

}
