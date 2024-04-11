package ru.practicum.explore_with_me.category.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explore_with_me.category.dto.CategoryDto;
import ru.practicum.explore_with_me.category.dto.NewCategoryDto;
import ru.practicum.explore_with_me.category.mapper.CategoryMapper;
import ru.practicum.explore_with_me.category.model.Category;
import ru.practicum.explore_with_me.category.repository.CategoryRepository;
import ru.practicum.explore_with_me.exception.NotFoundException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static ru.practicum.explore_with_me.utils.Const.CATEGORY;
import static ru.practicum.explore_with_me.utils.Const.ENTITY_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository repository;

    @Override
    public CategoryDto add(NewCategoryDto newCategoryDto) {
        Category category = repository.save(CategoryMapper.toCategory(newCategoryDto));
        return CategoryMapper.toCategoryDto(category);
    }

    @Override
    public void remove(long id) {
        if (repository.existsById(id)) repository.deleteById(id);
        else throw new NotFoundException(String.format(
                ENTITY_NOT_FOUND, CATEGORY, id
        ));
    }

    @Override
    public CategoryDto update(long id, NewCategoryDto newCategory) {
        Category category = repository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format(
                        ENTITY_NOT_FOUND, CATEGORY, id
                )));
        category.setName(newCategory.getName());
        return CategoryMapper.toCategoryDto(repository.save(category));
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryDto getById(long id) {
        Optional<Category> category = repository.findById(id);
        return CategoryMapper.toCategoryDto(category.orElseThrow(
                () -> new NotFoundException(String.format(
                        ENTITY_NOT_FOUND, CATEGORY, id
                ))));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryDto> getCategories(int from, int size) {
        Pageable pageable = PageRequest.of(from / size, size);
        return repository.findAll(pageable).stream()
                .map(CategoryMapper::toCategoryDto)
                .collect(Collectors.toList());
    }
}
