package com.myproject.service.impl;

import com.myproject.dto.request.VaseCreationRequest;
import com.myproject.dto.request.VaseUpdateRequest;
import com.myproject.dto.response.PlantResponse;
import com.myproject.dto.response.VasePageResponse;
import com.myproject.dto.response.VaseResponse;
import com.myproject.exception.InvalidDataException;
import com.myproject.exception.ResourceNotFoundException;
import com.myproject.model.Plant;
import com.myproject.model.Vase;
import com.myproject.repository.PlantRepository;
import com.myproject.repository.VaseRepository;
import com.myproject.service.VaseService;
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
@Slf4j(topic = "VASE_SERVICE")
@RequiredArgsConstructor
public class VaseServiceImpl implements VaseService {

    private final VaseRepository vaseRepository;
    private final PlantRepository plantRepository;

    @Override
    public VasePageResponse findAll(String keyword, String sort, int page, int size) {
        log.info("Find all vases");

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

        Page<Vase> entityPage;

        if (StringUtils.hasLength(keyword)){
            // call search method
            keyword = "%" + keyword.toLowerCase() + "%";
            entityPage = vaseRepository.searchByKeyword(keyword, pageable);
        } else {
            entityPage = vaseRepository.findAll(pageable);
        }

        return getVasePageResponse(page, size, entityPage);
    }

    @Override
    public VaseResponse findById(Integer id) {
        log.info("Find vase by id {}", id);

        Vase vase =  getVaseEntity(id);

        return VaseResponse.builder()
                .id(id)
                .vaseName(vase.getVaseName())
                .plant(vase.getPlant() != null ? PlantResponse.builder()
                        .id(vase.getPlant().getId())
                        .plantName(vase.getPlant().getPlantName())
                        .image(vase.getPlant().getImage())
                        .build() : null)
                .createdAt(vase.getCreatedAt())
                .updatedAt(vase.getUpdatedAt())
                .build();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer save(VaseCreationRequest req) {
        log.info("Save vase {}", req);

        Vase existingVase = vaseRepository.findByVaseName(req.getVaseName());
        if (existingVase != null) {
            throw new InvalidDataException("Vase already exists");
        }

        Vase vase = new Vase();
        vase.setVaseName(req.getVaseName());

        if (req.getPlantId() != null) {
            Plant plant = getPlantById(req.getPlantId());
            vase.setPlant(plant);
        }

        Vase result = vaseRepository.save(vase);
        log.info("Vase saved with id {}", result.getId());

        return result.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(VaseUpdateRequest req) {
        log.info("Update vase {}", req);

        // Get vase by id
        Vase vase =  getVaseEntity(req.getId());

        if (req.getVaseName() != null) {
            vase.setVaseName(req.getVaseName());
        }

        if (req.getPlantId() != null) {
            Plant plant = getPlantById(req.getPlantId());
            vase.setPlant(plant);
        }

        vaseRepository.save(vase);
        log.info("Vase updated: {}", vase);
    }

    @Override
    public void delete(Integer vaseId) {
        log.info("Delete vase {}", vaseId);

        Vase vase = getVaseEntity(vaseId);
        vaseRepository.delete(vase);
        log.info("Vase deleted: {}", vase);
    }

    private Vase getVaseEntity(Integer id) {
        return vaseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vase not found"));
    }

    private static VasePageResponse getVasePageResponse(int page, int size, Page<Vase> entityPage) {
        log.info("Get vase page response");

        List<VaseResponse> vaseList = entityPage.stream()
                .map(vase -> VaseResponse.builder()
                        .id(vase.getId())
                        .vaseName(vase.getVaseName())
                        .plant(vase.getPlant() != null ? PlantResponse.builder()
                                .id(vase.getPlant().getId())
                                .plantName(vase.getPlant().getPlantName())
                                .build() : null)
                        .build())
                .toList();

        VasePageResponse vasePageResponse = new VasePageResponse();
        vasePageResponse.setPageNumber(page);
        vasePageResponse.setPageSize(size);
        vasePageResponse.setTotalElements(entityPage.getTotalElements());
        vasePageResponse.setTotalPages(entityPage.getTotalPages());
        vasePageResponse.setVases(vaseList);

        return vasePageResponse;
    }

    private Plant getPlantById(Long plantId) {
        return plantRepository.findById(plantId)
                .orElseThrow(() -> new ResourceNotFoundException("Plant not found"));
    }
}
