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
import ru.practicum.explore_with_me.event.repository.EventRepository;
import ru.practicum.explore_with_me.exception.ConflictException;
import ru.practicum.explore_with_me.exception.NotFoundException;

import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.explore_with_me.utils.Const.CATEGORY;
import static ru.practicum.explore_with_me.utils.Const.ENTITY_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository repository;
    private final EventRepository eventRepository;

    @Override
    public CategoryDto add(NewCategoryDto newCategoryDto) {
        Category category = repository.save(CategoryMapper.toCategory(newCategoryDto));
        return CategoryMapper.toCategoryDto(category);
    }

    @Override
    public void remove(long id) {
        if (repository.existsById(id)) {
            if (eventRepository.existsByCategoryId(id)) {
                throw new ConflictException(String.format(
                        "%s is not empty", CATEGORY
                ));
            } else {
                repository.deleteById(id);
            }
        } else {
            throw new NotFoundException(String.format(
                    ENTITY_NOT_FOUND, CATEGORY, id
            ));
        }
    }

    @Override
    public CategoryDto update(long id, NewCategoryDto newCategory) {
        Category savedCategory = getCategoryIfExists(id);
        savedCategory.setName(newCategory.getName());
        return CategoryMapper.toCategoryDto(repository.save(savedCategory));
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryDto getById(long id) {
        return CategoryMapper.toCategoryDto(getCategoryIfExists(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryDto> getCategories(int from, int size) {
        Pageable pageable = PageRequest.of(from / size, size);
        return repository.findAll(pageable).stream()
                .map(CategoryMapper::toCategoryDto)
                .collect(Collectors.toList());
    }

    private Category getCategoryIfExists(long catId) {
        return repository.findById(catId)
                .orElseThrow(() -> new NotFoundException(String.format(
                        ENTITY_NOT_FOUND, CATEGORY, catId
                )));
    }
}
