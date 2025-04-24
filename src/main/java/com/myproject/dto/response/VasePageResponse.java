package com.myproject.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
public class VasePageResponse extends PageResponseAbstract implements Serializable {
    private List<VaseResponse> vases;
}
