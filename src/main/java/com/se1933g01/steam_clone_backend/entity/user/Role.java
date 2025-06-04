package com.se1933g01.steam_clone_backend.entity.user;

import java.util.List;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "Role")
public class Role {
    @Id
    @Column(name = "RoleID")
    private Long roleID;

    @Column(name = "Type")
    private String type;

    // ================ Relationships =============
    @OneToMany(mappedBy = "role")
    private List<User> user;

    // ================ Getter & Setter =============

    public Role() {
    }

    public Long getRoleID() {
        return roleID;
    }

    public void setRoleID(Long roleID) {
        this.roleID = roleID;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<User> getUser() {
        return user;
    }

    public void setUser(List<User> user) {
        this.user = user;
    }

}
