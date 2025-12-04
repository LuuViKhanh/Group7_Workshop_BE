package org.example.flyora_backend.service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.example.flyora_backend.DTOs.CreateProductDTO;
import org.example.flyora_backend.DTOs.OwnerProductListDTO;
import org.example.flyora_backend.DTOs.TopProductDTO;
import org.example.flyora_backend.dynamo.models.*;
import org.example.flyora_backend.repository.*;
import org.springframework.stereotype.Service;
// import org.springframework.transaction.annotation.Transactional; // DynamoDB không hỗ trợ Transactional kiểu này

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class OwnerServiceImpl implements OwnerService {

    private final ShopOwnerRepository shopOwnerRepository;
    private final ProductRepository productRepository;
    private final ProductCategoryRepository categoryRepository;
    private final BirdTypeRepository birdTypeRepository;
    
    // Các repo chi tiết
    private final FoodDetailRepository foodDetailRepository;
    private final ToyDetailRepository toyDetailRepository;
    private final FurnitureDetailRepository furnitureDetailRepository;
    private final OrderItemRepository orderItemRepository;
    private final PromotionRepository promotionRepository;
    private final ProductReviewRepository productReviewRepository;

    @Override
    public List<TopProductDTO> getTopSellingProducts() {
        return productRepository.findTopSellingProductsByShopOwner();
    }

    @Override
    public ProductDynamoDB createProduct(CreateProductDTO dto, Integer accountId) {
        // 1. Validate và lấy thông tin ID
        ProductCategoryDynamoDB category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Loại sản phẩm không hợp lệ"));

        BirdTypeDynamoDB birdType = birdTypeRepository.findById(dto.getBirdTypeId())
                .orElseThrow(() -> new RuntimeException("Loại chim không hợp lệ"));

        ShopOwnerDynamoDB shopOwner = shopOwnerRepository.findByAccountId(accountId)
                .orElseThrow(() -> new RuntimeException("ShopOwner không tồn tại"));

        // 2. Tạo Product
        ProductDynamoDB product = new ProductDynamoDB();
        product.setId(productRepository.generateNewId()); // Dùng hàm generateNewId của AbstractRepo
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setStock(dto.getStock());
        product.setCategoryId(category.getId()); // Lưu ID
        product.setBirdTypeId(birdType.getId()); // Lưu ID
        product.setSalesCount(0);
        product.setStatus(1);
        product.setShopOwnerId(shopOwner.getId()); // Lưu ID

        productRepository.save(product);

        // 3. Lưu chi tiết sản phẩm (Detail)
        saveProductDetail(product.getId(), category.getName(), dto);

        return product;
    }

    @Override
    public List<OwnerProductListDTO> getAllProductsByOwner(int accountId) {
        ShopOwnerDynamoDB owner = shopOwnerRepository.findByAccountId(accountId)
                .orElseThrow(() -> new RuntimeException("ShopOwner không tồn tại"));
        return productRepository.findAllByShopOwnerIdOrderByIdAsc(owner.getId());
    }

    @Override
    public ProductDynamoDB updateProduct(Integer productId, CreateProductDTO dto, Integer accountId) {
        // 1. Lấy sản phẩm
        ProductDynamoDB product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm với ID: " + productId));

        // 2. Cập nhật thông tin cơ bản
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setStock(dto.getStock());

        // 3. Cập nhật Category và BirdType
        ProductCategoryDynamoDB category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Loại sản phẩm không hợp lệ"));
        product.setCategoryId(category.getId());
        product.setBirdTypeId(dto.getBirdTypeId());

        // 4. Cập nhật bảng chi tiết
        updateProductDetail(productId, category.getName(), dto);

        // 5. Lưu lại
        productRepository.save(product);
        return product;
    }

    @Override
    public void deleteProduct(Integer productId, Integer accountId) {
        ShopOwnerDynamoDB owner = shopOwnerRepository.findByAccountId(accountId)
                .orElseThrow(() -> new RuntimeException("ShopOwner không tồn tại"));

        ProductDynamoDB product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));

        // Kiểm tra quyền sở hữu
        if (!product.getShopOwnerId().equals(owner.getId())) {
            throw new RuntimeException("Sản phẩm không thuộc sở hữu của bạn");
        }

        // Kiểm tra xem sản phẩm đã bán chưa
        // Cần implement existsByProductId trong OrderItemRepo hoặc dùng scan filter
        Integer isSold = orderItemRepository.findAll().stream()
                .anyMatch(item -> item.getProductId().equals(productId)) ? 1 : 0;
        
        if (isSold ==1) {
            throw new RuntimeException("Không thể xóa sản phẩm đã được bán");
        }

        // Lấy tên Category để xóa bảng chi tiết tương ứng
        String categoryName = categoryRepository.findById(product.getCategoryId())
                .map(ProductCategoryDynamoDB::getName).orElse("");

        switch (categoryName.toUpperCase()) {
            case "FOODS" -> foodDetailRepository.findByProductId(productId)
                    .ifPresent(d -> foodDetailRepository.deleteById(d.getId()));
            case "TOYS" -> toyDetailRepository.findByProductId(productId)
                    .ifPresent(d -> toyDetailRepository.deleteById(d.getId()));
            case "FURNITURE" -> furnitureDetailRepository.findByProductId(productId)
                    .ifPresent(d -> furnitureDetailRepository.deleteById(d.getId()));
        }

        // Xóa các liên kết phụ (Cần implement logic delete trong các repo này)
        // inventoryRepository.deleteAllByProductId(productId); 
        promotionRepository.deleteAllByProductId(productId);
        productReviewRepository.deleteAllByProductId(productId);

        // Xóa sản phẩm
        productRepository.deleteById(product.getId());
    }

    @Override
    public List<OwnerProductListDTO> searchProductsByOwner(Integer accountId, String keyword) {
        // 1. Lấy thông tin shop owner từ accountId
        ShopOwnerDynamoDB owner = shopOwnerRepository.findByAccountId(accountId)
                .orElseThrow(() -> new RuntimeException("ShopOwner không tồn tại"));
        
        // 2. Lấy tất cả sản phẩm của shop owner này
        List<OwnerProductListDTO> allProducts = productRepository.findAllByShopOwnerIdOrderByIdAsc(owner.getId());
        
        // 3. Nếu không có keyword, trả về tất cả
        if (keyword == null || keyword.trim().isEmpty()) {
            return allProducts;
        }
        
        // 4. Filter theo keyword (tìm kiếm trong tên sản phẩm)
        String searchKeyword = keyword.toLowerCase().trim();
        return allProducts.stream()
                .filter(product -> product.getName().toLowerCase().contains(searchKeyword))
                .collect(Collectors.toList());
    }
    
    // =================================================================
    // HELPER METHODS
    // =================================================================

    private void saveProductDetail(Integer productId, String categoryName, CreateProductDTO dto) {
        switch (categoryName.toUpperCase()) {
            case "FOODS" -> {
                FoodDetailDynamoDB detail = new FoodDetailDynamoDB();
                detail.setId(foodDetailRepository.generateNewId());
                detail.setProductId(productId);
                detail.setMaterial(dto.getMaterial());
                detail.setOrigin(dto.getOrigin());
                detail.setUsageTarget(dto.getUsageTarget());
                detail.setWeight(dto.getWeight());
                detail.setImageUrl(dto.getImageUrl());
                foodDetailRepository.save(detail);
            }
            case "TOYS" -> {
                ToyDetailDynamoDB detail = new ToyDetailDynamoDB();
                detail.setId(toyDetailRepository.generateNewId());
                detail.setProductId(productId);
                detail.setMaterial(dto.getMaterial());
                detail.setOrigin(dto.getOrigin());
                detail.setColor(dto.getColor());
                detail.setDimensions(dto.getDimensions());
                detail.setWeight(dto.getWeight());
                detail.setImageUrl(dto.getImageUrl());
                toyDetailRepository.save(detail);
            }
            case "FURNITURE" -> {
                FurnitureDetailDynamoDB detail = new FurnitureDetailDynamoDB();
                detail.setId(furnitureDetailRepository.generateNewId());
                detail.setProductId(productId);
                detail.setMaterial(dto.getMaterial());
                detail.setOrigin(dto.getOrigin());
                detail.setColor(dto.getColor());
                detail.setDimensions(dto.getDimensions());
                detail.setWeight(dto.getWeight());
                detail.setImageUrl(dto.getImageUrl());
                furnitureDetailRepository.save(detail);
            }
            default -> throw new RuntimeException("Loại sản phẩm không hỗ trợ: " + categoryName);
        }
    }

    private void updateProductDetail(Integer productId, String categoryName, CreateProductDTO dto) {
        switch (categoryName.toUpperCase()) {
            case "FOODS" -> {
                FoodDetailDynamoDB detail = foodDetailRepository.findByProductId(productId)
                        .orElse(new FoodDetailDynamoDB());
                if (detail.getId() == null) {
                    detail.setId(foodDetailRepository.generateNewId());
                    detail.setProductId(productId);
                }
                detail.setMaterial(dto.getMaterial());
                detail.setOrigin(dto.getOrigin());
                detail.setUsageTarget(dto.getUsageTarget());
                detail.setWeight(dto.getWeight());
                detail.setImageUrl(dto.getImageUrl());
                foodDetailRepository.save(detail);
            }
            case "TOYS" -> {
                ToyDetailDynamoDB detail = toyDetailRepository.findByProductId(productId)
                        .orElse(new ToyDetailDynamoDB());
                if (detail.getId() == null) {
                    detail.setId(toyDetailRepository.generateNewId());
                    detail.setProductId(productId);
                }
                detail.setMaterial(dto.getMaterial());
                detail.setOrigin(dto.getOrigin());
                detail.setColor(dto.getColor());
                detail.setDimensions(dto.getDimensions());
                detail.setWeight(dto.getWeight());
                detail.setImageUrl(dto.getImageUrl());
                toyDetailRepository.save(detail);
            }
            case "FURNITURE" -> {
                FurnitureDetailDynamoDB detail = furnitureDetailRepository.findByProductId(productId)
                        .orElse(new FurnitureDetailDynamoDB());
                if (detail.getId() == null) {
                    detail.setId(furnitureDetailRepository.generateNewId());
                    detail.setProductId(productId);
                }
                detail.setMaterial(dto.getMaterial());
                detail.setOrigin(dto.getOrigin());
                detail.setColor(dto.getColor());
                detail.setDimensions(dto.getDimensions());
                detail.setWeight(dto.getWeight());
                detail.setImageUrl(dto.getImageUrl());
                furnitureDetailRepository.save(detail);
            }
        }
    }

    @Override
    public List<TopProductDTO> getTopSellingProductsByOwner(Integer accountId) {
        ShopOwnerDynamoDB owner = shopOwnerRepository.findByAccountId(accountId)
                .orElseThrow(() -> new RuntimeException("ShopOwner không tồn tại"));

        Integer shopOwnerId = owner.getId();

        // Lấy product dạng RAW (DynamoDB objects)
        List<ProductDynamoDB> products = productRepository.findRawByShopOwnerId(shopOwnerId);

        if (products.isEmpty()) return Collections.emptyList();

        List<Integer> productIds = products.stream()
                .map(ProductDynamoDB::getId)
                .collect(Collectors.toList());

        Map<Integer, Integer> salesMap = orderItemRepository.findTotalSalesForProducts(productIds);

        return products.stream()
                .map(p -> {
                    String catName = categoryRepository.findById(p.getCategoryId())
                            .map(ProductCategoryDynamoDB::getName).orElse("Unknown");
                    String imageUrl = getImageUrl(p.getId(), catName);
                    Integer totalSold = salesMap.getOrDefault(p.getId(), 0);

                    return new TopProductDTO(
                            p.getId(),
                            p.getName(),
                            imageUrl,
                            totalSold,
                            p.getPrice()
                    );
                })
                .sorted((a, b) -> b.getTotalSold() - a.getTotalSold())
                .collect(Collectors.toList());
    }


    // helper trong cùng class OwnerServiceImpl (nếu chưa có, thêm private method)
    private String getImageUrl(Integer productId, String categoryName) {
        if ("FOODS".equalsIgnoreCase(categoryName)) {
            return foodDetailRepository.findByProductId(productId).map(FoodDetailDynamoDB::getImageUrl).orElse(null);
        } else if ("TOYS".equalsIgnoreCase(categoryName)) {
            return toyDetailRepository.findByProductId(productId).map(ToyDetailDynamoDB::getImageUrl).orElse(null);
        } else if ("FURNITURE".equalsIgnoreCase(categoryName)) {
            return furnitureDetailRepository.findByProductId(productId).map(FurnitureDetailDynamoDB::getImageUrl).orElse(null);
        }
        return null;
    }

}