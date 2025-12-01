package org.example.flyora_backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.flyora_backend.DTOs.ProductBestSellerDTO;
import org.example.flyora_backend.DTOs.ProductDetailDTO;
import org.example.flyora_backend.DTOs.ProductFilterDTO;
import org.example.flyora_backend.DTOs.ProductListDTO;
import org.example.flyora_backend.dynamo.models.ProductDynamoDB;
import org.example.flyora_backend.dynamo.response.ResponseObject;
import org.example.flyora_backend.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
@Tag(name = "Product API", description = "API liên quan đến sản phẩm: xem, thêm, xóa, lọc, tìm kiếm...")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @Operation(summary = "Lọc và lấy toàn bộ danh sách sản phẩm", description = """
                Nhận vào các điều kiện lọc trong body.
                Trả về một danh sách đầy đủ (không phân trang) tất cả các sản phẩm khớp với điều kiện.
            """)
    @PostMapping("/filter")
    public ResponseEntity<?> filterProducts(@RequestBody ProductFilterDTO request) {
        try {
            // Gọi đến phương thức đã được sửa, giờ nó trả về List
            List<ProductListDTO> productList = productService.filterProducts(request);
            return ResponseEntity.ok(productList);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Lỗi xử lý filter: " + e.getMessage());
        }
    }

    @Operation(summary = "Lấy chi tiết sản phẩm theo ID", description = "Trả về thông tin chi tiết của sản phẩm.")
    @GetMapping("/{id}")
    public ResponseEntity<ProductDetailDTO> getProductDetail(@PathVariable Integer id) {
        return ResponseEntity.ok(productService.getProductDetail(id));
    }

    @Operation(summary = "Lấy top 15 sản phẩm bán chạy", description = "Trả về top 5 sản phẩm bán chạy nhất từ mỗi danh mục (TOYS, FURNITURE, FOODS).")
    @GetMapping("/best-sellers/top15")
    public ResponseEntity<List<ProductBestSellerDTO>> getTop15BestSellers() {
        return ResponseEntity.ok(productService.getTop15BestSellers());
    }

    @Operation(summary = "Tìm sản phẩm theo tên", description = "Tìm kiếm sản phẩm theo từ khóa.")
    @GetMapping("/search")
    public ResponseEntity<List<ProductListDTO>> searchProductsByName(@RequestParam String name) {
        return ResponseEntity.ok(productService.searchByName(name));
    }

    @Operation(summary = "Lấy danh sách sản phẩm đang hoạt động", description = "Chỉ lấy các sản phẩm có status = true.")
    @GetMapping("/status")
    public ResponseEntity<ResponseObject> getProductByStatus() {
        try {
            return ResponseObject.APIResponse(200, "Get Available Product Success", HttpStatus.OK,
                    productService.getProductByStatus());
        } catch (Exception e) {
            return ResponseObject.APIResponse(400, "Get Available Product failed!", HttpStatus.BAD_REQUEST, null);
        }
    }

    @Operation(summary = "Tạo sản phẩm mới", description = "Thêm một sản phẩm vào cơ sở dữ liệu.")
    @PostMapping("")
    public ResponseEntity<ResponseObject> addProduct(@RequestBody ProductDynamoDB product) {
        try {
            return ResponseObject.APIResponse(200, "Add Product Success", HttpStatus.OK,
                    productService.addProduct(product));
        } catch (Exception e) {
            return ResponseObject.APIResponse(400, "Add Product failed!", HttpStatus.BAD_REQUEST, null);
        }
    }

    @Operation(summary = "Xoá sản phẩm theo ID", description = "Xoá một sản phẩm dựa vào ID.")
    @DeleteMapping("")
    public ResponseEntity<ResponseObject> deleteProductById(@RequestParam int id) {
        try {
            return ResponseObject.APIResponse(200, "Delete Product By ID Success", HttpStatus.OK,
                    productService.deleteProductById(id));
        } catch (Exception e) {
            return ResponseObject.APIResponse(400, "Delete Product By ID failed", HttpStatus.BAD_REQUEST, null);
        }
    }
}
