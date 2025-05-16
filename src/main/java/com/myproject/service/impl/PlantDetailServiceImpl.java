package com.myproject.service.impl;

import com.myproject.dto.request.PlantDetailRequest;
import com.myproject.dto.response.PlantDetailResponse;
import com.myproject.exception.InvalidDataException;
import com.myproject.exception.ResourceNotFoundException;
import com.myproject.model.Plant;
import com.myproject.model.PlantDetail;
import com.myproject.repository.PlantDetailRepository;
import com.myproject.repository.PlantRepository;
import com.myproject.service.PlantDetailService;
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

    private PlantDetail getPlantDetailByPlantId(Long plantId) {
        return plantDetailRepository.findByPlantId(plantId)
                .orElseThrow(() -> new ResourceNotFoundException("Plant information not found"));
    }
}
