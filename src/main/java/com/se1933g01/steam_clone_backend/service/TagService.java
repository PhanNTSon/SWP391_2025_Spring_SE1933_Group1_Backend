package com.se1933g01.steam_clone_backend.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.se1933g01.steam_clone_backend.dto.TagDTO;
import com.se1933g01.steam_clone_backend.entity.game.Tag;
import com.se1933g01.steam_clone_backend.mapper.EntityMapper;
import com.se1933g01.steam_clone_backend.repository.TagRepository;

import java.util.List;
import java.util.stream.Collectors;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TagService {

    private final TagRepository tagRepository;

    @Autowired
    public TagService(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    public List<TagDTO> getAllTags() {
        List<Tag> tags = tagRepository.findAll();

        return tags.stream()
                .map(EntityMapper::toTagDTO)
                .collect(Collectors.toList());
    }
}