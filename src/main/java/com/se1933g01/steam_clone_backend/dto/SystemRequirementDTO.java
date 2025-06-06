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
    // Không cần gameId ở đây nữa vì nó sẽ được lồng trong GameDetailDTO
    // Nếu bạn muốn sys_req_id (PK của SystemRequirement) thì thêm vào
    // private Integer id;
    private String os;
    private String storage;
    private String processor;
    private String memory;
    private String additionalNotes;
    private String graphics;
}
