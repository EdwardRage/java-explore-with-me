package ru.practicum.ewm.categories.controllers;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.categories.CategoryService;
import ru.practicum.ewm.categories.dto.CategoryResponseDto;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class PublicCategoriesController {
    private final CategoryService categoryService;

    @GetMapping
    public List<CategoryResponseDto> get(@RequestParam(defaultValue = "0") Integer from,
                              @RequestParam(defaultValue = "10") Integer size) {
        List<CategoryResponseDto> categoryList = categoryService.get(from, size);
        log.info("Получение категорий {}", categoryList);
        return categoryList;
    }

    @GetMapping("/{categoriesId}")
    public CategoryResponseDto getById(@PathVariable @NotNull long categoriesId) {
        CategoryResponseDto category = categoryService.getById(categoriesId);
        log.info("Получение информации категории по ее идентификатору {}", category);
        return category;
    }
}
