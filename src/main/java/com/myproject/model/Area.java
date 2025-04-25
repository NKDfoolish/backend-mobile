package com.myproject.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "tbl_area")
public class Area extends AbstractEntity<Integer> implements Serializable {
    
    @Column(name = "area_name", length = 255)
    private String areaName;

    @Column(name = "image", length = 255)
    private String image;

    @OneToMany(mappedBy = "area")
    private Set<Vase> vases = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "garden_id", nullable = true)
    @JsonIgnore
    private Garden garden;

}
