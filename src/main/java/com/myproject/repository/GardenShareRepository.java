package com.myproject.repository;

import com.myproject.common.GardenPermission;
import com.myproject.dto.response.SharedGardenResponse;
import com.myproject.model.GardenShare;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GardenShareRepository extends JpaRepository<GardenShare, Long> {

    /* ================================
     * BASIC CRUD / CHECK
     * ================================ */

    /**
     * Check user đã được share garden chưa
     */
    Optional<GardenShare> findByGardenIdAndUserId(
            Integer gardenId,
            Long userId
    );

    /**
     * Kiểm tra user có quyền cụ thể trên garden không
     */
    boolean existsByGardenIdAndUserIdAndPermission(
            Integer gardenId,
            Long userId,
            GardenPermission permission
    );

    /**
     * Kiểm tra user có bất kỳ quyền nào trên garden
     */
    boolean existsByGardenIdAndUserId(
            Integer gardenId,
            Long userId
    );

    /* ================================
     * QUERY LIST
     * ================================ */

    /**
     * Lấy danh sách user được share của 1 garden
     */
    List<GardenShare> findByGardenId(Integer gardenId);

    /**
     * Lấy danh sách garden được share cho user
     */
    List<GardenShare> findByUserId(Long userId);

    /**
     * Lấy danh sách gardenId được share cho user
     * (dùng để join sang GardenRepository)
     */
    @Query("""
        select gs.gardenId
        from GardenShare gs
        where gs.userId = :userId
    """)
    List<Integer> findSharedGardenIds(Long userId);

    /* ================================
     * PERMISSION CHECK (BUSINESS)
     * ================================ */

    /**
     * Check quyền VIEW hoặc CONTROL
     */
    @Query("""
        select count(gs) > 0
        from GardenShare gs
        where gs.gardenId = :gardenId
          and gs.userId = :userId
          and gs.permission = :permission
    """)
    boolean hasPermission(
            Integer gardenId,
            Long userId,
            GardenPermission permission
    );

    /**
     * Check quyền CONTROL (rất hay dùng)
     */
    default boolean hasControlPermission(Integer gardenId, Long userId) {
        return hasPermission(gardenId, userId, GardenPermission.CONTROL);
    }

    /* ================================
     * DELETE / UNSHARE
     * ================================ */

    /**
     * Unshare garden cho 1 user
     */
    @Modifying
    @Query("""
        delete from GardenShare gs
        where gs.gardenId = :gardenId
          and gs.userId = :userId
    """)
    void deleteByGardenIdAndUserId(
            Integer gardenId,
            Long userId
    );

    /**
     * Xoá toàn bộ share của 1 garden
     * (dùng khi owner xoá garden)
     */
    @Modifying
    @Query("""
        delete from GardenShare gs
        where gs.gardenId = :gardenId
    """)
    void deleteAllByGardenId(Integer gardenId);


    @Query("""
        select new com.myproject.dto.response.SharedGardenResponse(
            g.id,
            g.gardenName,
            concat(u.firstName, ' ', u.lastName),
            cast(gs.permission as string),
            gs.createdAt
        )
        from GardenShare gs
        join Garden g on g.id = gs.gardenId
        join UserEntity u on u.id = g.user.id
        where gs.userId = :userId
    """)
    List<SharedGardenResponse> findSharedGardens(Long userId);
}

