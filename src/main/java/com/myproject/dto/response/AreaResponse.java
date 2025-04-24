package com.myproject.dto.response;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AreaResponse implements Serializable {
    private Integer id;
    private String areaName;
    private String image;
    private Integer vaseSize;
}
