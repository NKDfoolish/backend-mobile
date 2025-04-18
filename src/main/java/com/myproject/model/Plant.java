package com.myproject.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "tbl_plant")
public class Plant extends AbstractEntity<Long> implements Serializable {

    @Column(name = "plant_name", length = 255)
    private String plantName;

    @Column(name = "image", length = 255)
    private String image;

    @Column(name = "description", length = 255)
    private String description;

    @OneToMany(mappedBy = "plant")
    @JsonIgnore
    private Set<Vase> vases = new HashSet<>();
}
