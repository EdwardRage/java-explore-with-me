package ru.practicum.ewm.categories;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.categories.dto.CategoryRequestDto;
import ru.practicum.ewm.categories.dto.CategoryResponseDto;
import ru.practicum.ewm.exception.NotFoundException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    public CategoryResponseDto create(CategoryRequestDto categoryRequestDto) {
        Category category = categoryMapper.toCategory(categoryRequestDto);

        categoryRepository.save(category);
        return categoryMapper.toCategoryDto(category);
    }

    @Override
    public void delete(long categoriesId) {
        Category category = categoryRepository.findById(categoriesId)
                .orElseThrow(() -> new NotFoundException("Категории с id = " + categoriesId + " не найдено"));
        categoryRepository.delete(category);
    }

    @Override
    public CategoryResponseDto update(long categoriesId, CategoryRequestDto categoryRequestDto) {
        Category category = categoryRepository.findById(categoriesId)
                .orElseThrow(() -> new NotFoundException("Категории с id = " + categoriesId + " не найдено"));
        category.setName(categoryRequestDto.getName());
        categoryRepository.save(category);

        return categoryMapper.toCategoryDto(category);
    }

    @Override
    public List<CategoryResponseDto> get(Integer from, Integer size) {
        return categoryRepository.findAll().stream()
                .skip(from)
                .limit(size)
                .map(categoryMapper::toCategoryDto)
                .toList();
    }

    @Override
    public CategoryResponseDto getById(long categoriesId) {
        Category category = categoryRepository.findById(categoriesId)
                .orElseThrow(() -> new NotFoundException("Категории с id = " + categoriesId + " не найдено"));
        return categoryMapper.toCategoryDto(category);
    }
}
