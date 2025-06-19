package com.se1933g01.steam_clone_backend.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.se1933g01.steam_clone_backend.dto.PublisherBasicDTO;
import com.se1933g01.steam_clone_backend.service.PublisherService;

import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author TS Huy
 */
@RestController
@RequestMapping("/publisher")
public class PublisherController {

    @Autowired
    private PublisherService publisherService1;

    @GetMapping("/list")
    @PreAuthorize("permitAll()")
    public ResponseEntity<List<PublisherBasicDTO>> getAllPublishers() {
        List<PublisherBasicDTO> publishers = publisherService1.getAllPublishersBasicDTO();
        if (publishers.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(publishers, HttpStatus.OK);
    }

}
