package com.myproject.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
public class AreaPageResponse extends PageResponseAbstract implements Serializable {
    private List<AreaResponse> areas;
}
