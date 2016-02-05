package com.caliente.express.api.requests;

import com.caliente.express.api.models.User;

/**
 * Created by John R. Kosinski on 23/1/2559.
 */
public class UpdateUserRequest extends ApiRequest {
    private User user;

    public User getUser(){
        return this.user;
    }

    public String getPermissionLevel(){
        return user.getPermissionLevel();
    }
    public void setPermissionLevel(String value){
        user.setPermissionLevel(value);
    }

    public UpdateUserRequest(){
        this.user = new User();
    }
}
