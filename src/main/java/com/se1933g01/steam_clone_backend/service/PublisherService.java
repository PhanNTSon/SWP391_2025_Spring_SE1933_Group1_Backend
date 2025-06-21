package com.se1933g01.steam_clone_backend.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import com.se1933g01.steam_clone_backend.repository.PublisherRepo;

import jakarta.persistence.EntityNotFoundException; // Hoặc exception tùy chỉnh
import org.hibernate.Hibernate; // Để khởi tạo các collection lazy
import org.springframework.transaction.annotation.Transactional;

import com.se1933g01.steam_clone_backend.dto.PublisherBasicDTO;
import com.se1933g01.steam_clone_backend.mapper.EntityMapper;

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
}
