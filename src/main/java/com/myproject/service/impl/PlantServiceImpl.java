package com.myproject.service.impl;

import com.myproject.dto.request.PlantCreationRequest;
import com.myproject.dto.request.PlantUpdateRequest;
import com.myproject.dto.response.PlantPageResponse;
import com.myproject.dto.response.PlantResponse;
import com.myproject.exception.InvalidDataException;
import com.myproject.exception.ResourceNotFoundException;
import com.myproject.model.Plant;
import com.myproject.repository.PlantRepository;
import com.myproject.service.PlantService;
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
@Slf4j(topic = "PLANT_SERVICE")
@RequiredArgsConstructor
public class PlantServiceImpl implements PlantService {
    private final PlantRepository plantRepository;

    @Override
    public PlantPageResponse findAll(String keyword, String sort, int page, int size) {
        log.info("Find all plants");

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

        Page<Plant> entityPage;

        if (StringUtils.hasLength(keyword)){
            // call search method
            keyword = "%" + keyword.toLowerCase() + "%";
            entityPage = plantRepository.searchByKeyword(keyword, pageable);
        } else {
            // call findAll method
            entityPage = plantRepository.findAll(pageable);
        }

        return getPlantPageResponse(page, size, entityPage);
    }

    @Override
    public PlantResponse findById(Long id) {
        log.info("Find plant by id {}", id);

        Plant plant = getPlantEntity(id);

        return PlantResponse.builder()
                .id(id)
                .plantName(plant.getPlantName())
                .image(plant.getImage())
                .description(plant.getDescription())
                .createdAt(plant.getCreatedAt())
                .updatedAt(plant.getUpdatedAt())
                .build();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public long save(PlantCreationRequest req) {
        log.info("Save plant: {}", req);

        Plant existingPlant = plantRepository.findByPlantName(req.getPlantName());
        if (existingPlant != null) {
            throw new InvalidDataException("Plant already exists");
        }

        Plant plant = new Plant();
        plant.setPlantName(req.getPlantName());
        plant.setDescription(req.getDescription());

        Plant result = plantRepository.save(plant);
        log.info("Plant saved: {}", plant);
        return result.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(PlantUpdateRequest req) {
        log.info("Update plant: {}", req);

        // Get plant by id
        Plant plant = getPlantEntity(req.getId());

        // Check if field plantName not null then update else skip
        if (req.getPlantName() != null) {
            plant.setPlantName(req.getPlantName());
        }

        if (req.getDescription() != null) {
            plant.setDescription(req.getDescription());
        }

        plantRepository.save(plant);
        log.info("Plant updated: {}", plant);
    }

    @Override
    public void delete(Long id) {
        log.info("Delete plant {}", id);

        Plant plant = getPlantEntity(id);
        plantRepository.delete(plant);
        log.info("Plant deleted: {}", plant);

    }

    private Plant getPlantEntity(Long id) {
        return plantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Plant not found"));
    }

    private static PlantPageResponse getPlantPageResponse(int page, int size, Page<Plant> plantEntities) {
        log.info("Convert Page<Plant> to PlantPageResponse");

        List<PlantResponse> plantList = plantEntities.stream()
                .map(plant -> PlantResponse.builder()
                        .id(plant.getId())
                        .plantName(plant.getPlantName())
                        .image(plant.getImage())
                        .build())
                .toList();

        PlantPageResponse response = new PlantPageResponse();
        response.setPageNumber(page);
        response.setPageSize(size);
        response.setTotalElements(plantEntities.getTotalElements());
        response.setTotalPages(plantEntities.getTotalPages());
        response.setPlants(plantList);

        return response;
    }
}
