package com.se1933g01.steamclonebackend.specification;

import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.CollectionUtils; // Import tiện ích của Spring
import org.springframework.util.StringUtils;

import com.se1933g01.steamclonebackend.entity.game.Game;
import com.se1933g01.steamclonebackend.entity.game.Tag;

import java.math.BigDecimal;
import java.util.List;

public class GameSpecification {

    /**
     * Tạo một Specification để lọc game có giá nhỏ hơn hoặc bằng maxPrice.
     * 
     * @param maxPrice Giá tối đa.
     * @return Specification hoặc null nếu maxPrice không hợp lệ.
     */
    public static Specification<Game> hasMaxPrice(BigDecimal maxPrice) { // Changed by Phan Son
        return (root, query, criteriaBuilder) -> {
            // Changed by Phan Son
            if (maxPrice == null || maxPrice.compareTo(BigDecimal.valueOf(60)) > 0) { // Giả sử 60 là giá trị "Any"
                return null; // Trả về null để không áp dụng điều kiện lọc này
            }
            // root.get("price") -> trỏ đến thuộc tính 'price' trong Game entity
            return criteriaBuilder.lessThanOrEqualTo(root.get("price"), maxPrice);
        };
    }

    /**
     * Tạo một Specification để lọc game có chứa BẤT KỲ tag nào trong danh sách
     * tagIds.
     * 
     * @param tagIds Danh sách các ID của tag.
     * @return Specification hoặc null nếu danh sách rỗng.
     */
    public static Specification<Game> hasTags(List<Integer> tagIds) {
        return (root, query, criteriaBuilder) -> {
            if (CollectionUtils.isEmpty(tagIds)) {
                return null;
            }
            // root.join("tags") -> tạo một phép JOIN đến collection 'tags' trong Game
            // entity
            Join<Game, Tag> tagsJoin = root.join("tags");
            // Lọc những game có tagId nằm trong danh sách tagIds được cung cấp
            return tagsJoin.get("tagId").in(tagIds);
        };
    }

    /**
     * Tương tự hasTags, tạo Specification để lọc theo publisher.
     * 
     * @param publisherIds Danh sách các ID của publisher.
     * @return Specification hoặc null nếu danh sách rỗng.
     */
    public static Specification<Game> hasPublishers(List<Long> publisherIds) {
        return (root, query, criteriaBuilder) -> {
            if (CollectionUtils.isEmpty(publisherIds)) {
                return null;
            }
            // root.get("publisher") -> trỏ đến thuộc tính 'publisher' trong Game entity
            // .get("publisherId") -> trỏ đến thuộc tính 'publisherId' trong Publisher
            // entity
            return root.get("publisher").get("publisherId").in(publisherIds);
        };
    }

    public static Specification<Game> hasSearchTerm(String searchTerm) {
        return (root, query, criteriaBuilder) -> {
            // StringUtils.hasText kiểm tra xem chuỗi có null, rỗng, hoặc chỉ chứa khoảng
            // trắng không.
            if (!StringUtils.hasText(searchTerm)) {
                return null; // Không áp dụng điều kiện lọc nếu không có từ khóa
            }
            // Tạo điều kiện WHERE LOWER(game.name) LIKE '%searchTerm%'
            return criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("name")), // Chuyển tên game về chữ thường
                    "%" + searchTerm.toLowerCase() + "%" // Chuyển từ khóa về chữ thường và thêm ký tự đại diện %
            );
        };
    }

    /**
     * LƯU Ý QUAN TRỌNG:
     * Logic hasTags() ở trên tìm game có CHỨA ÍT NHẤT MỘT (OR) trong các tag đã
     * chọn.
     * Nếu bạn muốn tìm game có TẤT CẢ (AND) các tag đã chọn, logic sẽ phức tạp hơn
     * đáng kể,
     * thường phải dùng subquery hoặc having count. Logic hiện tại là phổ biến và
     * hiệu quả nhất
     * cho hầu hết các trang thương mại điện tử.
     */
}
