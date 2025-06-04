package com.se1933g01.steam_clone_backend.entity.game;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "SystemRequirement")
public class SystemRequirement {
    @Id
    @Column(name = "SysReqID")
    private long sysReqId;

    @Column(name = "Os")
    private String os;
    @Column(name = "Storage")
    private String storage;
    @Column(name = "Processor")
    private String processor;
    @Column(name = "Memory")
    private String memory;
    @Column(name = "AdditionalNotes")
    private String additionalNotes;
    @Column(name = "Graphics")
    private String graphics;
    // ================ Relationships =============
    @OneToOne
    @MapsId
    @JoinColumn(name = "SysReqID")
    private Game game;
}
