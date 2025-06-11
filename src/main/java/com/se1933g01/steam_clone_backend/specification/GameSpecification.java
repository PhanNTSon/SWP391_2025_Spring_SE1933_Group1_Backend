package com.se1933g01.steam_clone_backend.specification;

import com.se1933g01.steam_clone_backend.entity.game.Game;
import com.se1933g01.steam_clone_backend.entity.game.Tag;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.CollectionUtils; // Import tiện ích của Spring
import java.util.List;

public class GameSpecification {

    /**
     * Tạo một Specification để lọc game có giá nhỏ hơn hoặc bằng maxPrice.
     * 
     * @param maxPrice Giá tối đa.
     * @return Specification hoặc null nếu maxPrice không hợp lệ.
     */
    public static Specification<Game> hasMaxPrice(Double maxPrice) {
        return (root, query, criteriaBuilder) -> {
            if (maxPrice == null || maxPrice >= 60) { // Giả sử 60 là giá trị "Any"
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
