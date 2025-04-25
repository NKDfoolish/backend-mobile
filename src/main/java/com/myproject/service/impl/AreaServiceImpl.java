package com.myproject.service.impl;

import com.myproject.dto.request.AreaCreationRequest;
import com.myproject.dto.request.AreaUpdateRequest;
import com.myproject.dto.response.*;
import com.myproject.exception.InvalidDataException;
import com.myproject.exception.ResourceNotFoundException;
import com.myproject.model.Area;
import com.myproject.model.Garden;
import com.myproject.repository.AreaRepository;
import com.myproject.repository.GardenRepository;
import com.myproject.service.AreaService;
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
@Slf4j(topic = "AREA_SERVICE")
@RequiredArgsConstructor
public class AreaServiceImpl implements AreaService {

    private final AreaRepository areaRepository;
    private final GardenRepository gardenRepository;

    @Override
    public AreaPageResponse findAll(String keyword, String sort, int page, int size) {
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

        Page<Area> entityPage;

        if (StringUtils.hasLength(keyword)){
            // call search method
            keyword = "%" + keyword.toLowerCase() + "%";
            entityPage = areaRepository.searchByKeyword(keyword, pageable);
        } else {
            entityPage = areaRepository.findAll(pageable);
        }

        return getAreaPageResponse(page, size, entityPage);
    }

    @Override
    public AreaResponse findById(Integer id) {
        log.info("Find area by id {}", id);

        Area area =  getAreaEntity(id);

        return AreaResponse.builder()
                .id(id)
                .areaName(area.getAreaName())
                .garden(area.getGarden())
                .createdAt(area.getCreatedAt())
                .updatedAt(area.getUpdatedAt())
                .build();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer save(AreaCreationRequest req) {
        log.info("Save area {}", req);

        Area existingArea = areaRepository.findByAreaName(req.getAreaName());
        if (existingArea != null) {
            throw new InvalidDataException("Area already exists");
        }

        Area area = new Area();
        area.setAreaName(req.getAreaName());

        if (req.getGardenId() != null) {
            Garden garden = getGardenById(req.getGardenId());
            area.setGarden(garden);
        }

        Area result = areaRepository.save(area);
        log.info("Area saved with id {}", result.getId());

        return result.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(AreaUpdateRequest req) {
        log.info("Update area {}", req);

        // Get vase by id
        Area area =  getAreaEntity(req.getId());

        if (req.getAreaName() != null) {
            area.setAreaName(req.getAreaName());
        }

        if (req.getGardenId() != null) {
            Garden garden = getGardenById(req.getGardenId());
            area.setGarden(garden);
        }

        areaRepository.save(area);
        log.info("Area updated: {}", area);
    }

    @Override
    public void delete(Integer areaId) {
        log.info("Delete area {}", areaId);

        Area area = getAreaEntity(areaId);
        areaRepository.delete(area);
        log.info("Area deleted: {}", area);
    }

    private Garden getGardenById(Integer gardenId) {
        return gardenRepository.findById(gardenId)
                .orElseThrow(() -> new ResourceNotFoundException("Garden not found"));
    }

    private Area getAreaEntity(Integer id) {
        return areaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Area not found"));
    }

    private static AreaPageResponse getAreaPageResponse(int page, int size, Page<Area> entityPage) {
        log.info("Get area page response");

        List<AreaResponse> areaList = entityPage.stream()
                .map(area -> AreaResponse.builder()
                        .id(area.getId())
                        .areaName(area.getAreaName())
                        .garden(area.getGarden())
                        .build())
                .toList();

        AreaPageResponse areaPageResponse = new AreaPageResponse();
        areaPageResponse.setPageNumber(page);
        areaPageResponse.setPageSize(size);
        areaPageResponse.setTotalElements(entityPage.getTotalElements());
        areaPageResponse.setTotalPages(entityPage.getTotalPages());
        areaPageResponse.setAreas(areaList);

        return areaPageResponse;
    }
}
