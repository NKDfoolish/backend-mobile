package com.myproject.service.impl;

import com.myproject.dto.request.PlantDetailRequest;
import com.myproject.dto.request.PlantDetailUpdateRequest;
import com.myproject.dto.response.PlantDetailResponse;
import com.myproject.exception.InvalidDataException;
import com.myproject.exception.ResourceNotFoundException;
import com.myproject.model.Plant;
import com.myproject.model.PlantDetail;
import com.myproject.repository.PlantDetailRepository;
import com.myproject.repository.PlantRepository;
import com.myproject.service.PlantDetailService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j(topic = "PLANT_DETAIL_SERVICE")
@RequiredArgsConstructor
public class PlantDetailServiceImpl implements PlantDetailService {

    private final PlantRepository plantRepository;
    private final PlantDetailRepository plantDetailRepository;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String save(PlantDetailRequest req) {
        log.info("save plant detail: {}", req);

        String plantName = req.getTableData().stream()
                .filter(entry -> "Common Name".equals(entry.get("attribute")))
                .findFirst()
                .map(entry -> entry.get("value"))
                .orElseThrow(() -> new IllegalArgumentException("Common Name not found in table data"));

        Plant existingPlant = plantRepository.findByPlantName(plantName);
        if (existingPlant != null) {
            throw new InvalidDataException("Plant with this name already exists");
        }

        Plant plant = new Plant();
        plant.setPlantName(plantName);
        plant.setDescription(req.getIntroduction());

        // Save to postgres
        Plant resultPlant = plantRepository.save(plant);

        PlantDetail plantDetail = new PlantDetail();
        plantDetail.setPlantId(resultPlant.getId());
        plantDetail.setIntroduction(req.getIntroduction());
        plantDetail.setTableData(req.getTableData());
        plantDetail.setCareData(req.getCareData());
        plantDetail.setTitle(req.getTitle());

        // Save to MongoDB
        PlantDetail resultPlantDetail = plantDetailRepository.save(plantDetail);

        log.info("save plant detail: {}", resultPlantDetail);

        return resultPlantDetail.getId();
    }

    @Override
    public PlantDetailResponse findByPlantId(Long plantId) {
        log.info("Find plantdetail by plant id: {}", plantId);

        PlantDetail plantDetail = getPlantDetailByPlantId(plantId);

        return PlantDetailResponse.builder()
                .plantId(plantId)
                .introduction(plantDetail.getIntroduction())
                .imageSource(plantDetail.getImageSource())
                .tableData(plantDetail.getTableData())
                .careData(plantDetail.getCareData())
                .title(plantDetail.getTitle())
                .build();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(PlantDetailUpdateRequest req) {
        log.info("update plant detail: {}", req);

        Plant plant = getPlantEntity(req.getPlantId());

        String plantName = req.getTableData().stream()
                .filter(entry -> "Common Name".equals(entry.get("attribute")))
                .findFirst()
                .map(entry -> entry.get("value"))
                .orElse(null);

        if (plantName != null) {
            plant.setPlantName(plantName);
        }

        if (req.getIntroduction() != null) {
            plant.setDescription(req.getIntroduction());
        }

        plantRepository.save(plant);

        PlantDetail plantDetail = getPlantDetailByPlantId(req.getPlantId());

        if (req.getIntroduction() != null) {
            plantDetail.setIntroduction(req.getIntroduction());
        }

        if (req.getTableData() != null) {
            plantDetail.setTableData(req.getTableData());
        }

        if (req.getCareData() != null) {
            plantDetail.setCareData(req.getCareData());
        }

        plantDetailRepository.save(plantDetail);

        log.info("update plant w information successfully");

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long plantId) {
        log.info("delete plant detail: {}", plantId);

        Plant plant = getPlantEntity(plantId);
        PlantDetail plantDetail = getPlantDetailByPlantId(plantId);
        plantRepository.delete(plant);
        plantDetailRepository.delete(plantDetail);
        log.info("delete plant detail successfully");
    }

    private Plant getPlantEntity(Long plantId) {
        return plantRepository.findById(plantId)
                .orElseThrow(() -> new ResourceNotFoundException("Plant not found"));
    }

    private PlantDetail getPlantDetailByPlantId(Long plantId) {
        return plantDetailRepository.findByPlantId(plantId)
                .orElseThrow(() -> new ResourceNotFoundException("Plant information not found"));
    }
}
