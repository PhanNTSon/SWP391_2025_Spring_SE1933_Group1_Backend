package com.se1933g01.steam_clone_backend.entity.user;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "Roles")
@Getter
@Setter
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "RoleID")
    private Integer roleId;

    @Column(name = "RoleName", nullable = false, length = 50)
    private String roleName; // Đổi tên thuộc tính cho phù hợp Java convention

    // Constructors, Getters, Setters (Lombok sẽ tự tạo nếu bạn dùng @Getter
    // @Setter)
    // Nếu không dùng Lombok, bạn phải tự viết:
    // public Role() {}
    // public Integer getRoleId() { return roleId; }
    // public void setRoleId(Integer roleId) { this.roleId = roleId; }
    // public String getRoleName() { return roleName; }
    // public void setRoleName(String roleName) { this.roleName = roleName; }
}