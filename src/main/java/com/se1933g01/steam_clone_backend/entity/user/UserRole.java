package com.se1933g01.steam_clone_backend.entity.user;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "UserRoles")
@Getter
@Setter
public class UserRole {

    @EmbeddedId // Đánh dấu sử dụng một ID nhúng
    private UserRoleId id;

    // Mối quan hệ Many-to-One với User
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId") // Ánh xạ thuộc tính 'userId' trong UserRoleId với User
    @JoinColumn(name = "UserID")
    private User user;

    // Mối quan hệ Many-to-One với Role
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("roleId") // Ánh xạ thuộc tính 'roleId' trong UserRoleId với Role
    @JoinColumn(name = "RoleID")
    private Role role;

    // Constructors, etc.
    public UserRole() {
        this.id = new UserRoleId(); // Khởi tạo id
    }

    public UserRole(User user, Role role) {
        this.user = user;
        this.role = role;
        this.id = new UserRoleId(user.getUserId(), role.getRoleId());
    }
}