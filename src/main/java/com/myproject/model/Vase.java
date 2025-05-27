package com.myproject.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@Entity
@Table(name = "tbl_vase")
public class Vase extends AbstractEntity<Integer> implements Serializable {

    @Column(name = "vase_name", length = 255, unique = true, nullable = false)
    private String vaseName;

    @Column(name = "device_id", unique = true, nullable = true)
    private Integer deviceId;

    @ManyToOne
    @JoinColumn(name = "plant_id", nullable = true)
    private Plant plant;

    @ManyToOne
    @JoinColumn(name = "area_id", nullable = true)
    @JsonIgnore
    private Area area;
}
