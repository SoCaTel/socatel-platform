package com.socatel.models;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "so_locality")
public class Locality {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "locality_id")
    private int id;

    @Column(name = "locality_name")
    private String name;

    @ManyToOne
    @JoinColumn(name="locality_parent_id")
    private Locality localityParent;

    public Locality() {}

    public Locality(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Locality that = (Locality) o;
        return Objects.equals(id, that.id);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Locality getLocalityParent() {
        return localityParent;
    }

    public void setLocalityParent(Locality localityParent) {
        this.localityParent = localityParent;
    }
}
