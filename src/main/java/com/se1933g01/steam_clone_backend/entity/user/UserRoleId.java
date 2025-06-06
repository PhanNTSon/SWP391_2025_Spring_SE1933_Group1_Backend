package com.se1933g01.steam_clone_backend.entity.user;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Embeddable // Đánh dấu là một lớp có thể nhúng
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode // Quan trọng cho composite keys
public class UserRoleId implements Serializable {
    @Column(name = "UserID")
    private Long userId;

    @Column(name = "RoleID")
    private Integer roleId;
}
