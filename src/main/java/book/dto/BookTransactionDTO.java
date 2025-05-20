package book.dto;

import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class BookTransactionDTO extends AbstractDTO<BookTransactionDTO> {
	private Integer id;
	private LocalDate borrowDate;
	private LocalDate returnDate;
	private Integer userId;
	private Integer bookId;

}