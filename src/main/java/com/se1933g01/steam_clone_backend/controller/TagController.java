package com.se1933g01.steam_clone_backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.se1933g01.steam_clone_backend.dto.TagDTO;
import com.se1933g01.steam_clone_backend.service.TagService;

@RestController
@RequestMapping("/tags") // URL cơ sở cho các API liên quan đến tag
public class TagController {

    private final TagService tagService;

    @Autowired
    public TagController(TagService tagService) {
        this.tagService = tagService;
    }


    @GetMapping
    public ResponseEntity<List<TagDTO>> getAllTags() {
        List<TagDTO> tags = tagService.getAllTags();

        if (tags.isEmpty()) {
            // Nếu không có tag nào, trả về status 204 No Content
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        // Nếu có, trả về danh sách tag và status 200 OK
        return new ResponseEntity<>(tags, HttpStatus.OK);
    }
}
