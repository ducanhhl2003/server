package book.excel;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import book.entity.BookEntity;

public class ExcelHelperBook {
	private static final String[] HEADERS = { "Code", "Title", "Author", "Categories" };
	private static final String SHEET = "Books";

	public static ByteArrayInputStream booksToExcel(List<BookEntity> books) throws IOException {
		Workbook workbook = new XSSFWorkbook();
		Sheet sheet = workbook.createSheet(SHEET);

		Row headerRow = sheet.createRow(0);
		for (int col = 0; col < HEADERS.length; col++) {
			Cell cell = headerRow.createCell(col);
			cell.setCellValue(HEADERS[col]);
		}

		int rowIdx = 1;
		for (BookEntity book : books) {
			Row row = sheet.createRow(rowIdx++);
			row.createCell(0).setCellValue(book.getCode());
			row.createCell(1).setCellValue(book.getTitle());
			row.createCell(2).setCellValue(book.getAuthor());
			row.createCell(3)
					.setCellValue(book.getCategories() != null
							? String.join(", ", book.getCategories().stream().map(c -> c.getName()).toList())
							: "");
		}

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		workbook.write(out);
		workbook.close();
		return new ByteArrayInputStream(out.toByteArray());
	}

	public static List<BookEntity> excelToBooks(MultipartFile file) {
		try {
			List<BookEntity> books = new ArrayList<>();
			Workbook workbook = new XSSFWorkbook(file.getInputStream());
			Sheet sheet = workbook.getSheet(SHEET);
			Iterator<Row> rows = sheet.iterator();

			if (rows.hasNext())
				rows.next(); // Bỏ qua header

			while (rows.hasNext()) {
				Row currentRow = rows.next();
				BookEntity book = new BookEntity();
				book.setCode(currentRow.getCell(0).getStringCellValue());
				book.setTitle(currentRow.getCell(1).getStringCellValue());
				book.setAuthor(currentRow.getCell(2).getStringCellValue());

				books.add(book);
			}

			workbook.close();
			return books;
		} catch (IOException e) {
			throw new RuntimeException("Lỗi khi đọc file Excel: " + e.getMessage());
		}
	}

	public static ByteArrayInputStream createErrorFile(List<String[]> errors) {
		try (Workbook workbook = new XSSFWorkbook()) {
			Sheet sheet = workbook.createSheet("Lỗi Import");

			Row header = sheet.createRow(0);
			header.createCell(0).setCellValue("Dòng");
			header.createCell(1).setCellValue("Lỗi");

			int rowNum = 1;
			for (String[] error : errors) {
				Row row = sheet.createRow(rowNum++);
				row.createCell(0).setCellValue(error[0]);
				row.createCell(1).setCellValue(error[1]);
			}

			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			workbook.write(outputStream);
			return new ByteArrayInputStream(outputStream.toByteArray());

		} catch (IOException e) {
			throw new RuntimeException("Lỗi tạo file báo lỗi: " + e.getMessage(), e);
		}
	}

}
