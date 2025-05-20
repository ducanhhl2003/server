package book.service;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import book.controller.output.ProductOutput;
import book.dto.ProductDTO;
import book.dto.ProductImageDTO;
import book.dto.TopSellingProductDTO;
import book.dto.search.ProductSearchDTO;
import book.entity.ProductEntity;
import book.entity.ProductImageEntity;

public interface IProductService {
	ProductDTO save(ProductDTO productDTO);

	void delete(Integer[] ids);

	List<ProductDTO> findAll(Pageable pageable);

	ProductDTO getProductById(Integer productId);

	int totalItem();

	Page<ProductDTO> searchProducts(ProductSearchDTO request);
	ProductOutput getProductList(int page, int limit, String keyword, Integer categoryId, Double minPrice,
			Double maxPrice);

//	ProductOutput getProductList(int page, int limit, String keyword, int categoryId);

	Map<String, Object> searchProductsResponse(ProductSearchDTO request);

	ProductImageEntity createProductImage(Integer productId, ProductImageDTO productImageDTO);

	List<ProductDTO> getProductsByIds(List<Integer> productIds);

	void updateProductThumbnail(Integer productId, String thumbnail);

	List<ProductImageDTO> getAllImagesByProductId(Integer productId);

	void updateThumbnail(Integer id, String newThumbnail);

//	List<TopSellingProductDTO> getTopSellingProducts(int topN);
	List<TopSellingProductDTO> getTopSellingProductsByMonth(int month, int year, int limit);
	List<ProductDTO> getHotProducts();
}
