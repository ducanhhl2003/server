package book.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import book.controller.output.ProductOutput;
import book.dto.ProductDTO;
import book.dto.ProductImageDTO;
import book.dto.TopSellingProductDTO;
import book.dto.search.ProductSearchDTO;
import book.entity.ProductEntity;
import book.entity.ProductImageEntity;
import book.service.IProductService;
import jakarta.validation.Valid;

@CrossOrigin
@RestController
@RequestMapping("/product")
public class ProductController {
	@Autowired
	private IProductService productService;

	@GetMapping("/{productId}")
	public ResponseEntity<?> getProductById(@PathVariable Integer productId) {
		ProductDTO product = productService.getProductById(productId);
		return ResponseEntity.ok(product);
	}

	@GetMapping
	public ResponseEntity<ProductOutput> showProduct(@RequestParam("page") int page, @RequestParam("limit") int limit,
			@RequestParam(required = false) Integer categoryId,
			@RequestParam(value = "keyword", required = false) String keyword,
			@RequestParam(value = "minPrice", required = false) Double minPrice,
			@RequestParam(value = "maxPrice", required = false) Double maxPrice) { // Thêm tham số keyword

		ProductOutput result = productService.getProductList(page, limit, keyword, categoryId,minPrice,maxPrice);

		return ResponseEntity.ok(result);
	}

	@PostMapping("/search")
	public ResponseEntity<Map<String, Object>> searchProducts(@RequestBody ProductSearchDTO searchRequest) {
		return ResponseEntity.ok(productService.searchProductsResponse(searchRequest));

	}

	@GetMapping("/hot")
	public List<ProductDTO> getHotProducts() {
		return productService.getHotProducts(); // Trả về các sản phẩm có ishot = true
	}

	@PostMapping
	public ResponseEntity<ProductDTO> createNew(@Valid @RequestBody ProductDTO model) {
		return ResponseEntity.ok(productService.save(model));
	}

//	@PostMapping(value = "uploads/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//	public ResponseEntity<?> uploadImages(@PathVariable("id") Integer productId, 
//	                                      @RequestParam("files") List<MultipartFile> files) {
//	    try {
//	        if (productId == null) {
//	            return ResponseEntity.badRequest().body("Product ID must not be null");
//	        }
//	        System.out.println("✅ Received request to upload images for product ID: " + productId);
//	        ProductEntity existingProduct = productService.getProductById(productId);
//	        System.out.println("✅ Product found: " + existingProduct.getName());
//
//	        // Kiểm tra danh sách file
//	        if (files == null || files.isEmpty()) {
//	            return ResponseEntity.badRequest().body("No files provided");
//	        }
//
//	        List<ProductImageEntity> productImages = new ArrayList<>();
//	        for (MultipartFile file : files) {
//	            if (file.isEmpty()) continue;
//	            String filename = storeFile(file);
//	            ProductImageEntity productImage = productService.createProductImage(productId, 
//	                ProductImageDTO.builder().imageUrl(filename).build());
//	            productImages.add(productImage);
//	        }
//	        return ResponseEntity.ok().body(productImages);
//	    } catch (Exception e) {
//	        return ResponseEntity.badRequest().body("Error: " + e.getMessage());
//	    }
//	}
	@PostMapping(value = "uploads/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<?> uploadImages(@PathVariable("id") Integer productId,
			@RequestParam("files") List<MultipartFile> files) {
		try {
			// Kiểm tra sản phẩm tồn tại
			ProductDTO existingProduct = productService.getProductById(productId);
			if (existingProduct == null) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found!");
			}

			if (files == null || files.isEmpty()) {
				return ResponseEntity.badRequest().body("No files uploaded!");
			}

			List<String> imageUrls = new ArrayList<>();
			for (MultipartFile file : files) {
				if (file.getSize() > 10 * 1024 * 1024) {
					return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE)
							.body("File is too large! Maximum size is 10MB");
				}
				if (!file.getContentType().startsWith("image/")) {
					return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body("File must be an image");
				}

				String filename = storeFile(file);
				imageUrls.add(filename);
			}
			List<ProductImageEntity> productImages = new ArrayList<>();
			for (MultipartFile file : files) {
				if (file.isEmpty())
					continue;
				String filename = storeFile(file);
				ProductImageEntity productImage = productService.createProductImage(productId,
						ProductImageDTO.builder().imageUrl(filename).build());
				productImages.add(productImage);
			}

			// Cập nhật thumbnail cho product (chỉ lấy ảnh đầu tiên)
			if (!imageUrls.isEmpty()) {
				productService.updateProductThumbnail(productId, imageUrls.get(0));
			}

			return ResponseEntity.ok(Collections.singletonMap("thumbnail", imageUrls.get(0)));
		} catch (Exception e) {
			return ResponseEntity.badRequest().body("Error: " + e.getMessage());
		}
	}

//	@PostMapping(value = "uploads/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//	public ResponseEntity<?> uploadImages(@PathVariable("id") Integer productId,
//	                                      @RequestParam("files") List<MultipartFile> files) {
//	    try {
//	        // Kiểm tra product có tồn tại hay không
//	        ProductDTO existingProduct = productService.getProductById(productId);
//	        if (existingProduct == null) {
//	            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found!");
//	        }
//
//	        // Kiểm tra danh sách file
//	        if (files == null || files.isEmpty()) {
//	            return ResponseEntity.badRequest().body("No files uploaded!");
//	        }
//
//	        List<ProductImageDTO> uploadedImages = new ArrayList<>();
//
//	        for (MultipartFile file : files) {
//	            if (file.getSize() == 0) {
//	                continue;
//	            }
//	            if (file.getSize() > 10 * 1024 * 1024) {
//	                return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE)
//	                        .body("File is too large! Maximum size is 10MB");
//	            }
//	            String contentType = file.getContentType();
//	            if (contentType == null || !contentType.startsWith("image/")) {
//	                return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
//	                        .body("File must be an image");
//	            }
//
//	            // Lưu file và tạo ProductImageEntity
//	            String filename = storeFile(file);
//	            ProductImageEntity productImage = productService.createProductImage(
//	                    existingProduct.getId(),
//	                    ProductImageDTO.builder().imageUrl(filename).build()
//	            );
//	            
//
//	            // Chuyển từ Entity sang DTO
//	            ProductImageDTO productImageDTO = ProductImageDTO.builder()
//	                    .id(productImage.getId())
//	                    .imageUrl(productImage.getImageUrl())
//	                    .build();
//	            uploadedImages.add(productImageDTO);
//	        }
//
//	        return ResponseEntity.ok(uploadedImages);
//	    }  catch (Exception e) {
//	        return ResponseEntity.badRequest().body("Error: " + e.getMessage());
//	    }
//	}

//	@PostMapping(value = "uploads/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//	public ResponseEntity<?> uploadImages(@PathVariable("id") Integer productId,
//			@RequestParam("files") List<MultipartFile> files) {
//		try {
//			ProductEntity existingProduct = productService.getProductById(productId);
//			files = files == null ? new ArrayList<MultipartFile>() : files;
//			List<ProductImageEntity> productImages = new ArrayList<>();
//			for (MultipartFile file : files) {
//				if (file.getSize() == 0) {
//					continue;
//				}
//				if (file.getSize() > 10 * 1024 * 1024) {
//					return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE)
//							.body("File is too large! Maximum size is 10MB");
//				}
//				String contentType = file.getContentType();
//				if (contentType == null || !contentType.startsWith("image/")) {
//					return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body("File must be an image");
//				}
//				String filename = storeFile(file);
//				ProductImageEntity productImage = productService.createProductImage(existingProduct.getId(),
//						ProductImageDTO.builder().imageUrl(filename).build());
//				productImages.add(productImage);
//
//			}
//			return ResponseEntity.ok().body(productImages);
//		} catch (Exception e) {
//			return ResponseEntity.badRequest().body(e.getMessage());
//		}
//	}

	@GetMapping("/images/{imageName}")
	public ResponseEntity<?> viewImage(@PathVariable String imageName) {
		try {
			Path imagePath = Paths.get("uploads/" + imageName);
			UrlResource resource = new UrlResource(imagePath.toUri());
			if (resource.exists()) {
				return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(resource);
			} else {
				return ResponseEntity.notFound().build();
			}
		} catch (Exception e) {
			return ResponseEntity.notFound().build();
		}
	}

	@PutMapping("/{id}/thumbnail")
	public ResponseEntity<?> updateThumbnail(@PathVariable Integer id, @RequestBody Map<String, String> body) {
		String newThumbnail = body.get("thumbnail");
		if (newThumbnail == null || newThumbnail.isEmpty()) {
			return ResponseEntity.badRequest().body("Thumbnail URL is required");
		}

		productService.updateThumbnail(id, newThumbnail);
		return ResponseEntity.ok("Thumbnail updated successfully");
	}

	public String storeFile(MultipartFile file) throws IOException {
		String filename = StringUtils.cleanPath(file.getOriginalFilename());
		String uniqueFilename = UUID.randomUUID().toString() + "_" + filename;
		Path uploadDir = Paths.get("uploads");
		if (!Files.exists(uploadDir)) {
			Files.createDirectories(uploadDir);
		}
		Path destination = Paths.get(uploadDir.toString(), uniqueFilename);

		Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);
		return uniqueFilename;

	}

	@GetMapping("/{productId}/images")
	public ResponseEntity<?> getAllImagesByProductId(@PathVariable Integer productId) {
		try {
			List<ProductImageDTO> images = productService.getAllImagesByProductId(productId);
			return ResponseEntity.ok(images);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
		}
	}

	@GetMapping("/by-ids")
	public ResponseEntity<?> getProductsByIds(@RequestParam("ids") String ids) {
		try {
			List<Integer> productIds = Arrays.stream(ids.split(",")).map(Integer::parseInt)
					.collect(Collectors.toList());

			List<ProductDTO> products = productService.getProductsByIds(productIds);
			return ResponseEntity.ok(products);
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	@PutMapping("/{id}")
	public ResponseEntity<ProductDTO> updateNew(@Valid @RequestBody ProductDTO model, @PathVariable("id") Integer id) {
		model.setId(id);
		return ResponseEntity.ok(productService.save(model));
	}

	@DeleteMapping
	public ResponseEntity<Void> deleteNew(@RequestBody Integer[] ids) {
		productService.delete(ids);
		return ResponseEntity.noContent().build();
	}

//	@GetMapping("/top-selling")
//	public ResponseEntity<?> getTopSellingProducts(@RequestParam(defaultValue = "5") int topN) {
//	    return ResponseEntity.ok( productService.getTopSellingProducts(topN));
//	}
	@GetMapping("/top-selling")
	public ResponseEntity<?> getTopSellingProductsByMonth(@RequestParam int month, @RequestParam int year,
			@RequestParam(defaultValue = "5") int limit) {
		List<TopSellingProductDTO> result = productService.getTopSellingProductsByMonth(month, year, limit);
		return ResponseEntity.ok(result);
	}

}
