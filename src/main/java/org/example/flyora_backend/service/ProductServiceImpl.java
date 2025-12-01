package org.example.flyora_backend.service;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

import org.example.flyora_backend.DTOs.ProductBestSellerDTO;
import org.example.flyora_backend.DTOs.ProductDetailDTO;
import org.example.flyora_backend.DTOs.ProductFilterDTO;
import org.example.flyora_backend.DTOs.ProductListDTO;
import org.example.flyora_backend.dynamo.models.FoodDetailDynamoDB;
import org.example.flyora_backend.dynamo.models.FurnitureDetailDynamoDB;
import org.example.flyora_backend.dynamo.models.ProductCategoryDynamoDB;
import org.example.flyora_backend.dynamo.models.ProductDynamoDB;
import org.example.flyora_backend.dynamo.models.ToyDetailDynamoDB;
import org.example.flyora_backend.repository.FoodDetailRepository;
import org.example.flyora_backend.repository.FurnitureDetailRepository;
import org.example.flyora_backend.repository.ProductCategoryRepository;
import org.example.flyora_backend.repository.ProductRepository;
import org.example.flyora_backend.repository.ToyDetailRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductCategoryRepository categoryRepository; // Inject thêm để lấy tên category
    private final FoodDetailRepository foodRepo;
    private final ToyDetailRepository toyRepo;
    private final FurnitureDetailRepository furnitureRepo;
    @Override
    public List<ProductListDTO> filterProducts(ProductFilterDTO filter) {
        return productRepository.filterProducts(
                filter.getName(),
                filter.getCategoryId(),
                filter.getBirdTypeId(),
                filter.getMinPrice(),
                filter.getMaxPrice());
    }

    @Override
    public List<ProductBestSellerDTO> getTop15BestSellers() {
        return productRepository.findTop15BestSellers();
    }

    @Override
    public List<ProductListDTO> searchByName(String name) {
        return productRepository.searchByName(name);
    }

    @Override
    public List<ProductListDTO> getProductByStatus() {
        // SỬA LỖI: Filter thủ công bằng Java Stream thay vì gọi hàm repo
        return productRepository.findAll().stream()
                .filter(p -> p.getStatus() == 1) // Chỉ lấy sản phẩm active
                .map(this::mapToDTO) // Hàm helper bên dưới
                .collect(Collectors.toList());
    }

    @Override
    public ProductDynamoDB addProduct(ProductDynamoDB product) {
        // SỬA LỖI: Bỏ Adapter, dùng trực tiếp DynamoDB
        productRepository.save(product);
        return product;
    }

    @Override
    public ProductDetailDTO getProductDetail(Integer id) {
        // 1. Tìm Product gốc
        ProductDynamoDB d = productRepository.findById(id)
             .orElseThrow(() -> new RuntimeException("Product not found"));

        // 2. Lấy tên Category
        String catName = categoryRepository.findById(d.getCategoryId())
                .map(ProductCategoryDynamoDB::getName).orElse("Unknown");

        // 3. LOGIC LẤY ẢNH (Mới thêm vào)
        String imageUrl = null;
        if ("FOODS".equalsIgnoreCase(catName)) {
            imageUrl = foodRepo.findByProductId(id).map(FoodDetailDynamoDB::getImageUrl).orElse(null);
        } else if ("TOYS".equalsIgnoreCase(catName)) {
            imageUrl = toyRepo.findByProductId(id).map(ToyDetailDynamoDB::getImageUrl).orElse(null);
        } else if ("FURNITURE".equalsIgnoreCase(catName)) {
            imageUrl = furnitureRepo.findByProductId(id).map(FurnitureDetailDynamoDB::getImageUrl).orElse(null);
        }

        // 4. Trả về DTO với imageUrl đã tìm được
        return new ProductDetailDTO(
                d.getId(),
                d.getName(),
                d.getDescription(),
                d.getPrice(),
                d.getStock(),
                catName,
                "Unknown", // Bạn cũng nên lấy BirdType thật nếu cần
                imageUrl   // <=== ĐÃ SỬA: Thay null bằng biến imageUrl
        );
    }

    @Override
    public Integer deleteProductById(int id) {
        // SỬA LỖI: Tên hàm chuẩn là deleteById
        productRepository.deleteById(id);
        return 1;
    }
    
    // Helper
    private ProductListDTO mapToDTO(ProductDynamoDB p) {
        ProductListDTO dto = new ProductListDTO();
        dto.setId(p.getId());
        dto.setName(p.getName());
        dto.setPrice(p.getPrice());
        dto.setStock(p.getStock());
        // Cần logic lấy ImageUrl/CategoryName ở đây nếu muốn đầy đủ
        return dto;
    }
}