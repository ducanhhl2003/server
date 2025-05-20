package book.controller;

import java.io.ByteArrayInputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

import book.controller.output.BookOutput;
import book.dto.BookDTO;
import book.dto.response.GenericResponse;
import book.dto.search.BookSearchDTO;
import book.service.IBookService;
import book.utils.LocalizationUtils;
import book.utils.MessageKeys;
import book.utils.ResponseUtil;
import jakarta.validation.Valid;

@CrossOrigin
@RestController
@RequestMapping("/book")
public class BookController {
	@Autowired
	private IBookService bookService;
	@Autowired
	private LocalizationUtils localizationUtils;

	@PreAuthorize("@customSecurityExpression.fileRole(#httpServletRequest)")
	@GetMapping
	public ResponseEntity<GenericResponse<BookOutput>> showBook(@RequestParam("page") int page, @RequestParam("limit") int limit) {
		BookOutput result = bookService.getBookList(page, limit);
		return ResponseUtil.success(MessageKeys.GET_BOOK_SUCCESSFULLY,
				localizationUtils.getLocalizedMessage(MessageKeys.GET_BOOK_SUCCESSFULLY),result);
	}

	@PreAuthorize("@customSecurityExpression.fileRole(#httpServletRequest)")
	@PostMapping("/search")
	public ResponseEntity<String> searchBooks(@RequestBody BookSearchDTO searchRequest) {
		return ResponseEntity.ok(bookService.searchBooksResponse(searchRequest));

	}

	@PostMapping
	@PreAuthorize("@customSecurityExpression.fileRole(#httpServletRequest)")
	public ResponseEntity<GenericResponse<BookDTO>> createNew(@Valid @RequestBody BookDTO model) {
		return ResponseUtil.success(MessageKeys.INSERT_BOOK_SUCCESSFULLY,
				localizationUtils.getLocalizedMessage(MessageKeys.INSERT_BOOK_SUCCESSFULLY),bookService.save(model));
	}

	@PutMapping("/{id}")
	@PreAuthorize("@customSecurityExpression.fileRole(#httpServletRequest)")
	public  ResponseEntity<GenericResponse<BookDTO>> updateNew(@Valid @RequestBody BookDTO model, @PathVariable("id") Integer id) {
		model.setId(id);
		return ResponseUtil.success(MessageKeys.UPDATE_BOOK_SUCCESSFULLY,
				localizationUtils.getLocalizedMessage(MessageKeys.UPDATE_BOOK_SUCCESSFULLY),bookService.save(model));
	}

	@DeleteMapping
	@PreAuthorize("@customSecurityExpression.fileRole(#httpServletRequest)")
	public ResponseEntity<Void> deleteNew(@RequestBody Integer[] ids) {
		bookService.delete(ids);
		return ResponseEntity.noContent().build();
	}

	@PreAuthorize("@customSecurityExpression.fileRole(#httpServletRequest)")
	@GetMapping("/export")
	public ResponseEntity<InputStreamResource> exportBooks() {
		String filename = "Books.xlsx";
		InputStreamResource file = new InputStreamResource(bookService.exportBooksToExcel());

		return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
				.contentType(
						MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
				.body(file);
	}

	@PostMapping("/import")
	@PreAuthorize("@customSecurityExpression.fileRole(#httpServletRequest)")
	public ResponseEntity<?> importBooks(@RequestParam("file") MultipartFile file) {
		ByteArrayInputStream errorStream = bookService.importBooksFromExcel(file);

		if (errorStream != null) {
			HttpHeaders headers = new HttpHeaders();
			headers.add("Content-Disposition", "attachment; filename=import_errors.xlsx");

			return ResponseEntity.status(HttpStatus.BAD_REQUEST).headers(headers)
					.contentType(MediaType.APPLICATION_OCTET_STREAM).body(new InputStreamResource(errorStream));
		}

		return ResponseEntity.ok().body("Import thành công");
	}

}
