package com.se1933g01.steamclonebackend.entity.user; // Hoặc package entity của bạn

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Getter;
import java.io.Serializable;

/**
 * Lớp này đại diện cho khóa chính tổ hợp của bảng Library.
 * Nó phải implement Serializable và có các phương thức equals() và hashCode().
 */
@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class LibraryId implements Serializable {

    @Column(name = "UserID")
    private Long userId;

    @Column(name = "GameID")
    private Long gameId;
}
