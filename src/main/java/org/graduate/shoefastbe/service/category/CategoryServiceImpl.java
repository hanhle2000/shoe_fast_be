package org.graduate.shoefastbe.service.category;

import lombok.AllArgsConstructor;
import org.graduate.shoefastbe.base.error_success_handle.CodeAndMessage;
import org.graduate.shoefastbe.dto.category.CategoryRequest;
import org.graduate.shoefastbe.dto.category.CategoryResponse;
import org.graduate.shoefastbe.entity.Category;
import org.graduate.shoefastbe.entity.Product;
import org.graduate.shoefastbe.mapper.CategoryMapper;
import org.graduate.shoefastbe.repository.CategoryRepository;
import org.graduate.shoefastbe.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@AllArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final ProductRepository productRepository;
    @Override
    public Page<CategoryResponse> getAllCategory(Pageable pageable) {
        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
                Sort.by(Sort.Order.asc("id")));
        Page<Category> entities = categoryRepository.findAllByIsActive(Boolean.TRUE,sortedPageable);
        return entities.map(categoryMapper::getResponseBy);
    }

    @Override
    public Page<CategoryResponse> getAllCategoryAdmin(Pageable pageable) {
        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
                Sort.by(Sort.Order.asc("id")));
        Page<Category> entities = categoryRepository.findAll(sortedPageable);
        return entities.map(categoryMapper::getResponseBy);
    }
    @Override
    public CategoryResponse create(CategoryRequest categoryRequest) {
        Category category = categoryMapper.getEntityBy(categoryRequest);
        category.setCreateDate(LocalDate.now());
        category.setModifyDate(LocalDate.now());
        categoryRepository.save(category);
        return categoryMapper.getResponseBy(category);
    }

    @Override
    @Transactional
    public CategoryResponse update(CategoryRequest categoryRequest) {
        Category cate = categoryRepository.findById(categoryRequest.getId())
                .orElseThrow(() -> new RuntimeException(CodeAndMessage.ERR3));
        if(Objects.nonNull(cate)){
            cate.setIsActive(categoryRequest.getIsActive());
            cate.setModifyDate(LocalDate.now());
            cate.setDescription(categoryRequest.getDescription());
            cate.setName(categoryRequest.getName());
            List<Product> products = productRepository.findAllProductByCategory(cate.getId());
            if(Boolean.FALSE.equals(categoryRequest.getIsActive())){
                for(Product p: products){
                    p.setIsActive(Boolean.FALSE);
                    productRepository.save(p);
                }
            }else{
                for(Product p: products){
                    p.setIsActive(Boolean.TRUE);
                    productRepository.save(p);
                }
            }
            categoryRepository.save(cate);
        }
        return categoryMapper.getResponseBy(cate);
    }

    @Override
    public CategoryResponse getDetail(Long id) {
        return categoryMapper.getResponseBy(
                categoryRepository.findById(id).orElseThrow(
                        () -> new RuntimeException(CodeAndMessage.ERR3)
                )
        );
    }
}
