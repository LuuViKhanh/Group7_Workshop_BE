package org.example.flyora_backend.service;

import java.util.List;
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
    public List<OwnerProductListDTO> searchProductsByOwner(String keyword) {
        // Logic tìm kiếm: Lấy tất cả của Owner rồi filter bằng Java (hoặc dùng Scan Filter Expression)
        // Ở đây giả sử lấy list DTO và filter trên memory
        // Lưu ý: method này cần userId để filter đúng owner, nhưng interface bạn đưa chỉ có keyword.
        // Tôi sẽ tạm thời trả về rỗng hoặc bạn cần sửa Interface để truyền thêm ownerId.
        return List.of(); 
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
}