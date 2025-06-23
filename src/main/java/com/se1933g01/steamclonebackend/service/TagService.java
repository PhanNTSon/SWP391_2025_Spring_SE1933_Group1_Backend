package com.se1933g01.steamclonebackend.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.se1933g01.steamclonebackend.dto.TagDTO;
import com.se1933g01.steamclonebackend.entity.game.Tag;
import com.se1933g01.steamclonebackend.mapper.EntityMapper;
import com.se1933g01.steamclonebackend.repository.TagRepository;


@Service
public class TagService {

    private final TagRepository tagRepository;

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