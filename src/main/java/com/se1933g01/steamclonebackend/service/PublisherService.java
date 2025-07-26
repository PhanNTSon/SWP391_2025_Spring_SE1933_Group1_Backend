package com.se1933g01.steamclonebackend.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import com.se1933g01.steamclonebackend.dto.PublisherBasicDTO;
import com.se1933g01.steamclonebackend.entity.user.Publisher;
import com.se1933g01.steamclonebackend.mapper.EntityMapper;
import com.se1933g01.steamclonebackend.repository.PublisherRepo;

@Service
public class PublisherService {

    private final PublisherRepo publisherRepo;

    public PublisherService(PublisherRepo publisherRepo) {
        this.publisherRepo = publisherRepo;
    }

    @Transactional(readOnly = true)
    public List<PublisherBasicDTO> getAllPublishersBasicDTO() {
        return publisherRepo.findAll().stream()
                .map(EntityMapper::toPublisherBasicDTO)
                .collect(Collectors.toList());
    }
    @Transactional
    public Publisher findById(Long id){
        return publisherRepo.findById(id).orElseThrow();
    }

@Transactional(readOnly = true)
public PublisherBasicDTO getPublisherBasicDTOById(Long id) {
    Publisher publisher = publisherRepo.findById(id).orElseThrow();
    return EntityMapper.toPublisherBasicDTO(publisher);
}
}
