package com.myproject.dto;

import org.springframework.core.io.Resource;

public record FileData(String contentType, Resource resource) {
}
