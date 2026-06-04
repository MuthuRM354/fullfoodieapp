package com.foodieapp.user.dto;

import lombok.Data;

@Data
public class ProfileRequest {
    private String address;
    private String city;
    private String state;
    private String pincode;
    private String profileImageUrl;
}
