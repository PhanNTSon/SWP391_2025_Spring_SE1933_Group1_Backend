package com.se1933g01.steam_clone_backend.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SystemRequirementDTO {
    private String os;
    private String storage;
    private String processor;
    private String memory;
    private String additionalNotes;
    private String graphics;
}
