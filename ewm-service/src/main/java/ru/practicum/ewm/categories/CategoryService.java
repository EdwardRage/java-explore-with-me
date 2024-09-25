package ru.practicum.ewm.categories;

import ru.practicum.ewm.categories.dto.CategoryRequestDto;
import ru.practicum.ewm.categories.dto.CategoryResponseDto;

import java.util.List;

public interface CategoryService {

    CategoryResponseDto create(CategoryRequestDto categoryRequestDto);

    void delete(long categoriesId);

    CategoryResponseDto update(long categoriesId, CategoryRequestDto categoryRequestDto);

    List<CategoryResponseDto> get(Integer from, Integer size);

    CategoryResponseDto getById(long categoriesId);
}
