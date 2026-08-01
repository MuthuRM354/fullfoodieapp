package com.foodieapp.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SavedAddressRequest {
    @NotBlank
    private String label;
    @NotBlank
    private String addressLine;
    private boolean isDefault;
}
