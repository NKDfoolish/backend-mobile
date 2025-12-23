package com.myproject.service.impl;

import com.myproject.common.GardenPermission;
import com.myproject.dto.request.ShareGardenRequest;
import com.myproject.dto.response.SharedGardenResponse;
import com.myproject.exception.ForBiddenException;
import com.myproject.exception.InvalidDataException;
import com.myproject.exception.ResourceNotFoundException;
import com.myproject.model.Garden;
import com.myproject.model.GardenShare;
import com.myproject.model.UserEntity;
import com.myproject.repository.GardenRepository;
import com.myproject.repository.GardenShareRepository;
import com.myproject.repository.UserRepository;
import com.myproject.service.GardenShareService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j(topic = "GARDEN_SHARE_SERVICE")
@RequiredArgsConstructor
public class GardenShareServiceImpl implements GardenShareService {

    private final GardenShareRepository gardenShareRepository;
    private final GardenRepository gardenRepository;
    private final UserRepository userRepository;

    // =========================
    // SHARE GARDEN
    // =========================
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void shareGarden(Long ownerId, ShareGardenRequest req) {
        log.info("Share garden {} to user {}", req.getGardenId(), req.getUserId());

        Garden garden = getGardenEntity(req.getGardenId());

        // Check owner
        if (!garden.getUser().getId().equals(ownerId)) {
            throw new ForBiddenException("You are not allowed to share this garden");
        }

        // Không cho share cho chính mình
        if (ownerId.equals(req.getUserId())) {
            throw new InvalidDataException("Cannot share garden to yourself");
        }

        // Check user tồn tại
        getUserEntity(req.getUserId());

        // Check trùng
        if (gardenShareRepository.existsByGardenIdAndUserId(
                req.getGardenId(), req.getUserId())) {
            throw new InvalidDataException("Garden already shared to this user");
        }

        GardenShare share = new GardenShare();
        share.setGardenId(req.getGardenId());
        share.setUserId(req.getUserId());
        share.setPermission(GardenPermission.valueOf(req.getPermission()));
        share.setSharedBy(ownerId);
        share.setCreatedAt(LocalDateTime.now());

        gardenShareRepository.save(share);
        log.info("Garden shared successfully");
    }

    // =========================
    // GET SHARED GARDENS
    // =========================
    @Override
    public List<SharedGardenResponse> getSharedGardens(Long userId) {
        log.info("Get shared gardens for user {}", userId);

        return gardenShareRepository.findSharedGardens(userId)
                .stream()
                .map(p -> new SharedGardenResponse(
                        p.getGardenId(),
                        p.getGardenName(),
                        p.getOwnerName(),
                        p.getPermission(),
                        p.getSharedAt()
                ))
                .toList();
    }

    // =========================
    // REVOKE SHARE
    // =========================
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void revokeShare(Long ownerId, Integer gardenId, Long userId) {
        log.info("Revoke share gardenId={}, userId={}", gardenId, userId);

        Garden garden = getGardenEntity(gardenId);

        if (!garden.getUser().getId().equals(ownerId)) {
            throw new ForBiddenException("You are not allowed to revoke this share");
        }

        GardenShare share = gardenShareRepository
                .findByGardenIdAndUserId(gardenId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Share record not found"));

        gardenShareRepository.delete(share);
        log.info("Garden share revoked");
    }

    // =========================
    // PERMISSION CHECK
    // =========================
    @Override
    public boolean hasViewPermission(Long userId, Integer gardenId) {
        Garden garden = getGardenEntity(gardenId);

        // Owner luôn có quyền
        if (garden.getUser().getId().equals(userId)) {
            return true;
        }

        return gardenShareRepository
                .findByGardenIdAndUserId(gardenId, userId)
                .isPresent();
    }

    @Override
    public boolean hasControlPermission(Long userId, Integer gardenId) {
        Garden garden = getGardenEntity(gardenId);

        // Owner luôn có quyền
        if (garden.getUser().getId().equals(userId)) {
            return true;
        }

        GardenShare share = gardenShareRepository
                .findByGardenIdAndUserId(gardenId, userId)
                .orElseThrow(() -> new ForBiddenException("No permission for this garden"));

        return "CONTROL".equalsIgnoreCase(String.valueOf(share.getPermission()));
    }

    // =========================
    // PRIVATE HELPERS (giống GardenServiceImpl)
    // =========================
    private Garden getGardenEntity(Integer id) {
        return gardenRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Garden not found"));
    }

    private UserEntity getUserEntity(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
}
