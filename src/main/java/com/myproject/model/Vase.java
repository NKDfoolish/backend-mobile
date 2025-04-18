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

    @Column(name = "vase_name", length = 255)
    private String vaseName;

    @ManyToOne
    @JoinColumn(name = "plant_id", nullable = false)
    private Plant plant;

    @ManyToOne
    @JoinColumn(name = "area_id", nullable = false)
    @JsonIgnore
    private Area area;
}
