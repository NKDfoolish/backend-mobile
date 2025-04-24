package com.myproject.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@ToString
public class VaseCreationRequest implements Serializable {
    @NotBlank(message = "Vase name is required")
    private String vaseName;

    private Long plantId;
}
