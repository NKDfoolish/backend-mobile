package com.myproject.dto.response;

import lombok.*;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AreaDetailResponse implements Serializable {
    private Integer id;
    private String areaName;
    private String image;
    private Date createdAt;
    private Date updatedAt;
    private List<VaseResponse> vases;
}
