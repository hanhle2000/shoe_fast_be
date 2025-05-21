package org.graduate.shoefastbe.service.products;

import lombok.AllArgsConstructor;
import org.graduate.shoefastbe.base.authen.TokenHelper;
import org.graduate.shoefastbe.base.error_success_handle.CodeAndMessage;
import org.graduate.shoefastbe.common.Common;
import org.graduate.shoefastbe.common.IdAndName;
import org.graduate.shoefastbe.common.cloudinary.CloudinaryHelper;
import org.graduate.shoefastbe.dto.category.AttributeDtoRequest;
import org.graduate.shoefastbe.dto.product.*;
import org.graduate.shoefastbe.entity.*;
import org.graduate.shoefastbe.mapper.ProductMapper;
import org.graduate.shoefastbe.repository.*;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
@Transactional(readOnly = true)
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final AttributeRepository attributeRepository;
    private final CustomRepository customRepository;
    private final SalesRepository salesRepository;
    private final ImageRepository imageRepository;
    private final BrandsRepository brandsRepository;
    private final ProductMapper productMapper;
    private final ProductCategoryRepository productCategoryRepository;
    private final ProductAccountLikeMapRepository productAccountLikeMapRepository;
    private final CategoryRepository categoryRepository;
    private final CartItemRepository cartItemRepository;
    private final OrderDetailRepository orderDetailRepository;

    @Override
    public Page<ProductDtoResponse> getAllProduct(Pageable pageable, String accessToken) {
        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
                Sort.by(Sort.Order.desc("createDate"), Sort.Order.desc("id")));
        if (Boolean.TRUE.equals(TokenHelper.getUserIdFromToken(accessToken).equals(0L)) || Objects.isNull(accessToken)) {
            Page<Product> productEntities = productRepository.findAllByIsActive(Boolean.TRUE, sortedPageable);
            return getProductDtoResponses(productEntities);
        } else {
            // lấy sản phẩm của người dùng đã đăng nhập -> lấy ra các sản phẩm thích
            Long userId = TokenHelper.getUserIdFromToken(accessToken);
            Page<Product> productEntities = productRepository.findAllByIsActive(Boolean.TRUE, sortedPageable);

            Map<Long, Boolean> likeMap = productAccountLikeMapRepository.findAllByAccountId(userId)
                    .stream().collect(Collectors.toMap(ProductAccountLikeMap::getProductId, ProductAccountLikeMap::getLiked));
            Page<ProductDtoResponse> productDtoResponses = getProductDtoResponses(productEntities);
            productDtoResponses.forEach(
                    productDtoResponse -> {
                        Boolean liked = likeMap.get(productDtoResponse.getId());
                        productDtoResponse.setLiked(Objects.isNull(liked) ? Boolean.FALSE : liked);
                    }
            );
            return productDtoResponses;
        }
    }

    public Page<ProductDtoResponse> getAllProductForBrand(Pageable pageable, String accessToken) {
        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
                Sort.by(Sort.Order.desc("createDate"), Sort.Order.desc("id")));
        if (Boolean.TRUE.equals(TokenHelper.getUserIdFromToken(accessToken).equals(0L)) || Objects.isNull(accessToken)) {
            Page<Product> productEntities = productRepository.findAll(sortedPageable);
            return getProductDtoResponses(productEntities);
        } else {
            Long userId = TokenHelper.getUserIdFromToken(accessToken);
            Page<Product> productEntities = productRepository.findAll(sortedPageable);

            Map<Long, Boolean> likeMap = productAccountLikeMapRepository.findAllByAccountId(userId)
                    .stream().collect(Collectors.toMap(ProductAccountLikeMap::getProductId, ProductAccountLikeMap::getLiked));
            Page<ProductDtoResponse> productDtoResponses = getProductDtoResponses(productEntities);
            productDtoResponses.forEach(
                    productDtoResponse -> {
                        Boolean liked = likeMap.get(productDtoResponse.getId());
                        productDtoResponse.setLiked(Objects.isNull(liked) ? Boolean.FALSE : liked);
                    }
            );
            return productDtoResponses;
        }
    }

    @Override
    @Transactional
    public Boolean likeProduct(Long productId, Boolean liked, String accessToken) {
        Long userId = TokenHelper.getUserIdFromToken(accessToken);
        if (Objects.isNull(userId)) throw new RuntimeException(CodeAndMessage.ERR10);
        ProductAccountLikeMap productAccountLikeMap = productAccountLikeMapRepository.findByProductIdAndAccountId(
                productId, userId
        );
        Product product = productRepository.findById(productId).orElseThrow(
                () -> new RuntimeException(CodeAndMessage.ERR3)
        );
        if (Objects.nonNull(productAccountLikeMap)) {
            if (Boolean.TRUE.equals(liked)) {
                productAccountLikeMap.setLiked(liked);
                productAccountLikeMapRepository.save(productAccountLikeMap);
                product.setView(product.getView() + 1);
                productRepository.save(product);
            } else {
                productAccountLikeMap.setLiked(liked);
                productAccountLikeMapRepository.save(productAccountLikeMap);
                product.setView(product.getView() - 1);
                productRepository.save(product);
            }
        } else {
            productAccountLikeMap = ProductAccountLikeMap.builder()
                    .accountId(userId)
                    .productId(productId)
                    .liked(liked)
                    .build();
            productAccountLikeMapRepository.save(productAccountLikeMap);
            product.setView(product.getView() + 1);
            productRepository.save(product);
        }
        return productAccountLikeMap.getLiked();
    }

    @Override
    public Page<ProductDtoResponse> getAllProductFilter(ProductDtoRequest productDtoRequest, Pageable pageable) {
        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
                Sort.by(Sort.Order.desc("createDate"), Sort.Order.desc("id")));
        Page<Attribute> attributeEntities = customRepository.getAttributeFilter(productDtoRequest.getBrandIds(),
                productDtoRequest.getCategoryIds(), productDtoRequest.getMin(), productDtoRequest.getMax(), sortedPageable);
        Map<Long, Product> longProductEntityMap = productRepository.findAllByIdIn(attributeEntities.stream()
                        .map(Attribute::getProductId).collect(Collectors.toSet()))
                .stream()
                .collect(Collectors.toMap(
                        Product::getId, Function.identity()
                ));
        Map<Long, Attribute> attributeMap = attributeEntities.stream().collect(Collectors.toMap(
                Attribute::getProductId, Function.identity()
        ));
        Map<Long, Image> imageMap = imageRepository.findAllByProductIdIn(longProductEntityMap.values()
                        .stream().map(Product::getId).collect(Collectors.toList()))
                .stream().filter(image -> image.getName().equals("main"))
                .collect(Collectors.toMap(Image::getProductId, Function.identity()));

        List<Brands> brandsEntities = brandsRepository.findAllByIdIn(longProductEntityMap.values()
                .stream()
                .map(Product::getBrandId)
                .collect(Collectors.toSet()));
        Map<Long, Brands> brandsEntityMap = brandsEntities.stream().collect(Collectors.toMap(
                Brands::getId, Function.identity()
        ));

        List<Sales> salesEntities = salesRepository.findAllByIdIn(longProductEntityMap.values()
                .stream()
                .map(Product::getSaleId)
                .collect(Collectors.toSet()));
        Map<Long, Sales> salesEntityMap = salesEntities.stream().collect(Collectors.toMap(
                Sales::getId, Function.identity()
        ));

        return attributeEntities.map(
                attribute -> {
                    Product product = longProductEntityMap.get(attribute.getProductId());
                    return ProductDtoResponse.builder()
                            .id(product.getId())
                            .name(product.getName())
                            .price(attribute.getPrice())
                            .brand(brandsEntityMap.get(product.getBrandId()).getName())
                            .code(product.getCode())
                            .view(product.getView())
                            .description(product.getDescription())
                            .image(imageMap.get(product.getId()).getImageLink())
                            .discount(salesEntityMap.get(product.getSaleId()).getDiscount())
                            .isActive(product.getIsActive())
                            .build();
                }
        );
    }

    @Override
    public ProductDetailResponse getProductDetail(Long productId) {
        Product product = productRepository.findById(productId).orElseThrow(
                () -> new RuntimeException(CodeAndMessage.ERR3)
        );
        List<Attribute> attributeEntities = attributeRepository.findAllByProductId(productId);
        Double price = (double) 0;
        for (Attribute attribute : attributeEntities) {
            if (attribute.getSize().equals(Common.SIZE_AVG)) {
                price = attribute.getPrice();
            }
        }
        List<ProductCategory> categoryEntities = productCategoryRepository.findAllByProductId(productId);
        List<Category> categories = categoryRepository.findAllByIdIn(categoryEntities.stream().map(ProductCategory::getCategoryId).collect(Collectors.toList()));
        List<IdAndName> categoryIdName = categories.stream().map(category -> IdAndName.builder().id(category.getId()).name(category.getName()).build()).collect(Collectors.toList());
        Brands brands = brandsRepository.findById(product.getBrandId()).orElseThrow(
                () -> new RuntimeException(CodeAndMessage.ERR3)
        );
        Sales sales = salesRepository.findById(product.getSaleId()).orElseThrow(
                () -> new RuntimeException(CodeAndMessage.ERR3)
        );
        List<Image> images = imageRepository.findAllByProductId(productId);
        List<String> imgURLs = new ArrayList<>();
        for (Image image : images) {
            imgURLs.add(image.getImageLink());
        }
        return ProductDetailResponse.builder()
                .attributes(attributeEntities)
                .main(images.stream().filter(image -> image.getName().equals("main")).collect(Collectors.toList()).get(0).getImageLink())
                .price(price)
                .brandId(product.getBrandId())
                .categoryIds(categoryEntities.stream().map(ProductCategory::getCategoryId).collect(Collectors.toList()))
                .images(imgURLs)
                .saleId(product.getSaleId())
                .brand(brands.getName())
                .categories(categoryIdName)
                .code(product.getCode())
                .description(product.getDescription())
                .discount(sales.getDiscount())
                .id(productId)
                .name(product.getName())
                .view(product.getView())
                .isActive(product.getIsActive())
                .build();
    }

    @Override
    public Page<ProductDtoResponse> getProductRelate(Long productId, Long brandId, Pageable pageable) {
        Page<Product> productEntities = customRepository.getProductRelate(productId, brandId, pageable);
        return getProductDtoResponses(productEntities);
    }

    @Override
    public Page<ProductDtoResponse> getProductBySearch(String search, Pageable pageable) {
        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
                Sort.by(Sort.Order.desc("createDate"), Sort.Order.desc("id")));
        Page<Product> productEntities = productRepository.getProductBySearch(search, sortedPageable);
        return getProductDtoResponses(productEntities);
    }

    @Override
    public Page<ProductDtoResponse> getAllProductWishlist(String accessToken, Pageable pageable) {
        if (Boolean.TRUE.equals(TokenHelper.getUserIdFromToken(accessToken).equals(0L)) || Objects.isNull(accessToken)) {
            throw new RuntimeException(CodeAndMessage.ERR10);
        }
        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
                Sort.by(Sort.Order.desc("createDate"), Sort.Order.desc("id")));
        Long userId = TokenHelper.getUserIdFromToken(accessToken);
        List<ProductAccountLikeMap> productAccountLikeMaps = productAccountLikeMapRepository.findAllByAccountIdAndLiked(userId, Boolean.TRUE);
        Page<Product> products = productRepository.findAllByIdIn(productAccountLikeMaps.stream()
                .map(ProductAccountLikeMap::getProductId).collect(Collectors.toList()), sortedPageable);
        List<Product> productList = new ArrayList<>();
        for (Product p : products) {
            if (p.getIsActive().equals(Boolean.TRUE)) {
                productList.add(p);
            }
        }
        Page<Product> productPageFilterPage = new PageImpl<>(productList, pageable, productList.size());
        return getProductDtoResponses(productPageFilterPage);
    }

    @Override
    public Long countProduct() {
        return productRepository.count();
    }

    @Override
    public Page<ProductDtoResponse> getProductByBrand(Long brandId, Pageable pageable) {
        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
                Sort.by(Sort.Order.desc("createDate"), Sort.Order.desc("id")));
        if (brandId == 0) {
            return getAllProductForBrand(pageable, null);
        } else {
            Page<Product> productEntities = productRepository.findAllByBrandId(brandId, sortedPageable);
            return getProductDtoResponses(productEntities);
        }
    }


    @Override
    @Transactional
    public ProductDtoResponse create(CreateProductRequest createProductRequest, List<MultipartFile> multipartFiles) {
        List<AttributeDtoRequest> attributes = createProductRequest.getAttribute();
        for (AttributeDtoRequest attributeDtoRequest : attributes) {
            if (attributeDtoRequest.getStock().equals(1L)) {
                throw new RuntimeException(CodeAndMessage.ERR13);
            }
        }
        if (Boolean.TRUE.equals(productRepository.existsByNameOrCode(createProductRequest.getName().trim(), createProductRequest.getCode().trim()))) {
            throw new RuntimeException(CodeAndMessage.ERR11);
        }
        Product product = productRepository.findByCode(createProductRequest.getCode());
        if (Objects.nonNull(product)) {
            throw new RuntimeException(CodeAndMessage.ERR11);
        }
        /*Create product from data*/
        Product productEntity = Product.builder()
                .createDate(LocalDate.now())
                .modifyDate(LocalDate.now())
                .name(createProductRequest.getName())
                .code(createProductRequest.getCode())
                .description(createProductRequest.getDescription())
                .view(1L)
                .brandId(createProductRequest.getBrandId())
                .saleId(createProductRequest.getSaleId())
                .isActive(Boolean.TRUE)
                .build();
        productRepository.save(productEntity);

        List<Long> categoryIds = createProductRequest.getCategoryId();
        for (Long id : categoryIds) {
            ProductCategory productCategory = ProductCategory.builder()
                    .categoryId(id)
                    .productId(productEntity.getId())
                    .build();
            productCategoryRepository.save(productCategory);

        }
        /*Create image of product*/
        List<String> imageUrl = getImageUrls(multipartFiles);
        for (int i = 0; i < imageUrl.size(); i++) {
            Image image = new Image();
            if (i == 0) {
                image.setName("main");
            } else {
                image.setName("other");
            }
            image.setImageLink(imageUrl.get(i));
            image.setCreateDate(LocalDate.now());
            image.setModifyDate(LocalDate.now());
            image.setIsActive(Boolean.TRUE);
            image.setProductId(productEntity.getId());
            imageRepository.save(image);
        }
        /*Create attribute of product*/
        List<AttributeDtoRequest> reqAttributeDtos = createProductRequest.getAttribute();
        for (AttributeDtoRequest r : reqAttributeDtos) {
            Attribute attribute = new Attribute();
            attribute.setName(productEntity.getName());
            attribute.setSize(r.getSize());
            attribute.setPrice(r.getPrice());
            attribute.setStock(r.getStock());
            attribute.setCache(0L);
            attribute.setCreateDate(LocalDate.now());
            attribute.setModifyDate(LocalDate.now());
            attribute.setProductId(productEntity.getId());
            attributeRepository.save(attribute);
        }
        return productMapper.getResponseFromEntity(productEntity);
    }

    private List<String> getImageUrls(List<MultipartFile> multipartFiles) {
        if (Objects.isNull(multipartFiles) || multipartFiles.isEmpty()) {
            return new ArrayList<>();
        }
        List<String> imageUrls = new ArrayList<>();
        for (MultipartFile multipartFile : multipartFiles) {
            imageUrls.add(CloudinaryHelper.uploadAndGetFileUrl(multipartFile));
        }
        return imageUrls;
    }

    @Override
    @Transactional
    public ProductDtoResponse update(CreateProductRequest createProductRequest, List<MultipartFile> multipartFiles) {
        List<AttributeDtoRequest> attributes = createProductRequest.getAttribute();
        for (AttributeDtoRequest attributeDtoRequest : attributes) {
            if (attributeDtoRequest.getStock().equals(1L)) {
                throw new RuntimeException(CodeAndMessage.ERR13);
            }
        }
        Product productEntity = productRepository.findById(createProductRequest.getId()).orElseThrow(
                () -> new RuntimeException(CodeAndMessage.ERR3)
        );
        if (Boolean.TRUE.equals(productRepository.existsByNameOrCode(createProductRequest.getName().trim(), createProductRequest.getCode().trim())) &&
                !createProductRequest.getCode().equals(productEntity.getCode())) {
            throw new RuntimeException(CodeAndMessage.ERR11);
        }
        List<Image> images = imageRepository.findAllByProductId(productEntity.getId());
        if (Objects.nonNull(images)) {
            imageRepository.deleteAll(images);
        }
        List<String> imageUrl = getImageUrls(multipartFiles);
        for (int i = 0; i < imageUrl.size(); i++) {
            Image image = new Image();
            if (i == 0) {
                image.setName("main");
            } else {
                image.setName("other");
            }
            image.setImageLink(imageUrl.get(i));
            image.setCreateDate(LocalDate.now());
            image.setModifyDate(LocalDate.now());
            image.setIsActive(Boolean.TRUE);
            image.setProductId(productEntity.getId());
            imageRepository.save(image);
        }
        productMapper.update(productEntity, createProductRequest);
        productEntity.setView(1L);
        productEntity.setIsActive(createProductRequest.getIsActive());
        productRepository.save(productEntity);

        List<Long> categoryIds = createProductRequest.getCategoryId();
        for (Long id : categoryIds) {
            ProductCategory productCategory = productCategoryRepository.findByProductIdAndCategoryId(productEntity.getId(),id);
            productCategory.setProductId(productEntity.getId());
            productCategory.setCategoryId(id);
            productCategoryRepository.save(productCategory);

        }
        /*Create attribute of product*/
        List<AttributeDtoRequest> reqAttributeDtos = createProductRequest.getAttribute();
        List<Attribute> attributeList = attributeRepository.findAllByProductId(productEntity.getId());
        List<Long> attributeIds = new ArrayList<>();
        if(Objects.nonNull(attributeList) && !attributeList.isEmpty()){
            attributeIds = attributeList.stream().map(Attribute::getId).collect(Collectors.toList());
        }
        List<CartItem> cartItems = cartItemRepository.findAllByAttributeIdIn(attributeIds);
        List<OrderDetail> orderDetails = orderDetailRepository.findAllByAttributeIdIn(attributeIds);
        if(!cartItems.isEmpty() || !orderDetails.isEmpty()){
            throw  new RuntimeException(CodeAndMessage.ERR15);
        }
        attributeRepository.deleteAll(attributeList);
        for (AttributeDtoRequest r : reqAttributeDtos) {
            Attribute attribute = new Attribute();
            attribute.setName(productEntity.getName());
            attribute.setSize(r.getSize());
            attribute.setPrice(r.getPrice());
            attribute.setStock(r.getStock());
            attribute.setCache(0L);
            attribute.setCreateDate(LocalDate.now());
            attribute.setModifyDate(LocalDate.now());
            attribute.setProductId(productEntity.getId());
            attributeRepository.save(attribute);
        }
        return productMapper.getResponseFromEntity(productEntity);
    }

    @Override
    public Page<IdAndName> getListHotProduct(Pageable pageable) {
        List<Attribute> attributeList = attributeRepository.findAll().stream()
                .sorted(Comparator.comparing(Attribute::getCache).reversed())
                .collect(Collectors.toList());
        Page<Attribute> productListHot = new PageImpl<>(attributeList, pageable, attributeList.size());
        return productListHot.map(
                attribute -> IdAndName.builder()
                        .id(attribute.getId())
                        .name(attribute.getName())
                        .size(attribute.getSize())
                        .number(attribute.getCache())
                        .build()
        );
    }

    private Page<ProductDtoResponse> getProductDtoResponses(Page<Product> productEntities) {
        List<Attribute> attributeEntities = customRepository.getAttributeByProductId(productEntities
                .stream().map(Product::getId).collect(Collectors.toSet()));

        Map<Long, Attribute> attributeMap = attributeEntities.stream().collect(Collectors.toMap(
                Attribute::getProductId, Function.identity()
        ));
        List<Brands> brandsEntities = brandsRepository.findAllByIdIn(productEntities
                .stream()
                .map(Product::getBrandId)
                .collect(Collectors.toSet()));
        Map<Long, Brands> brandsEntityMap = brandsEntities.stream().collect(Collectors.toMap(
                Brands::getId, Function.identity()
        ));

        List<Sales> salesEntities = salesRepository.findAllByIdIn(productEntities
                .stream()
                .map(Product::getSaleId)
                .collect(Collectors.toSet()));
        Map<Long, Sales> salesEntityMap = salesEntities.stream().collect(Collectors.toMap(
                Sales::getId, Function.identity()
        ));
        Map<Long, Image> imageMap = imageRepository.findAllByProductIdIn(productEntities
                        .stream().map(Product::getId).collect(Collectors.toList()))
                .stream().filter(image -> image.getName().equals("main"))
                .collect(Collectors.toMap(Image::getProductId, Function.identity()));

        return productEntities.map(
                product -> {
                    Attribute attribute = attributeMap.get(product.getId());
                    return ProductDtoResponse.builder()
                            .id(product.getId())
                            .name(product.getName())
                            .price(Objects.nonNull(attribute)? attribute.getPrice() : 0L)
                            .brand(brandsEntityMap.get(product.getBrandId()).getName())
                            .code(product.getCode())
                            .view(product.getView())
                            .description(product.getDescription())
                            .image(imageMap.get(product.getId()).getImageLink())
                            .discount(salesEntityMap.get(product.getSaleId()).getDiscount())
                            .isActive(product.getIsActive())
                            .liked(Boolean.FALSE)
                            .build();
                }
        );
    }

}
