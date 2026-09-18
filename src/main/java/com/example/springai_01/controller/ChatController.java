package com.example.springai_01.controller;

import com.example.springai_01.config.CommanConfig;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ChatController {
    @Resource
    private CommanConfig commanConfig;


}
