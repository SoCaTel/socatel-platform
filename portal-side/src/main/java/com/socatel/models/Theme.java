package com.socatel.models;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "so_theme")
public class Theme {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "theme_id")
    private int id;

    @Column(name = "theme_name")
    private String name;

    @ManyToMany(mappedBy = "themes", fetch = FetchType.LAZY)
    private Set<Group> groups;

    @ManyToMany(mappedBy = "themes", fetch = FetchType.LAZY)
    private Set<User> users;

    @ManyToMany(mappedBy = "themes", fetch = FetchType.LAZY)
    private Set<Service> services;

    public Theme() {}

    public Theme(String name) {
        this.name = name;
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

    public Set<Group> getGroups() {
        return groups;
    }

    public void setGroups(Set<Group> groups) {
        this.groups = groups;
    }

    public Set<User> getUsers() {
        return users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }

    public Set<Service> getServices() {
        return services;
    }

    public void setServices(Set<Service> services) {
        this.services = services;
    }
}
