package ru.practicum.ewm.categories.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.categories.dto.CategoryRequestDto;
import ru.practicum.ewm.categories.CategoryService;
import ru.practicum.ewm.categories.dto.CategoryResponseDto;

@Slf4j
@RestController
@RequestMapping("/admin/categories")
@RequiredArgsConstructor
public class AdminCategoriesController {
    private final CategoryService categoryService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryResponseDto create(@Valid @RequestBody CategoryRequestDto categoryRequestDto) {
        CategoryResponseDto category = categoryService.create(categoryRequestDto);
        log.info("Новая категория добавлена {}", category);
        return category;
    }

    @PatchMapping("/{categoriesId}")
    public CategoryResponseDto update(@PathVariable long categoriesId,
                           @Valid @RequestBody CategoryRequestDto categoryRequestDto) {
        CategoryResponseDto category = categoryService.update(categoriesId, categoryRequestDto);
        log.info("В категорию с id = {} внесены изменения", categoriesId);
        return category;
    }

    @DeleteMapping("/{categoriesId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable long categoriesId) {
        categoryService.delete(categoriesId);
        log.info("Категория с id = {} удалена", categoriesId);
    }
}
