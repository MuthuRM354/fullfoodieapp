package com.foodieapp.user.dto;

import lombok.Data;

@Data
public class UserUpdateRequest {
    private String name;
    private String phone;
    private String address;
}
