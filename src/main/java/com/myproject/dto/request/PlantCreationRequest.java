package com.myproject.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@ToString
public class PlantCreationRequest implements Serializable {
    @NotBlank(message = "Plant name is required")
    private String plantName;

    private String description;
}
