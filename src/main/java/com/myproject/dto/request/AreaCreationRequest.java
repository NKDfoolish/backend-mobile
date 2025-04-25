package com.myproject.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@ToString
public class AreaCreationRequest implements Serializable {
    @NotBlank(message = "Area name is required")
    private String areaName;

    private Integer gardenId;
}
