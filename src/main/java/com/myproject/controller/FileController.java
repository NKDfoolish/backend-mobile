package com.myproject.controller;

import com.myproject.service.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j(topic = "FILE_CONTROLLER")
public class FileController {

    private final FileService fileService;


}
