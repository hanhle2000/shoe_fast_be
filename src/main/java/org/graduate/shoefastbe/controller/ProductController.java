package org.graduate.shoefastbe.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import lombok.*;
import org.graduate.shoefastbe.common.IdAndName;
import org.graduate.shoefastbe.dto.brands.BrandResponse;
import org.graduate.shoefastbe.dto.category.AttributeDtoRequest;
import org.graduate.shoefastbe.dto.product.CreateProductRequest;
import org.graduate.shoefastbe.dto.product.ProductDetailResponse;
import org.graduate.shoefastbe.dto.product.ProductDtoRequest;
import org.graduate.shoefastbe.dto.product.ProductDtoResponse;
import org.graduate.shoefastbe.entity.Product;
import org.graduate.shoefastbe.repository.ProductRepository;
import org.graduate.shoefastbe.service.RecommendationService;
import org.graduate.shoefastbe.service.products.ProductService;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/product")
@CrossOrigin("*")
@AllArgsConstructor
public class ProductController {
    private final ProductService productService;
    private final RecommendationService recommendationService;

    @GetMapping("/get-all")
    @Operation(summary = "Lấy page product")
    Page<ProductDtoResponse> getAllProduct(@ParameterObject Pageable pageable,@RequestHeader(value = "Authorization", required = false) String accessToken) {
        return productService.getAllProduct(pageable,accessToken);
    }

    @PutMapping("/like")
    @Operation(summary = "Lấy page product")
    Boolean likeProduct(@RequestParam Long productId,
                        @RequestParam Boolean liked,
                        @RequestHeader("Authorization") String accessToken) {

        return productService.likeProduct(productId, liked, accessToken);
    }

    @PostMapping("/get-all/filter")
    @Operation(summary = "Lấy page product filter")
    Page<ProductDtoResponse> getAllProductFilter(@RequestBody ProductDtoRequest productDtoRequest,
                                                 @ParameterObject Pageable pageable) {
        return productService.getAllProductFilter(productDtoRequest, pageable);
    }

    @GetMapping()
    @Operation(summary = "Lấy chi tiết product")
    ProductDetailResponse getDetailProduct(@RequestParam Long id) {
        return productService.getProductDetail(id);
    }

    @GetMapping("/relate")
    @Operation(summary = "Lấy các sản phẩm liên quan")
    Page<ProductDtoResponse> getRelateProduct(@RequestParam Long id, @RequestParam Long brandId,
                                              @ParameterObject Pageable pageable) {
        return productService.getProductRelate(id, brandId, pageable);
    }

    @GetMapping("/search")
    @Operation(summary = "Lấy các sản phẩm theo search")
    Page<ProductDtoResponse> getProductBySearch(@RequestParam String search,
                                                @ParameterObject Pageable pageable) {
        return productService.getProductBySearch(search, pageable);
    }
    //admin

    @GetMapping("/count")
    @Operation(summary = "Lấy số lượng product")
    Long countByProduct() {
        return productService.countProduct();
    }

    @GetMapping("/by-brand")
    Page<ProductDtoResponse> getAllProductByBrand(@RequestParam Long brandId,
                                                  @ParameterObject Pageable pageable) {
        return productService.getProductByBrand(brandId, pageable);
    }

    @GetMapping("/wish-list")
    Page<ProductDtoResponse> getAllProductWishlist(@RequestHeader(value = "Authorization", required = false) String accessToken,
                                                  @ParameterObject Pageable pageable) {
        return productService.getAllProductWishlist(accessToken, pageable);
    }


    @PostMapping("/create")
    public ProductDtoResponse create(@ModelAttribute CreateProductRequest createProductRequest,
                                     @RequestPart("files") List<MultipartFile> multipartFileList) throws IOException {
        return productService.create(createProductRequest, multipartFileList);
    }


    @PutMapping("/modify")
    public ProductDtoResponse update(@ModelAttribute CreateProductRequest createProductRequest,
                              @RequestPart("files") List<MultipartFile> multipartFileList) throws IOException {
        return productService.update(createProductRequest,multipartFileList);
    }
    @GetMapping("/recommendation")
    public Page<ProductDtoResponse> getRecommendations(@RequestParam("id") Long productId,
                                                       @ParameterObject Pageable pageable) throws IOException {
        return recommendationService.getRecommendations(productId,pageable);
    }
//    @GetMapping("/list/hot")
//    @Operation(summary = "Lấy sản pham bn chay nhat")
//    public Page<IdAndName> getListHotProduct(@ParameterObject Pageable pageable){
//        return productService.getListHotProduct(pageable);
//    }
}
