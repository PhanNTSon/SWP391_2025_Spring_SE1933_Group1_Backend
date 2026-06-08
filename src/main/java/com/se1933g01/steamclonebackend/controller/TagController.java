package com.se1933g01.steamclonebackend.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.se1933g01.steamclonebackend.dto.TagDTO;
import com.se1933g01.steamclonebackend.service.TagService;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author TS Huy
 */
@RestController
@RequestMapping("/tags") // URL cơ sở cho các API liên quan đến tag
public class TagController {

    private final TagService tagService;

    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    @GetMapping
    @PreAuthorize("permitAll()")
    public ResponseEntity<List<TagDTO>> getAllTags() {
        List<TagDTO> tags = tagService.getAllTags();

        if (tags.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(tags, HttpStatus.OK);
    }

    @GetMapping("/{tagId}")
    @PreAuthorize("permitAll()")
    public ResponseEntity<TagDTO> getTagById(@PathVariable Integer tagId) { 
        TagDTO tag = tagService.getTagById(tagId);
        return ResponseEntity.ok(tag);
    }

}
