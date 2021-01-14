package com.socatel.knowledge_base_dump;

import com.socatel.models.Locality;

public class DbLocalityRowDump {
    private Integer locality_id;
    private String locality_name;
    private DbLocalityRowDump locality_parent;

    public DbLocalityRowDump() {
        super();
    }

    public DbLocalityRowDump(Locality locality) {
        locality_id = locality.getId();
        locality_name = locality.getName();
        locality_parent = locality.getLocalityParent() == null ? null : new DbLocalityRowDump(locality.getLocalityParent());
    }

    public Integer getLocality_id() {
        return locality_id;
    }

    public void setLocality_id(Integer locality_id) {
        this.locality_id = locality_id;
    }

    public String getLocality_name() {
        return locality_name;
    }

    public void setLocality_name(String locality_name) {
        this.locality_name = locality_name;
    }

    public DbLocalityRowDump getLocality_parent() {
        return locality_parent;
    }

    public void setLocality_parent(DbLocalityRowDump locality_parent) {
        this.locality_parent = locality_parent;
    }
}
