package com.myproject.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AreaUpdateRequest {

    @NotNull(message = "Id is required")
    private Integer id;

    private String areaName;

    private Integer gardenId;
}
