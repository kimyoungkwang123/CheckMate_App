package com.example.demo.controller;

import org.springframework.web.bind.annotation.*;

import com.example.demo.YourEntity;
import com.example.demo.YourEntityRepository;

import java.util.List;

@RestController
@RequestMapping("/api")
public class YourEntityController {
    private final YourEntityRepository repository;

    public YourEntityController(YourEntityRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/data")
    public List<YourEntity> fetchData() {
        return repository.findAll();
    }

    // 추가적인 API 엔드포인트 및 로직을 여기에 추가할 수 있습니다.
}
