package com.se1933g01.steamclonebackend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.se1933g01.steamclonebackend.entity.user.Library;
import com.se1933g01.steamclonebackend.entity.user.LibraryId;

@Repository
public interface LibraryRepository extends JpaRepository<Library, LibraryId> {

    /**
     * SỬA LẠI: Dùng phương thức truy vấn dẫn xuất để tìm tất cả các game
     * trong thư viện của một user, hỗ trợ cả phân trang và sắp xếp.
     *
     * Spring Data JPA sẽ tự động tạo câu lệnh query từ tên của phương thức.
     * Tên "findByIdUserId" có nghĩa là: "Tìm theo thuộc tính 'userId' bên trong
     * thuộc tính 'id'".
     *
     * @param userId   ID của người dùng.
     * @param pageable Đối tượng chứa thông tin phân trang (trang số mấy, kích
     *                 thước) và sắp xếp.
     * @return một đối tượng Page chứa danh sách Library và thông tin phân trang.
     */
    Page<Library> findByIdUserId(Long userId, Pageable pageable);

    Optional<Library> findById_UserIdAndId_GameId(Long userId, Long gameId);
    List<Library> findByUser_UserId(Long userId);
}
