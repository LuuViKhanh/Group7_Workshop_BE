package org.example.flyora_backend.service;

import java.util.List;

import org.example.flyora_backend.DTOs.CreateProductDTO;
import org.example.flyora_backend.DTOs.OwnerProductListDTO;
import org.example.flyora_backend.DTOs.TopProductDTO;
import org.example.flyora_backend.dynamo.models.ProductDynamoDB;

public interface OwnerService {
    List<TopProductDTO> getTopSellingProducts();

    public ProductDynamoDB createProduct(CreateProductDTO dto, Integer accountId);

    List<OwnerProductListDTO> getAllProductsByOwner(int accountId);

    public ProductDynamoDB updateProduct(Integer productId, CreateProductDTO dto, Integer accountId);

    public void deleteProduct(Integer productId, Integer accountId);
    
    List<OwnerProductListDTO> searchProductsByOwner(Integer accountId, String keyword);

    List<TopProductDTO> getTopSellingProductsByOwner(Integer accountId);

}
