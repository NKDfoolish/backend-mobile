package com.myproject.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ShareGardenRequest {
    @NotNull
    @Min(1)
    private Integer gardenId;

    @NotNull
    @Min(1)
    private Long userId;

    @NotBlank
    private String permission; // VIEW | CONTROL
}
