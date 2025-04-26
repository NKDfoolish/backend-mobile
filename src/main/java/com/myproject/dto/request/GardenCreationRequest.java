package com.myproject.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@ToString
public class GardenCreationRequest implements Serializable {
    @NotBlank(message = "Garden name is required")
    private String gardenName;

    private Long userId;
}
