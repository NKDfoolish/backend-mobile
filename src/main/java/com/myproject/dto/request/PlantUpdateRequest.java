package com.myproject.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlantUpdateRequest {

    @NotNull(message = "Id is required")
    private Long id;

    private String plantName;

    private String description;
}
