package org.graduate.shoefastbe.service.products;

import org.graduate.shoefastbe.common.IdAndName;
import org.graduate.shoefastbe.dto.product.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


public interface ProductService {
    Page<ProductDtoResponse> getAllProduct(Pageable pageable, String accessToken);
    Boolean likeProduct(Long productId, Boolean liked, String accessToken);

    Page<ProductDtoResponse> getAllProductFilter(ProductDtoRequest productDtoRequest, Pageable pageable);

    ProductDetailResponse getProductDetail(Long productId);
    Page<ProductDtoResponse> getProductRelate(Long productId, Long brandId, Pageable pageable);
    Page<ProductDtoResponse> getProductBySearch(String search, Pageable pageable);
    Page<ProductDtoResponse> getAllProductWishlist(String accessToken, Pageable pageable);
    Long countProduct();
    Page<ProductDtoResponse> getProductByBrand(Long brandId, Pageable pageable);

    //admin
    ProductDtoResponse create(CreateProductRequest createProductRequest, List<MultipartFile> multipartFiles);
    ProductDtoResponse update( CreateProductRequest createProductRequest,List<MultipartFile> multipartFiles);
    Page<IdAndName> getListHotProduct(Pageable pageable);

}
