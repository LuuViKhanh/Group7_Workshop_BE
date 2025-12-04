package org.example.flyora_backend.repository;

import org.example.flyora_backend.DTOs.*;
import org.example.flyora_backend.dynamo.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Repository
public class ProductRepository extends AbstractDynamoRepository<ProductDynamoDB> {

    // Inject các repo phụ để "Join" dữ liệu thủ công
    @Autowired private ProductCategoryRepository categoryRepo;
    @Autowired private BirdTypeRepository birdTypeRepo;
    @Autowired private FoodDetailRepository foodRepo;
    @Autowired private ToyDetailRepository toyRepo;
    @Autowired private FurnitureDetailRepository furnitureRepo;

    public ProductRepository(DynamoDbEnhancedClient client) {
        super(client, ProductDynamoDB.class, "Product");
    }

    // =================================================================
    // 1. Filter Products (Thay thế logic SQL phức tạp)
    // =================================================================
    public List<ProductListDTO> filterProducts(String name, Integer categoryId, Integer birdTypeId, BigDecimal minPrice, BigDecimal maxPrice) {
        // Bước 1: Lấy tất cả và Filter bằng Java Stream (Vì Scan filter của DynamoDB cũng tốn chi phí tương đương)
        List<ProductDynamoDB> products = findAll().stream()
            .filter(p -> {
                if (p.getStatus() != 1) return false; // Chỉ lấy hàng active
                if (name != null && !p.getName().toLowerCase().contains(name.toLowerCase())) return false;
                if (categoryId != null && !categoryId.equals(p.getCategoryId())) return false;
                if (birdTypeId != null && !birdTypeId.equals(p.getBirdTypeId())) return false;
                if (minPrice != null && p.getPrice().compareTo(minPrice) < 0) return false;
                if (maxPrice != null && p.getPrice().compareTo(maxPrice) > 0) return false;
                return true;
            })
            .collect(Collectors.toList());

        return mapToProductListDTO(products);
    }

    // =================================================================
    // 2. Search By Name
    // =================================================================
    public List<ProductListDTO> searchByName(String name) {
        return filterProducts(name, null, null, null, null);
    }

    // =================================================================
    // 3. Find Top 15 Best Sellers
    // =================================================================
    public List<ProductBestSellerDTO> findTop15BestSellers() {
        List<ProductDynamoDB> allProducts = findAll();

        // Sort giảm dần theo salesCount
        allProducts.sort((p1, p2) -> {
            int s1 = p1.getSalesCount() != null ? p1.getSalesCount() : 0;
            int s2 = p2.getSalesCount() != null ? p2.getSalesCount() : 0;
            return Integer.compare(s2, s1);
        });

        // Lấy top 15
        List<ProductBestSellerDTO> result = new ArrayList<>();
        for (ProductDynamoDB p : allProducts.subList(0, Math.min(allProducts.size(), 15))) {
            String catName = getCategoryName(p.getCategoryId());
            String img = getImageUrl(p.getId(), catName);
            
            result.add(new ProductBestSellerDTO(
                p.getId(), p.getName(), catName, p.getPrice(), 
                p.getSalesCount() != null ? p.getSalesCount() : 0, 
                img
            ));
        }
        return result;
    }

    // =================================================================
    // 4. Top Selling By Shop Owner
    // =================================================================
    public List<TopProductDTO> findTopSellingProductsByShopOwner() {
        List<ProductDynamoDB> allProducts = findAll();
        
        // Sort theo salesCount DESC
        allProducts.sort((p1, p2) -> Integer.compare(
                p2.getSalesCount() != null ? p2.getSalesCount() : 0,
                p1.getSalesCount() != null ? p1.getSalesCount() : 0
        ));

        return allProducts.stream().map(p -> {
            String catName = getCategoryName(p.getCategoryId());
            return new TopProductDTO(
                p.getId(), p.getName(), 
                getImageUrl(p.getId(), catName), 
                p.getSalesCount() != null ? p.getSalesCount() : 0, 
                p.getPrice()
            );
        }).collect(Collectors.toList());
    }

    // =================================================================
    // 5. Find All By Shop Owner (Cho trang quản lý của chủ shop)
    // =================================================================
    public List<OwnerProductListDTO> findAllByShopOwnerIdOrderByIdAsc(int ownerId) {
        try {
            // Try GSI first
            List<ProductDynamoDB> products = table.index("shop_owner_id-index")
                    .query(QueryConditional.keyEqualTo(k -> k.partitionValue(ownerId)))
                    .stream()
                    .flatMap(p -> p.items().stream())
                    .collect(Collectors.toList());
            
            // Sort theo ID ASC (Java logic)
            products.sort(Comparator.comparingInt(ProductDynamoDB::getId));

            List<OwnerProductListDTO> dtos = new ArrayList<>();
            for (ProductDynamoDB p : products) {
                 String catName = getCategoryName(p.getCategoryId());
                 String img = getImageUrl(p.getId(), catName);
                 String status = (p.getStock() != null && p.getStock() > 0) ? "Còn hàng" : "Hết hàng";
                 
                 dtos.add(new OwnerProductListDTO(p.getId(), p.getName(), p.getPrice(), p.getStock(), status, img));
            }
            return dtos;
        } catch (Exception e) {
            // Fallback: scan all products and filter by ownerId
            List<ProductDynamoDB> allProducts = findAll();
            List<ProductDynamoDB> ownerProducts = allProducts.stream()
                    .filter(p -> p.getShopOwnerId() != null && p.getShopOwnerId().equals(ownerId))
                    .sorted(Comparator.comparingInt(ProductDynamoDB::getId))
                    .collect(Collectors.toList());
            
            List<OwnerProductListDTO> dtos = new ArrayList<>();
            for (ProductDynamoDB p : ownerProducts) {
                 String catName = getCategoryName(p.getCategoryId());
                 String img = getImageUrl(p.getId(), catName);
                 String status = (p.getStock() != null && p.getStock() > 0) ? "Còn hàng" : "Hết hàng";
                 
                 dtos.add(new OwnerProductListDTO(p.getId(), p.getName(), p.getPrice(), p.getStock(), status, img));
            }
            return dtos;
        }
    }

    // =================================================================
    // HELPER FUNCTIONS (Thay thế JOIN)
    // =================================================================
    
    private List<ProductListDTO> mapToProductListDTO(List<ProductDynamoDB> products) {
        List<ProductListDTO> dtos = new ArrayList<>();
        for (ProductDynamoDB p : products) {
            ProductListDTO dto = new ProductListDTO();
            dto.setId(p.getId());
            dto.setName(p.getName());
            dto.setPrice(p.getPrice());
            dto.setStock(p.getStock());

            String catName = getCategoryName(p.getCategoryId());
            dto.setCategory(catName);
            
            // Gọi thêm hàm lấy tên BirdType nếu cần (dto.setBirdType...)
            String birdName = birdTypeRepo.findById(p.getBirdTypeId()).map(BirdTypeDynamoDB::getName).orElse("Unknown");
            dto.setBirdType(birdName);

            dto.setImageUrl(getImageUrl(p.getId(), catName));
            dtos.add(dto);
        }
        return dtos;
    }

    private String getCategoryName(Integer catId) {
        if (catId == null) return "UNKNOWN";
        return categoryRepo.findById(catId).map(ProductCategoryDynamoDB::getName).orElse("UNKNOWN");
    }

    private String getImageUrl(Integer productId, String categoryName) {
        if ("FOODS".equalsIgnoreCase(categoryName)) {
            return foodRepo.findByProductId(productId).map(FoodDetailDynamoDB::getImageUrl).orElse(null);
        } else if ("TOYS".equalsIgnoreCase(categoryName)) {
            return toyRepo.findByProductId(productId).map(ToyDetailDynamoDB::getImageUrl).orElse(null);
        } else if ("FURNITURE".equalsIgnoreCase(categoryName)) {
            return furnitureRepo.findByProductId(productId).map(FurnitureDetailDynamoDB::getImageUrl).orElse(null);
        }
        return null;
    }

    public List<TopProductDTO> findTopSellingProductsByShopOwnerId(Integer shopOwnerId) {
        return this.findAll().stream()
                .filter(p -> p.getShopOwnerId() != null && p.getShopOwnerId().equals(shopOwnerId))
                .map(p -> {
                    String catName = getCategoryName(p.getCategoryId());
                    String img = getImageUrl(p.getId(), catName);

                    return new TopProductDTO(
                            p.getId(),
                            p.getName(),
                            img,
                            p.getSalesCount() != null ? p.getSalesCount() : 0,
                            p.getPrice()
                    );
                })
                .sorted((a, b) -> Integer.compare(b.getTotalSold(), a.getTotalSold()))
                .collect(Collectors.toList());
    }

    public List<ProductDynamoDB> findRawByShopOwnerId(Integer ownerId) {
        try {
            return table.index("shop_owner_id-index")
                    .query(QueryConditional.keyEqualTo(k -> k.partitionValue(ownerId)))
                    .stream()
                    .flatMap(p -> p.items().stream())
                    .sorted(Comparator.comparingInt(ProductDynamoDB::getId))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            return findAll().stream()
                    .filter(p -> ownerId.equals(p.getShopOwnerId()))
                    .sorted(Comparator.comparingInt(ProductDynamoDB::getId))
                    .collect(Collectors.toList());
        }
    }

}