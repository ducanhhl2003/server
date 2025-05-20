package book.service.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import book.controller.output.ProductOutput;
import book.dto.ProductDTO;
import book.dto.ProductImageDTO;
import book.dto.TopSellingProductDTO;
import book.dto.search.ProductSearchDTO;
import book.entity.CategoryEntity;
import book.entity.ProductEntity;
import book.entity.ProductImageEntity;
import book.exception.DataNotFoundException;
import book.repository.CategoryRepository;
import book.repository.OrderDetailRepository;
import book.repository.ProductImageRepository;
import book.repository.ProductRepository;
import book.service.IProductService;
import book.utils.MessageKeys;
import jakarta.transaction.Transactional;

@Service
public class ProductService implements IProductService {

	@Autowired
	private ProductRepository productRepository;
	@Autowired
	private ProductImageRepository productImageRepository;
	@Autowired
	private CategoryRepository categoryRepository;
	@Autowired
	private OrderDetailRepository orderDetailRepository;

	@Autowired
	private ModelMapper modelMapper;

	@Override
	@Transactional
	public ProductDTO save(ProductDTO productDTO) {
		ProductEntity productEntity;

		if (productDTO.getId() != null) {
			productEntity = productRepository.findById(productDTO.getId())
					.orElseThrow(() -> new DataNotFoundException(MessageKeys.PRODUCT_NOT_FOUND, productDTO.getId()));

			modelMapper.map(productDTO, productEntity);
		} else {
			productEntity = modelMapper.map(productDTO, ProductEntity.class);
		}

		// Xử lý danh mục (categories)
		if (productDTO.getCategoryName() != null && !productDTO.getCategoryName().isEmpty()) {
			CategoryEntity category = categoryRepository.findOneByName(productDTO.getCategoryName());
			if (category == null) {
				throw new DataNotFoundException(MessageKeys.CATEGORY_NOT_FOUND, productDTO.getCategoryName());
			}
			// Gán danh mục vào sản phẩm (Dùng Set thay vì List)
			productEntity.setCategories(new HashSet<>(Set.of(category)));
		} else {
			productEntity.setCategories(new HashSet<>()); // Tránh NullPointerException
		}

		// Lưu vào DB
		productEntity = productRepository.save(productEntity);

		// Map lại DTO để trả về
		ProductDTO resultDTO = modelMapper.map(productEntity, ProductDTO.class);

		// Set categoryName nếu có danh mục
		if (productEntity.getCategories() != null && !productEntity.getCategories().isEmpty()) {
			resultDTO.setCategoryName(productEntity.getCategories().stream().map(CategoryEntity::getName)
					.collect(Collectors.joining(", ")) // Ghép nhiều danh mục thành chuỗi
			);
		}

		return resultDTO;
	}

	@Override
	@Transactional
	public void delete(Integer[] ids) {
		for (Integer id : ids) {
			ProductEntity product = productRepository.findById(id)
					.orElseThrow(() -> new DataNotFoundException(MessageKeys.PRODUCT_NOT_FOUND, id));
			product.setIsDeleted(true);
			productRepository.save(product);
		}
	}

	@Override
	public List<ProductDTO> findAll(Pageable pageable) {
		return productRepository.findAll(pageable).getContent().stream()
				.map(entity -> modelMapper.map(entity, ProductDTO.class)).collect(Collectors.toList());
	}

	@Override
	public int totalItem() {
		return (int) productRepository.count();
	}

	@Override
	public Page<ProductDTO> searchProducts(ProductSearchDTO request) {
		Pageable pageable = PageRequest.of(request.getPage(), request.getSize(),
				Sort.by(Sort.Direction.fromString(request.getSortDirection()), request.getSortBy()));

		Page<ProductEntity> products = productRepository.searchProducts(request.getName(), request.getPrice(),
				request.getDescription(), request.getCategoryName(), pageable);

		return products.map(product -> {
			ProductDTO ProductDTO = modelMapper.map(product, ProductDTO.class);

			if (product.getCategories() != null && !product.getCategories().isEmpty()) {
				String categoryNames = product.getCategories().stream().map(CategoryEntity::getName)
						.collect(Collectors.joining(", "));
				String categoryIds = product.getCategories().stream().map(category -> String.valueOf(category.getId()))
						.collect(Collectors.joining(", "));
				ProductDTO.setCategoryName(categoryNames);
				ProductDTO.setCategoryId(categoryIds);
			}

			return ProductDTO;
		});
	}

	@Override
	public ProductOutput getProductList(int page, int limit, String keyword, Integer categoryId, Double minPrice,
			Double maxPrice) {
		Pageable pageable = PageRequest.of(page - 1, limit);

		Page<ProductEntity> productPage = productRepository.findByCategoryAndKeywordAndPrice(categoryId, keyword,
				minPrice, maxPrice, pageable);

		List<ProductDTO> products = productPage.getContent().stream().map(product -> {
			ProductDTO productDTO = modelMapper.map(product, ProductDTO.class);
			productDTO.setCategoryId(product.getCategories().stream().map(c -> String.valueOf(c.getId()))
					.collect(Collectors.joining(", ")));
			productDTO.setCategoryName(
					product.getCategories().stream().map(CategoryEntity::getName).collect(Collectors.joining(", ")));
			return productDTO;
		}).collect(Collectors.toList());

		ProductOutput result = new ProductOutput();
		result.setPage(page);
		result.setListResult(products);
		result.setTotalPage(productPage.getTotalPages());
		return result;
	}

//	@Override
//	public ProductOutput getProductList(int page, int limit, String keyword, int categoryId) {
//		Pageable pageable = PageRequest.of(page - 1, limit);
//		Page<ProductEntity> productPage = productRepository.findByCategoryAndKeyword(categoryId, keyword, pageable);
//		List<ProductDTO> products = productPage.getContent().stream().map(product -> {
//			ProductDTO productDTO = modelMapper.map(product, ProductDTO.class);
//			productDTO.setCategoryId(product.getCategories().stream().map(c -> String.valueOf(c.getId()))
//					.collect(Collectors.joining(", ")));
//			productDTO.setCategoryName(
//					product.getCategories().stream().map(CategoryEntity::getName).collect(Collectors.joining(", ")));
//			return productDTO;
//		}).collect(Collectors.toList());
//
//		ProductOutput result = new ProductOutput();
//		result.setPage(page);
//		result.setListResult(products);
//		result.setTotalPage(productPage.getTotalPages());
////	    List<ProductEntity> product = productRepository.findByCategory(categoryId);
////	    System.out.println("📌 Products in Category " + categoryId + ": " + products);
//		return result;
//	}

//	@Override
//	public ProductOutput getProductList(int page, int limit,String keyword,int categoryId) {
//	    Pageable pageable = PageRequest.of(page - 1, limit);
//	    Page<ProductEntity> productPage;
//
//	    // Gọi repository với cả categoryId và keyword
//	    productPage = productRepository.findByCategoryAndKeyword(categoryId, keyword, pageable);
//
//	    List<ProductDTO> products = productPage.getContent().stream().map(product -> {
//	        ProductDTO productDTO = modelMapper.map(product, ProductDTO.class);
//
//	        if (product.getCategories() != null && !product.getCategories().isEmpty()) {
//	            String categoryNames = product.getCategories().stream()
//	                    .map(CategoryEntity::getName)
//	                    .collect(Collectors.joining(", "));
//
//	            String categoryIds = product.getCategories().stream()
//	                    .map(category -> String.valueOf(category.getId()))
//	                    .collect(Collectors.joining(", "));
//
//	            productDTO.setCategoryName(categoryNames);
//	            productDTO.setCategoryId(categoryIds);
//	        }
//
//	        return productDTO;
//	    }).collect(Collectors.toList());
//
//	    ProductOutput result = new ProductOutput();
//	    result.setPage(page);
//	    result.setListResult(products);
//	    result.setTotalPage(productPage.getTotalPages());
//
//	    return result;
//	}

	@Override
	public Map<String, Object> searchProductsResponse(ProductSearchDTO request) {
		Page<ProductDTO> ProductPage = searchProducts(request);

		Map<String, Object> response = new HashMap<>();
		response.put("code", "200");
		response.put("message", "Search successful");
		response.put("data", ProductPage.getContent());
		response.put("currentPage", ProductPage.getNumber());
		response.put("totalPages", ProductPage.getTotalPages());
		response.put("totalItems", ProductPage.getTotalElements());

		return response;
	}

	public ProductImageEntity createProductImage(Integer productId, ProductImageDTO productImageDTO) {

		if (productId == null) {
			throw new IllegalArgumentException("Product not null");
		}

		ProductEntity existingProduct = productRepository.findById(productId)
				.orElseThrow(() -> new DataNotFoundException(MessageKeys.PRODUCT_NOT_FOUND, productId));

		ProductImageEntity newProductImage = ProductImageEntity.builder().product(existingProduct)
				.imageUrl(productImageDTO.getImageUrl()).build();

		return productImageRepository.save(newProductImage);
	}

//	public ProductImageEntity createProductImage(Integer productId, ProductImageDTO productImageDTO) {
//	    ProductEntity existingProduct = productRepository.findById(productImageDTO.getProductId())
//				.orElseThrow(() -> new NotFoundException("Không tìm thấy người dùng với ID: " + productImageDTO.getProductId()));
//	    ProductImageEntity newProductImage = ProductImageEntity.builder()
//	    		.product(existingProduct)
//	    		.imageUrl(productImageDTO.getImageUrl())
//	    		.build();
//	    
//	    int size = productImageRepository.findByProductId(productId).size();
//	    if(size >= 5)
//	    {
//	    	throw new NotFoundException("Number of images must be <= 5");
//	    }
//	    
//	    return productImageRepository.save(newProductImage);
//	}

	@Override
	public ProductDTO getProductById(Integer productId) {
		ProductEntity product = productRepository.findById(productId)
				.orElseThrow(() -> new DataNotFoundException(MessageKeys.PRODUCT_NOT_FOUND, productId));

		ModelMapper modelMapper = new ModelMapper();
		ProductDTO productDTO = modelMapper.map(product, ProductDTO.class);

		// Mapping thủ công cho danh sách ảnh
		List<ProductImageDTO> imageDTOs = product.getProductImages().stream()
				.map(img -> modelMapper.map(img, ProductImageDTO.class)).collect(Collectors.toList());

		productDTO.setImages(imageDTOs);
		return productDTO;
	}

	@Override
	public List<ProductDTO> getProductsByIds(List<Integer> productIds) {
		List<ProductEntity> products = productRepository.findProductsByIds(productIds);
		ModelMapper modelMapper = new ModelMapper();

		return products.stream().map(product -> {
			ProductDTO productDTO = modelMapper.map(product, ProductDTO.class);

			// Mapping danh sách ảnh
			List<ProductImageDTO> imageDTOs = product.getProductImages().stream()
					.map(img -> modelMapper.map(img, ProductImageDTO.class)).collect(Collectors.toList());

			productDTO.setImages(imageDTOs);
			return productDTO;
		}).collect(Collectors.toList());
	}

	@Override
	public void updateProductThumbnail(Integer productId, String thumbnail) {
		ProductEntity product = productRepository.findById(productId)
				.orElseThrow(() -> new DataNotFoundException("Product not found with id: " + productId));

		product.setThumbnail(thumbnail); // Cập nhật thumbnail
		productRepository.save(product); // Lưu lại sản phẩm
	}

	@Override
	public List<ProductImageDTO> getAllImagesByProductId(Integer productId) {
		List<ProductImageEntity> productImages = productImageRepository.findByProductId(productId);
		return productImages.stream().map(image -> new ProductImageDTO(image.getId(), image.getImageUrl()))
				.collect(Collectors.toList());
	}

	@Override
	public void updateThumbnail(Integer id, String newThumbnail) {
		ProductEntity product = productRepository.findById(id)
				.orElseThrow(() -> new DataNotFoundException("Product not found with id: " + id));

		product.setThumbnail(newThumbnail);
		productRepository.save(product);
	}

//	public List<TopSellingProductDTO> getTopSellingProducts(int topN) {
//	    Pageable pageable = PageRequest.of(0, topN);
//	    return orderDetailRepository.findTopSellingProducts(pageable);
//	}
	public List<TopSellingProductDTO> getTopSellingProductsByMonth(int month, int year, int limit) {
		Pageable pageable = PageRequest.of(0, limit);
		return orderDetailRepository.findTopSellingProductsByMonth(month, year, pageable);
	}

	public List<ProductDTO> getHotProducts() {
		List<ProductEntity> hotProducts = productRepository.findByIshotTrue();
		return hotProducts.stream().map(product -> modelMapper.map(product, ProductDTO.class)) // Chuyển từ Entity sang
																								// DTO
				.collect(Collectors.toList());
	}

}
