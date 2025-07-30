package com.se1933g01.steamclonebackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.se1933g01.steamclonebackend.entity.community.family.FamilyLibrary;
import com.se1933g01.steamclonebackend.entity.community.family.FamilyLibraryId;

@Repository
public interface FamilyLibraryRepo extends JpaRepository<FamilyLibrary, FamilyLibraryId> {

}
