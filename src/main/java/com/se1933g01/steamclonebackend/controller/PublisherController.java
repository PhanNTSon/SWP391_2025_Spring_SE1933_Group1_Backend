package com.se1933g01.steamclonebackend.controller;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.se1933g01.steamclonebackend.dto.PublisherBasicDTO;
import com.se1933g01.steamclonebackend.service.PublisherService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


/**
 * @author TS Huy
 */
@RestController
@RequestMapping("/publisher")
public class PublisherController {

    private PublisherService publisherService;

    public PublisherController(PublisherService publisherService1) {
        publisherService = publisherService1;
    }

    @GetMapping("/list")
    @PreAuthorize("permitAll()")
    public ResponseEntity<List<PublisherBasicDTO>> getAllPublishers() {
        List<PublisherBasicDTO> publishers = publisherService.getAllPublishersBasicDTO();
        if (publishers.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(publishers, HttpStatus.OK);
    }

    @GetMapping("/{publisherId}")
    @PreAuthorize("permitAll()")
    public ResponseEntity<PublisherBasicDTO> getPublisherById(@PathVariable Long publisherId) {
        if (publisherId == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(publisherService.getPublisherBasicDTOById(publisherId));
    }
    

}
