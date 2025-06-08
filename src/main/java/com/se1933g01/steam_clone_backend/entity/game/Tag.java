package com.se1933g01.steam_clone_backend.entity.game;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "Tags")
@Getter
@Setter
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TagID")
    private Integer tagId;

    @Column(name = "TagName", nullable = false, unique = true, length = 50)
    private String tagName;

    // Mối quan hệ Many-to-Many với Game sẽ được xử lý qua GameTags
    // @ManyToMany(mappedBy = "tags")
    // private Set<Game> games;
}
