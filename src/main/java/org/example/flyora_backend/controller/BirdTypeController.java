package org.example.flyora_backend.controller;

import java.util.List;

import org.example.flyora_backend.dynamo.models.BirdTypeDynamoDB;
import org.example.flyora_backend.repository.BirdTypeRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.*;

@RestController
@RequestMapping("/api/v1/bird-types")
@Tag(name = "BirdType List")
@RequiredArgsConstructor
public class BirdTypeController {

    private final BirdTypeRepository birdTypeRepository;

    @GetMapping
    @Operation(
        summary = "Lấy danh sách loại chim",
        description = "Trả về toàn bộ danh sách BirdType trong hệ thống (id, name)."
    )
    public ResponseEntity<List<BirdTypeDynamoDB>> getAllBirdTypes() {
        return ResponseEntity.ok(birdTypeRepository.findAll());
    }
}