package com.myproject.service.impl;

import com.myproject.dto.request.GardenCreationRequest;
import com.myproject.dto.request.GardenUpdateRequest;
import com.myproject.dto.response.AreaResponse;
import com.myproject.dto.response.GardenPageResponse;
import com.myproject.dto.response.GardenResponse;
import com.myproject.exception.InvalidDataException;
import com.myproject.exception.ResourceNotFoundException;
import com.myproject.model.Area;
import com.myproject.model.Garden;
import com.myproject.model.UserEntity;
import com.myproject.repository.GardenRepository;
import com.myproject.repository.UserRepository;
import com.myproject.service.GardenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j(topic = "GARDEN_SERVICE")
@RequiredArgsConstructor
public class GardenServiceImpl implements GardenService {

    private final GardenRepository gardenRepository;
    private final UserRepository userRepository;

    @Override
    public GardenPageResponse findAll(String keyword, String sort, int page, int size) {
        log.info("find all areas");

        Sort.Order order = new Sort.Order(Sort.Direction.ASC, "id");
        if (StringUtils.hasLength(sort)){
            Pattern pattern = Pattern.compile("(\\w+?)(:)(.*)"); // column_name:asc|desc
            Matcher matcher = pattern.matcher(sort);
            if (matcher.find()){
                if (matcher.group(3).equalsIgnoreCase("asc")){
                    order = new Sort.Order(Sort.Direction.ASC, matcher.group(1));
                } else if (matcher.group(3).equalsIgnoreCase("desc")){
                    order = new Sort.Order(Sort.Direction.DESC, matcher.group(1));
                }
            }
        }

        // when Fe want to get page 1, it will return page 0
        int pageNo = 0;
        if (page > 0) {
            pageNo = page - 1;
        }

        // Paging
        Pageable pageable = PageRequest.of(pageNo, size, Sort.by(order));

        Page<Garden> entityPage;

        if (StringUtils.hasLength(keyword)){
            // call search method
            keyword = "%" + keyword.toLowerCase() + "%";
            entityPage = gardenRepository.searchByKeyword(keyword, pageable);
        } else {
            entityPage = gardenRepository.findAll(pageable);
        }

        return getGardenPageResponse(page, size, entityPage);
    }

    @Override
    public GardenResponse findById(Integer id) {
        log.info("Find garden by id {}", id);

        Garden garden =  getGardenEntity(id);

        return GardenResponse.builder()
                .id(id)
                .gardenName(garden.getGardenName())
                .userId(garden.getUser().getId())
                .createdAt(garden.getCreatedAt())
                .updatedAt(garden.getUpdatedAt())
                .build();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer save(GardenCreationRequest req) {
        log.info("Save garden {}", req);

        Garden existGarden = gardenRepository.findByGardenName(req.getGardenName());
        if (existGarden != null) {
            throw new InvalidDataException("Garden already exists");
        }

        Garden garden = new Garden();
        garden.setGardenName(req.getGardenName());

        if (req.getUserId() != null) {
            UserEntity user = getUserById(req.getUserId());
            garden.setUser(user);
        }

        Garden result = gardenRepository.save(garden);
        log.info("Garden saved with id {}", result.getId());

        return result.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(GardenUpdateRequest req) {
        log.info("Update garden {}", req);

        // Get vase by id
        Garden garden =  getGardenEntity(req.getId());

        if (req.getGardenName() != null) {
            garden.setGardenName(req.getGardenName());
        }

        if (req.getUserId() != null) {
            UserEntity user = getUserById(req.getUserId());
            garden.setUser(user);
        }

        gardenRepository.save(garden);
        log.info("Garden updated: {}", garden);
    }

    @Override
    public void delete(Integer gardenId) {
        log.info("Delete garden {}", gardenId);

        Garden garden = getGardenEntity(gardenId);
        gardenRepository.delete(garden);
        log.info("Garden deleted: {}", garden);
    }

    private UserEntity getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private Garden getGardenEntity(Integer id) {
        return gardenRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Garden not found"));
    }

    private GardenPageResponse getGardenPageResponse(int page, int size, Page<Garden> entityPage) {
        log.info("Get garden page response");

        List<GardenResponse> gardenList = entityPage.stream()
                .map(garden -> GardenResponse.builder()
                        .id(garden.getId())
                        .gardenName(garden.getGardenName())
                        .userId(garden.getUser().getId())
                        .build())
                .toList();

        GardenPageResponse gardenPageResponse = new GardenPageResponse();
        gardenPageResponse.setPageNumber(page);
        gardenPageResponse.setPageSize(size);
        gardenPageResponse.setTotalElements(entityPage.getTotalElements());
        gardenPageResponse.setTotalPages(entityPage.getTotalPages());
        gardenPageResponse.setGardens(gardenList);

        return gardenPageResponse;
    }
}
